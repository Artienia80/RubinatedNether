package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ResonanceRecipe implements Recipe<SingleRecipeInput> {

    private final String group;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final Optional<TriggerData> offering;
    private final Optional<TriggerData> key;
    private final boolean requireShrineStoneNeighbor;

    public record TriggerData(double radius, int attempts) {}

    public ResonanceRecipe(String group, Ingredient ingredient, ItemStack result,
                           Optional<TriggerData> offering, Optional<TriggerData> key,
                           boolean requireShrineStoneNeighbor) {
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.offering = offering;
        this.key = key;
        this.requireShrineStoneNeighbor = requireShrineStoneNeighbor;
    }

    @Override public boolean matches(SingleRecipeInput input, Level level) { return ingredient.test(input.item()); }
    @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider provider) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider provider) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return RNRecipeSerializers.RESONANCE.get(); }
    @Override public RecipeType<?> getType() { return RNRecipes.RESONANCE.get(); }

    public String getGroup() { return group; }
    public Ingredient getIngredient() { return ingredient; }
    public ItemStack getResult() { return result; }
    public Optional<TriggerData> getOffering() { return offering; }
    public Optional<TriggerData> getKey() { return key; }
    public boolean requiresShrineStoneNeighbor() { return requireShrineStoneNeighbor; }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get());
    }

}