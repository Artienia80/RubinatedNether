package net.artienia.rubinated_nether.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class Platform {

    // Forge and fabric do this differently so we need to make a platform class
    @ExpectPlatform
    public static boolean hasCraftingRemainder(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ItemStack getCraftingRemainder(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean rubyLensEquipped(Player player) {
        return false;
    }

    @ExpectPlatform
    public static TagKey<Block> getGlassTag() {
        throw new AssertionError();
    }

}
