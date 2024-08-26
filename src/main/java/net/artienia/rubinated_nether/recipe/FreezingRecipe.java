package net.artienia.rubinated_nether.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class FreezingRecipe extends AbstractCookingRecipe {
    private final ModBookCategory category;

    public FreezingRecipe(ResourceLocation id, String group, ModBookCategory category, Ingredient ingredient, ItemStack result, float experience, int freezingTime) {
        super(ModRecipeTypes.FREEZING.get(), id, group, CookingBookCategory.MISC, ingredient, result, experience, freezingTime);
        this.category = category;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public ModBookCategory modCategory() {
        return this.category;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FREEZING.get();
    }

    public static class Serializer extends ModCookingSerializer<FreezingRecipe> {
        public Serializer() {
            super(FreezingRecipe::new, 800);
        }
    }
}
