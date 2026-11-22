package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlockStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class FlippableStairBlock extends StairBlock {
	public FlippableStairBlock(BlockState baseState, BlockBehaviour.Properties properties) {
		super(baseState, properties);
		this.registerDefaultState(this.defaultBlockState().setValue(RNBlockStateProperties.FLIPPED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(RNBlockStateProperties.FLIPPED);
	}
}
