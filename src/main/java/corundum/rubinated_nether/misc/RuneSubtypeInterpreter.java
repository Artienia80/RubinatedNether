package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.content.RNDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Carved runes are the plain rune item plus a carving component. Without this JEI sees one single "rune"
 * and treats the carved ones as duplicates; with it every carving is its own entry.
 */
public class RuneSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final RuneSubtypeInterpreter INSTANCE = new RuneSubtypeInterpreter();

    private RuneSubtypeInterpreter() {
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack stack, UidContext context) {
        // Rubination is an enum, so identity comparison is fine; null means the blank rune
        return stack.get(RNDataComponents.RUNE_CARVING.get());
    }

    @SuppressWarnings("removal")
    @Override
    public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
        var carving = stack.get(RNDataComponents.RUNE_CARVING.get());
        return carving == null ? "" : carving.getSerializedName();
    }
}
