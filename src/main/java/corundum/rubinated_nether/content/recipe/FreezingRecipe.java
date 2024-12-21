package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.content.RNRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FreezingRecipe extends AbstractCookingRecipe {
    private final RNBookCategory category;

    public FreezingRecipe(String group, RNBookCategory category, Ingredient ingredient, ItemStack result, float experience, int freezingTime) {
        super(RNRecipes.FREEZING.get(), group, CookingBookCategory.MISC, ingredient, result, experience, freezingTime);
        this.category = category;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return super.getIngredients();
    }

    public ItemStack getResult() {
        return this.result;
    }

    public RNBookCategory modCategory() {
        return this.category;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNRecipeSerializers.FREEZING.get();
    }

    public static class Serializer extends FreezerCookingSerializer<FreezingRecipe> {
        public Serializer() {
            super(FreezingRecipe::new, 800);
        }
    }
}