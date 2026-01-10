package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystallizedBronzeCrystalBlock extends AmethystClusterBlock {
    private static final int GROWTH_CHANCE = 5;

    // 5 pixels wide, 6 pixels tall hitbox
    protected static final VoxelShape NORTH_AABB = box(5.5, 5.5, 10.0, 10.5, 11.5, 16.0);
    protected static final VoxelShape SOUTH_AABB = box(5.5, 5.5, 0.0, 10.5, 11.5, 6.0);
    protected static final VoxelShape EAST_AABB = box(0.0, 5.5, 5.5, 6.0, 11.5, 10.5);
    protected static final VoxelShape WEST_AABB = box(10.0, 5.5, 5.5, 16.0, 11.5, 10.5);
    protected static final VoxelShape UP_AABB = box(5.5, 0.0, 5.5, 10.5, 6.0, 10.5);
    protected static final VoxelShape DOWN_AABB = box(5.5, 10.0, 5.5, 10.5, 16.0, 10.5);

    public CrystallizedBronzeCrystalBlock(Properties properties) {
        super(5.0F, 3.0F, properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return switch (direction) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            case DOWN -> DOWN_AABB;
            case UP -> UP_AABB;
        };
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROWTH_CHANCE) == 0) {
            BlockState clusterState = RNBlocks.CRYSTALLIZED_BRONZE_CLUSTER.get()
                    .defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));

            level.setBlock(pos, clusterState, 3);
        }
    }
}