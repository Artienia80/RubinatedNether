package net.artienia.rubinated_nether.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

import java.util.Iterator;

public class BlackIceBlock extends Block {

    // Block required for support
    private static final Block SUPPORT_BLOCK = Blocks.STONE; // Replace with your desired support block.

    public BlackIceBlock(Properties properties) {
        super(properties);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.below());
        if (blockState.is(Blocks.BLUE_ICE)) {
            BlockPos blockPos = pos.below();
            Iterator var6 = Direction.Plane.HORIZONTAL.iterator();
            return true;
        }
            return false;
        }
    }

//    @Override
//    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
//        super.onPlace(state, level, pos, oldState, isMoving);
//        // Check support immediately when the block is placed
//        checkForSupport(state, level, pos);
//    }
//
//    @Override
//    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
//        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
//        // Check support whenever a neighboring block changes
//        checkForSupport(state, level, pos);
//    }
//
//    private void checkForSupport(BlockState state, Level level, BlockPos pos) {
//        BlockPos belowPos = pos.below(); // Position of the block below
//        BlockState belowState = level.getBlockState(belowPos); // State of the block below
//
//        // If the block below is not the required support block, break this block
//        if (!belowState.is(SUPPORT_BLOCK)) {
//            level.destroyBlock(pos, true); // Break the block and drop its item
//        }
//    }
//}
