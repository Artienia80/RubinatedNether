package net.artienia.rubinated_nether.platform.forge;

import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

public class PlatformImpl {
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.hasCraftingRemainingItem();
    }

    public static ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getCraftingRemainingItem();
    }

    public static boolean rubyLensEquipped(Player player) {
        return ModList.get().isLoaded("curios") ? rubyLensEquippedCurios(player) : player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get());
    }

    private static boolean rubyLensEquippedCurios(Player player) {
        Item rubyLens = ModItems.RUBY_LENS.get();
        return CuriosApi.getCuriosInventory(player)
            .map(inventory -> inventory.findFirstCurio(rubyLens).isPresent())
            .orElse(false)
        || player.getItemBySlot(EquipmentSlot.HEAD).is(rubyLens);
    }

    public static TagKey<Block> getGlassTag() {
        return Tags.Blocks.GLASS;
    }
}
