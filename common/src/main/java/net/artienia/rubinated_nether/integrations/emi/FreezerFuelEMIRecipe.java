package net.artienia.rubinated_nether.integrations.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.integrations.viewers.FuelRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FreezerFuelEMIRecipe implements EmiRecipe {

	private static final String ID_FORMAT = "freezer_fuel/%s/%s";

	private final EmiIngredient stack;
	private final int time;
	private final ResourceLocation id;

	public FreezerFuelEMIRecipe(FuelRecipe recipe) {
		stack = EmiIngredient.of(recipe.getInput().stream().map(EmiStack::of).toList());
		time = recipe.getBurnTime();
		ResourceLocation loc = BuiltInRegistries.ITEM.getKey(recipe.getInput().get(0).getItem());
		id = RubinatedNether.id(ID_FORMAT.formatted(loc.getNamespace(), loc.getPath()));

	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EMIStuff.EMPTY_FREEZE, 1, 1);
		widgets.addAnimatedTexture(EMIStuff.FULL_FREEZE, 1, 1, 1000 * time / 20, false, true, true);
		widgets.addSlot(stack, 18, 0).recipeContext(this);
		widgets.addText(Component.translatable("gui.rubinated_nether.emi.freezes",
			EMIStuff.TEXT_FORMAT.format(time / 200f)), 38, 5, -1, true);
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EMIModPlugin.FREEZER_FUEL;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(stack);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of();
	}

	@Override
	public int getDisplayWidth() {
		return 144;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}
}
