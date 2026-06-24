package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public final class RuneCarvingRecipeProvider {

    private RuneCarvingRecipeProvider() {}

    public static void registerShrineTiers(RecipeOutput recipeOutput, List<? extends ItemLike> shrineTiers) {
        for (ItemLike shrineTier : shrineTiers) {
            registerBlankRuneFromShrineTier(recipeOutput, shrineTier);

            for (Rubination rubination : Rubination.carvableValues()) {
                registerCarvedRuneFromInput(recipeOutput, shrineTier, rubination);
            }
        }
    }

    public static void registerBlankRune(RecipeOutput recipeOutput) {
        for (Rubination rubination : Rubination.carvableValues()) {
            registerCarvedRuneFromInput(recipeOutput, RNItems.RUNE.get(), rubination);
        }
    }

    private static void registerBlankRuneFromShrineTier(RecipeOutput recipeOutput, ItemLike shrineTier) {
        ItemStack result = new ItemStack(RNItems.RUNE.get());

        String shrineTierName = itemName(shrineTier);
        String recipeName = "carved_rune_00_blank_from_" + shrineTierName;
        ResourceLocation recipeId = RubinatedNether.id(recipeName);

        StonecutterRecipe recipe = new StonecutterRecipe(
                "rune_carving",
                Ingredient.of(shrineTier),
                result
        );

        recipeOutput.accept(
                recipeId,
                recipe,
                Advancement.Builder.advancement()
                        .parent(ResourceLocation.withDefaultNamespace("recipes/root"))
                        .addCriterion("has_" + shrineTierName, InventoryChangeTrigger.TriggerInstance.hasItems(shrineTier))
                        .rewards(AdvancementRewards.Builder.recipe(recipeId))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .build(RubinatedNether.id("recipes/" + recipeName))
        );
    }

    private static void registerCarvedRuneFromInput(RecipeOutput recipeOutput, ItemLike input, Rubination rubination) {
        ItemStack result = RuneCarvingHelper.withCarving(new ItemStack(RNItems.RUNE.get()), rubination);

        String inputName = itemName(input);
        int order = Rubination.carvableValues().indexOf(rubination) + 1;
        String recipeName = "carved_rune_" + String.format("%02d", order) + "_" + rubination.getSerializedName() + "_from_" + inputName;
        ResourceLocation recipeId = RubinatedNether.id(recipeName);

        StonecutterRecipe recipe = new StonecutterRecipe(
                "rune_carving",
                Ingredient.of(input),
                result
        );

        recipeOutput.accept(
                recipeId,
                recipe,
                Advancement.Builder.advancement()
                        .parent(ResourceLocation.withDefaultNamespace("recipes/root"))
                        .addCriterion("has_" + inputName, InventoryChangeTrigger.TriggerInstance.hasItems(input))
                        .rewards(AdvancementRewards.Builder.recipe(recipeId))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .build(RubinatedNether.id("recipes/" + recipeName))
        );
    }

    private static String itemName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }
}