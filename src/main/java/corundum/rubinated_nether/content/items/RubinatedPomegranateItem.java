package corundum.rubinated_nether.content.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RubinatedPomegranateItem extends Item {
	public RubinatedPomegranateItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
