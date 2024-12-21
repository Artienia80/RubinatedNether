package corundum.rubinated_nether.content.recipe;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.Iterator;

public class FreezerServerPlaceRecipe extends ServerPlaceRecipe<SingleRecipeInput, FreezingRecipe> {

    public FreezerServerPlaceRecipe(RecipeBookMenu<SingleRecipeInput, FreezingRecipe> recipeBookMenu) {
        super(recipeBookMenu);
    }

    @Override
    public void placeRecipe(int width, int height, int result, RecipeHolder<?> p_301225_, Iterator<Integer> iterator, int idk) {
        int k1 = 0;

        if (k1 == result) {
            k1++;
        }

        for (int slot = 0; slot < 3; slot++) {
            if (!iterator.hasNext()) {
                return;
            }

            this.addItemToSlot(iterator.next(), k1, idk, slot, 0);

            k1++;
        }
    }
}
