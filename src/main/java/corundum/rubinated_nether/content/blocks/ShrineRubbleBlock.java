package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.utils.FallingWaterCarry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;

public class ShrineRubbleBlock extends Block implements QuasiGravityBlock, Fallable {
    public ShrineRubbleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        QuasiGravity.schedule(level, pos, this);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        QuasiGravity.schedule(level, pos, this);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        QuasiGravity.settle(level, pos);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
        FallingWaterCarry.dropOffWater(level, pos, fallingBlock);
    }
}
