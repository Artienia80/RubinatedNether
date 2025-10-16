package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.recipe.JEI.FreezerRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Objects;

@JeiPlugin
public class RNJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		RubinatedNether.LOGGER.info("Rubinating your JEI");
		registration.addRecipeCategories(new FreezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		// woman next
		var man = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

		registration.addRecipes(
			FreezerRecipeCategory.RECIPE_TYPE, 
			man.getAllRecipesFor(RNRecipes.FREEZING.get()).stream().map(RecipeHolder::value).toList()
		);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(
			new ItemStack(RNBlocks.FREEZER.get()), 
			FreezerRecipeCategory.RECIPE_TYPE
		);
	}
}
