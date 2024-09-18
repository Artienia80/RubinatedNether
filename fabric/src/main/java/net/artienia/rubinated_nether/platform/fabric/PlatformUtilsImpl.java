package net.artienia.rubinated_nether.platform.fabric;

import net.minecraft.world.item.ItemStack;

public class PlatformUtilsImpl {
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.getItem().hasCraftingRemainingItem();
    }

    public static ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getItem().getRecipeRemainder(stack);
    }
}
