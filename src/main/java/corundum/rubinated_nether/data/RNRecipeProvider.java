package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.items.PocketwatchData;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.content.recipe.BaptismRecipe;
import corundum.rubinated_nether.content.recipe.CrackingRecipe;
import corundum.rubinated_nether.content.recipe.DerubinationRecipe;
import corundum.rubinated_nether.content.recipe.PetrificationRecipe;
import corundum.rubinated_nether.content.recipe.ResonanceRecipe;
import corundum.rubinated_nether.content.recipe.RuneCarvingRecipeProvider;
import corundum.rubinated_nether.content.trim.RNBronzeTrimTarnishRecipe;
import corundum.rubinated_nether.content.trim.RNTrimMaterials;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.minecraft.data.recipes.SimpleCookingRecipeBuilder.blasting;
import static net.minecraft.data.recipes.SimpleCookingRecipeBuilder.smelting;


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

			RNBlocks.COBBLED_SHRINE_STONE,
			RNBlocks.COBBLED_SHRINE_STONE_SLAB,
			RNBlocks.COBBLED_SHRINE_STONE_STAIRS,
			RNBlocks.COBBLED_SHRINE_STONE_WALL,

			RNBlocks.SHRINE_STONE_RUBBLE,
			RNBlocks.SHRINE_STONE_RUBBLE_SLAB,
			RNBlocks.SHRINE_STONE_RUBBLE_STAIRS,
			RNBlocks.SHRINE_STONE_RUBBLE_WALL,

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
				RNBlocks.COBBLED_SHRINE_STONE,

				RNBlocks.COBBLED_SHRINE_STONE_SLAB,
				RNBlocks.COBBLED_SHRINE_STONE_STAIRS,
				RNBlocks.COBBLED_SHRINE_STONE_WALL,

				RNBlocks.SHRINE_STONE_RUBBLE,
				RNBlocks.SHRINE_STONE_RUBBLE_SLAB,
				RNBlocks.SHRINE_STONE_RUBBLE_STAIRS,
				RNBlocks.SHRINE_STONE_RUBBLE_WALL,

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

		RuneCarvingRecipeProvider.registerBlankRune(recipeOutput);

		baptism(recipeOutput, Ingredient.of(RNTags.Items.OFFERING_BRAZIER_ITEM), RNItems.RITUAL_OFFERING);
		baptism(recipeOutput, Ingredient.of(RNItems.POMEGRANATE), RNItems.RUBINATED_POMEGRANATE);
		for (Rubination rubination : Rubination.carvableValues()) {
			baptism(recipeOutput,
					DataComponentIngredient.of(false, RNDataComponents.RUNE_CARVING, rubination, RNItems.RUNE.get()),
					RNItems.getRuneFor(rubination));
		}

		Bronzecutter(recipeOutput, RNBlocks.BRONZE_BLOCK,
				RNBlocks.CHISELED_BRONZE,
				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CUT_BRONZE_PILLAR,
				RNBlocks.BRONZE_GRATE,
				RNBlocks.BRONZE_BUTTON
		);

		Bronzecutter(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CHISELED_BRONZE,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB
		);

		tarnish(recipeOutput, this::lamp, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_LAMP);
		tarnish(recipeOutput, this::bulb, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_BULB);
		tarnish(recipeOutput, this::chandelier, RNBlocks.BRONZE_LANTERN, RNBlocks.BRONZE_GRATE, RNBlocks.BRONZE_CHANDELIER);

		tarnish(recipeOutput, this::laser, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_LASER);

		tarnish(recipeOutput, (output, input, result) -> grate(output, input, result, 4),
				RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_GRATE);

		tarnish(recipeOutput, this::stairsAndSlab, RNBlocks.CUT_BRONZE_BRICKS, RNBlocks.CUT_BRONZE_BRICKS_STAIRS, RNBlocks.CUT_BRONZE_BRICKS_SLAB);
		tarnish(recipeOutput, (output, input, result) -> twoByTwo(output, input, result, 4),
				RNBlocks.BRONZE_BLOCK, RNBlocks.CUT_BRONZE_BRICKS);
		tarnish(recipeOutput, (output, input, result) -> oneByTwo(output, input, result, 1),
				RNBlocks.CUT_BRONZE_BRICKS_SLAB, RNBlocks.CHISELED_BRONZE);
		tarnish(recipeOutput, (output, input, result) -> oneByTwo(output, input, result, 2),
				RNBlocks.BRONZE_BLOCK, RNBlocks.CUT_BRONZE_PILLAR);

		winder(recipeOutput);
		petrified(recipeOutput);
		petrification(recipeOutput);
		blockSwaps(recipeOutput);
		morion(recipeOutput);

		tarnish(recipeOutput, this::spring, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_SPRING);
		tarnish(recipeOutput, this::vent, RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_VENT);
		tarnish(recipeOutput, (output, input, result) -> button(output, input, result, 4),
				RNBlocks.BRONZE_BLOCK, RNBlocks.BRONZE_BUTTON);


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
				RNBlocks.COBBLED_SHRINE_STONE,
				RNBlocks.COBBLED_SHRINE_STONE_STAIRS,
				RNBlocks.COBBLED_SHRINE_STONE_SLAB
		);

		wall(
				recipeOutput,
				RNBlocks.COBBLED_SHRINE_STONE,
				RNBlocks.COBBLED_SHRINE_STONE_WALL
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

		stairsAndSlab(
			recipeOutput,
			RNBlocks.SHRINE_STONE_RUBBLE,
			RNBlocks.SHRINE_STONE_RUBBLE_STAIRS,
			RNBlocks.SHRINE_STONE_RUBBLE_SLAB
		);

		wall(
			recipeOutput,
			RNBlocks.SHRINE_STONE_RUBBLE,
			RNBlocks.SHRINE_STONE_RUBBLE_WALL
		);

		stonecutterList(
			recipeOutput,
			RNBlocks.SHRINE_STONE_RUBBLE,

			RNBlocks.SHRINE_STONE_RUBBLE_SLAB,
			RNBlocks.SHRINE_STONE_RUBBLE_STAIRS,
			RNBlocks.SHRINE_STONE_RUBBLE_WALL,

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

		twoByTwo(
			recipeOutput,
			RNBlocks.COBBLED_SHRINE_STONE,
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
			RNItems.RUBY_SHARD,
			RNItems.RUBY,
			1
		);

		threeByThree(
			recipeOutput,
			RNItems.MOLTEN_RUBY_NUGGET,
			RNItems.MOLTEN_RUBY,
			1
		);

		threeByThree(
			recipeOutput,
			RNItems.MOLTEN_RUBY,
			RNBlocks.MOLTEN_RUBY_BLOCK,
			1
		);

		threeByThree(
			recipeOutput,
			RNItems.RUBY,
			RNBlocks.RUBY_BLOCK,
			1
		);

		one(
			recipeOutput,
			RNBlocks.RUBY_BLOCK,
			RNItems.RUBY,
			9
		);

		one(
			recipeOutput,
			RNBlocks.MOLTEN_RUBY_BLOCK,
			RNItems.MOLTEN_RUBY,
			9
		);

		one(
			recipeOutput,
			RNItems.RUBY,
			RNItems.RUBY_SHARD,
			9
		);

		one(
			recipeOutput,
			RNItems.MOLTEN_RUBY,
			RNItems.MOLTEN_RUBY_NUGGET,
			9
		);

		twoByTwo(
				recipeOutput,
				RNItems.BRONZE_SCRAP,
				RNItems.BRONZE_SHOT,
				8
		);

		twoByTwo(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT,
				RNItems.CRYSTALLIZED_BRONZE_SHOT,
				8
		);

		threeByThree(
				recipeOutput,
				RNItems.BRONZE_SCRAP,
				RNBlocks.BRONZE_BLOCK,
				1
		);

		threeByThree(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
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


		generateAllWaxingRecipes(recipeOutput);

		gearboxAndStrongboxCrafting(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RNItems.COGS_BANNER_PATTERN.get())
				.requires(Items.PAPER)
				.requires(RNItems.COGWHEEL)
				.unlockedBy(getHasName(RNItems.COGWHEEL), has(RNItems.COGWHEEL))
				.save(recipeOutput);

		waxRecipes(recipeOutput, FeatureFlagSet.of(FeatureFlags.VANILLA));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBY_GLASS, 8)
				.define('X', Blocks.GLASS)
				.define('O', RNItems.RUBY)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNBlocks.RUBY_GLASS), has(RNBlocks.RUBY_GLASS))
				.unlockedBy(getHasName(RNItems.RUBY), has(RNItems.RUBY))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.MOLTEN_RUBY_GLASS, 8)
				.define('X', Blocks.GLASS)
				.define('O', RNItems.MOLTEN_RUBY)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(Blocks.GLASS), has(Blocks.GLASS))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY), has(RNItems.MOLTEN_RUBY))
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


		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.BRONZE_LANTERN, 1)
				.define('X', RNItems.BRONZE_POWDER)
				.define('O', RNItems.MOLTEN_RUBY)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNItems.BRONZE_POWDER), has(RNItems.BRONZE_POWDER))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY), has(RNItems.MOLTEN_RUBY))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.CRYSTALLIZED_BRONZE_LANTERN, 1)
				.define('X', RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT)
				.define('O', RNItems.MOLTEN_RUBY)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT), has(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY), has(RNItems.MOLTEN_RUBY))
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

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.CRYSTALLIZED_BRONZE_CHAIN, 1)
				.define('X', RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT)
				.define('O', RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT)
				.pattern(" X ")
				.pattern(" O ")
				.pattern(" X ")
				.unlockedBy(getHasName(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT), has(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT))
				.unlockedBy(getHasName(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT), has(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT))
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
				.define('B', RNItems.BRONZE_ROD)
				.define('C', RNBlocks.MOLTEN_RUBY_BLOCK)
				.define('D', Items.NETHERITE_INGOT)
				.pattern("BCB")
				.pattern("BDB")
				.pattern("AAA")
				.unlockedBy(getHasName(RNBlocks.MOLTEN_RUBY_BLOCK), has(RNBlocks.MOLTEN_RUBY_BLOCK))
				.unlockedBy(getHasName(RNItems.BRONZE_ROD), has(RNItems.BRONZE_ROD))
				.unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
				.unlockedBy(getHasName(Blocks.OBSIDIAN), has(Blocks.OBSIDIAN))
				.save(recipeOutput);

		trimSmithing(recipeOutput, RNItems.GREED_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "greed_rune"));
		trimSmithing(recipeOutput, RNItems.GLUTTONY_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "gluttony_rune"));
		trimSmithing(recipeOutput, RNItems.SLOTH_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "sloth_rune"));
		trimSmithing(recipeOutput, RNItems.WRATH_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "wrath_rune"));
		trimSmithing(recipeOutput, RNItems.ENVY_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "envy_rune"));
		trimSmithing(recipeOutput, RNItems.VAINGLORY_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "vainglory_rune"));
		trimSmithing(recipeOutput, RNItems.PRIDE_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "pride_rune"));
		trimSmithing(recipeOutput, RNItems.ACEDIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "acedia_rune"));
		trimSmithing(recipeOutput, RNItems.LUXURIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "luxuria_rune"));
		trimSmithing(recipeOutput, RNItems.INSIDIAE_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "insidiae_rune"));
		trimSmithing(recipeOutput, RNItems.SUPERBIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "superbia_rune"));
		trimSmithing(recipeOutput, RNItems.TRISTIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "tristia_rune"));
		trimSmithing(recipeOutput, RNItems.STUDIOSE_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "studiose_rune"));
		trimSmithing(recipeOutput, RNItems.ARDENTER_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "ardenter_rune"));
		trimSmithing(recipeOutput, RNItems.NIMIS_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "nimis_rune"));
		trimSmithing(recipeOutput, RNItems.IRA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "ira_rune"));
		trimSmithing(recipeOutput, RNItems.INVIDIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "invidia_rune"));
		trimSmithing(recipeOutput, RNItems.GULA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "gula_rune"));
		trimSmithing(recipeOutput, RNItems.IGNAVIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "ignavia_rune"));
		trimSmithing(recipeOutput, RNItems.KENODOXIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "kenodoxia_rune"));
		trimSmithing(recipeOutput, RNItems.PHILARGYRIA_RUNE.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "philargyria_rune"));

		trimSmithing(recipeOutput, RNItems.BRONZE_SCRAP.get(), ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "bronze_scrap_trim"));
		bronzeTrimTarnish(recipeOutput, RNTrimMaterials.BRONZE,            RNTrimMaterials.DISCOLORED_BRONZE, "bronze_to_discolored_trim");
		bronzeTrimTarnish(recipeOutput, RNTrimMaterials.DISCOLORED_BRONZE, RNTrimMaterials.CORRODED_BRONZE,   "discolored_to_corroded_trim");
		bronzeTrimTarnish(recipeOutput, RNTrimMaterials.CORRODED_BRONZE,   RNTrimMaterials.TARNISHED_BRONZE,  "corroded_to_tarnished_trim");
		trimSmithing(recipeOutput, RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get().asItem(),
				ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "crystallized_bronze_trim"));

		freezing(recipeOutput, Blocks.MAGMA_BLOCK,			RNBlocks.PUMICE,       400,  1.0f);
		freezing(recipeOutput, Blocks.POWDER_SNOW,			Blocks.SNOW_BLOCK,        600,  1.0f);
		freezing(recipeOutput, Items.MAGMA_CREAM,			Items.SLIME_BALL,         400,  1.0f);
		freezing(recipeOutput, RNItems.MOLTEN_RUBY,			RNItems.RUBY,             400,  1.0f);
		freezing(recipeOutput, Items.POWDER_SNOW_BUCKET,	Blocks.POWDER_SNOW,     300,  0.0f);
		freezing(recipeOutput, Blocks.ICE,					Blocks.PACKED_ICE,        4800, 1.0f);
		freezing(recipeOutput, Items.LAVA_BUCKET,			Blocks.OBSIDIAN,          400,  1.0f);
		freezing(recipeOutput, Blocks.FROSTED_ICE,			Blocks.ICE,               2400, 1.0f);
		freezing(recipeOutput, Items.WATER_BUCKET,			Blocks.FROSTED_ICE,     4800, 1.0f);
		freezing(recipeOutput, Blocks.SNOW_BLOCK,			Blocks.FROSTED_ICE,     1200, 1.0f);
		freezing(recipeOutput, Blocks.BLUE_ICE,				RNBlocks.DRY_ICE,         19200,1.0f);
		freezing(recipeOutput, RNItems.BRONZE_ROD,			Items.BREEZE_ROD,         400,  1.0f);
		freezing(recipeOutput, Blocks.PACKED_ICE,			Blocks.BLUE_ICE,          9600, 1.0f);
		freezing(recipeOutput, RNBlocks.COBBLED_SHRINE_STONE, RNBlocks.SHRINE_STONE, 400, 1.0f);
		freezing(recipeOutput, RNBlocks.SHRINE_STONE_RUBBLE, RNBlocks.COBBLED_SHRINE_STONE, 400, 1.0f);

		smelting(recipeOutput, RNItems.BRONZE_ROD, Items.BLAZE_ROD,   0.7f, 200);
		blasting(recipeOutput, RNItems.BRONZE_ROD, Items.BLAZE_ROD,   0.7f, 100);
		smelting(recipeOutput, RNItems.RUBY,       RNItems.MOLTEN_RUBY, 0.7f, 200);
		blasting(recipeOutput, RNItems.RUBY,       RNItems.MOLTEN_RUBY, 0.7f, 100);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE,
				Ingredient.of(RNTags.Items.SHRINE_STONE_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_STAIRS,
				Ingredient.of(RNTags.Items.SHRINE_STONE_STAIRS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_SLAB,
				Ingredient.of(RNTags.Items.SHRINE_STONE_SLAB_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_WALL,
				Ingredient.of(RNTags.Items.SHRINE_STONE_WALL_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.COBBLED_SHRINE_STONE,
				Ingredient.of(RNTags.Items.COBBLED_SHRINE_STONE_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.COBBLED_SHRINE_STONE_STAIRS,
				Ingredient.of(RNTags.Items.COBBLED_SHRINE_STONE_STAIRS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.COBBLED_SHRINE_STONE_SLAB,
				Ingredient.of(RNTags.Items.COBBLED_SHRINE_STONE_SLAB_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.COBBLED_SHRINE_STONE_WALL,
				Ingredient.of(RNTags.Items.COBBLED_SHRINE_STONE_WALL_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.POLISHED_SHRINE_STONE,
				Ingredient.of(RNTags.Items.POLISHED_SHRINE_STONE_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.POLISHED_SHRINE_STONE_STAIRS,
				Ingredient.of(RNTags.Items.POLISHED_SHRINE_STONE_STAIRS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.POLISHED_SHRINE_STONE_SLAB,
				Ingredient.of(RNTags.Items.POLISHED_SHRINE_STONE_SLAB_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.POLISHED_SHRINE_STONE_WALL,
				Ingredient.of(RNTags.Items.POLISHED_SHRINE_STONE_WALL_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_PILLAR,
				Ingredient.of(RNTags.Items.SHRINE_STONE_PILLAR_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_BRICKS,
				Ingredient.of(RNTags.Items.SHRINE_STONE_BRICKS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
				Ingredient.of(RNTags.Items.SHRINE_STONE_BRICKS_STAIRS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_BRICKS_SLAB,
				Ingredient.of(RNTags.Items.SHRINE_STONE_BRICKS_SLAB_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_BRICKS_WALL,
				Ingredient.of(RNTags.Items.SHRINE_STONE_BRICKS_WALL_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.CHISELED_SHRINE_STONE_BRICKS,
				Ingredient.of(RNTags.Items.CHISELED_SHRINE_STONE_BRICKS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_TILES,
				Ingredient.of(RNTags.Items.SHRINE_STONE_TILES_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_TILES_STAIRS,
				Ingredient.of(RNTags.Items.SHRINE_STONE_TILES_STAIRS_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_TILES_SLAB,
				Ingredient.of(RNTags.Items.SHRINE_STONE_TILES_SLAB_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_TILES_WALL,
				Ingredient.of(RNTags.Items.SHRINE_STONE_TILES_WALL_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);


		resonance(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_BRICKS,
				Ingredient.of(RNBlocks.SHRINE_STONE_BRICKS),
				new ResonanceRecipe.TriggerData(20, 150),
				new ResonanceRecipe.TriggerData(20, 300), true);

		resonance(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_TILES,
				Ingredient.of(RNBlocks.SHRINE_STONE_TILES),
				new ResonanceRecipe.TriggerData(20, 150),
				new ResonanceRecipe.TriggerData(20, 300), true);

		resonance(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_PILLAR,
				Ingredient.of(RNBlocks.SHRINE_STONE_PILLAR),
				new ResonanceRecipe.TriggerData(20, 150),
				new ResonanceRecipe.TriggerData(20, 300), true);

		resonance(recipeOutput, RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS,
				Ingredient.of(RNBlocks.CHISELED_SHRINE_STONE_BRICKS),
				new ResonanceRecipe.TriggerData(20, 150),
				new ResonanceRecipe.TriggerData(20, 300), true);

		resonance(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_COFFER,
				Ingredient.of(RNBlocks.SHRINE_STONE_COFFER),
				new ResonanceRecipe.TriggerData(20, 150),
				new ResonanceRecipe.TriggerData(20, 300), true);

		resonance(recipeOutput, RNBlocks.SHRINE_STONE_RUBBLE,
				Ingredient.of(RNTags.Items.SHRINE_RUBBLE_CANDIDATE),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);

		resonance(recipeOutput, RNBlocks.SUSPICIOUS_SHRINE_STONE_RUBBLE,
				Ingredient.of(Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL),
				new ResonanceRecipe.TriggerData(20, 2500),
				new ResonanceRecipe.TriggerData(20, 5000), false);


		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RNItems.POCKETWATCH.get())
				.requires(DataComponentIngredient.of(false, RNDataComponents.POCKETWATCH_DATA.get(), PocketwatchData.BROKEN, RNItems.POCKETWATCH.get()))
				.requires(RNBlocks.BRONZE_GEARBOX.get())
				.unlockedBy("has_broken_pocketwatch", has(RNItems.POCKETWATCH.get()))
				.save(recipeOutput, RubinatedNether.MODID + ":pocketwatch_repair");

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RNItems.BRONZE_DRILL, 1)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('G', RNTags.Items.GEARBOXES)
				.define('N', Items.NETHERITE_INGOT)
				.pattern(" X ")
				.pattern("XGX")
				.pattern(" N ")
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy("has_gearbox", has(RNTags.Items.GEARBOXES))
				.unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RNItems.BRONZE_CHAINSAW, 1)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('G', RNTags.Items.GEARBOXES)
				.define('N', Items.NETHERITE_INGOT)
				.pattern("X X")
				.pattern("XGX")
				.pattern(" N ")
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy("has_gearbox", has(RNTags.Items.GEARBOXES))
				.unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RNItems.BRONZE_JACKHAMMER, 1)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('G', RNTags.Items.GEARBOXES)
				.define('N', Items.NETHERITE_INGOT)
				.pattern("XXX")
				.pattern(" G ")
				.pattern(" N ")
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy("has_gearbox", has(RNTags.Items.GEARBOXES))
				.unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RNItems.POCKETWATCH, 1)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('G', RNTags.Items.GEARBOXES)
				.define('R', RNItems.RUBY)
				.pattern("XXX")
				.pattern("XGX")
				.pattern("XRX")
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy("has_gearbox", has(RNTags.Items.GEARBOXES))
				.unlockedBy("has_ruby", has(RNItems.RUBY))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RNItems.MUSIC_BOX, 1)
				.define('X', RNItems.BRONZE_SCRAP)
				.define('G', RNTags.Items.GEARBOXES)
				.define('D', Items.DIAMOND)
				.pattern("XXX")
				.pattern("XGX")
				.pattern("XDX")
				.unlockedBy(getHasName(RNItems.BRONZE_SCRAP), has(RNItems.BRONZE_SCRAP))
				.unlockedBy("has_gearbox", has(RNTags.Items.GEARBOXES))
				.unlockedBy("has_diamond", has(Items.DIAMOND))
				.save(recipeOutput);

	}

	private void morion(RecipeOutput recipeOutput) {
		ItemLike morion = RNItems.NETHER_MORION;
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.MORION_BLOCK)
				.define('M', morion)
				.pattern("MM")
				.pattern("MM")
				.unlockedBy(getHasName(morion), has(morion))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.CHISELED_MORION_BLOCK)
				.define('S', RNBlocks.MORION_SLAB)
				.pattern("S")
				.pattern("S")
				.unlockedBy(getHasName(RNBlocks.MORION_SLAB), has(RNBlocks.MORION_SLAB))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.MORION_PILLAR, 2)
				.define('B', RNBlocks.MORION_BLOCK)
				.pattern("B")
				.pattern("B")
				.unlockedBy(getHasName(RNBlocks.MORION_BLOCK), has(RNBlocks.MORION_BLOCK))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.MORION_BRICKS, 4)
				.define('B', RNBlocks.MORION_BLOCK)
				.pattern("BB")
				.pattern("BB")
				.unlockedBy(getHasName(RNBlocks.MORION_BLOCK), has(RNBlocks.MORION_BLOCK))
				.save(recipeOutput);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RNBlocks.MORION_BLOCK), RecipeCategory.BUILDING_BLOCKS, RNBlocks.SMOOTH_MORION, 0.1F, 200)
				.unlockedBy(getHasName(RNBlocks.MORION_BLOCK), has(RNBlocks.MORION_BLOCK))
				.save(recipeOutput);
		stairsAndSlab(recipeOutput, RNBlocks.MORION_BLOCK, RNBlocks.MORION_STAIRS, RNBlocks.MORION_SLAB);
		stairsAndSlab(recipeOutput, RNBlocks.SMOOTH_MORION, RNBlocks.SMOOTH_MORION_STAIRS, RNBlocks.SMOOTH_MORION_SLAB);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.TILED_MORION, 4)
				.define('B', RNBlocks.MORION_BRICKS)
				.pattern("BB")
				.pattern("BB")
				.unlockedBy(getHasName(RNBlocks.MORION_BRICKS), has(RNBlocks.MORION_BRICKS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.TILED_QUARTZ, 4)
				.define('B', Blocks.QUARTZ_BRICKS)
				.pattern("BB")
				.pattern("BB")
				.unlockedBy(getHasName(Blocks.QUARTZ_BRICKS), has(Blocks.QUARTZ_BRICKS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.CHECKERED_TILES, 4)
				.define('Q', RNBlocks.TILED_QUARTZ)
				.define('M', RNBlocks.TILED_MORION)
				.pattern("QM")
				.pattern("MQ")
				.unlockedBy(getHasName(RNBlocks.TILED_MORION), has(RNBlocks.TILED_MORION))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.REDSTONE_ACCUMULATOR)
				.define('T', Blocks.REDSTONE_TORCH)
				.define('M', morion)
				.define('S', Blocks.STONE)
				.pattern(" T ")
				.pattern("TMT")
				.pattern("SSS")
				.unlockedBy(getHasName(morion), has(morion))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.MOONPHASE_DETECTOR)
				.define('G', Blocks.GLASS)
				.define('M', morion)
				.define('W', ItemTags.WOODEN_SLABS)
				.pattern("GGG")
				.pattern("MMM")
				.pattern("WWW")
				.unlockedBy(getHasName(morion), has(morion))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.TINTED_GLASS, 2)
				.define('M', morion)
				.define('G', Blocks.GLASS)
				.pattern(" M ")
				.pattern("MGM")
				.pattern(" M ")
				.unlockedBy(getHasName(morion), has(morion))
				.save(recipeOutput, RubinatedNether.id("tinted_glass_from_morion"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE)
				.requires(Ingredient.of(Blocks.COBBLESTONE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.GRANITE))
				.requires(morion)
				.unlockedBy(getHasName(morion), has(morion))
				.save(recipeOutput, RubinatedNether.id("blackstone_from_morion"));
	}

	private void petrified(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, RNBlocks.PETRIFIED_FLAGS, 4)
				.requires(RNTags.Items.PETRIFIED_SNAGS)
				.group("petrified_flags")
				.unlockedBy("has_petrified_snags", has(RNTags.Items.PETRIFIED_SNAGS))
				.save(recipeOutput);

		petrifiedRind(recipeOutput, RNBlocks.PETRIFIED_SNAG, RNBlocks.PETRIFIED_RIND);
		petrifiedRind(recipeOutput, RNBlocks.STRIPPED_PETRIFIED_SNAG, RNBlocks.STRIPPED_PETRIFIED_RIND);

		stairsAndSlab(
				recipeOutput,
				RNBlocks.PETRIFIED_FLAGS,
				RNBlocks.PETRIFIED_STAIRS,
				RNBlocks.PETRIFIED_SLAB
		);
		wall(recipeOutput, RNBlocks.PETRIFIED_FLAGS, RNBlocks.PETRIFIED_WALL);

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, RNBlocks.PETRIFIED_FENCE, 3)
				.define('F', RNBlocks.PETRIFIED_FLAGS)
				.define('S', Items.STICK)
				.pattern("FSF")
				.pattern("FSF")
				.group("petrified_fence")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.PETRIFIED_FENCE_GATE)
				.define('F', RNBlocks.PETRIFIED_FLAGS)
				.define('S', Items.STICK)
				.pattern("SFS")
				.pattern("SFS")
				.group("petrified_fence_gate")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.PETRIFIED_DOOR, 3)
				.define('F', RNBlocks.PETRIFIED_FLAGS)
				.pattern("FF")
				.pattern("FF")
				.pattern("FF")
				.group("petrified_door")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.PETRIFIED_TRAPDOOR, 2)
				.define('F', RNBlocks.PETRIFIED_FLAGS)
				.pattern("FFF")
				.pattern("FFF")
				.group("petrified_trapdoor")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.PETRIFIED_PRESSURE_PLATE)
				.define('F', RNBlocks.PETRIFIED_FLAGS)
				.pattern("FF")
				.group("petrified_pressure_plate")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		button(recipeOutput, RNBlocks.PETRIFIED_FLAGS, RNBlocks.PETRIFIED_BUTTON, 1);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, RNBlocks.PETRIFIED_SIGN_ITEM, 3)
				.define('#', RNBlocks.PETRIFIED_FLAGS)
				.define('X', Items.STICK)
				.pattern("###")
				.pattern("###")
				.pattern(" X ")
				.group("petrified_sign")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, RNBlocks.PETRIFIED_HANGING_SIGN_ITEM, 6)
				.define('#', RNBlocks.STRIPPED_PETRIFIED_SNAG)
				.define('X', Items.CHAIN)
				.pattern("X X")
				.pattern("###")
				.pattern("###")
				.group("petrified_hanging_sign")
				.unlockedBy(getHasName(RNBlocks.STRIPPED_PETRIFIED_SNAG), has(RNBlocks.STRIPPED_PETRIFIED_SNAG))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, RNItems.PETRIFIED_BOAT)
				.define('#', RNBlocks.PETRIFIED_FLAGS)
				.pattern("# #")
				.pattern("###")
				.group("petrified_boat")
				.unlockedBy(getHasName(RNBlocks.PETRIFIED_FLAGS), has(RNBlocks.PETRIFIED_FLAGS))
				.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, RNItems.PETRIFIED_CHEST_BOAT)
				.requires(RNItems.PETRIFIED_BOAT)
				.requires(Items.CHEST)
				.group("petrified_chest_boat")
				.unlockedBy(getHasName(RNItems.PETRIFIED_BOAT), has(RNItems.PETRIFIED_BOAT))
				.save(recipeOutput);
	}

	private void petrifiedRind(RecipeOutput recipeOutput, ItemLike snag, ItemLike rind) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, rind, 3)
				.define('S', snag)
				.pattern("SS")
				.pattern("SS")
				.group("petrified_rind")
				.unlockedBy(getHasName(snag), has(snag))
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

	private void button(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int count) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, output, count)
				.requires(input)
				.group(getItemName(output))
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput);
	}

	private void spring(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike spring) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, spring, 6)
				.define('X', RNItems.BRONZE_ROD)
				.define('O', blockInput)
				.pattern(" X ")
				.pattern(" O ")
				.pattern(" X ")
				.unlockedBy(getHasName(RNItems.BRONZE_ROD), has(RNItems.BRONZE_ROD))
				.unlockedBy(getHasName(blockInput), has(blockInput))
				.save(recipeOutput);
	}

	private void vent(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike vent) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, vent, 1)
				.define('X', RNItems.BRONZE_ROD)
				.define('O', blockInput)
				.pattern("OXO")
				.pattern("XXX")
				.pattern("OXO")
				.unlockedBy(getHasName(RNItems.BRONZE_ROD), has(RNItems.BRONZE_ROD))
				.unlockedBy(getHasName(blockInput), has(blockInput))
				.save(recipeOutput);
	}

	private void gearboxAndStrongboxCrafting(RecipeOutput recipeOutput) {
		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> gearbox = getBlockByName(prefix + "BRONZE_GEARBOX");
			Supplier<? extends Block> chiseled = getBlockByName(prefix + "CHISELED_BRONZE");
			Supplier<? extends Block> bronzeBlock = getBlockByName(prefix + "BRONZE_BLOCK");
			Supplier<? extends Block> strongbox = getBlockByName(prefix + "BRONZE_STRONGBOX");
			Supplier<? extends Block> grate = getBlockByName(prefix + "BRONZE_GRATE");

			gearboxRecipe(recipeOutput, gearbox.get(), chiseled.get());
			gearboxRecipe(recipeOutput, waxed(gearbox), waxed(chiseled));
			strongboxRecipe(recipeOutput, strongbox.get(), bronzeBlock.get(), grate.get());
			strongboxRecipe(recipeOutput, waxed(strongbox), waxed(bronzeBlock), waxed(grate));
		}
	}

	private void gearboxRecipe(RecipeOutput recipeOutput, ItemLike gearbox, ItemLike chiseledBronze) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gearbox, 1)
				.define('C', RNItems.COGWHEEL)
				.define('K', RNItems.WINDING_KEY)
				.define('B', chiseledBronze)
				.pattern("CKC")
				.pattern("CBC")
				.pattern("CCC")
				.unlockedBy(getHasName(RNItems.COGWHEEL), has(RNItems.COGWHEEL))
				.unlockedBy(getHasName(RNItems.WINDING_KEY), has(RNItems.WINDING_KEY))
				.save(recipeOutput);
	}

	private void strongboxRecipe(RecipeOutput recipeOutput, ItemLike strongbox, ItemLike bronzeBlock, ItemLike grate) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, strongbox, 1)
				.define('B', bronzeBlock)
				.define('C', RNItems.COGWHEEL)
				.define('R', grate)
				.pattern("BCB")
				.pattern("CRC")
				.pattern("BCB")
				.unlockedBy(getHasName(RNItems.COGWHEEL), has(RNItems.COGWHEEL))
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

	private void bronzeTrimTarnish(RecipeOutput recipeOutput,
								   ResourceKey<TrimMaterial> inputMaterial,
								   ResourceKey<TrimMaterial> outputMaterial,
								   String id) {
		ResourceLocation recipeId = RubinatedNether.id(id);
		recipeOutput.accept(
				recipeId,
				new RNBronzeTrimTarnishRecipe(inputMaterial, outputMaterial),
				Advancement.Builder.advancement()
						.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
						.addCriterion("has_bronze_scrap", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.BRONZE_SCRAP))
						.rewards(AdvancementRewards.Builder.recipe(recipeId))
						.requirements(AdvancementRequirements.Strategy.OR)
						.build(RubinatedNether.id("recipes/" + id)));
	}

	private boolean isBronzeBlock(ItemLike input) {
		String inputName = input.asItem().toString();
		return inputName.contains("bronze_block") ||
				inputName.contains("discolored_bronze_block") ||
				inputName.contains("corroded_bronze_block") ||
				inputName.contains("tarnished_bronze_block") ||
				inputName.contains("crystallized_bronze_block");
	}

	private ItemLike lesserBronzeFor(String prefix) {
		return "CRYSTALLIZED_".equals(prefix)
				? RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT
				: RNItems.BRONZE_SCRAP;
	}

	public static ItemLike waxed(Supplier<? extends Block> block) {
		return BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(block.get())));
	}

	private void wax(RecipeOutput recipeOutput, Supplier<? extends Block> block) {
		ItemLike waxedBlock = waxed(block);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, waxedBlock)
				.requires(block.get())
				.requires(RNTags.Items.WAX)
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
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_HEAD);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_SPRING);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_VENT);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_BUTTON);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_GEARBOX);
		waxAllTarnishVariants(recipeOutput, RNBlocks.BRONZE_WINDER);

	}

	private void waxAllTarnishVariants(RecipeOutput recipeOutput, Supplier<? extends Block> baseBlock) {
		String baseName = getFieldNameFromSupplier(baseBlock);

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> tarnishedBlock = getBlockByName(prefix + baseName);
			wax(recipeOutput, tarnishedBlock);
		}
	}

	private void laser(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike lesserBronze, ItemLike laser) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, laser, 1)
				.define('A', blockInput)
				.define('B', lesserBronze)
				.define('C', Items.REDSTONE)
				.define('D', RNItems.NETHER_MORION)
				.define('E', RNBlocks.RUBY_BLOCK)
				.pattern(" E ")
				.pattern("BDB")
				.pattern("ACA")
				.unlockedBy(getHasName(RNBlocks.RUBY_BLOCK), has(RNBlocks.RUBY_BLOCK))
				.unlockedBy(getHasName(lesserBronze), has(lesserBronze))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(RNItems.NETHER_MORION), has(RNItems.NETHER_MORION))
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

	private void lamp(RecipeOutput recipeOutput, ItemLike blockInput, ItemLike lesserBronze, ItemLike lamp) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, lamp, 3)
				.define('X', lesserBronze)
				.define('A', blockInput)
				.define('O', RNBlocks.MOLTEN_RUBY_BLOCK)
				.define('I', RNBlocks.RUBY_GLASS_PANE)
				.pattern("XAX")
				.pattern("IOI")
				.pattern("XAX")
				.unlockedBy(getHasName(RNBlocks.RUBY_GLASS_PANE), has(RNBlocks.RUBY_GLASS_PANE))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY), has(RNItems.MOLTEN_RUBY))
				.unlockedBy(getHasName(lesserBronze), has(lesserBronze))
				.unlockedBy(getHasName(blockInput), has(blockInput))
				.save(recipeOutput);
	}

	private void winder(RecipeOutput recipeOutput) {
		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> slab = getBlockByName(prefix + "CUT_BRONZE_BRICKS_SLAB");
			Supplier<? extends Block> winder = getBlockByName(prefix + "BRONZE_WINDER");
			winderRecipe(recipeOutput, slab.get(), winder.get());
			winderRecipe(recipeOutput, waxed(slab), waxed(winder));
		}
	}

	private void winderRecipe(RecipeOutput recipeOutput, ItemLike slab, ItemLike winder) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, winder, 1)
				.define('K', RNItems.WINDING_KEY)
				.define('S', slab)
				.pattern("K")
				.pattern("S")
				.unlockedBy(getHasName(RNItems.WINDING_KEY), has(RNItems.WINDING_KEY))
				.unlockedBy(getHasName(slab), has(slab))
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

	private void tarnish(RecipeOutput recipeOutput, RecipeGeneratorWithExtra generator,
	                     ItemLike baseInput, ItemLike baseResult) {
		String inputName = getFieldName(baseInput);
		String resultName = getFieldName(baseResult);

		for (String prefix : TARNISH_PREFIXES) {
			ItemLike input = getBlockByName(prefix + inputName);
			ItemLike result = getBlockByName(prefix + resultName);
			ItemLike lesserBronze = lesserBronzeFor(prefix);
			generator.generate(recipeOutput, input, lesserBronze, result);
		}

		if (baseInput instanceof Supplier) {
			tarnishWaxed(recipeOutput, generator, (Supplier<? extends Block>) baseInput, (Supplier<? extends Block>) baseResult);
		}
	}

	private void tarnishWaxed(RecipeOutput recipeOutput, RecipeGeneratorWithExtra generator,
	                          Supplier<? extends Block> baseInput, Supplier<? extends Block> baseResult) {
		String inputName = getFieldNameFromSupplier(baseInput);
		String resultName = getFieldNameFromSupplier(baseResult);

		for (String prefix : TARNISH_PREFIXES) {
			Supplier<? extends Block> input = getBlockByName(prefix + inputName);
			Supplier<? extends Block> result = getBlockByName(prefix + resultName);
			ItemLike waxedInput = waxed(input);
			ItemLike waxedResult = waxed(result);
			ItemLike lesserBronze = lesserBronzeFor(prefix);
			generator.generate(recipeOutput, waxedInput, lesserBronze, waxedResult);
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

	private void freezing(RecipeOutput recipeOutput, ItemLike input, ItemLike output, int cookingTime, float xp) {
		String inputName = getItemName(input);
		String outputName = getItemName(output);
		ResourceLocation id = RubinatedNether.id(outputName + "_from_freezing_" + inputName);
		RNFreezingRecipeBuilder.freezing(Ingredient.of(input), output, xp, cookingTime)
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput, id);
	}

	private void smelting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float xp, int cookingTime) {
		String id = getItemName(output) + "_from_smelting_" + getItemName(input);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, xp, cookingTime)
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput, ResourceLocation.withDefaultNamespace(id));
	}

	private void blasting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float xp, int cookingTime) {
		String id = getItemName(output) + "_from_blasting_" + getItemName(input);
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, output, xp, cookingTime)
				.unlockedBy(getHasName(input), has(input))
				.save(recipeOutput, ResourceLocation.withDefaultNamespace(id));
	}

	private void blockSwaps(RecipeOutput recipeOutput) {
		derubination(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_TILES.get(), RNBlocks.SHRINE_STONE_TILES.get());
		derubination(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get(), RNBlocks.SHRINE_STONE_PILLAR.get());
		derubination(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(), RNBlocks.SHRINE_STONE_BRICKS.get());
		derubination(recipeOutput, RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(), RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get());
		derubination(recipeOutput, RNBlocks.RUBINATED_SHRINE_STONE_COFFER.get(), RNBlocks.SHRINE_STONE_COFFER.get());

		cracking(recipeOutput, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
		cracking(recipeOutput, Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS);
		cracking(recipeOutput, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS);
		cracking(recipeOutput, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
		cracking(recipeOutput, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
		cracking(recipeOutput, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
	}

	private void derubination(RecipeOutput recipeOutput, Block source, Block result) {
		ResourceLocation id = RubinatedNether.id(BuiltInRegistries.BLOCK.getKey(result).getPath() + "_from_derubination");
		recipeOutput.accept(id, new DerubinationRecipe(source, result), null);
	}

	private void cracking(RecipeOutput recipeOutput, Block source, Block result) {
		ResourceLocation id = RubinatedNether.id(BuiltInRegistries.BLOCK.getKey(result).getPath() + "_from_cracking");
		recipeOutput.accept(id, new CrackingRecipe(source, result), null);
	}

	/**
	 * World recipes for the Soul Starved Sand + Soul Fire petrification:
	 * a block in the source tag becomes its petrified counterpart.
	 */
	private void petrification(RecipeOutput recipeOutput) {
		petrification(recipeOutput, RNTags.Blocks.PETRIFIABLE_SNAGS, RNBlocks.PETRIFIED_SNAG.get());
		petrification(recipeOutput, RNTags.Blocks.PETRIFIABLE_STRIPPED_SNAGS, RNBlocks.STRIPPED_PETRIFIED_SNAG.get());
		petrification(recipeOutput, RNTags.Blocks.PETRIFIABLE_RINDS, RNBlocks.PETRIFIED_RIND.get());
		petrification(recipeOutput, RNTags.Blocks.PETRIFIABLE_STRIPPED_RINDS, RNBlocks.STRIPPED_PETRIFIED_RIND.get());
		petrification(recipeOutput, BlockTags.PLANKS, RNBlocks.PETRIFIED_FLAGS.get());
		petrification(recipeOutput, BlockTags.WOODEN_STAIRS, RNBlocks.PETRIFIED_STAIRS.get());
		petrification(recipeOutput, BlockTags.WOODEN_SLABS, RNBlocks.PETRIFIED_SLAB.get());
		petrification(recipeOutput, BlockTags.WOODEN_FENCES, RNBlocks.PETRIFIED_FENCE.get());
		petrification(recipeOutput, BlockTags.FENCE_GATES, RNBlocks.PETRIFIED_FENCE_GATE.get());
		petrification(recipeOutput, BlockTags.WOODEN_DOORS, RNBlocks.PETRIFIED_DOOR.get());
		petrification(recipeOutput, BlockTags.WOODEN_TRAPDOORS, RNBlocks.PETRIFIED_TRAPDOOR.get());
		petrification(recipeOutput, BlockTags.WOODEN_BUTTONS, RNBlocks.PETRIFIED_BUTTON.get());
		petrification(recipeOutput, BlockTags.WOODEN_PRESSURE_PLATES, RNBlocks.PETRIFIED_PRESSURE_PLATE.get());
		petrification(recipeOutput, BlockTags.STANDING_SIGNS, RNBlocks.PETRIFIED_SIGN.get());
		petrification(recipeOutput, BlockTags.WALL_SIGNS, RNBlocks.PETRIFIED_WALL_SIGN.get());
		petrification(recipeOutput, BlockTags.CEILING_HANGING_SIGNS, RNBlocks.PETRIFIED_HANGING_SIGN.get());
		petrification(recipeOutput, BlockTags.WALL_HANGING_SIGNS, RNBlocks.PETRIFIED_WALL_HANGING_SIGN.get());
	}

	private void petrification(RecipeOutput recipeOutput, TagKey<Block> source, Block result) {
		ResourceLocation id = RubinatedNether.id(BuiltInRegistries.BLOCK.getKey(result).getPath() + "_from_petrification");
		recipeOutput.accept(id, new PetrificationRecipe(source, result), null);
	}

	private void baptism(RecipeOutput recipeOutput, Ingredient ingredient, ItemLike result) {
		ResourceLocation id = RubinatedNether.id(getItemName(result) + "_from_baptism");
		recipeOutput.accept(id, new BaptismRecipe(ingredient, new ItemStack(result), 1), null);
	}

	private void resonance(RecipeOutput recipeOutput, ItemLike result, Ingredient ingredient,
						   @Nullable ResonanceRecipe.TriggerData offering,
						   @Nullable ResonanceRecipe.TriggerData key,
						   boolean shrineStoneNeighbor) {
		String id = getItemName(result) + "_from_resonance";
		var builder = RNResonanceRecipeBuilder.resonance(ingredient, result)
				.unlockedBy(getHasName(result), has(result));
		if (offering != null) builder.offering(offering.radius(), offering.attempts());
		if (key != null)      builder.key(key.radius(), key.attempts());
		if (shrineStoneNeighbor) builder.requireShrineStoneNeighbor();
		builder.save(recipeOutput, RubinatedNether.id(id));
	}
}
