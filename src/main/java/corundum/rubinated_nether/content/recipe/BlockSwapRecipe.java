package corundum.rubinated_nether.content.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockSwapRecipe implements Recipe<BlockSwapRecipe.Input> {

    public record Input(BlockState state) implements RecipeInput {
        @Override public ItemStack getItem(int index) { return ItemStack.EMPTY; }
        @Override public int size() { return 1; }
        @Override public boolean isEmpty() { return false; }
    }

    private final Block source;
    private final Block result;

    protected BlockSwapRecipe(Block source, Block result) {
        this.source = source;
        this.result = result;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return input.state().is(source);
    }

    @Override public ItemStack assemble(Input input, HolderLookup.Provider provider) { return new ItemStack(result); }
    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider provider) { return new ItemStack(result); }

    public Block getSource() { return source; }
    public Block getResultBlock() { return result; }
}
