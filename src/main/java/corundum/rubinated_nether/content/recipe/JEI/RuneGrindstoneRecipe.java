package corundum.rubinated_nether.content.recipe.JEI;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/** JEI-only view of the grindstone rule: any of {@code stones} in BOTH slots gives {@code output}. */
public record RuneGrindstoneRecipe(List<ItemStack> stones, ItemStack output) {
}
