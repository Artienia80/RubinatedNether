package corundum.rubinated_nether.content.recipe.JEI;

import org.jetbrains.annotations.Nullable;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.recipe.FreezingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FreezerRecipeCategory implements IRecipeCategory<FreezingRecipe> {
	public static final RecipeType<FreezingRecipe> RECIPE_TYPE = RecipeType.create(
		RubinatedNether.MODID, 
		"freezing", 
		FreezingRecipe.class
	);

	protected final IGuiHelper helper;

	public FreezerRecipeCategory(IGuiHelper helper) {
		this.helper = helper;
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return helper.createDrawableIngredient(
			VanillaTypes.ITEM_STACK, 
			new ItemStack(RNBlocks.FREEZER.get())
		);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("gui.rubinated_nether.jei.freezer");
	}

	@Override
	public RecipeType<FreezingRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FreezingRecipe recipe, IFocusGroup focuses) {

	}
}
