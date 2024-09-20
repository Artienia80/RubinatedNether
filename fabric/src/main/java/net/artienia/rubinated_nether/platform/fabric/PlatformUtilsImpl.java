package net.artienia.rubinated_nether.platform.fabric;

import dev.architectury.platform.Platform;
import dev.emi.trinkets.api.TrinketsApi;
import net.artienia.rubinated_nether.item.ModItems;
import net.artienia.rubinated_nether.item.RubyLens;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlatformUtilsImpl {
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.getItem().hasCraftingRemainingItem();
    }

    public static ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getItem().getRecipeRemainder(stack);
    }

    public static boolean rubyLensEquipped(Player player) {
        return Platform.isModLoaded("trinkets") ? rubyLensEquippedTrinkets(player) : player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get());
    }

    private static boolean rubyLensEquippedTrinkets(Player player) {
        Item rubyLens = ModItems.RUBY_LENS.get();
        return TrinketsApi.getTrinketComponent(player)
            .map(component -> component.isEquipped(rubyLens))
            .orElse(false)
            || player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get());
    }
}
