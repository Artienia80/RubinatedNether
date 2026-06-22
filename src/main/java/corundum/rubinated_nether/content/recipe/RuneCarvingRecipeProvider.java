package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
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

    public static void registerAll(RecipeOutput recipeOutput, List<? extends ItemLike> shrineTiers) {
        for (ItemLike shrineTier : shrineTiers) {
            for (Rubination rubination : Rubination.carvableValues()) {
                register(recipeOutput, shrineTier, rubination);
            }
        }
    }

    private static void register(RecipeOutput recipeOutput, ItemLike shrineTier, Rubination rubination) {
        ItemStack result = new ItemStack(RNItems.RUNE.get());
        result.set(RNDataComponents.RUNE_CARVING.get(), rubination);

        String shrineTierName = itemName(shrineTier);
        int order = Rubination.carvableValues().indexOf(rubination);
        String recipeName = "carved_rune_" + String.format("%02d", order) + "_" + rubination.getSerializedName() + "_from_" + shrineTierName;
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
                        .addCriterion("has_rune", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUNE))
                        .addCriterion("has_" + shrineTierName, InventoryChangeTrigger.TriggerInstance.hasItems(shrineTier))
                        .rewards(AdvancementRewards.Builder.recipe(recipeId))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .build(RubinatedNether.id("recipes/" + recipeName))
        );
    }

    private static String itemName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }
}