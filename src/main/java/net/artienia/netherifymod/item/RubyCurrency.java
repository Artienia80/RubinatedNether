package net.artienia.netherifymod.item;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
public class RubyCurrency extends Item {

    public RubyCurrency(Item.Properties properties) {
        super(properties);
    }
    @Override
    public boolean isPiglinCurrency(ItemStack stack) {
        return true;
    }


}
