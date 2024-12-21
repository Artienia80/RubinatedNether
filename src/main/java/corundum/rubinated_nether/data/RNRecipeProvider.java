package corundum.rubinated_nether.data;

import java.util.concurrent.CompletableFuture;

import java.util.List;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;

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

	}

	@SuppressWarnings("unused")
	private void twoByTwo(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
				.group("idk")
				.define('I', input)
				.pattern("II")
				.pattern("II")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
	}
	private void stairsAndSlab(RecipeOutput recipeOutput, ItemLike input, ItemLike stairs, ItemLike slab) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4)
				.group("stairs")
				.define('S', input)
				.pattern("  S")
				.pattern(" SS")
				.pattern("SSS")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6)
				.group("slabs")
				.define('S', input)
				.pattern("SSS")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
	}
	private void wall(RecipeOutput recipeOutput, ItemLike input, ItemLike wall) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wall, 6)
				.group("doors")
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
