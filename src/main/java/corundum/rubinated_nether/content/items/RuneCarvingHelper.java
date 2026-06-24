package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

public final class RuneCarvingHelper {

    private RuneCarvingHelper() {}

    public static boolean isCarved(ItemStack stack) {
        return getCarving(stack) != Rubination.EMPTY;
    }

    public static Rubination getCarving(ItemStack stack) {
        Rubination carving = stack.get(RNDataComponents.RUNE_CARVING.get());
        return carving != null ? carving : Rubination.EMPTY;
    }

    private static int getCmdForRubination(Rubination rubination) {
        return switch (Rubination.parseRubinationTextureName(rubination)) {
            case "weapon"   -> 2;
            case "armor"    -> 3;
            case "bow"      -> 4;
            case "crossbow" -> 5;
            case "trident"  -> 6;
            case "mace"     -> 7;
            default         -> 1; // "tool"
        };
    }

    public static ItemStack withCarving(ItemStack runeStack, Rubination rubination) {
        ItemStack result = runeStack.copy();
        if (rubination == Rubination.EMPTY) {
            result.remove(RNDataComponents.RUNE_CARVING.get());
            result.remove(DataComponents.CUSTOM_MODEL_DATA);
        } else {
            result.set(RNDataComponents.RUNE_CARVING.get(), rubination);
            result.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getCmdForRubination(rubination)));
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
}