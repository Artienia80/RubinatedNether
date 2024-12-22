package corundum.rubinated_nether.data;

import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class RNRecipeProvider extends RecipeProvider {
	protected RNRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {

		stonecutterList(
			recipeOutput,
			RNBlocks.ALTAR_STONE,

			RNBlocks.CHISELED_ALTAR_STONE_BRICKS,

			RNBlocks.ALTAR_STONE_BRICKS_SLAB,
			RNBlocks.ALTAR_STONE_BRICKS_STAIRS,
			RNBlocks.ALTAR_STONE_BRICKS_WALL,
			RNBlocks.ALTAR_STONE_BRICKS,

			RNBlocks.ALTAR_STONE_TILES_SLAB,
			RNBlocks.ALTAR_STONE_TILES_STAIRS,
			RNBlocks.ALTAR_STONE_TILES_WALL,
			RNBlocks.ALTAR_STONE_TILES,

			RNBlocks.POLISHED_ALTAR_STONE_SLAB,
			RNBlocks.POLISHED_ALTAR_STONE_STAIRS,
			RNBlocks.POLISHED_ALTAR_STONE_WALL,
			RNBlocks.POLISHED_ALTAR_STONE,

			RNBlocks.ALTAR_STONE_PILLAR
		);

		stonecutterList(
			recipeOutput,
			RNBlocks.POLISHED_ALTAR_STONE,

			RNBlocks.ALTAR_STONE_BRICKS_SLAB,
			RNBlocks.ALTAR_STONE_BRICKS_STAIRS,
			RNBlocks.ALTAR_STONE_BRICKS_WALL,
			RNBlocks.ALTAR_STONE_BRICKS,

			RNBlocks.ALTAR_STONE_TILES_SLAB,
			RNBlocks.ALTAR_STONE_TILES_STAIRS,
			RNBlocks.ALTAR_STONE_TILES_WALL,
			RNBlocks.ALTAR_STONE_TILES,

			RNBlocks.POLISHED_ALTAR_STONE_SLAB,
			RNBlocks.POLISHED_ALTAR_STONE_STAIRS,
			RNBlocks.POLISHED_ALTAR_STONE_WALL
		);

		stonecutterList(
			recipeOutput,
			RNBlocks.ALTAR_STONE_BRICKS,

			RNBlocks.ALTAR_STONE_BRICKS_SLAB,
			RNBlocks.ALTAR_STONE_BRICKS_STAIRS,
			RNBlocks.ALTAR_STONE_BRICKS_WALL,
			RNBlocks.ALTAR_STONE_BRICKS,

			RNBlocks.ALTAR_STONE_TILES_SLAB,
			RNBlocks.ALTAR_STONE_TILES_STAIRS,
			RNBlocks.ALTAR_STONE_TILES_WALL
		);

		stairsAndSlab(
			recipeOutput,
			RNBlocks.POLISHED_ALTAR_STONE,
			RNBlocks.POLISHED_ALTAR_STONE_STAIRS,
			RNBlocks.POLISHED_ALTAR_STONE_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.POLISHED_ALTAR_STONE,
			RNBlocks.POLISHED_ALTAR_STONE_WALL
		);

		stairsAndSlab(
			recipeOutput,
			RNBlocks.ALTAR_STONE_BRICKS,
			RNBlocks.ALTAR_STONE_BRICKS_STAIRS,
			RNBlocks.ALTAR_STONE_BRICKS_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.ALTAR_STONE_BRICKS,
			RNBlocks.ALTAR_STONE_BRICKS_WALL
		);

		stairsAndSlab(
			recipeOutput,
			RNBlocks.ALTAR_STONE_TILES,
			RNBlocks.ALTAR_STONE_TILES_STAIRS,
			RNBlocks.ALTAR_STONE_TILES_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.ALTAR_STONE_TILES,
			RNBlocks.ALTAR_STONE_TILES_WALL
		);

		twoByTwo(
			recipeOutput,
			RNBlocks.ALTAR_STONE,
			RNBlocks.POLISHED_ALTAR_STONE,
			4
		);
		twoByTwo(
			recipeOutput,
			RNBlocks.POLISHED_ALTAR_STONE,
			RNBlocks.ALTAR_STONE_BRICKS,
			4
		);

		oneByTwo(
			recipeOutput,
			RNBlocks.ALTAR_STONE_BRICKS_SLAB,
			RNBlocks.CHISELED_ALTAR_STONE_BRICKS,
			1
		);

		oneByTwo(
			recipeOutput,
			RNBlocks.ALTAR_STONE,
			RNBlocks.ALTAR_STONE_PILLAR,
			2
		);

		threeByThree(
			recipeOutput,
			RNItems.RUBY_SHARD_ITEM,
			RNItems.RUBY_ITEM,
			1
		);

		threeByThree(
			recipeOutput,
			RNItems.MOLTEN_RUBY_NUGGET_ITEM,
			RNItems.MOLTEN_RUBY_ITEM,
			1
		);

		threeByThree(
			recipeOutput,
			RNItems.MOLTEN_RUBY_ITEM,
			RNBlocks.MOLTEN_RUBY_BLOCK,
			1
		);

		threeByThree(
			recipeOutput,
			RNItems.RUBY_ITEM,
			RNBlocks.RUBY_BLOCK,
			1
		);

		one(
			recipeOutput,
			RNBlocks.RUBY_BLOCK,
			RNItems.RUBY_ITEM,
			9
		);

		one(
			recipeOutput,
			RNBlocks.MOLTEN_RUBY_BLOCK,
			RNItems.MOLTEN_RUBY_ITEM,
			9
		);

		one(
			recipeOutput,
			RNItems.RUBY_ITEM,
			RNItems.RUBY_SHARD_ITEM,
			9
		);

		one(
			recipeOutput,
			RNItems.MOLTEN_RUBY_ITEM,
			RNItems.MOLTEN_RUBY_NUGGET_ITEM,
			9
		);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS, 8)
			.define('X', RNBlocks.CHISELED_ALTAR_STONE_BRICKS)
			.define('O', RNItems.RUBY_ITEM)
			.pattern("XXX")
			.pattern("XOX")
			.pattern("XXX")
			.unlockedBy(getHasName(RNBlocks.CHISELED_ALTAR_STONE_BRICKS), has(RNItems.RUBY_ITEM))
			.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.BLEEDING_OBSIDIAN, 8)
			.define('X', Blocks.CRYING_OBSIDIAN)
			.define('O', RNItems.RUBY_ITEM)
			.pattern("XXX")
			.pattern("XOX")
			.pattern("XXX")
			.unlockedBy(getHasName(Blocks.CRYING_OBSIDIAN), has(RNItems.RUBY_ITEM))
			.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNItems.MUSIC_DISC_SHIMMER, 1)
			.define('X', RNItems.RUBY_SHARD_ITEM)
			.define('O', Items.MUSIC_DISC_PIGSTEP)
			.pattern("XXX")
			.pattern("XOX")
			.pattern("XXX")
			.unlockedBy(getHasName(RNItems.RUBY_SHARD_ITEM), has(Items.MUSIC_DISC_PIGSTEP))
			.save(recipeOutput);
	}

	private void twoByTwo(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
			.group(output.toString())
			.define('I', input)
			.pattern("II")
			.pattern("II")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput, output.asItem().toString().toLowerCase() + "_via_twobytwo");
	}

	private void oneByTwo(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
			.group(output.toString())
			.define('I', input)
			.pattern("I")
			.pattern("I")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput, output.asItem().toString().toLowerCase() + "_via_onebytwo");
	}

	private void threeByThree(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
			.group(output.toString())
			.define('I', input)
			.pattern("III")
			.pattern("III")
			.pattern("III")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput, output.asItem().toString().toLowerCase() + "_via_threebythree");
	}

	private void one(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
			.group(output.toString())
			.define('I', input)
			.pattern("I")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput, output.asItem().toString().toLowerCase() + "_via_one");
	}

	private void stairsAndSlab(RecipeOutput recipeOutput, ItemLike input, ItemLike stairs, ItemLike slab) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4)
			.define('S', input)
			.pattern("  S")
			.pattern(" SS")
			.pattern("SSS")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6)
			.define('S', input)
			.pattern("SSS")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput);
	}

	private void wall(RecipeOutput recipeOutput, ItemLike input, ItemLike wall) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wall, 6)
			.define('P', input)
			.pattern("PPP")
			.pattern("PPP")
			.unlockedBy(getHasName(input), has(input))
			.save(recipeOutput);
	}

	private void stonecutterList(RecipeOutput recipeOutput, ItemLike input, ItemLike... outputs) {
		for (ItemLike output : outputs) {
			var count = 1;

			if (output.asItem().toString().contains("slab")) {
				count++;
			}

			stonecutterResultFromBase(
				recipeOutput,
				RecipeCategory.BUILDING_BLOCKS,
				output,
				input,
				count
			);
		}
	}
}
