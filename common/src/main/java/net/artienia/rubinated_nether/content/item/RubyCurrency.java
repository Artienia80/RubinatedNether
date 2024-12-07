package net.artienia.rubinated_nether.content.item;

import net.artienia.rubinated_nether.utils.PiglinCurrency;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RubyCurrency extends Item implements PiglinCurrency {
	public RubyCurrency(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isPiglinCurrency(ItemStack stack) {
		return true;
	}
}
