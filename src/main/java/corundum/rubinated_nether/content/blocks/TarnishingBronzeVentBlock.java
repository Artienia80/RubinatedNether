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

import java.util.List;

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
                .setValue(FACING, context.getClickedFace())
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

            if (powered && !level.getBlockTicks().hasScheduledTick(pos, this)) {
                ((ServerLevel) level).scheduleTick(pos, this, 1);
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
            level.setBlock(pos, state.setValue(POWERED, true).setValue(SIGNAL_STRENGTH, signal), Block.UPDATE_ALL);
            ((ServerLevel) level).scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            checkAndFireCrystallized(state, level, pos);
            level.scheduleTick(pos, this, 1);
            return;
        }

        if (!state.getValue(POWERED)) return;

        Direction facing = state.getValue(FACING);
        int signal = state.getValue(SIGNAL_STRENGTH);
        int smokeRange = calculateSmokeRange(level, pos, facing, signal);

        damageTickCounter++;
        if (damageTickCounter >= 20) {
            dealSteamDamage(level, pos, facing, signal, smokeRange);
            damageTickCounter = 0;
        }

        pushEntities(level, pos, facing, signal, smokeRange);
        level.scheduleTick(pos, this, 1);
    }

    private void checkAndFireCrystallized(BlockState state, ServerLevel level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        int range = RNConfig.crystallizedVentRange;
        AABB detectionBox = buildDetectionAABB(pos, facing, range);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, detectionBox);
        if (entities.isEmpty()) return;

        int smokeRange = calculateSmokeRange(level, pos, facing, range);
        dealSteamDamage(level, pos, facing, range, smokeRange);
    }

    private AABB buildDetectionAABB(BlockPos pos, Direction facing, int range) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
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

        if (facing.getStepX() > 0) minX += 1.0;
        else if (facing.getStepX() < 0) maxX -= 1.0;
        if (facing.getStepY() > 0) minY += 1.0;
        else if (facing.getStepY() < 0) maxY -= 1.0;
        if (facing.getStepZ() > 0) minZ += 1.0;
        else if (facing.getStepZ() < 0) maxZ -= 1.0;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private int calculateSmokeRange(Level level, BlockPos pos, Direction facing, int maxRange) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        VoxelShape smokeColumn = rotateSmokeShape(facing);

        for (int i = 0; i < maxRange; i++) {
            cursor.move(facing);
            BlockState state = level.getBlockState(cursor);

            if (state.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) continue;

            VoxelShape collision = Shapes.join(
                    state.getCollisionShape(level, cursor),
                    smokeColumn,
                    BooleanOp.AND
            );

            if (!collision.isEmpty()) {
                return i;
            }
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
            if (damage > 0) {
                entity.hurt(level.damageSources().inFire(), damage);
            }
        }
    }

    private void pushEntities(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange) {
        double scale = getSteamFlowScale();
        if (scale <= 0.0 || signal <= 0 || smokeRange <= 0) return;

        AABB steamBox = buildSteamAABB(pos, facing, smokeRange);
        List<Entity> entities = level.getEntities((Entity) null, steamBox, e -> !e.isSpectator());

        Vec3 baseFlow = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        for (Entity entity : entities) {
            int dist = distanceAlongFacing(pos, entity.blockPosition(), facing);
            if (dist < 0 || dist >= smokeRange) continue;

            double falloff = 1.0 - (double) dist / smokeRange;
            Vec3 flow = baseFlow.scale(scale * falloff);

            Vec3 current = entity.getDeltaMovement();
            double minThreshold = 0.003;
            if (Math.abs(current.x) < minThreshold && Math.abs(current.z) < minThreshold
                    && flow.length() < minThreshold * 1.5) {
                flow = baseFlow.normalize().scale(minThreshold * 1.5);
            }

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
        double projection = ex * facing.getStepX() + ey * facing.getStepY() + ez * facing.getStepZ();
        return projection - 1.0;
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