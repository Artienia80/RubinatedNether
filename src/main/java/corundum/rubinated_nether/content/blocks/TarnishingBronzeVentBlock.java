package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.RNConfig;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TarnishingBronzeVentBlock extends DirectionalBlock implements BEBlock<TarnishingBronzeVentBlock.VentBlockEntity>, TarnishingBronze {

    public static final MapCodec<TarnishingBronzeVentBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(TarnishingBronzeVentBlock::getAge),
                    propertiesCodec()
            ).apply(instance, TarnishingBronzeVentBlock::new)
    );

    private static final VoxelShape SMOKE_SEGMENT_BASE = Shapes.box(0.3, 0, 0.3, 0.7, 1, 0.7);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WAXED = TarnishingBronze.WAXED;
    public static final IntegerProperty SIGNAL_STRENGTH = IntegerProperty.create("signal_strength", 0, 15);

    private static final double GEYSER_IMPULSE = 0.2;
    private static final double GEYSER_BASE_MAX_SPEED = 0.3;
    private static final double GEYSER_MAX_SPEED_PER_BLOCK = 0.1;
    private static final double ANTI_GRAVITY_LIFT = 0.10;
    private static final Predicate<Entity> LAUNCH_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isAlive);

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
        BlockState placement = this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(WAXED, false);

        if (tarnishStage == TarnishStage.CRYSTALLIZED && !context.getLevel().isClientSide()) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
        }

        return placement;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide()) return;
        if (tarnishStage == TarnishStage.CRYSTALLIZED) return;

        int signal = level.getBestNeighborSignal(pos);
        boolean powered = signal > 0;
        boolean signalChanged = state.getValue(POWERED) != powered || state.getValue(SIGNAL_STRENGTH) != signal;

        if (signalChanged) {
            state = state.setValue(POWERED, powered).setValue(SIGNAL_STRENGTH, signal);
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }

        if (powered) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.scheduleTick(pos, this, 1);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide()) return;

        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.scheduleTick(pos, this, 1);
            }
            return;
        }

        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            BlockState newState = state.setValue(POWERED, true).setValue(SIGNAL_STRENGTH, signal);
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            level.scheduleTick(pos, this, 1);
        }
    }

    public static boolean isFaceBlocked(Level level, BlockPos pos, Direction dir) {
        BlockPos adj = pos.relative(dir);
        BlockState adjState = level.getBlockState(adj);
        if (adjState.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) return false;
        VoxelShape col = adjState.getCollisionShape(level, adj);
        if (col.isEmpty()) return false;
        return !Shapes.join(col, rotateSmokeShape(dir), BooleanOp.AND).isEmpty();
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (tarnishStage.equals(TarnishStage.CRYSTALLIZED)) {
            checkAndFireCrystallized(state, level, pos);
            level.scheduleTick(pos, this, 1);
            return;
        }

        if (!state.getValue(POWERED)) return;
        if (getLaunchImpulse() <= 0) return;

        int signalRange = state.getValue(SIGNAL_STRENGTH);
        Direction facing = state.getValue(FACING);

        var result = getRanges(level, pos, state);
        int front = result.getLeft();
        int back = result.getRight();

        if (front > 0 && checkMagmaBlock(level, pos, facing.getOpposite(), back) && isDamageTick(level, pos)) {
            dealSteamDamage(level, pos, facing, signalRange, front);
        }

        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockState curState = level.getBlockState(pos);
        if (!(curState.getBlock() instanceof TarnishingBronzeVentBlock ventBlock)) return;

        Direction facing = curState.getValue(FACING);

        if (ventBlock.getAge() == TarnishStage.CRYSTALLIZED) {
            if (isFaceBlocked(level, pos, facing)) return;
            AABB detectionBox = buildDetectionAABB(pos, facing, RNConfig.crystallizedVentRange);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, detectionBox);
            if (entities.isEmpty()) return;
            int smokeRange = calculateSmokeRange(level, pos, facing, RNConfig.crystallizedVentRange);
            if (smokeRange <= 0) return;
            spawnSteamParticles(level, pos, facing, RNConfig.crystallizedVentRange / 40.0, 1.0, random);
            return;
        }

        if (!curState.getValue(POWERED)) return;
        int signalRange = curState.getValue(SIGNAL_STRENGTH);
        if (signalRange <= 0) return;

        var result = getRanges(level, pos, curState);
        int front = result.getKey();
        int back = result.getValue();

        if (front > 0)
            spawnSteamParticles(level, pos, facing, signalRange / 40.0, 1.0, random);

        if (back > 0)
            spawnSteamParticles(level, pos, facing.getOpposite(), -(signalRange / 40.0), back, random);
    }

    private Pair<Integer, Integer> getRanges(Level level, BlockPos pos, BlockState state) {
        int signalRange = state.getValue(SIGNAL_STRENGTH);
        Direction facing = state.getValue(FACING);

        int front = isFaceBlocked(level, pos, facing) ? 0
                : calculateSmokeRange(level, pos, facing, signalRange);

        int back = isFaceBlocked(level, pos, facing.getOpposite()) ? 0
                : calculateSmokeRange(level, pos, facing.getOpposite(), signalRange);

        return Pair.of(front, back);
    }

    private static void spawnSteamParticles(Level level,
                                            BlockPos pos, Direction facing,
                                            double initialSpeed, double spawnOffset, RandomSource random) {
        double ox = pos.getX() + 0.5;
        double oy = pos.getY() + 0.5;
        double oz = pos.getZ() + 0.5;
        double dx = facing.getStepX();
        double dy = facing.getStepY();
        double dz = facing.getStepZ();

        double rawCount = RNConfig.ventSmokeParticleMultiplier;
        int baseCount = (int) rawCount;
        if (random.nextDouble() < (rawCount - baseCount)) baseCount++;

        for (int i = 0; i < baseCount; i++) {
            double sx = facing.getAxis() != Direction.Axis.X ? (random.nextDouble() - 0.5) * 0.8 : 0;
            double sy = facing.getAxis() != Direction.Axis.Y ? (random.nextDouble() - 0.5) * 0.8 : 0;
            double sz = facing.getAxis() != Direction.Axis.Z ? (random.nextDouble() - 0.5) * 0.8 : 0;

            double vx = dx * initialSpeed + (random.nextDouble() - 0.5) * 0.01;
            double vy = dy * initialSpeed + (random.nextDouble() - 0.5) * 0.01;
            double vz = dz * initialSpeed + (random.nextDouble() - 0.5) * 0.01;

            level.addParticle(RNParticleTypes.STEAM.get(), true,
                    ox + dx * spawnOffset + sx,
                    oy + dy * spawnOffset + sy,
                    oz + dz * spawnOffset + sz,
                    vx, vy, vz);
        }
    }

    private void checkAndFireCrystallized(BlockState state, ServerLevel level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        int range = RNConfig.crystallizedVentRange;

        if (isFaceBlocked(level, pos, facing)) return;

        AABB detectionBox = buildDetectionAABB(pos, facing, range);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, detectionBox);
        if (entities.isEmpty()) return;

        int smokeRange = calculateSmokeRange(level, pos, facing, range);
        dealSteamDamage(level, pos, facing, range, smokeRange);
    }

    public static AABB buildDetectionAABB(BlockPos pos, Direction facing, int range) {
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        double hw = 0.375;
        double ex = facing.getStepX() * range;
        double ey = facing.getStepY() * range;
        double ez = facing.getStepZ() * range;

        double minX = cx + Math.min(0, ex) - (facing.getAxis() != Direction.Axis.X ? hw : 0);
        double maxX = cx + Math.max(0, ex) + (facing.getAxis() != Direction.Axis.X ? hw : 0);
        double minY = cy + Math.min(0, ey) - (facing.getAxis() != Direction.Axis.Y ? hw : 0);
        double maxY = cy + Math.max(0, ey) + (facing.getAxis() != Direction.Axis.Y ? hw : 0);
        double minZ = cz + Math.min(0, ez) - (facing.getAxis() != Direction.Axis.Z ? hw : 0);
        double maxZ = cz + Math.max(0, ez) + (facing.getAxis() != Direction.Axis.Z ? hw : 0);

        if (facing.getStepX() > 0) minX += 1.0; else if (facing.getStepX() < 0) maxX -= 1.0;
        if (facing.getStepY() > 0) minY += 1.0; else if (facing.getStepY() < 0) maxY -= 1.0;
        if (facing.getStepZ() > 0) minZ += 1.0; else if (facing.getStepZ() < 0) maxZ -= 1.0;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static int calculateSmokeRange(Level level, BlockPos pos, Direction facing, int maxRange) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        VoxelShape smokeColumn = rotateSmokeShape(facing);

        for (int i = 0; i < maxRange; i++) {
            cursor.move(facing);
            BlockState state = level.getBlockState(cursor);
            if (state.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) continue;
            VoxelShape collision = Shapes.join(state.getCollisionShape(level, cursor), smokeColumn, BooleanOp.AND);
            if (!collision.isEmpty()) return i;
        }

        return maxRange;
    }


    public static boolean checkMagmaBlock(Level level, BlockPos pos, Direction facing, int range) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        cursor.move(facing, range + 1);
        BlockState state = level.getBlockState(cursor);
        return state.is(Blocks.MAGMA_BLOCK);
    }

    private static VoxelShape rotateSmokeShape(Direction facing) {
        return switch (facing.getAxis()) {
            case Z -> Shapes.box(0.125, 0.125, 0, 0.875, 0.875, 1); // was 0.3/0.7
            case X -> Shapes.box(0, 0.125, 0.125, 1, 0.875, 0.875);
            case Y -> Shapes.box(0.125, 0, 0.125, 0.875, 1, 0.875);
        };
    }

    private void dealSteamDamage(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange) {
        if (signal <= 0 || smokeRange <= 0) return;
        AABB steamBox = buildDetectionAABB(pos, facing, smokeRange);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, steamBox);

        for (LivingEntity entity : entities) {
            double dist = distanceAlongFacing(pos, BlockPos.containing(entity.position()), facing);
            if (dist < 0 || dist >= smokeRange) continue;
            float damage = (float) (signal - dist) / 3.0f;
            if (damage > 0) entity.hurt(level.damageSources().inFire(), damage);
        }
    }


    static void launchTick(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof TarnishingBronzeVentBlock vent)) return;
        if (!state.getValue(POWERED)) return;

        double impulse = vent.getLaunchImpulse();
        if (impulse <= 0) return;

        Direction facing = state.getValue(FACING);
        var ranges = vent.getRanges(level, pos, state);

        if (ranges.getLeft() > 0)
            launchColumn(level, pos, facing, facing, ranges.getLeft(), impulse);
        if (ranges.getRight() > 0)
            launchColumn(level, pos, facing.getOpposite(), facing, ranges.getRight(), impulse);
    }


    private static void launchColumn(Level level, BlockPos pos, Direction column, Direction flow, int range, double impulse) {
        Vec3 columnDir = Vec3.atLowerCornerOf(column.getNormal());
        AABB box = new AABB(pos.relative(column)).expandTowards(columnDir.scale(range - 1));
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, box, LAUNCH_PREDICATE);
        if (entities.isEmpty()) return;

        Vec3 flowDir = Vec3.atLowerCornerOf(flow.getNormal());
        double strength = impulse / GEYSER_IMPULSE;
        double maxSpeed = (GEYSER_BASE_MAX_SPEED + range * GEYSER_MAX_SPEED_PER_BLOCK) * strength;

        Vec3 push = flowDir.scale(impulse);
        if (flow == Direction.UP) push = push.add(0, ANTI_GRAVITY_LIFT * strength, 0);

        for (Entity entity : entities) {
            if (flow == Direction.UP) cushionFall(entity);

            if (!isSimulatedHere(level, entity)) continue;
            if (entity instanceof Player player && player.getAbilities().flying) continue;
            if (entity.isPassenger() || entity.getType().is(RNTags.EntityTypes.NOT_AFFECTED_BY_VENTS)) continue;
            if (entity.getDeltaMovement().dot(flowDir) >= maxSpeed) continue;

            entity.addDeltaMovement(push);

            if (!level.isClientSide) entity.hurtMarked = true;
        }
    }

    private static boolean isSimulatedHere(Level level, Entity entity) {
        if (entity instanceof Player player)
            return level.isClientSide && player.isLocalPlayer();
        if (entity.getControllingPassenger() instanceof Player rider)
            return level.isClientSide && rider.isLocalPlayer();
        return !level.isClientSide;
    }

    private static void cushionFall(Entity entity) {
        if (entity.getDeltaMovement().y > -0.5 && entity.fallDistance > 1.0F) {
            entity.fallDistance = 1.0F;
        }
    }

    private static boolean isDamageTick(Level level, BlockPos pos) {
        return Math.floorMod(level.getGameTime() + pos.hashCode(), 20L) == 0;
    }

    private double getLaunchImpulse() {
        return switch (tarnishStage) {
            case UNAFFECTED,
                 CRYSTALLIZED -> 0.0;
            case DISCOLORED   -> GEYSER_IMPULSE * 0.25;
            case CORRODED     -> GEYSER_IMPULSE * 0.5;
            case TARNISHED    -> GEYSER_IMPULSE;
        };
    }

    private int distanceAlongFacing(BlockPos ventPos, BlockPos entityPos, Direction facing) {
        int ex = entityPos.getX() - ventPos.getX();
        int ey = entityPos.getY() - ventPos.getY();
        int ez = entityPos.getZ() - ventPos.getZ();
        return ex * facing.getStepX() + ey * facing.getStepY() + ez * facing.getStepZ() - 1;
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
    public TarnishStage getAge() { return tarnishStage; }

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
    protected MapCodec<? extends TarnishingBronzeVentBlock> codec() { return CODEC; }

    @Override
    public BlockEntityType<? extends VentBlockEntity> getBlockEntityType() {
        return RNBlockEntities.BRONZE_VENT.get();
    }

    @Override
    public Class<? extends VentBlockEntity> getBlockEntityClass() {
        return VentBlockEntity.class;
    }


    public static class VentBlockEntity extends BlockEntity implements TickableBlockEntity {
        public VentBlockEntity(BlockPos pos, BlockState state) {
            super(RNBlockEntities.BRONZE_VENT.get(), pos, state);
        }

        @Override
        public void tick(boolean clientSide) {
            if (this.level != null) launchTick(this.level, this.worldPosition, this.getBlockState());
        }

        @Override
        public void tick() {}
    }
}