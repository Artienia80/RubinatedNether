package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNBlockStateProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class FlippableBlockItem extends BlockItem {
	public FlippableBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected BlockState getPlacementState(BlockPlaceContext context) {
		BlockState state = super.getPlacementState(context);
		if (state == null || !state.hasProperty(RNBlockStateProperties.FLIPPED)) {
			return state;
		}
		if (state.hasProperty(SlabBlock.TYPE) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
			return state;
		}
		return state.setValue(RNBlockStateProperties.FLIPPED, context.getHand() == InteractionHand.OFF_HAND);
	}
}
