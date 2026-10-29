package corundum.rubinated_nether.content.recipe.JEI;

import net.minecraft.world.item.ItemStack;

/** JEI-only view of a grindstone rule: {@code stoneCount} of {@code stone} (one slot or both) gives {@code output}. */
public record RuneGrindstoneRecipe(ItemStack stone, int stoneCount, ItemStack output) {
}
