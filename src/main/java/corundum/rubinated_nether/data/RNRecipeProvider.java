package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public class RNRecipeProvider extends RecipeProvider {
	protected RNRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {

		stonecutterList(
			recipeOutput,
			RNBlocks.SHRINE_STONE,

			RNBlocks.SHRINE_STONE_SLAB,
			RNBlocks.SHRINE_STONE_STAIRS,
			RNBlocks.SHRINE_STONE_WALL,

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
			RNBlocks.POLISHED_SHRINE_STONE_WALL,

			RNBlocks.CHISELED_SHRINE_STONE_BRICKS,
			RNBlocks.SHRINE_STONE_PILLAR

		);

		stonecutterList(
			recipeOutput,
			RNBlocks.SHRINE_STONE_BRICKS,

			RNBlocks.SHRINE_STONE_BRICKS_SLAB,
			RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
			RNBlocks.SHRINE_STONE_BRICKS_WALL,

			RNBlocks.SHRINE_STONE_TILES_SLAB,
			RNBlocks.SHRINE_STONE_TILES_STAIRS,
			RNBlocks.SHRINE_STONE_TILES_WALL,
			RNBlocks.SHRINE_STONE_TILES,
			RNBlocks.CHISELED_SHRINE_STONE_BRICKS
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.SHRINE_STONE_TILES,

				RNBlocks.SHRINE_STONE_TILES_SLAB,
				RNBlocks.SHRINE_STONE_TILES_STAIRS,
				RNBlocks.SHRINE_STONE_TILES_WALL
		);

		Bronzecutter(recipeOutput, RNBlocks.BRONZE_BLOCK,
				RNBlocks.CHISELED_BRONZE,
				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CUT_BRONZE_PILLAR,
				RNBlocks.BRONZE_GRATE
		);

		Bronzecutter(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CHISELED_BRONZE,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB
		);

		tarnish(recipeOutput, this::lamp, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_LAMP);
		tarnish(recipeOutput, this::bulb, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_BULB);
		tarnish(recipeOutput, this::chandelier, RNBlocks.BRONZE_LANTERN, RNBlocks.BRONZE_GRATE, RNBlocks.BRONZE_CHANDELIER);

		tarnish(recipeOutput, (output, input, result) -> laser(output, input, result, false),
				RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_LASER);

		tarnish(recipeOutput, (output, input, result) -> grate(output, input, result, 4),
				RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_GRATE);

		tarnish(recipeOutput, this::stairsAndSlab, RNBlocks.CUT_BRONZE_BRICKS, RNBlocks.CUT_BRONZE_BRICKS_STAIRS, RNBlocks.CUT_BRONZE_BRICKS_SLAB);
		tarnish(recipeOutput, (output, input, result) -> twoByTwo(output, input, result, 4),
				RNBlocks.BRONZE_BLOCK, RNBlocks.CUT_BRONZE_BRICKS);
		tarnish(recipeOutput, (output, input, result) -> oneByTwo(output, input, result, 1),
				RNBlocks.CUT_BRONZE_BRICKS_SLAB, RNBlocks.CHISELED_BRONZE);
		tarnish(recipeOutput, (output, input, result) -> oneByTwo(output, input, result, 2),
				RNBlocks.BRONZE_BLOCK, RNBlocks.CUT_BRONZE_PILLAR);

		stairsAndSlab(
				recipeOutput,
				RNBlocks.SHRINE_STONE,
				RNBlocks.SHRINE_STONE_STAIRS,
				RNBlocks.SHRINE_STONE_SLAB
		);

		wall(
				recipeOutput,
				RNBlocks.SHRINE_STONE,
				RNBlocks.SHRINE_STONE_WALL
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
		twoByTwo(
				recipeOutput,
				RNBlocks.SHRINE_STONE_BRICKS,
				RNBlocks.SHRINE_STONE_TILES,
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
			RNBlocks.POLISHED_SHRINE_STONE_SLAB,
			RNBlocks.SHRINE_STONE_PILLAR,
			1
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

		twoByTwo(
				recipeOutput,
				RNItems.BRONZE_SCRAP,
				RNItems.BRONZE_SHOT,
				8
		);

		threeByThree(
				recipeOutput,
				RNItems.BRONZE_SCRAP,
				RNBlocks.BRONZE_BLOCK,
				1
		);

		one(
				recipeOutput,
				RNItems.BRONZE_ROD,
				RNItems.BRONZE_POWDER,
				9
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS,
				2
		);

		pane(
				recipeOutput,
				RNBlocks.RUBY_GLASS,
				RNBlocks.RUBY_GLASS_PANE
		);

		pane(
				recipeOutput,
				RNBlocks.MOLTEN_RUBY_GLASS,
				RNBlocks.MOLTEN_RUBY_GLASS_PANE
		);

		pane(
				recipeOutput,
				RNBlocks.ORNATE_RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS_PANE
		);

		laser(recipeOutput, Blocks.COPPER_BLOCK, RNBlocks.COPPER_LASER, true);
		laser(recipeOutput, Blocks.EXPOSED_COPPER, RNBlocks.EXPOSED_COPPER_LASER, true);
		laser(recipeOutput, Blocks.WEATHERED_COPPER, RNBlocks.WEATHERED_COPPER_LASER, true);
		laser(recipeOutput, Blocks.OXIDIZED_COPPER, RNBlocks.OXIDIZED_COPPER_LASER, true);
		laser(recipeOutput, Blocks.WAXED_COPPER_BLOCK, RNBlocks.WAXED_COPPER_LASER, true);
		laser(recipeOutput, Blocks.WAXED_EXPOSED_COPPER, RNBlocks.WAXED_EXPOSED_COPPER_LASER, true);
		laser(recipeOutput, Blocks.WAXED_WEATHERED_COPPER, RNBlocks.WAXED_WEATHERED_COPPER_LASER, true);
		laser(recipeOutput, Blocks.WAXED_OXIDIZED_COPPER, RNBlocks.WAXED_OXIDIZED_COPPER_LASER, true);

		generateAllWaxingRecipes(recipeOutput);

		waxRecipes(recipeOutput, FeatureFlagSet.of(FeatureFlags.VANILLA));

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


		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNItems.BRONZE_SCRAP, 1)
				.define('X', RNItems.BRONZE_POWDER)
				.define('O', Items.COPPER_INGOT)
				.pattern(" O ")
				.pattern("OXO")
				.pattern(" O ")
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy(getHasName(RNItems.BRONZE_POWDER), has(RNItems.BRONZE_POWDER))
				.save(recipeOutput);


		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNItems.RITUAL_OFFERING, 1)
				.define('X', RNItems.BRONZE_ROD)
				.define('O', RNItems.RUBY_ITEM)
				.pattern("XXX")
				.pattern(" O ")
				.unlockedBy(getHasName(RNItems.BRONZE_ROD), has(RNItems.BRONZE_ROD))
				.unlockedBy(getHasName(RNItems.RUBY_ITEM), has(RNItems.RUBY_ITEM))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.BRONZE_LANTERN, 1)
				.define('X', RNItems.BRONZE_POWDER)
				.define('O', RNItems.MOLTEN_RUBY_ITEM)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNItems.BRONZE_POWDER), has(RNItems.BRONZE_POWDER))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY_ITEM), has(RNItems.MOLTEN_RUBY_ITEM))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.BRONZE_CHAIN, 1)
				.define('X', RNItems.BRONZE_POWDER)
				.define('O', RNItems.BRONZE_SCRAP)
				.pattern(" X ")
				.pattern(" O ")
				.pattern(" X ")
				.unlockedBy(getHasName(RNItems.BRONZE_POWDER), has(RNItems.BRONZE_POWDER))
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNItems.RUBY_LENS, 1)
				.define('X', Items.COPPER_INGOT)
				.define('L', Items.LEATHER)
				.define('O', RNBlocks.RUBY_GLASS_PANE)
				.pattern("XLX")
				.pattern("OXO")
				.unlockedBy(getHasName(RNBlocks.RUBY_GLASS_PANE), has(RNBlocks.RUBY_GLASS_PANE))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.save(recipeOutput);



		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.BRAZIER, 1)
				.define('A', Blocks.OBSIDIAN)
				.define('B', Items.COPPER_INGOT)
				.define('C', RNBlocks.MOLTEN_RUBY_BLOCK)
				.define('D', Items.NETHERITE_INGOT)
				.pattern("BCB")
				.pattern("BDB")
				.pattern("AAA")
				.unlockedBy(getHasName(RNBlocks.MOLTEN_RUBY_BLOCK), has(RNBlocks.MOLTEN_RUBY_BLOCK))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
				.unlockedBy(getHasName(Blocks.OBSIDIAN), has(Blocks.OBSIDIAN))
				.save(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, RNBlocks.WAXED_COPPER_LASER)
				.requires(RNBlocks.COPPER_LASER)
				.requires(Items.HONEYCOMB)
				.group(getItemName(RNBlocks.WAXED_COPPER_LASER))
				.unlockedBy(getHasName(RNBlocks.COPPER_LASER), has(RNBlocks.COPPER_LASER))
				.save(recipeOutput, getConversionRecipeName(RNBlocks.WAXED_COPPER_LASER, Items.HONEYCOMB));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, RNBlocks.WAXED_EXPOSED_COPPER_LASER)
				.requires(RNBlocks.EXPOSED_COPPER_LASER)
				.requires(Items.HONEYCOMB)
				.group(getItemName(RNBlocks.WAXED_EXPOSED_COPPER_LASER))
				.unlockedBy(getHasName(RNBlocks.EXPOSED_COPPER_LASER), has(RNBlocks.EXPOSED_COPPER_LASER))
				.save(recipeOutput, getConversionRecipeName(RNBlocks.WAXED_EXPOSED_COPPER_LASER, Items.HONEYCOMB));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, RNBlocks.WAXED_WEATHERED_COPPER_LASER)
				.requires(RNBlocks.WEATHERED_COPPER_LASER)
				.requires(Items.HONEYCOMB)
				.group(getItemName(RNBlocks.WAXED_WEATHERED_COPPER_LASER))
				.unlockedBy(getHasName(RNBlocks.WEATHERED_COPPER_LASER), has(RNBlocks.WEATHERED_COPPER_LASER))
				.save(recipeOutput, getConversionRecipeName(RNBlocks.WAXED_WEATHERED_COPPER_LASER, Items.HONEYCOMB));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, RNBlocks.WAXED_OXIDIZED_COPPER_LASER)
				.requires(RNBlocks.OXIDIZED_COPPER_LASER)
				.requires(Items.HONEYCOMB)
				.group(getItemName(RNBlocks.WAXED_OXIDIZED_COPPER_LASER))
				.unlockedBy(getHasName(RNBlocks.OXIDIZED_COPPER_LASER), has(RNBlocks.OXIDIZED_COPPER_LASER))
				.save(recipeOutput, getConversionRecipeName(RNBlocks.WAXED_OXIDIZED_COPPER_LASER, Items.HONEYCOMB));

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
			.pattern("S  ")
			.pattern("SS ")
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

	private void pane(RecipeOutput recipeOutput, ItemLike input, ItemLike wall) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wall, 16)
				.define('P', input)
				.pattern("PPP")
				.pattern("PPP")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
	}

	private void grate(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, count)
				.group(output.toString())
				.define('I', input)
				.pattern(" I ")
				.pattern("I I")
				.pattern(" I ")
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
	}

	private void stonecutterList(RecipeOutput recipeOutput, ItemLike input, ItemLike... outputs) {
		for (ItemLike output : outputs) {
			var count = 1;

			if (output.asItem().toString().contains("slab")) {
				count = 2;
			}

			if (isBronzeBlock(input)) {
				count *= 4;
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

	private boolean isBronzeBlock(ItemLike input) {
		String inputName = input.asItem().toString();
		return inputName.contains("bronze_block") ||
				inputName.contains("discolored_bronze_block") ||
				inputName.contains("corroded_bronze_block") ||
				inputName.contains("tarnished_bronze_block") ||
				inputName.contains("crystallized_bronze_block");
	}

	public static ItemLike waxed(Supplier<? extends Block> block) {
		return BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(block.get())));
	}

	private void wax(RecipeOutput recipeOutput, Supplier<? extends Block> block) {
		ItemLike waxedBlock = waxed(block);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, waxedBlock)
				.requires(block.get())
				.requires(Items.HONEYCOMB)
				.group(getItemName(waxedBlock))
				.unlockedBy(getHasName(block.get()), has(block.get()))
				.save(recipeOutput, getConversionRecipeName(waxedBlock, Items.HONEYCOMB));
	}

	private void generateAllWaxingRecipes(RecipeOutput recipeOutput) {

		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_BLOCK);
		waxAllTarnishVariants(recipeOutput, RNBlocks.CHISELED_BRONZE);
		waxAllTarnishVariants(recipeOutput, RNBlocks.CUT_BRONZE_PILLAR);
		waxAllTarnishVariants(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS);
		waxAllTarnishVariants(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS_STAIRS);
		waxAllTarnishVariants(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS_SLAB);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_BULB);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_CHANDELIER);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_LANTERN);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_LAMP);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_CHAIN);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_LASER);
	}

	private void waxAllTarnishVariants(RecipeOutput recipeOutput, Supplier<? extends Block> baseBlock) {
		String baseName = getFieldNameFromSupplier(baseBlock);

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> tarnishedBlock = getBlockByName(prefix + baseName);
			wax(recipeOutput, tarnishedBlock);
		}
	}



	private void laser(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike laser, boolean isCopper) {
		ItemLike scrapOrIngot = isCopper ? Items.COPPER_INGOT : RNItems.BRONZE_SCRAP;
		ItemLike glowOrQuartz = isCopper ? Items.QUARTZ : Items.GLOWSTONE;

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, laser, 1)
				.define('A', blockInput)
				.define('B', scrapOrIngot)
				.define('C', Items.REDSTONE)
				.define('D', glowOrQuartz)
				.define('E', RNBlocks.RUBY_BLOCK)
				.pattern(" E ")
				.pattern("BDB")
				.pattern("ACA")
				.unlockedBy(getHasName(RNBlocks.RUBY_BLOCK), has(RNBlocks.RUBY_BLOCK))
				.unlockedBy(getHasName(scrapOrIngot), has(scrapOrIngot))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(glowOrQuartz), has(glowOrQuartz))
				.unlockedBy(getHasName(blockInput), has(blockInput))
				.save(recipeOutput);
	}

	private void bulb(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike bulb) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, bulb, 4)
				.define('B', blockInput)
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(blockInput), has(blockInput))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);
	}

	private void lamp(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike lamp) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, lamp, 3)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('A', blockInput)
				.define('O', RNBlocks.MOLTEN_RUBY_BLOCK)
				.define('I', RNBlocks.RUBY_GLASS_PANE)
				.pattern("XAX")
				.pattern("IOI")
				.pattern("XAX")
				.unlockedBy(getHasName(RNBlocks.RUBY_GLASS_PANE), has(RNBlocks.RUBY_GLASS_PANE))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY_ITEM), has(RNItems.MOLTEN_RUBY_ITEM))
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy(getHasName(blockInput), has(blockInput))
				.save(recipeOutput);
	}

	private void chandelier(RecipeOutput recipeOutput, ItemLike lanternInput, ItemLike grateInput, ItemLike chandelier) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, chandelier, 1)
				.define('X', lanternInput)
				.define('O', grateInput)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(lanternInput), has(lanternInput))
				.unlockedBy(getHasName(grateInput), has(grateInput))
				.save(recipeOutput);
	}
	private static final String[] TARNISH_PREFIXES = {
			"",
			"DISCOLORED_",
			"CORRODED_",
			"TARNISHED_",
			"CRYSTALLIZED_"
	};

	@FunctionalInterface
	private interface RecipeGenerator {
		void generate(RecipeOutput output, ItemLike input, ItemLike result);
	}

	@FunctionalInterface
	private interface RecipeGeneratorWithExtra {
		void generate(RecipeOutput output, ItemLike input1, ItemLike input2, ItemLike result);
	}

	private void tarnish(RecipeOutput recipeOutput, RecipeGenerator generator,
						 ItemLike baseInput, ItemLike baseResult) {
		String inputName = getFieldName(baseInput);
		String resultName = getFieldName(baseResult);

		for (String prefix : TARNISH_PREFIXES) {
			ItemLike input = getBlockByName(prefix + inputName);
			ItemLike result = getBlockByName(prefix + resultName);
			generator.generate(recipeOutput, input, result);
		}

		if (baseInput instanceof Supplier) {
			tarnishWaxed(recipeOutput, generator, (Supplier<? extends Block>) baseInput, (Supplier<? extends Block>) baseResult);
		}
	}

	private void tarnish(RecipeOutput recipeOutput, RecipeGeneratorWithExtra generator,
						 ItemLike baseInput1, ItemLike baseInput2, ItemLike baseResult) {
		String input1Name = getFieldName(baseInput1);
		String input2Name = getFieldName(baseInput2);
		String resultName = getFieldName(baseResult);

		for (String prefix : TARNISH_PREFIXES) {
			ItemLike input1 = getBlockByName(prefix + input1Name);
			ItemLike input2 = getBlockByName(prefix + input2Name);
			ItemLike result = getBlockByName(prefix + resultName);
			generator.generate(recipeOutput, input1, input2, result);
		}

		if (baseInput1 instanceof Supplier && baseInput2 instanceof Supplier && baseResult instanceof Supplier) {
			tarnishWaxed(recipeOutput, generator,
					(Supplier<? extends Block>) baseInput1,
					(Supplier<? extends Block>) baseInput2,
					(Supplier<? extends Block>) baseResult);
		}
	}

	private String getFieldName(ItemLike itemLike) {
		if (itemLike instanceof net.minecraft.world.item.Item ||
				!(itemLike instanceof Supplier)) {
			String registryName = BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
			return registryName
					.replace("waxed_", "")
					.toUpperCase()
					.replace("rubinated_nether:", "");
		}

		for (var field : RNBlocks.class.getFields()) {
			try {
				if (field.get(null) == itemLike) {
					return field.getName();
				}
			} catch (Exception ignored) {}
		}

		for (var field : RNItems.class.getFields()) {
			try {
				if (field.get(null) == itemLike) {
					return field.getName();
				}
			} catch (Exception ignored) {}
		}

		throw new RuntimeException("Could not find field name for: " + itemLike);
	}

	@SuppressWarnings("unchecked")
	private <T extends ItemLike> T getBlockByName(String name) {
		try {
			var field = RNBlocks.class.getField(name);
			return (T) field.get(null);
		} catch (NoSuchFieldException e) {
			try {
				var field = RNItems.class.getField(name);
				return (T) field.get(null);
			} catch (Exception ex) {
				throw new RuntimeException("Could not find block or item: " + name, ex);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error accessing field: " + name, e);
		}
	}

	private void tarnishWaxed(RecipeOutput recipeOutput, RecipeGenerator generator,
							  Supplier<? extends Block> baseInput, Supplier<? extends Block> baseResult) {
		String inputName = getFieldNameFromSupplier(baseInput);
		String resultName = getFieldNameFromSupplier(baseResult);

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> input = getBlockByName(prefix + inputName);
			Supplier<? extends Block> result = getBlockByName(prefix + resultName);
			ItemLike waxedInput = waxed(input);
			ItemLike waxedResult = waxed(result);
			generator.generate(recipeOutput, waxedInput, waxedResult);
		}
	}

	private void tarnishWaxed(RecipeOutput recipeOutput, RecipeGeneratorWithExtra generator,
							  Supplier<? extends Block> baseInput1, Supplier<? extends Block> baseInput2,
							  Supplier<? extends Block> baseResult) {
		String input1Name = getFieldNameFromSupplier(baseInput1);
		String input2Name = getFieldNameFromSupplier(baseInput2);
		String resultName = getFieldNameFromSupplier(baseResult);

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> input1 = getBlockByName(prefix + input1Name);
			Supplier<? extends Block> input2 = getBlockByName(prefix + input2Name);
			Supplier<? extends Block> result = getBlockByName(prefix + resultName);
			ItemLike waxedInput1 = waxed(input1);
			ItemLike waxedInput2 = waxed(input2);
			ItemLike waxedResult = waxed(result);
			generator.generate(recipeOutput, waxedInput1, waxedInput2, waxedResult);
		}
	}

	private String getFieldNameFromSupplier(Supplier<? extends Block> supplier) {
		for (var field : RNBlocks.class.getFields()) {
			try {
				if (field.get(null) == supplier) {
					return field.getName();
				}
			} catch (Exception ignored) {}
		}
		throw new RuntimeException("Could not find field name for supplier");
	}

	@SafeVarargs
	private final void Bronzecutter(RecipeOutput recipeOutput,
										  Supplier<? extends Block> baseInput,
										  Supplier<? extends Block>... baseOutputs) {
		String inputName = getFieldNameFromSupplier(baseInput);

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> input = getBlockByName(prefix + inputName);

			ItemLike[] outputs = new ItemLike[baseOutputs.length];
			for (int i = 0; i < baseOutputs.length; i++) {
				String outputName = getFieldNameFromSupplier(baseOutputs[i]);
				outputs[i] = getBlockByName(prefix + outputName);
			}

			stonecutterListArray(recipeOutput, (ItemLike) input, outputs);
		}

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> input = getBlockByName(prefix + inputName);
			ItemLike waxedInput = waxed(input);

			ItemLike[] waxedOutputs = new ItemLike[baseOutputs.length];
			for (int i = 0; i < baseOutputs.length; i++) {
				String outputName = getFieldNameFromSupplier(baseOutputs[i]);
				Supplier<? extends Block> output = getBlockByName(prefix + outputName);
				waxedOutputs[i] = waxed(output);
			}

			stonecutterListArray(recipeOutput, waxedInput, waxedOutputs);
		}
	}

	private void stonecutterListArray(RecipeOutput recipeOutput, ItemLike input, ItemLike[] outputs) {
		for (ItemLike output : outputs) {
			var count = 1;

			if (output.asItem().toString().contains("slab")) {
				count = 2;
			}

			if (isBronzeBlock(input)) {
				count *= 4;
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
