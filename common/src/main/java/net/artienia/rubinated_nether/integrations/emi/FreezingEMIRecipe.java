package net.artienia.rubinated_nether.integrations.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.artienia.rubinated_nether.recipe.FreezingRecipe;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FreezingEMIRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final FreezingRecipe recipe;

    public FreezingEMIRecipe(FreezingRecipe recipe) {
        this.recipe = recipe;
        this.id = recipe.getId();
        this.input = EmiIngredient.of(recipe.getIngredients().get(0));
        this.output = EmiStack.of(recipe.getResult());
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int seconds = 50 * recipe.getCookingTime();
        widgets.addFillingArrow(24, 5, seconds)
            .tooltip(List.of(ClientTooltipComponent.create(Component.translatable("emi.cooking.time", seconds).getVisualOrderText())));

        widgets.addTexture(EMIStuff.FULL_FREEZE, 1, 24);

        widgets.addText(Component.translatable("emi.cooking.experience", recipe.getExperience()), 26, 28, -1, true);
        widgets.addSlot(input, 0, 4);
        widgets.addSlot(output, 56, 0).large(true).recipeContext(this);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIModPlugin.FREEZING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }
}
