package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.content.RNRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class DerubinationRecipe extends BlockSwapRecipe {

    public DerubinationRecipe(Block source, Block result) {
        super(source, result);
    }

    @Override public RecipeSerializer<?> getSerializer() { return RNRecipeSerializers.DERUBINATION.get(); }
    @Override public RecipeType<?> getType() { return RNRecipes.DERUBINATION.get(); }
}
