package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class BaptismRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int fuelLevels;

    public BaptismRecipe(Ingredient ingredient, ItemStack result, int fuelLevels) {
        this.ingredient = ingredient;
        this.result = result;
        this.fuelLevels = fuelLevels;
    }

    @Override public boolean matches(SingleRecipeInput input, Level level) { return ingredient.test(input.item()); }
    @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider provider) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider provider) { return result; }
    @Override public RecipeSerializer<?> getSerializer() { return RNRecipeSerializers.BAPTISM.get(); }
    @Override public RecipeType<?> getType() { return RNRecipes.BAPTISM.get(); }

    public Ingredient getIngredient() { return ingredient; }
    public ItemStack getResult() { return result; }
    public int getFuelLevels() { return fuelLevels; }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(RNBlocks.BRAZIER.get());
    }

}
