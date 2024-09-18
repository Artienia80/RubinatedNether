package net.artienia.rubinated_nether.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.recipe.FreezingRecipe;
import net.artienia.rubinated_nether.recipe.ModRecipeTypes;
import net.artienia.rubinated_nether.screen.FreezerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIModPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(RubinatedNether.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FreezingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ModFuelCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(ModFuelCategory.RECIPE_TYPE, ModFuelRecipeMaker.getFuelRecipes());

        List<FreezingRecipe> freezingRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.FREEZING.get());
        registration.addRecipes(FreezingCategory.FREEZING_RECIPE_TYPE, freezingRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FreezerScreen.class, 60, 30, 20, 30,
                FreezingCategory.FREEZING_RECIPE_TYPE);
    }
}
