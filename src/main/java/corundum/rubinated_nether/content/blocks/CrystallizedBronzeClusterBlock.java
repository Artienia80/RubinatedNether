package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CrystallizedBronzeClusterBlock extends AmethystClusterBlock {
    private static final int SPREAD_CHANCE = 10;

    // 6 pixels wide, 7 pixels tall hitbox
    private static final VoxelShape NORTH_AABB = Block.box(5.0, 5.0, 9.0, 11.0, 12.0, 16.0);
    private static final VoxelShape SOUTH_AABB = Block.box(5.0, 5.0, 0.0, 11.0, 12.0, 7.0);
    private static final VoxelShape EAST_AABB = Block.box(0.0, 5.0, 5.0, 7.0, 12.0, 11.0);
    private static final VoxelShape WEST_AABB = Block.box(9.0, 5.0, 5.0, 16.0, 12.0, 11.0);
    private static final VoxelShape UP_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0);
    private static final VoxelShape DOWN_AABB = Block.box(5.0, 9.0, 5.0, 11.0, 16.0, 11.0);

    public CrystallizedBronzeClusterBlock(Properties properties) {
        super(5.0F, 3.0F, properties);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
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
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(SPREAD_CHANCE) == 0) {
            trySpreadToAdjacentBlocks(level, pos, random);
        }
    }

    private void trySpreadToAdjacentBlocks(ServerLevel level, BlockPos crystalPos, RandomSource random) {
        Direction facing = level.getBlockState(crystalPos).getValue(FACING);

        for (Direction direction : Direction.values()) {
            if (direction == facing || direction == facing.getOpposite()) {
                continue;
            }

            BlockPos adjacentPos = crystalPos.relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);

            if (adjacentState.is(RNTags.Blocks.CRYSTALLIZED_BLOCKS) &&
                    adjacentState.isCollisionShapeFullBlock(level, adjacentPos)) {

                BlockPos newBlightPos = adjacentPos.relative(direction);

                if (canPlaceBlight(level, newBlightPos)) {
                    BlockState newBlightState = RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get()
                            .stateFacing(direction);

                    if (newBlightState.canSurvive(level, newBlightPos)) {
                        level.setBlock(newBlightPos, newBlightState, 3);
                        break;
                    }
                }
            }
        }
    }

    private boolean canPlaceBlight(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int count = 1 + builder.getLevel().random.nextInt(2);
        return List.of(new ItemStack(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get(), count));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get());
    }
}