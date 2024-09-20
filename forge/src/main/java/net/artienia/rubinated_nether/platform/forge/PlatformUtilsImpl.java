package net.artienia.rubinated_nether.platform.forge;

import dev.architectury.platform.Platform;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class PlatformUtilsImpl {
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.hasCraftingRemainingItem();
    }

    public static ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getCraftingRemainingItem();
    }

    public static boolean rubyLensEquipped(Player player) {
        return Platform.isModLoaded("curios") ? rubyLensEquippedCurios(player) : player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get());
    }

    private static boolean rubyLensEquippedCurios(Player player) {
        Item rubyLens = ModItems.RUBY_LENS.get();
        return CuriosApi.getCuriosInventory(player)
            .map(inventory -> inventory.findFirstCurio(rubyLens).isPresent())
            .orElse(false)
        || player.getItemBySlot(EquipmentSlot.HEAD).is(rubyLens);
    }
}
