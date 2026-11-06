package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.Block;
import corundum.rubinated_nether.utils.FallingWaterCarry;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static net.minecraft.world.level.block.FallingBlock.isFree;

public class BronzeGrateBlock extends TarnishingBronzeBlock implements SimpleWaterloggedBlock, Fallable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    private static final int MAX_COLUMN_HEIGHT = 256;
    private static final int MAX_REDSTONE_PROPAGATION = 256;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int CASCADE_DELAY = 5; // Base delay for cascade

    public BronzeGrateBlock(TarnishStage tarnishStage, Properties properties) {
        super(tarnishStage, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LOCKED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
        FallingWaterCarry.dropOffWater(level, pos, fallingBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(WAXED) && TarnishingBronze.canCrystallize(state.getBlock());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) return;

        boolean hasCatalystNearby = BlockPos.betweenClosedStream(
                pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
        ).anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

        if (hasCatalystNearby) {
            this.getCrystallized(state).ifPresent(newState -> level.setBlockAndUpdate(pos, newState));
        } else {
            this.changeOverTime(state, level, pos, random);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (entity instanceof ItemEntity || entity instanceof FallingBlockEntity) return;

        // Don't trigger falling if a BronzeEntity is stepping on it
        if (entity instanceof BronzeEntity) return;

        if (hasImmediateRedstoneSignal(level, pos)) return;

        TarnishStage tarnishStage = getAgeFromBlock(state);

        // Handle all tarnish states, not just crystallized
        int delay;
        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            delay = 5; // Fast fall for crystallized (cascade behavior)
        } else {
            delay = getDelayForTarnishState(tarnishStage); // Normal delays for other states
        }

        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            // Only schedule and grant advancement if the grate will actually fall
            // Check if there's a block below or if it's already supported
            if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
                level.scheduleTick(pos, this, delay);

                // Grant advancement to player after the delay
                if (entity instanceof ServerPlayer serverPlayer) {
                    scheduleAdvancementGrant(serverPlayer, (ServerLevel) level, delay);
                }
            }
        }
    }

    private void scheduleAdvancementGrant(ServerPlayer player, ServerLevel level, int delay) {
        level.getServer().tell(new TickTask(
                level.getServer().getTickCount() + delay,
                () -> {
                    var advancementHolder = player.server.getAdvancements()
                            .get(RubinatedNether.id("pitfalls"));

                    if (advancementHolder != null) {
                        var progress = player.getAdvancements().getOrStartProgress(advancementHolder);
                        if (!progress.isDone()) {
                            for (String criterion : progress.getRemainingCriteria()) {
                                player.getAdvancements().award(advancementHolder, criterion);
                            }
                        }
                    }
                }
        ));
    }

    // Add this method back from the original code
    private int getDelayForTarnishState(TarnishStage state) {
        return switch (state) {
            case CRYSTALLIZED -> 5; // 0.25 second
            case UNAFFECTED   -> 20; // 1 second
            case DISCOLORED   -> 40; // 2 seconds
            case CORRODED     -> 60; // 3 seconds
            case TARNISHED    -> 80; // 4 seconds
        };
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isConnectedToRedstone(level, pos)) return;

        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            boolean isCrystallized = getAgeFromBlock(state) == TarnishStage.CRYSTALLIZED;

            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
            FallingWaterCarry.pickUpWater(level, pos, state, fallingBlock);
            this.falling(fallingBlock);

            // Only trigger immediate neighbors when this block falls
            if (isCrystallized) {
                triggerImmediateNeighbors(level, pos);
            }
        }
    }

    private void triggerImmediateNeighbors(ServerLevel level, BlockPos fallenPos) {
        // Check horizontal neighbors
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos neighborPos = fallenPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (!(neighborState.getBlock() instanceof BronzeGrateBlock)) continue;

            BronzeGrateBlock neighborGrate = (BronzeGrateBlock) neighborState.getBlock();

            // Check if neighbor is crystallized and should fall
            if (neighborGrate.getAgeFromBlock(neighborState) != TarnishStage.CRYSTALLIZED) continue;
            if (neighborGrate.hasImmediateRedstoneSignal(level, neighborPos)) continue;
            if (level.getBlockTicks().hasScheduledTick(neighborPos, neighborGrate)) continue;

            // Only schedule this immediate neighbor
            level.scheduleTick(neighborPos, neighborGrate, CASCADE_DELAY);
        }

        // Check block above
        BlockPos abovePos = fallenPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (aboveState.getBlock() instanceof BronzeGrateBlock) {
            BronzeGrateBlock aboveGrate = (BronzeGrateBlock) aboveState.getBlock();

            // Check if the grate above can fall (any tarnish state)
            if (!aboveGrate.hasImmediateRedstoneSignal(level, abovePos) &&
                    !level.getBlockTicks().hasScheduledTick(abovePos, aboveGrate)) {

                TarnishStage aboveTarnish = aboveGrate.getAgeFromBlock(aboveState);
                int delay = aboveTarnish == TarnishStage.CRYSTALLIZED ? CASCADE_DELAY : getDelayForTarnishState(aboveTarnish);
                level.scheduleTick(abovePos, aboveGrate, delay);
            }
        }
    }

    private boolean hasImmediateRedstoneSignal(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    protected void falling(FallingBlockEntity entity) {

    }

    private TarnishStage getAgeFromBlock(BlockState state) {
        return ((TarnishingBronzeBlock) state.getBlock()).getAge();
    }

    private boolean isConnectedToRedstone(Level level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        toCheck.add(origin);

        while (!toCheck.isEmpty() && visited.size() < MAX_REDSTONE_PROPAGATION) {
            BlockPos current = toCheck.poll();
            if (!visited.add(current)) continue;

            if (level.hasNeighborSignal(current)) {
                return true;
            }

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!visited.contains(neighbor)) {
                    BlockState neighborState = level.getBlockState(neighbor);
                    if (neighborState.getBlock() instanceof BronzeGrateBlock) {
                        toCheck.add(neighbor);
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide || oldState.is(state.getBlock())) return;
        refreshNetwork(level, pos);
        passThrough(level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide) return;
        if (level.hasNeighborSignal(pos) || state.getValue(LOCKED)) {
            refreshNetwork(level, pos);
        }
        passThrough(level, pos);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(LOCKED) && context instanceof EntityCollisionContext entityContext
                && entityContext.getEntity() instanceof FallingBlockEntity) {
            return Shapes.empty();
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        if (player == null && !state.getValue(LOCKED)) return false;
        return SimpleWaterloggedBlock.super.canPlaceLiquid(player, level, pos, state, fluid);
    }

    private static void refreshNetwork(Level level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        List<BlockPos> members = new ArrayList<>();
        boolean powered = false;
        toCheck.add(origin);

        while (!toCheck.isEmpty() && visited.size() < MAX_REDSTONE_PROPAGATION) {
            BlockPos current = toCheck.poll();
            if (!visited.add(current)) continue;
            members.add(current);
            if (level.hasNeighborSignal(current)) powered = true;

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!visited.contains(neighbor) && level.getBlockState(neighbor).getBlock() instanceof BronzeGrateBlock) {
                    toCheck.add(neighbor);
                }
            }
        }

        for (BlockPos member : members) {
            BlockState memberState = level.getBlockState(member);
            if (memberState.getValue(LOCKED) == powered) continue;
            level.setBlock(member, memberState.setValue(LOCKED, powered), Block.UPDATE_CLIENTS);
            if (!powered) passThrough(level, member);
        }
    }

    private static boolean isOpenGrate(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof BronzeGrateBlock && !state.getValue(LOCKED);
    }

    private static void passThrough(Level level, BlockPos pos) {
        if (!isOpenGrate(level, pos)) return;

        BlockPos top = pos;
        for (int i = 0; i < MAX_COLUMN_HEIGHT && isOpenGrate(level, top.above()); i++) {
            top = top.above();
        }
        BlockPos bottom = pos;
        for (int i = 0; i < MAX_COLUMN_HEIGHT && isOpenGrate(level, bottom.below()); i++) {
            bottom = bottom.below();
        }

        BlockPos exit = bottom.below();
        if (exit.getY() < level.getMinBuildHeight()) return;

        releaseGravityBlock(level, top.above(), exit);
        relayFluid(level, top, bottom, exit);
    }

    private static void releaseGravityBlock(Level level, BlockPos abovePos, BlockPos exit) {
        BlockState above = level.getBlockState(abovePos);
        if (!(above.getBlock() instanceof FallingBlock)) return;
        if (!isFree(level.getBlockState(exit))) return;

        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, abovePos, above);
        if (above.getBlock() instanceof AnvilBlock) {
            fallingBlock.setHurtsEntities(2.0F, 40);
        }
    }

    private static void relayFluid(Level level, BlockPos top, BlockPos bottom, BlockPos exit) {
        FluidState supply = level.getFluidState(top.above());
        for (BlockPos cell = bottom; supply.isEmpty() && cell.getY() <= top.getY(); cell = cell.above()) {
            supply = level.getFluidState(cell);
        }
        if (supply.isEmpty() || !(supply.getType() instanceof FlowingFluid flowing)) return;

        BlockState exitState = level.getBlockState(exit);
        if (!exitState.getFluidState().isEmpty()) return;
        if (!exitState.isAir()) {
            if (!exitState.canBeReplaced(supply.getType())) return;
            Block.dropResources(exitState, level, exit);
        }
        level.setBlock(exit, flowing.getFlowing(8, true).createLegacyBlock(), Block.UPDATE_ALL);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.getBlock() instanceof BronzeGrateBlock || super.skipRendering(state, adjacentState, direction);
    }
}