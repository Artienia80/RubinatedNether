package corundum.rubinated_nether.content.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class StrongboxBlockItem extends BlockItem {
	public StrongboxBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
}
