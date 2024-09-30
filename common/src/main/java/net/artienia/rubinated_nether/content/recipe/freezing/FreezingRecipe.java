package net.artienia.rubinated_nether.content.recipe.freezing;

import net.artienia.rubinated_nether.content.RNRecipes;
import net.artienia.rubinated_nether.content.recipe.ModBookCategory;
import net.artienia.rubinated_nether.content.recipe.ModCookingSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FreezingRecipe extends AbstractCookingRecipe {
    private final ModBookCategory category;

    public FreezingRecipe(ResourceLocation id, String group, ModBookCategory category, Ingredient ingredient, ItemStack result, float experience, int freezingTime) {
        super(RNRecipes.FREEZING.get(), id, group, CookingBookCategory.MISC, ingredient, result, experience, freezingTime);
        this.category = category;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return super.getIngredients();
    }

    public ItemStack getResult() {
        return this.result;
    }

    public ModBookCategory modCategory() {
        return this.category;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNRecipes.FREEZING_SERIALIZER.get();
    }

    public static class Serializer extends ModCookingSerializer<FreezingRecipe> {
        public Serializer() {
            super(FreezingRecipe::new, 800);
        }
    }
}
