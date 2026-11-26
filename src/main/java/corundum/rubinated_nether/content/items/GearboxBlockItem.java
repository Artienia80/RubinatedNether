package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.utils.BoxVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class GearboxBlockItem extends BlockItem {
	public GearboxBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		return BoxVariant.name(stack, super.getName(stack));
	}
}
