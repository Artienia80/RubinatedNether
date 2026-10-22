package corundum.rubinated_nether.content.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The Ritual Offering shares the rubinated look of runes: rubinated rarity (Rubin text) plus the rubinated glint.
 */
public class RitualOfferingItem extends Item {
	public RitualOfferingItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
