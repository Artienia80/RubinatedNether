package net.artienia.rubinated_nether.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class PlatformUtils {

    // Forge and fabric do this differently so we need to make a platform class
    @ExpectPlatform
    public static boolean hasCraftingRemainder(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ItemStack getCraftingRemainder(ItemStack stack) {
        throw new AssertionError();
    }

}
