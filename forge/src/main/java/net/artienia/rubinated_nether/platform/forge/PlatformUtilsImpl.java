package net.artienia.rubinated_nether.platform.forge;

import net.minecraft.world.item.ItemStack;

public class PlatformUtilsImpl {
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.hasCraftingRemainingItem();
    }

    public static ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getCraftingRemainingItem();
    }
}
