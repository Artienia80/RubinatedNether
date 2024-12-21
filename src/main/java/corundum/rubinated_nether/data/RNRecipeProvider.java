package corundum.rubinated_nether.data;

import java.util.concurrent.CompletableFuture;

import java.util.List;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class RNRecipeProvider extends RecipeProvider {
	protected RNRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {

		stonecutterList(
				recipeOutput,
				RNBlocks.ALTAR_STONE,
				List.of(
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
				)
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.POLISHED_ALTAR_STONE,
				List.of(
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
				)
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.ALTAR_STONE_BRICKS,
				List.of(
						RNBlocks.ALTAR_STONE_BRICKS_SLAB,
						RNBlocks.ALTAR_STONE_BRICKS_STAIRS,
						RNBlocks.ALTAR_STONE_BRICKS_WALL,
						RNBlocks.ALTAR_STONE_BRICKS,

						RNBlocks.ALTAR_STONE_TILES_SLAB,
						RNBlocks.ALTAR_STONE_TILES_STAIRS,
						RNBlocks.ALTAR_STONE_TILES_WALL
				)
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
	}

	private void twoByTwo(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
				.define('I', input)
				.pattern("II")
				.pattern("II")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
	}

	private void oneByTwo(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
				.define('I', input)
				.pattern("I")
				.pattern("I")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
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

	private void stonecutterList(RecipeOutput recipeOutput, ItemLike input, List<ItemLike> outputs) {
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
