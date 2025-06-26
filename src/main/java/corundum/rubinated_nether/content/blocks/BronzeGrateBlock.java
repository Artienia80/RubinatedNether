package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;

import static net.minecraft.world.level.block.FallingBlock.isFree;

public class BronzeGrateBlock extends TarnishingBronzeBlock {
    private static final int MAX_PROPAGATION = 200;

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
        if (isConnectedToRedstone(level, pos) || entity instanceof ItemEntity) return;

        int delay = getDelayForTarnishState(getAgeFromBlock(state));
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, delay);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isConnectedToRedstone(level, pos)) return;

        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
            this.falling(fallingBlock);
        }
    }

    protected void falling(FallingBlockEntity entity) {

    }

    private int getDelayForTarnishState(TarnishState state) {
        return switch (state) {
            case CRYSTALLIZED -> 5; // 0.25 second
            case UNAFFECTED   -> 20; // 1 second
            case DISCOLORED   -> 40; // 2 seconds
            case CORRODED     -> 60; // 3 seconds
            case TARNISHED    -> 80; // 4 seconds
        };
    }

    private TarnishState getAgeFromBlock(BlockState state) {
        return ((TarnishingBronzeBlock) state.getBlock()).getAge();
    }

    private boolean isConnectedToRedstone(Level level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        toCheck.add(origin);

        while (!toCheck.isEmpty() && visited.size() < MAX_PROPAGATION) {
            BlockPos current = toCheck.poll();
            visited.add(current);

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

    // Add these methods to your BronzeGrateBlock class

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0; // Allows full light through
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true; // Allows skylight to pass through
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F; // No shadow/shade reduction
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true; // Use the block's shape for light occlusion calculations
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty(); // No visual obstruction for rendering
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        // Don't skip rendering when adjacent to another grate of the same type
        return adjacentState.is(this) ? true : super.skipRendering(state, adjacentState, direction);
    }
}
