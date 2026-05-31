package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.gearbox.GearboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GearboxBlock extends TarnishingBronzeBlock {
    public static final MapCodec<GearboxBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(GearboxBlock::getAge),
                    propertiesCodec()
            ).apply(instance, GearboxBlock::new)
    );

    public static final int MAX_CRANK = 15;
    public static final double PLAYER_SCAN_RADIUS = 64.0;
    public static final double CRYSTALLIZED_TRIGGER_RADIUS = 16.0;

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty COOLING_DOWN = BooleanProperty.create("cooling_down");
    public static final IntegerProperty CRANK_LEVEL = BlockStateProperties.LEVEL;

    public GearboxBlock(TarnishStage stage, Properties properties) {
        super(stage, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(POWERED, false)
                .setValue(COOLING_DOWN, false)
                .setValue(CRANK_LEVEL, 0));
    }

    public MapCodec<GearboxBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, POWERED, COOLING_DOWN, CRANK_LEVEL);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
                .setValue(COOLING_DOWN, false)
                .setValue(CRANK_LEVEL, 0);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel && !isPeaceful(level)) {
            boolean powered = serverLevel.hasNeighborSignal(pos);
            if (powered && !state.getValue(LIT) && !state.getValue(COOLING_DOWN)) {
                fireLit(serverLevel, state.setValue(POWERED, true), pos);
                serverLevel.setBlock(pos, serverLevel.getBlockState(pos).setValue(POWERED, true), 3);
            }
            // Crystallized gearboxes start polling immediately on placement
            if (getAge() == TarnishStage.CRYSTALLIZED) {
                serverLevel.scheduleTick(pos, this, 20);
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    private static boolean isPeaceful(Level level) {
        return level.getDifficulty() == Difficulty.PEACEFUL;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) {
            // If the world is on peaceful, deactivate immediately
            if (isPeaceful(level)) {
                if (state.getValue(LIT) || state.getValue(COOLING_DOWN) || state.getValue(CRANK_LEVEL) > 0) {
                    level.setBlock(pos, state
                            .setValue(LIT, false)
                            .setValue(COOLING_DOWN, false)
                            .setValue(CRANK_LEVEL, 0), 3);
                    if (level.getBlockEntity(pos) instanceof GearboxBlockEntity be) {
                        be.forceReset();
                    }
                }
                return;
            }

            if (state.getValue(COOLING_DOWN)) {
                super.tick(state, level, pos, random);
                // Crystallized gearboxes keep polling even during cooldown
                if (getAge() == TarnishStage.CRYSTALLIZED) {
                    level.scheduleTick(pos, this, 20);
                }
                return;
            }

            // Crystallized gearboxes auto-trigger when a player is within 16 blocks
            if (getAge() == TarnishStage.CRYSTALLIZED
                    && !state.getValue(LIT)
                    && isCrankable(state)) {
                AABB triggerBox = AABB.ofSize(
                        net.minecraft.world.phys.Vec3.atCenterOf(pos),
                        CRYSTALLIZED_TRIGGER_RADIUS * 2,
                        CRYSTALLIZED_TRIGGER_RADIUS * 2,
                        CRYSTALLIZED_TRIGGER_RADIUS * 2
                );
                boolean playerNearby = !level.getEntitiesOfClass(Player.class, triggerBox).isEmpty();
                if (playerNearby) {
                    fireLit(level, state, pos);
                    level.scheduleTick(pos, this, 20);
                    return;
                }
                // Keep polling every second while idle
                level.scheduleTick(pos, this, 20);
            }

            int crankCount = state.getValue(CRANK_LEVEL);
            if (crankCount > 0 && crankCount < MAX_CRANK) {
                decreaseCrankLevel(level, state, pos);
            } else if (crankCount == MAX_CRANK) {
                fireLit(level, state, pos);
            }
        }
        super.tick(state, level, pos, random);
    }

    void fireLit(ServerLevel level, BlockState state, BlockPos pos) {
        // Never activate on peaceful
        if (isPeaceful(level)) return;

        Difficulty difficulty = level.getDifficulty();

        int baseTotal;
        int baseWaveSize;
        int waveDelaySecs;

        switch (difficulty) {
            case HARD ->   { baseTotal = 16; baseWaveSize = 4; waveDelaySecs = 100; }
            case NORMAL -> { baseTotal = 12; baseWaveSize = 3; waveDelaySecs = 80;  }
            default ->     { baseTotal = 6;  baseWaveSize = 2; waveDelaySecs = 60;  }
        }

        // Scan for nearby players
        AABB scanBox = AABB.ofSize(
                net.minecraft.world.phys.Vec3.atCenterOf(pos),
                PLAYER_SCAN_RADIUS * 2, PLAYER_SCAN_RADIUS * 2, PLAYER_SCAN_RADIUS * 2
        );
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, scanBox);
        int playerCount = Math.max(1, nearbyPlayers.size());

        // Bad omen check — any nearby player with bad omen
        boolean hasBadOmen = nearbyPlayers.stream()
                .anyMatch(p -> p.hasEffect(MobEffects.BAD_OMEN));

        // Apply player count multiplier first
        int totalBronzes = baseTotal * playerCount;
        int waveSize = baseWaveSize * playerCount;

        // Apply bad omen bonus to base total only (not multiplied)
        if (hasBadOmen) {
            totalBronzes += baseTotal / 2;
        }

        state = state.setValue(CRANK_LEVEL, 0).setValue(LIT, true);
        level.setBlock(pos, state, 3);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);

        if (level.getBlockEntity(pos) instanceof GearboxBlockEntity be) {
            be.startSpawning(totalBronzes, waveSize, waveDelaySecs * 20);
        }
    }

    public void finishCooldown(ServerLevel level, BlockState state, BlockPos pos) {
        state = state.setValue(COOLING_DOWN, false);
        if (state.getValue(POWERED) && !isPeaceful(level)) {
            fireLit(level, state, pos);
        } else {
            level.setBlock(pos, state, 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(COOLING_DOWN)) {
            for (int i = 0; i < 4; i++) {
                double ox = (random.nextDouble() - 0.5) * 0.6;
                double oz = (random.nextDouble() - 0.5) * 0.6;
                level.addParticle(ParticleTypes.LARGE_SMOKE,
                        pos.getX() + 0.5 + ox, pos.getY() + 1.0, pos.getZ() + 0.5 + oz,
                        0.0, 0.03, 0.0);
            }
        } else if (state.getValue(LIT)) {
            switch (state.getValue(FACING)) {
                case NORTH, SOUTH -> {
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() - 0.2,
                            0.0, 0.02D, -0.01);
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1.2,
                            0.0, 0.02D, 0.01);
                }
                case WEST, EAST -> {
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() - 0.2, pos.getY() + 0.5, pos.getZ() + 0.5,
                            -0.01, 0.02D, 0.0);
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 1.2, pos.getY() + 0.5, pos.getZ() + 0.5,
                            0.01, 0.02D, 0.0);
                }
            }
        }
        super.animateTick(state, level, pos, random);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && isCrankable(state) && !isPeaceful(level)) {
            increaseCrankLevel(level, state, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private boolean isCrankable(BlockState state) {
        return !state.getValue(LIT) && !state.getValue(COOLING_DOWN);
    }

    private static void increaseCrankLevel(Level level, BlockState state, BlockPos pos) {
        state = state.setValue(CRANK_LEVEL, Mth.clamp(state.getValue(CRANK_LEVEL) + 1, 0, MAX_CRANK));
        level.setBlock(pos, state, 3);
        level.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.scheduleTick(pos, state.getBlock(), 40);
    }

    private static void decreaseCrankLevel(Level level, BlockState state, BlockPos pos) {
        state = state.setValue(CRANK_LEVEL, Mth.clamp(state.getValue(CRANK_LEVEL) - 1, 0, MAX_CRANK));
        level.setBlock(pos, state, 3);
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.scheduleTick(pos, state.getBlock(), 20);
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag && !state.getValue(LIT) && !state.getValue(COOLING_DOWN) && !isPeaceful(level)) {
                fireLit(level, state.setValue(POWERED, true), pos);
            } else {
                level.setBlock(pos, state.setValue(POWERED, flag), 3);
            }
        }
    }

    public static boolean noViewBlocking(BlockState _1, BlockGetter _2, BlockPos _3) {
        return false;
    }
}