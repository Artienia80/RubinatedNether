package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.util.StringRepresentable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
import java.util.Optional;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static net.minecraft.world.level.block.FallingBlock.isFree;

public class BronzeGrateBlock extends TarnishingBronzeBlock implements LiquidBlockContainer, BucketPickup, Fallable {
    public enum StoredFluid implements StringRepresentable {
        NONE("none"),
        WATER("water"),
        LAVA("lava");

        private final String name;

        StoredFluid(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        FlowingFluid still() {
            return this == LAVA ? Fluids.LAVA : Fluids.WATER;
        }

        FlowingFluid flowing() {
            return this == LAVA ? Fluids.FLOWING_LAVA : Fluids.FLOWING_WATER;
        }

        static StoredFluid of(Fluid fluid) {
            if (Fluids.WATER.isSame(fluid)) return WATER;
            if (Fluids.LAVA.isSame(fluid)) return LAVA;
            return NONE;
        }
    }

    public static final EnumProperty<StoredFluid> FLUID = EnumProperty.create("fluid", StoredFluid.class);
    public static final IntegerProperty FLUID_LEVEL = IntegerProperty.create("fluid_level", 0, 8);
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    private static final int MAX_COLUMN_HEIGHT = 256;
    private static final int MAX_REDSTONE_PROPAGATION = 256;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int CASCADE_DELAY = 5; // Base delay for cascade

    public BronzeGrateBlock(TarnishStage tarnishStage, Properties properties) {
        super(tarnishStage, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FLUID, StoredFluid.NONE).setValue(FLUID_LEVEL, 0).setValue(LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLUID, FLUID_LEVEL, LOCKED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return withFluid(this.defaultBlockState(), fluid);
    }

    public static BlockState withFluid(BlockState state, FluidState fluid) {
        StoredFluid kind = fluid.isEmpty() ? StoredFluid.NONE : StoredFluid.of(fluid.getType());
        if (kind == StoredFluid.NONE) {
            return state.setValue(FLUID, StoredFluid.NONE).setValue(FLUID_LEVEL, 0);
        }
        int level = fluid.isSource() ? 0 : fluid.getValue(FlowingFluid.FALLING) ? 8 : 8 - fluid.getAmount();
        return state.setValue(FLUID, kind).setValue(FLUID_LEVEL, level);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        StoredFluid kind = state.getValue(FLUID);
        if (kind == StoredFluid.NONE) return super.getFluidState(state);

        int level = state.getValue(FLUID_LEVEL);
        if (level == 0) return kind.still().getSource(false);
        return level >= 8 ? kind.flowing().getFlowing(8, true) : kind.flowing().getFlowing(8 - level, false);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        FluidState stored = state.getFluidState();
        if (!stored.isEmpty())
            GrateFluidScheduler.schedule(level, pos, stored.getType().getTickDelay(level));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return (!state.getValue(WAXED) && TarnishingBronze.canCrystallize(state.getBlock()))
                || (state.getValue(FLUID) != StoredFluid.NONE && !state.getValue(LOCKED));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LOCKED) && state.getValue(FLUID) != StoredFluid.NONE) {
            GrateFluidScheduler.schedule(level, pos, state.getFluidState().getType().getTickDelay(level));
        }
        if (state.getValue(WAXED) || !TarnishingBronze.canCrystallize(state.getBlock())) return;

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
            if (state.getValue(FLUID) != StoredFluid.NONE) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
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
        FluidState stored = state.getFluidState();
        if (!stored.isEmpty()) {
            GrateFluidScheduler.schedule(level, pos, stored.getType().getTickDelay(level));
        }
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
        if (!state.getValue(LOCKED) && context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity == null || entity instanceof FallingBlockEntity) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getCollisionShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (!state.getValue(LOCKED)) return Shapes.empty();
        return super.getOcclusionShape(state, level, pos);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(LOCKED)
                && state.getValue(FLUID) == StoredFluid.NONE
                && StoredFluid.of(fluid) != StoredFluid.NONE;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!canPlaceLiquid(null, level, pos, state, fluidState.getType())) return false;
        level.setBlock(pos, withFluid(state, fluidState), Block.UPDATE_ALL);
        GrateFluidScheduler.schedule(level, pos, fluidState.getType().getTickDelay(level));
        return true;
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue(LOCKED) || state.getValue(FLUID) == StoredFluid.NONE || state.getValue(FLUID_LEVEL) != 0) {
            return ItemStack.EMPTY;
        }
        Fluid fluid = state.getFluidState().getType();
        level.setBlock(pos, withFluid(state, Fluids.EMPTY.defaultFluidState()), Block.UPDATE_ALL);
        return new ItemStack(fluid.getBucket());
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
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
            level.setBlock(member, memberState.setValue(LOCKED, powered), Block.UPDATE_ALL);
            if (!powered) {
                FluidState stored = memberState.getFluidState();
                if (!stored.isEmpty()) {
                    GrateFluidScheduler.schedule(level, member, stored.getType().getTickDelay(level));
                }
                passThrough(level, member);
            }
        }
    }

    static void updateFluid(ServerLevel level, BlockPos pos) {
        BlockState block = level.getBlockState(pos);
        if (!(block.getBlock() instanceof BronzeGrateBlock) || block.getValue(LOCKED)) return;

        FluidState state = block.getFluidState();
        if (state.isEmpty() || !(state.getType() instanceof FlowingFluid fluid)) return;

        if (!state.isSource()) {
            FluidState next = FlowingFluidAccess.getNewLiquid(fluid, level, pos, block);
            int delay = FlowingFluidAccess.getSpreadDelay(fluid, level, pos, state, next);
            if (next.isEmpty()) {
                state = next;
                level.setBlock(pos, withFluid(block, next), Block.UPDATE_ALL);
            } else if (!next.equals(state)) {
                state = next;
                BlockState updated = withFluid(block, next);
                level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
                GrateFluidScheduler.schedule(level, pos, delay);
                level.updateNeighborsAt(pos, updated.getBlock());
            }
        }

        FlowingFluidAccess.spread(fluid, level, pos, state);
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