package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public final class RuneCarvingRecipeProvider {

    private RuneCarvingRecipeProvider() {}

    /** Carved runes can only be made by carving a BLANK rune (a rune without any carving). */
    public static void registerBlankRune(RecipeOutput recipeOutput) {
        Ingredient blankRune = DataComponentIngredient.of(true, DataComponentMap.EMPTY, RNItems.RUNE.get());
        for (Rubination rubination : Rubination.carvableValues()) {
            registerCarvedRune(recipeOutput, blankRune, rubination);
        }
    }

    private static void registerCarvedRune(RecipeOutput recipeOutput, Ingredient ingredient, Rubination rubination) {
        ItemStack result = RuneCarvingHelper.withCarving(new ItemStack(RNItems.RUNE.get()), rubination);

        String inputName = "rune";
        int order = Rubination.carvableValues().indexOf(rubination) + 1;
        String recipeName = "carved_rune_" + String.format("%02d", order) + "_" + rubination.getSerializedName() + "_from_" + inputName;
        ResourceLocation recipeId = RubinatedNether.id(recipeName);

        StonecutterRecipe recipe = new StonecutterRecipe(
                "rune_carving",
                ingredient,
                result
        );

        recipeOutput.accept(
                recipeId,
                recipe,
                Advancement.Builder.advancement()
                        .parent(ResourceLocation.withDefaultNamespace("recipes/root"))
                        .addCriterion("has_" + inputName, InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUNE.get()))
                        .rewards(AdvancementRewards.Builder.recipe(recipeId))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .build(RubinatedNether.id("recipes/" + recipeName))
        );
    }

}