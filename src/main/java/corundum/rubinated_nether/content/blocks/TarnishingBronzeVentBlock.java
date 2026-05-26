package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TarnishingBronzeVentBlock extends DirectionalBlock implements TarnishingBronze {

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

    private static final int BACK_RANGE = 15;

    // Server-side range cache: BlockPos -> [frontRange, backRange]
    // Keyed per-position so shared block instances don't bleed state.
    private static final Map<BlockPos, int[]> rangeCache = new HashMap<>();

    private final TarnishStage tarnishStage;
    private int damageTickCounter = 0;

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

        if (state.getValue(POWERED) != powered || state.getValue(SIGNAL_STRENGTH) != signal) {
            level.setBlock(pos, state.setValue(POWERED, powered).setValue(SIGNAL_STRENGTH, signal), Block.UPDATE_ALL);

            if (powered) {
                updateRangeCache(state.setValue(SIGNAL_STRENGTH, signal), (ServerLevel) level, pos);
                if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                    ((ServerLevel) level).scheduleTick(pos, this, 1);
                }
            } else {
                rangeCache.remove(pos);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide()) return;

        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                ((ServerLevel) level).scheduleTick(pos, this, 1);
            }
            return;
        }

        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            BlockState newState = state.setValue(POWERED, true).setValue(SIGNAL_STRENGTH, signal);
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            updateRangeCache(newState, (ServerLevel) level, pos);
            ((ServerLevel) level).scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        rangeCache.remove(pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // Recomputes and stores front/back ranges for this position.
    private void updateRangeCache(BlockState state, ServerLevel level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        int signal = state.getValue(SIGNAL_STRENGTH);

        // Skip if the block immediately in front is solid — range is 0
        int front = isFaceBlocked(level, pos, facing) ? 0
                : calculateSmokeRange(level, pos, facing, signal);
        int back = isFaceBlocked(level, pos, facing.getOpposite()) ? 0
                : calculateSmokeRange(level, pos, facing.getOpposite(), BACK_RANGE);

        rangeCache.put(pos.immutable(), new int[]{front, back});
    }

    // Returns true if the block adjacent in that direction would block steam entirely.
    private boolean isFaceBlocked(Level level, BlockPos pos, Direction dir) {
        BlockPos adj = pos.relative(dir);
        BlockState adj_state = level.getBlockState(adj);
        if (adj_state.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) return false;
        VoxelShape col = adj_state.getCollisionShape(level, adj);
        if (col.isEmpty()) return false;
        VoxelShape smoke = rotateSmokeShape(dir);
        return !Shapes.join(col, smoke, BooleanOp.AND).isEmpty();
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            checkAndFireCrystallized(state, level, pos);
            level.scheduleTick(pos, this, 1);
            return;
        }

        if (!state.getValue(POWERED)) return; // no reschedule — neighborChanged will restart it

        double scale = getSteamFlowScale();
        int signal = state.getValue(SIGNAL_STRENGTH);
        Direction facing = state.getValue(FACING);

        int[] ranges = rangeCache.get(pos);
        // Fallback: compute if cache is missing (e.g. after reload)
        if (ranges == null) {
            updateRangeCache(state, level, pos);
            ranges = rangeCache.get(pos);
        }

        int smokeRange = ranges[0];
        int backRange  = ranges[1];

        if (smokeRange > 0) {
            damageTickCounter++;
            if (damageTickCounter >= 20) {
                dealSteamDamage(level, pos, facing, signal, smokeRange);
                damageTickCounter = 0;
            }
            if (scale > 0.0) pushEntities(level, pos, facing, signal, smokeRange, scale);
        }

        if (backRange > 0 && scale > 0.0) {
            pullEntities(level, pos, facing.getOpposite(), signal, backRange, scale);
        }

        level.scheduleTick(pos, this, 1);
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

    private AABB buildDetectionAABB(BlockPos pos, Direction facing, int range) {
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        double hw = 0.6;
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

    private int calculateSmokeRange(Level level, BlockPos pos, Direction facing, int maxRange) {
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

    private VoxelShape rotateSmokeShape(Direction facing) {
        return switch (facing.getAxis()) {
            case Z -> SMOKE_SEGMENT_BASE;
            case X -> Shapes.box(0, 0.3, 0.3, 1, 0.7, 0.7);
            case Y -> Shapes.box(0.3, 0.3, 0, 0.7, 0.7, 1);
        };
    }

    private void dealSteamDamage(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange) {
        if (signal <= 0 || smokeRange <= 0) return;
        AABB steamBox = buildSteamAABB(pos, facing, smokeRange);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, steamBox);

        for (LivingEntity entity : entities) {
            double dist = exactDistanceAlongFacing(pos, entity.position(), facing);
            if (dist < 0 || dist >= smokeRange) continue;
            float damage = (float) (signal - dist) / 3.0f;
            if (damage > 0) entity.hurt(level.damageSources().inFire(), damage);
        }
    }

    private void pushEntities(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange, double scale) {
        AABB steamBox = buildSteamAABB(pos, facing, smokeRange);
        List<Entity> entities = level.getEntities((Entity) null, steamBox, e -> !e.isSpectator());
        Vec3 baseFlow = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        for (Entity entity : entities) {
            int dist = distanceAlongFacing(pos, entity.blockPosition(), facing);
            if (dist < 0 || dist >= smokeRange) continue;
            double falloff = 1.0 - (double) dist / smokeRange;
            Vec3 flow = baseFlow.scale(scale * falloff);
            Vec3 current = entity.getDeltaMovement();
            double minT = 0.003;
            if (Math.abs(current.x) < minT && Math.abs(current.z) < minT && flow.length() < minT * 1.5)
                flow = baseFlow.normalize().scale(minT * 1.5);
            entity.setDeltaMovement(current.add(flow));
            entity.hurtMarked = true;
        }
    }

    private void pullEntities(ServerLevel level, BlockPos pos, Direction back, int signal, int backRange, double scale) {
        AABB suctionBox = buildSteamAABB(pos, back, backRange);
        List<Entity> entities = level.getEntities((Entity) null, suctionBox, e -> !e.isSpectator());
        Direction facing = back.getOpposite();
        Vec3 baseFlow = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        for (Entity entity : entities) {
            int dist = distanceAlongFacing(pos, entity.blockPosition(), back);
            if (dist < 0 || dist >= backRange) continue;
            double falloff = 1.0 - (double) dist / backRange;
            Vec3 flow = baseFlow.scale(scale * falloff);
            Vec3 current = entity.getDeltaMovement();
            double minT = 0.003;
            if (Math.abs(current.x) < minT && Math.abs(current.z) < minT && flow.length() < minT * 1.5)
                flow = baseFlow.normalize().scale(minT * 1.5);
            entity.setDeltaMovement(current.add(flow));
            entity.hurtMarked = true;
        }
    }

    private double getSteamFlowScale() {
        return switch (tarnishStage) {
            case UNAFFECTED   -> 0.0;
            case DISCOLORED   -> 0.014;
            case CORRODED     -> 0.028;
            case TARNISHED    -> 0.056;
            case CRYSTALLIZED -> 0.0;
        };
    }

    private double exactDistanceAlongFacing(BlockPos ventPos, Vec3 entityPos, Direction facing) {
        double ex = entityPos.x - (ventPos.getX() + 0.5);
        double ey = entityPos.y - (ventPos.getY() + 0.5);
        double ez = entityPos.z - (ventPos.getZ() + 0.5);
        return ex * facing.getStepX() + ey * facing.getStepY() + ez * facing.getStepZ() - 1.0;
    }

    private int distanceAlongFacing(BlockPos ventPos, BlockPos entityPos, Direction facing) {
        int ex = entityPos.getX() - ventPos.getX();
        int ey = entityPos.getY() - ventPos.getY();
        int ez = entityPos.getZ() - ventPos.getZ();
        return ex * facing.getStepX() + ey * facing.getStepY() + ez * facing.getStepZ() - 1;
    }

    private AABB buildSteamAABB(BlockPos pos, Direction facing, int range) {
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
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

        if (facing.getStepX() > 0) minX += 1.0; else if (facing.getStepX() < 0) maxX -= 1.0;
        if (facing.getStepY() > 0) minY += 1.0; else if (facing.getStepY() < 0) maxY -= 1.0;
        if (facing.getStepZ() > 0) minZ += 1.0; else if (facing.getStepZ() < 0) maxZ -= 1.0;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
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
}