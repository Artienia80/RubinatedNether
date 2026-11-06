package corundum.rubinated_nether.content.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class VaseBlockItem extends BlockItem {
	public VaseBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
}
