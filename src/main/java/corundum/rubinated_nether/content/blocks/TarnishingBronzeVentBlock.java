package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TarnishingBronzeVentBlock extends DirectionalBlock implements TarnishingBronze {

    public static final MapCodec<TarnishingBronzeVentBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(TarnishingBronzeVentBlock::getAge),
                    propertiesCodec()
            ).apply(instance, TarnishingBronzeVentBlock::new)
    );

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WAXED = TarnishingBronze.WAXED;
    public static final IntegerProperty SIGNAL_STRENGTH = IntegerProperty.create("signal_strength", 0, 15);

    private final TarnishStage tarnishStage;

    public TarnishingBronzeVentBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishStage = tarnishStage;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(POWERED, false)
                        .setValue(SIGNAL_STRENGTH, 0)
                        .setValue(WAXED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, SIGNAL_STRENGTH, WAXED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(WAXED, false);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide()) return;

        int signal = level.getBestNeighborSignal(pos);
        boolean powered = signal > 0;

        if (state.getValue(POWERED) != powered || state.getValue(SIGNAL_STRENGTH) != signal) {
            level.setBlock(pos, state.setValue(POWERED, powered).setValue(SIGNAL_STRENGTH, signal), Block.UPDATE_ALL);

            if (powered && !level.getBlockTicks().hasScheduledTick(pos, this)) {
                ((ServerLevel) level).scheduleTick(pos, this, 20);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide()) return;
        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            level.setBlock(pos, state.setValue(POWERED, true).setValue(SIGNAL_STRENGTH, signal), Block.UPDATE_ALL);
            ((ServerLevel) level).scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) return;

        dealSteamDamage(level, pos, state.getValue(FACING), state.getValue(SIGNAL_STRENGTH));
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) return;

        int signal = state.getValue(SIGNAL_STRENGTH);
        Direction facing = state.getValue(FACING);

        double ox = pos.getX() + 0.5;
        double oy = pos.getY() + 0.5;
        double oz = pos.getZ() + 0.5;

        double dx = facing.getStepX();
        double dy = facing.getStepY();
        double dz = facing.getStepZ();

        double initialSpeed = signal / 40.0;

        for (int i = 0; i < 6; i++) {
            Vec3 spread = perpendicularSpread(facing, random, 0.4);
            double vx = dx * initialSpeed + (random.nextDouble() - 0.5) * 0.01;
            double vy = dy * initialSpeed + (random.nextDouble() - 0.5) * 0.01;
            double vz = dz * initialSpeed + (random.nextDouble() - 0.5) * 0.01;

            level.addParticle(RNParticleTypes.STEAM.get(), true,
                    ox + dx + spread.x,
                    oy + dy + spread.y,
                    oz + dz + spread.z,
                    vx, vy, vz);
        }
    }

    private void dealSteamDamage(ServerLevel level, BlockPos pos, Direction facing, int signal) {
        if (signal <= 0) return;

        AABB steamBox = buildSteamAABB(pos, facing, signal);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, steamBox);

        for (LivingEntity entity : entities) {
            int dist = distanceAlongFacing(pos, entity.blockPosition(), facing);
            if (dist < 0 || dist >= signal) continue;

            float damage = (signal - dist) / 3.0f;
            if (damage > 0) {
                entity.hurt(level.damageSources().inFire(), damage);
                entity.push(facing.getStepX() * 0.15, facing.getStepY() * 0.05, facing.getStepZ() * 0.15);
            }
        }
    }

    private int distanceAlongFacing(BlockPos ventPos, BlockPos entityPos, Direction facing) {
        int ex = entityPos.getX() - ventPos.getX();
        int ey = entityPos.getY() - ventPos.getY();
        int ez = entityPos.getZ() - ventPos.getZ();
        int projection = ex * facing.getStepX() + ey * facing.getStepY() + ez * facing.getStepZ();
        return projection - 1;
    }

    private AABB buildSteamAABB(BlockPos pos, Direction facing, int range) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        double ex = facing.getStepX() * range;
        double ey = facing.getStepY() * range;
        double ez = facing.getStepZ() * range;
        double hw = 0.6;

        double minX = cx + Math.min(0, ex) - (facing.getAxis() != Direction.Axis.X ? hw : 0);
        double maxX = cx + Math.max(0, ex) + (facing.getAxis() != Direction.Axis.X ? hw : 0);
        double minY = cy + Math.min(0, ey) - (facing.getAxis() != Direction.Axis.Y ? hw : 0);
        double maxY = cy + Math.max(0, ey) + (facing.getAxis() != Direction.Axis.Y ? hw : 0);
        double minZ = cz + Math.min(0, ez) - (facing.getAxis() != Direction.Axis.Z ? hw : 0);
        double maxZ = cz + Math.max(0, ez) + (facing.getAxis() != Direction.Axis.Z ? hw : 0);

        if (facing.getStepX() > 0) minX += 1.0;
        else if (facing.getStepX() < 0) maxX -= 1.0;
        if (facing.getStepY() > 0) minY += 1.0;
        else if (facing.getStepY() < 0) maxY -= 1.0;
        if (facing.getStepZ() > 0) minZ += 1.0;
        else if (facing.getStepZ() < 0) maxZ -= 1.0;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private Vec3 perpendicularSpread(Direction facing, RandomSource random, double maxSpread) {
        double s = (random.nextDouble() - 0.5) * 2.0 * maxSpread;
        double t = (random.nextDouble() - 0.5) * 2.0 * maxSpread;
        return switch (facing.getAxis()) {
            case X -> new Vec3(0, s, t);
            case Y -> new Vec3(s, 0, t);
            case Z -> new Vec3(s, t, 0);
        };
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(WAXED) && TarnishingBronze.canCrystallize(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) return;
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public TarnishStage getAge() {
        return tarnishStage;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        return waxing(stack, state, level, pos, player, hand, hitResult)
                ? ItemInteractionResult.SUCCESS
                : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level,
                                       BlockPos pos, Player player) {
        return new ItemStack(
                state.getValue(WAXED)
                        ? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
                        : this
        );
    }

    @Override
    protected MapCodec<? extends TarnishingBronzeVentBlock> codec() {
        return CODEC;
    }
}