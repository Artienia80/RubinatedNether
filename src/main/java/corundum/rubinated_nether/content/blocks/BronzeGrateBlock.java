package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static net.minecraft.world.level.block.FallingBlock.isFree;

public class BronzeGrateBlock extends TarnishingBronzeBlock {
    private static final int MAX_REDSTONE_PROPAGATION = 256;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int CASCADE_DELAY = 5; // Base delay for cascade

    public BronzeGrateBlock(TarnishState tarnishState, BlockBehaviour.Properties properties) {
        super(tarnishState, properties);
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
        if (entity instanceof ItemEntity) return;

        if (hasImmediateRedstoneSignal(level, pos)) return;

        TarnishState tarnishState = getAgeFromBlock(state);

        // Handle all tarnish states, not just crystallized
        int delay;
        if (tarnishState == TarnishState.CRYSTALLIZED) {
            delay = 5; // Fast fall for crystallized (cascade behavior)
        } else {
            delay = getDelayForTarnishState(tarnishState); // Normal delays for other states
        }

        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, delay);
        }
    }

    // Add this method back from the original code
    private int getDelayForTarnishState(TarnishState state) {
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
            boolean isCrystallized = getAgeFromBlock(state) == TarnishState.CRYSTALLIZED;

            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
            this.falling(fallingBlock);

            // Only trigger immediate neighbors when this block falls
            if (isCrystallized) {
                triggerImmediateNeighbors(level, pos);
            }
        }
    }

    private void triggerImmediateNeighbors(ServerLevel level, BlockPos fallenPos) {
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos neighborPos = fallenPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (!(neighborState.getBlock() instanceof BronzeGrateBlock)) continue;

            BronzeGrateBlock neighborGrate = (BronzeGrateBlock) neighborState.getBlock();

            // Check if neighbor is crystallized and should fall
            if (neighborGrate.getAgeFromBlock(neighborState) != TarnishState.CRYSTALLIZED) continue;
            if (neighborGrate.hasImmediateRedstoneSignal(level, neighborPos)) continue;
            if (level.getBlockTicks().hasScheduledTick(neighborPos, neighborGrate)) continue;

            // Only schedule this immediate neighbor
            level.scheduleTick(neighborPos, neighborGrate, CASCADE_DELAY);
        }
    }

    private boolean hasImmediateRedstoneSignal(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    protected void falling(FallingBlockEntity entity) {

    }

    private TarnishState getAgeFromBlock(BlockState state) {
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
        return adjacentState.is(this) ? true : super.skipRendering(state, adjacentState, direction);
    }
}