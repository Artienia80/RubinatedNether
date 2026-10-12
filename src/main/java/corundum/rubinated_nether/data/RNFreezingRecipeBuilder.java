package corundum.rubinated_nether.data;

import corundum.rubinated_nether.content.recipe.FreezingRecipe;
import corundum.rubinated_nether.content.recipe.RNBookCategory;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class RNFreezingRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    @Nullable private String group;

    private RNFreezingRecipeBuilder(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
        this.ingredient = ingredient;
        this.result = new ItemStack(result);
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    public static RNFreezingRecipeBuilder freezing(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
        return new RNFreezingRecipeBuilder(ingredient, result, experience, cookingTime);
    }

    @Override
    public RNFreezingRecipeBuilder unlockedBy(String name, net.minecraft.advancements.Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RNFreezingRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        this.ensureValid(id);

        var builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        output.accept(
                id,
                new FreezingRecipe(
                        this.group == null ? "" : this.group,
                        RNBookCategory.FREEZABLE_MISC,
                        this.ingredient,
                        this.result,
                        this.experience,
                        this.cookingTime
                ),
                builder.build(id.withPrefix("recipes/freezing/" + id.getPath()))
        );
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}