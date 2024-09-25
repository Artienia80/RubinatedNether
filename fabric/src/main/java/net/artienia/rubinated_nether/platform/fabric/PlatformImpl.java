package net.artienia.rubinated_nether.platform.fabric;

import dev.emi.trinkets.api.TrinketsApi;
import net.artienia.rubinated_nether.item.ModItems;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class PlatformImpl {
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.getItem().hasCraftingRemainingItem();
    }

    public static ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getItem().getRecipeRemainder(stack);
    }

    public static boolean rubyLensEquipped(Player player) {
        return FabricLoader.getInstance().isModLoaded("trinkets") ? rubyLensEquippedTrinkets(player) : player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get());
    }

    private static boolean rubyLensEquippedTrinkets(Player player) {
        Item rubyLens = ModItems.RUBY_LENS.get();
        return TrinketsApi.getTrinketComponent(player)
            .map(component -> component.isEquipped(rubyLens))
            .orElse(false)
            || player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get());
    }

    public static TagKey<Block> getGlassTag() {
        return ConventionalBlockTags.GLASS_BLOCKS;
    }
}
