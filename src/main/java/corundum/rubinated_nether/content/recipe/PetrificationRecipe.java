package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A world recipe: a block in the source tag is turned into result, preserving block state properties.
 */
public class PetrificationRecipe implements Recipe<PetrificationRecipe.Input> {
	public record Input(BlockState state) implements RecipeInput {
		@Override public ItemStack getItem(int index) { return ItemStack.EMPTY; }
		@Override public int size() { return 1; }
		@Override public boolean isEmpty() { return false; }
	}

	private final TagKey<Block> source;
	private final Block result;

	public PetrificationRecipe(TagKey<Block> source, Block result) {
		this.source = source;
		this.result = result;
	}

	@Override
	public boolean matches(Input input, Level level) {
		BlockState state = input.state();
		return state.is(source) && !state.is(result);
	}

	@Override public ItemStack assemble(Input input, HolderLookup.Provider provider) { return new ItemStack(result); }
	@Override public boolean canCraftInDimensions(int width, int height) { return true; }
	@Override public ItemStack getResultItem(HolderLookup.Provider provider) { return new ItemStack(result); }
	@Override public RecipeSerializer<?> getSerializer() { return RNRecipeSerializers.PETRIFICATION.get(); }
	@Override public RecipeType<?> getType() { return RNRecipes.PETRIFICATION.get(); }

	public TagKey<Block> getSource() { return source; }
	public Block getResultBlock() { return result; }

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RNBlocks.SOUL_STARVED_SAND.get());
	}
}
