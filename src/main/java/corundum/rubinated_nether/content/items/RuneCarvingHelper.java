package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RuneCarvingHelper {

    private RuneCarvingHelper() {}

    public static boolean isCarved(ItemStack stack) {
        return getCarving(stack) != Rubination.EMPTY;
    }

    public static Rubination getCarving(ItemStack stack) {
        Rubination carving = stack.get(RNDataComponents.RUNE_CARVING.get());
        return carving != null ? carving : Rubination.EMPTY;
    }

    public static ItemStack withCarving(ItemStack runeStack, Rubination rubination) {
        ItemStack result = runeStack.copy();
        if (rubination == Rubination.EMPTY) {
            result.remove(RNDataComponents.RUNE_CARVING.get());
        } else {
            result.set(RNDataComponents.RUNE_CARVING.get(), rubination);
        }
        return result;
    }

    public static Component carvedDisplayName(ItemStack stack) {
        Rubination carving = getCarving(stack);
        if (carving == Rubination.EMPTY) {
            return null;
        }
        return Component.translatable("item.rubinated_nether.rune.carved", carving.getCapitalisedName());
    }

    public static void appendCarvingTooltip(ItemStack stack, List<Component> tooltipComponents) {
        Rubination carving = getCarving(stack);
        if (carving == Rubination.EMPTY) {
            return;
        }
        tooltipComponents.add(
                Component.translatable("item.rubinated_nether.rune.carving_of", carving.getCapitalisedName())
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}