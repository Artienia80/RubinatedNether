package net.artienia.rubinated_nether.integrations.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.integrations.viewers.FuelRecipe;
import net.artienia.rubinated_nether.integrations.viewers.FreezerFuelRecipeMaker;
import net.artienia.rubinated_nether.content.RNRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Function;

@EmiEntrypoint
public class EMIModPlugin implements EmiPlugin {

	public static final EmiRecipeCategory FREEZING = new EmiRecipeCategory(RubinatedNether.id("freezing"), EmiStack.of(RNBlocks.FREEZER.get()));
	public static final EmiRecipeCategory FREEZER_FUEL = new EmiRecipeCategory(RubinatedNether.id("freezer_fuel"), EMIStuff.FREEZE_ICON);

	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(FREEZING);
		registry.addCategory(FREEZER_FUEL);
		registry.addWorkstation(FREEZING, EmiStack.of(RNBlocks.FREEZER.get()));
		registry.addWorkstation(FREEZER_FUEL, EmiStack.of(RNBlocks.FREEZER.get()));

		addAll(registry, RNRecipes.FREEZING.get(), FreezingEMIRecipe::new);

		for(FuelRecipe recipe : FreezerFuelRecipeMaker.getFuelRecipes()) {
			registry.addRecipe(new FreezerFuelEMIRecipe(recipe));
		}
	}

	private <C extends Container, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<T, EmiRecipe> constructor) {
		for (T recipe : registry.getRecipeManager().getAllRecipesFor(type)) {
			registry.addRecipe(constructor.apply(recipe));
		}
	}

}
