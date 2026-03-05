package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.recipe.ResonanceRecipe;
import corundum.rubinated_nether.content.recipe.ResonanceRecipeSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RNResonanceRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final ItemStack result;
    @Nullable private String group;
    @Nullable private ResonanceRecipe.TriggerData offering;
    @Nullable private ResonanceRecipe.TriggerData key;
    private boolean requireShrineStoneNeighbor = false;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    private RNResonanceRecipeBuilder(Ingredient ingredient, ItemLike result) {
        this.ingredient = ingredient;
        this.result = new ItemStack(result);
    }

    public static RNResonanceRecipeBuilder resonance(Ingredient ingredient, ItemLike result) {
        return new RNResonanceRecipeBuilder(ingredient, result);
    }

    public RNResonanceRecipeBuilder offering(double radius, int attempts) {
        this.offering = new ResonanceRecipe.TriggerData(radius, attempts);
        return this;
    }

    public RNResonanceRecipeBuilder key(double radius, int attempts) {
        this.key = new ResonanceRecipe.TriggerData(radius, attempts);
        return this;
    }

    public RNResonanceRecipeBuilder requireShrineStoneNeighbor() {
        this.requireShrineStoneNeighbor = true;
        return this;
    }

    @Override
    public RNResonanceRecipeBuilder unlockedBy(String name, net.minecraft.advancements.Criterion<?> criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public RNResonanceRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        this.advancement
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        output.accept(
                id,
                new ResonanceRecipe(
                        this.group == null ? "" : this.group,
                        this.ingredient,
                        this.result,
                        Optional.ofNullable(this.offering),
                        Optional.ofNullable(this.key),
                        this.requireShrineStoneNeighbor
                ),
                this.advancement.build(id.withPrefix("recipes/"))
        );
    }
}