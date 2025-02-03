package corundum.rubinated_nether.data;

import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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
			RNBlocks.SHRINE_STONE,

			RNBlocks.CHISELED_SHRINE_STONE_BRICKS,

			RNBlocks.SHRINE_STONE_BRICKS_SLAB,
			RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
			RNBlocks.SHRINE_STONE_BRICKS_WALL,
			RNBlocks.SHRINE_STONE_BRICKS,

			RNBlocks.SHRINE_STONE_TILES_SLAB,
			RNBlocks.SHRINE_STONE_TILES_STAIRS,
			RNBlocks.SHRINE_STONE_TILES_WALL,
			RNBlocks.SHRINE_STONE_TILES,

			RNBlocks.POLISHED_SHRINE_STONE_SLAB,
			RNBlocks.POLISHED_SHRINE_STONE_STAIRS,
			RNBlocks.POLISHED_SHRINE_STONE_WALL,
			RNBlocks.POLISHED_SHRINE_STONE,

			RNBlocks.SHRINE_STONE_PILLAR
		);

		stonecutterList(
			recipeOutput,
			RNBlocks.POLISHED_SHRINE_STONE,

			RNBlocks.SHRINE_STONE_BRICKS_SLAB,
			RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
			RNBlocks.SHRINE_STONE_BRICKS_WALL,
			RNBlocks.SHRINE_STONE_BRICKS,

			RNBlocks.SHRINE_STONE_TILES_SLAB,
			RNBlocks.SHRINE_STONE_TILES_STAIRS,
			RNBlocks.SHRINE_STONE_TILES_WALL,
			RNBlocks.SHRINE_STONE_TILES,

			RNBlocks.POLISHED_SHRINE_STONE_SLAB,
			RNBlocks.POLISHED_SHRINE_STONE_STAIRS,
			RNBlocks.POLISHED_SHRINE_STONE_WALL
		);

		stonecutterList(
			recipeOutput,
			RNBlocks.SHRINE_STONE_BRICKS,

			RNBlocks.SHRINE_STONE_BRICKS_SLAB,
			RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
			RNBlocks.SHRINE_STONE_BRICKS_WALL,
			RNBlocks.SHRINE_STONE_BRICKS,

			RNBlocks.SHRINE_STONE_TILES_SLAB,
			RNBlocks.SHRINE_STONE_TILES_STAIRS,
			RNBlocks.SHRINE_STONE_TILES_WALL
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.BRONZE_BLOCK,

				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CUT_BRONZE_BRICKS,

				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,

				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,

				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CORRODED_BRONZE_BLOCK,

				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CORRODED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,

				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.TARNISHED_BRONZE_BLOCK,

				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.TARNISHED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,

				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,

				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,

				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB
		);

		stairsAndSlab(
			recipeOutput,
			RNBlocks.POLISHED_SHRINE_STONE,
			RNBlocks.POLISHED_SHRINE_STONE_STAIRS,
			RNBlocks.POLISHED_SHRINE_STONE_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.POLISHED_SHRINE_STONE,
			RNBlocks.POLISHED_SHRINE_STONE_WALL
		);

		stairsAndSlab(
			recipeOutput,
			RNBlocks.SHRINE_STONE_BRICKS,
			RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
			RNBlocks.SHRINE_STONE_BRICKS_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.SHRINE_STONE_BRICKS,
			RNBlocks.SHRINE_STONE_BRICKS_WALL
		);

		stairsAndSlab(
			recipeOutput,
			RNBlocks.SHRINE_STONE_TILES,
			RNBlocks.SHRINE_STONE_TILES_STAIRS,
			RNBlocks.SHRINE_STONE_TILES_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.SHRINE_STONE_TILES,
			RNBlocks.SHRINE_STONE_TILES_WALL
		);

		twoByTwo(
			recipeOutput,
			RNBlocks.SHRINE_STONE,
			RNBlocks.POLISHED_SHRINE_STONE,
			4
		);
		twoByTwo(
			recipeOutput,
			RNBlocks.POLISHED_SHRINE_STONE,
			RNBlocks.SHRINE_STONE_BRICKS,
			4
		);

		oneByTwo(
			recipeOutput,
			RNBlocks.SHRINE_STONE_BRICKS_SLAB,
			RNBlocks.CHISELED_SHRINE_STONE_BRICKS,
			1
		);

		oneByTwo(
			recipeOutput,
			RNBlocks.SHRINE_STONE,
			RNBlocks.SHRINE_STONE_PILLAR,
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

		stairsAndSlab(
				recipeOutput,
				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB
		);

		stairsAndSlab(
				recipeOutput,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB
		);

		stairsAndSlab(
				recipeOutput,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB
		);

		stairsAndSlab(
				recipeOutput,
				RNBlocks.TARNISHED_BRONZE_BLOCK,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB
		);

		stairsAndSlab(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB
		);

		one(
				recipeOutput,
				RNItems.BRONZE_ROD,
				RNItems.BRONZE_SCRAP,
				64
		);

		twoByTwo(
				recipeOutput,
				RNItems.BRONZE_SCRAP,
				RNBlocks.BRONZE_BLOCK,
				4
		);

		twoByTwo(
				recipeOutput,
				RNBlocks.BRONZE_BLOCK,
				RNBlocks.CUT_BRONZE_BRICKS,
				4
		);

		twoByTwo(
				recipeOutput,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
				4
		);

		twoByTwo(
				recipeOutput,
				RNBlocks.CORRODED_BRONZE_BLOCK,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
				4
		);

		twoByTwo(
				recipeOutput,
				RNBlocks.TARNISHED_BRONZE_BLOCK,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
				4
		);

		twoByTwo(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
				4
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.BRONZE_BLOCK,
				RNBlocks.CUT_BRONZE_PILLAR,
				2
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,
				RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR,
				2
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.CORRODED_BRONZE_BLOCK,
				RNBlocks.CORRODED_CUT_BRONZE_PILLAR,
				2
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.TARNISHED_BRONZE_BLOCK,
				RNBlocks.TARNISHED_CUT_BRONZE_PILLAR,
				2
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR,
				2
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS,
				2
		);

		wall(
				recipeOutput,
				RNBlocks.RUBY_GLASS,
				RNBlocks.RUBY_GLASS_PANE
		);

		wall(
				recipeOutput,
				RNBlocks.MOLTEN_RUBY_GLASS,
				RNBlocks.MOLTEN_RUBY_GLASS_PANE
		);

		wall(
				recipeOutput,
				RNBlocks.ORNATE_RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS_PANE
		);


		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RNItems.BRONZE_SCRAP,4)
				.requires(RNItems.BRONZE_SCRAP,1)
				.requires(Items.COPPER_INGOT,3)
				.requires(RNItems.MOLTEN_RUBY_NUGGET_ITEM, 3)
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY_NUGGET_ITEM), has(RNItems.MOLTEN_RUBY_NUGGET_ITEM))
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.group("bronze_dupe")
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBY_GLASS, 8)
				.define('X', Blocks.GLASS)
				.define('O', RNItems.RUBY_ITEM)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNBlocks.RUBY_GLASS), has(RNBlocks.RUBY_GLASS))
				.unlockedBy(getHasName(RNItems.RUBY_ITEM), has(RNItems.RUBY_ITEM))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.MOLTEN_RUBY_GLASS, 8)
				.define('X', Blocks.GLASS)
				.define('O', RNItems.MOLTEN_RUBY_ITEM)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(Blocks.GLASS), has(Blocks.GLASS))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY_ITEM), has(RNItems.MOLTEN_RUBY_ITEM))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS, 8)
			.define('X', RNBlocks.CHISELED_SHRINE_STONE_BRICKS)
			.define('O', RNItems.RUBY_ITEM)
			.pattern("XXX")
			.pattern("XOX")
			.pattern("XXX")
			.unlockedBy(getHasName(RNBlocks.CHISELED_SHRINE_STONE_BRICKS), has(RNBlocks.CHISELED_SHRINE_STONE_BRICKS))
			.unlockedBy(getHasName(RNItems.RUBY_ITEM), has(RNItems.RUBY_ITEM))
			.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBINATED_SHRINE_STONE_BRICKS, 8)
				.define('X', RNBlocks.SHRINE_STONE_BRICKS)
				.define('O', RNItems.RUBY_ITEM)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNBlocks.SHRINE_STONE_BRICKS), has(RNBlocks.SHRINE_STONE_BRICKS))
				.unlockedBy(getHasName(RNItems.RUBY_ITEM), has(RNItems.RUBY_ITEM))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.BLEEDING_OBSIDIAN, 8)
			.define('X', Blocks.CRYING_OBSIDIAN)
			.define('O', RNItems.RUBY_ITEM)
			.pattern("XXX")
			.pattern("XOX")
			.pattern("XXX")
			.unlockedBy(getHasName(Blocks.CRYING_OBSIDIAN), has(Blocks.CRYING_OBSIDIAN))
			.unlockedBy(getHasName(RNItems.RUBY_ITEM), has(RNItems.RUBY_ITEM))
			.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNItems.MUSIC_DISC_SHIMMER, 1)
			.define('X', RNItems.RUBY_SHARD_ITEM)
			.define('O', Items.MUSIC_DISC_PIGSTEP)
			.pattern("XXX")
			.pattern("XOX")
			.pattern("XXX")
			.unlockedBy(getHasName(RNItems.RUBY_SHARD_ITEM), has(RNItems.RUBY_SHARD_ITEM))
			.unlockedBy(getHasName(Items.MUSIC_DISC_PIGSTEP), has(Items.MUSIC_DISC_PIGSTEP))
			.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNBlocks.FREEZER, 1)
				.define('X', Blocks.COPPER_BLOCK)
				.define('O', Blocks.BASALT)
				.define('I', Items.BREEZE_ROD)
				.pattern("XXX")
				.pattern("XIX")
				.pattern("OOO")
				.unlockedBy(getHasName(Blocks.COPPER_BLOCK), has(Blocks.COPPER_BLOCK))
				.unlockedBy(getHasName(Blocks.BASALT), has(Blocks.BASALT))
				.unlockedBy(getHasName(Items.BREEZE_ROD), has(Items.BREEZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNItems.BRONZE_SHOT, 1)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('O', Items.COPPER_INGOT)
				.pattern(" X ")
				.pattern("XOX")
				.pattern(" X ")
				.unlockedBy(getHasName(RNItems.BRONZE_SHOT), has(RNItems.BRONZE_SHOT))
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
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
