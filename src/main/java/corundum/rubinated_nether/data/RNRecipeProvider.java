package corundum.rubinated_nether.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.CommentedConfig;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.datamaps.WaxableBronze;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.flag.FeatureFlagSet;


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

				RNBlocks.CHISELED_BRONZE,
				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CUT_BRONZE_BRICKS,

				RNBlocks.CHISELED_BRONZE,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,

				RNBlocks.DISCOLORED_CHISELED_BRONZE,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,

				RNBlocks.DISCOLORED_CHISELED_BRONZE,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CORRODED_BRONZE_BLOCK,

				RNBlocks.CORRODED_CHISELED_BRONZE,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CORRODED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,

				RNBlocks.CORRODED_CHISELED_BRONZE,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.TARNISHED_BRONZE_BLOCK,

				RNBlocks.TARNISHED_CHISELED_BRONZE,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.TARNISHED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,

				RNBlocks.TARNISHED_CHISELED_BRONZE,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,

				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR
		);

		stonecutterList(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,

				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_PILLAR)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_SLAB)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_PILLAR)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR)))
		);

		stonecutterList(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS))),

				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB)))
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

		twoByTwo(
				recipeOutput,
				RNItems.BRONZE_POWDER,
				RNBlocks.BRONZE_BLOCK,
				1
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

		stairsAndSlab(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_SLAB)))
		);

		stairsAndSlab(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB)))
		);

		stairsAndSlab(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB)))
		);

		stairsAndSlab(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB)))
		);

		stairsAndSlab(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB)))
		);


		one(
				recipeOutput,
				RNItems.BRONZE_ROD,
				RNItems.BRONZE_POWDER,
				16
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
				RNBlocks.CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CHISELED_BRONZE,
				1
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.DISCOLORED_CHISELED_BRONZE,
				1
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CORRODED_CHISELED_BRONZE,
				1
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.TARNISHED_CHISELED_BRONZE,
				1
		);

		oneByTwo(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE,
				1
		);

		twoByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS))),
				4
		);

		twoByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS))),
				4
		);

		twoByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS))),
				4
		);

		twoByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS))),
				4
		);

		twoByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS))),
				4
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_PILLAR))),
				2
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR))),
				2
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_PILLAR))),
				2
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR))),
				2
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR))),
				2
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CHISELED_BRONZE))),
				1
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_CHISELED_BRONZE))),
				1
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_CHISELED_BRONZE))),
				1
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_CHISELED_BRONZE))),
				1
		);

		oneByTwo(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE))),
				1
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

		grate(
				recipeOutput,
				RNBlocks.BRONZE_BLOCK,
				RNBlocks.BRONZE_GRATE,
				4
		);

		grate(
				recipeOutput,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,
				RNBlocks.DISCOLORED_BRONZE_GRATE,
				4
		);

		grate(
				recipeOutput,
				RNBlocks.CORRODED_BRONZE_BLOCK,
				RNBlocks.CORRODED_BRONZE_GRATE,
				4
		);

		grate(
				recipeOutput,
				RNBlocks.TARNISHED_BRONZE_BLOCK,
				RNBlocks.TARNISHED_BRONZE_GRATE,
				4
		);

		grate(
				recipeOutput,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE,
				4
		);

		grate(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_GRATE))),
				4
		);

		grate(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_GRATE))),
				4
		);

		grate(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_GRATE))),
				4
		);

		grate(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_GRATE))),
				4
		);

		grate(
				recipeOutput,
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK))),
				BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_GRATE))),
				4
		);

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

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RNItems.BRONZE_SHOT, 1)
				.define('X', RNItems.BRONZE_POWDER)
				.define('O', Items.COPPER_INGOT)
				.pattern(" X ")
				.pattern("XOX")
				.pattern(" X ")
				.unlockedBy(getHasName(RNItems.BRONZE_SHOT), has(RNItems.BRONZE_SHOT))
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

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBY_LANTERN, 1)
				.define('X', Items.COPPER_INGOT)
				.define('O', RNItems.MOLTEN_RUBY_ITEM)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY_ITEM), has(RNItems.MOLTEN_RUBY_ITEM))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.CHANDELIER, 1)
				.define('X', RNBlocks.RUBY_LANTERN)
				.define('O', Items.COPPER_INGOT)
				.pattern("XXX")
				.pattern("XOX")
				.pattern("XXX")
				.unlockedBy(getHasName(RNBlocks.RUBY_LANTERN), has(RNBlocks.RUBY_LANTERN))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.LAVA_LAMP, 1)
				.define('X', Items.COPPER_INGOT)
				.define('O', RNItems.MOLTEN_RUBY_ITEM)
				.define('I', RNBlocks.RUBY_GLASS_PANE)
				.pattern("XXX")
				.pattern("IOI")
				.pattern("XXX")
				.unlockedBy(getHasName(RNBlocks.RUBY_GLASS_PANE), has(RNBlocks.RUBY_GLASS_PANE))
				.unlockedBy(getHasName(RNItems.MOLTEN_RUBY_ITEM), has(RNItems.MOLTEN_RUBY_ITEM))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
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

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RNBlocks.RUBY_LASER, 1)
				.define('A', Blocks.COPPER_BLOCK)
				.define('B', Items.COPPER_INGOT)
				.define('C', Items.REDSTONE)
				.define('D', Items.GLOWSTONE)
				.define('E', RNBlocks.RUBY_BLOCK)
				.pattern(" E ")
				.pattern("BDB")
				.pattern("ACA")
				.unlockedBy(getHasName(RNBlocks.RUBY_BLOCK), has(RNBlocks.RUBY_BLOCK))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.GLOWSTONE), has(Items.GLOWSTONE))
				.unlockedBy(getHasName(Blocks.COPPER_BLOCK), has(Blocks.COPPER_BLOCK))
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

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.BRONZE_BULB, 4)
				.define('B', RNBlocks.BRONZE_BLOCK)
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(RNBlocks.BRONZE_BLOCK), has(RNBlocks.BRONZE_BLOCK))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.DISCOLORED_BRONZE_BULB, 4)
				.define('B', RNBlocks.DISCOLORED_BRONZE_BLOCK)
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(RNBlocks.DISCOLORED_BRONZE_BLOCK), has(RNBlocks.DISCOLORED_BRONZE_BLOCK))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.CORRODED_BRONZE_BULB, 4)
				.define('B', RNBlocks.CORRODED_BRONZE_BLOCK)
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(RNBlocks.CORRODED_BRONZE_BLOCK), has(RNBlocks.CORRODED_BRONZE_BLOCK))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.TARNISHED_BRONZE_BULB, 4)
				.define('B', RNBlocks.TARNISHED_BRONZE_BLOCK)
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(RNBlocks.TARNISHED_BRONZE_BLOCK), has(RNBlocks.TARNISHED_BRONZE_BLOCK))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RNBlocks.CRYSTALLIZED_BRONZE_BULB, 4)
				.define('B', RNBlocks.CRYSTALLIZED_BRONZE_BLOCK)
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK), has(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BULB))), 4)
				.define('B', BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK))))
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK)))), has(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.BRONZE_BLOCK)))))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BULB))), 4)
				.define('B', BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK))))
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK)))), has(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.DISCOLORED_BRONZE_BLOCK)))))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BULB))), 4)
				.define('B', BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK))))
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK)))), has(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CORRODED_BRONZE_BLOCK)))))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BULB))), 4)
				.define('B', BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK))))
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK)))), has(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.TARNISHED_BRONZE_BLOCK)))))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
				.save(recipeOutput);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BULB))), 4)
				.define('B', BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK))))
				.define('R', Items.REDSTONE)
				.define('X', Items.BLAZE_ROD)
				.pattern(" B ")
				.pattern("BXB")
				.pattern(" R ")
				.unlockedBy(getHasName(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK)))), has(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK)))))
				.unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
				.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
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

	private void wax(RecipeOutput recipeOutput, Supplier<? extends Block> block) {
		ItemLike waxedBlock = BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(block.get())));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, waxedBlock)
				.requires(block.get())
				.requires(Items.HONEYCOMB)
				.group(getItemName(waxedBlock))
				.unlockedBy(getHasName(block.get()), has(block.get()))
				.save(recipeOutput, getConversionRecipeName(waxedBlock, Items.HONEYCOMB));
	}

	private void generateAllWaxingRecipes(RecipeOutput recipeOutput) {
		wax(recipeOutput, RNBlocks.BRONZE_BLOCK);
		wax(recipeOutput, RNBlocks.CHISELED_BRONZE);
		wax(recipeOutput, RNBlocks.CUT_BRONZE_PILLAR);
		wax(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS);
		wax(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS_STAIRS);
		wax(recipeOutput, RNBlocks.CUT_BRONZE_BRICKS_SLAB);
		wax(recipeOutput, RNBlocks.BRONZE_BULB);

		wax(recipeOutput, RNBlocks.DISCOLORED_BRONZE_BLOCK);
		wax(recipeOutput, RNBlocks.DISCOLORED_CHISELED_BRONZE);
		wax(recipeOutput, RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR);
		wax(recipeOutput, RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS);
		wax(recipeOutput, RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS);
		wax(recipeOutput, RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB);
		wax(recipeOutput, RNBlocks.DISCOLORED_BRONZE_BULB);

		wax(recipeOutput, RNBlocks.CORRODED_BRONZE_BLOCK);
		wax(recipeOutput, RNBlocks.CORRODED_CHISELED_BRONZE);
		wax(recipeOutput, RNBlocks.CORRODED_CUT_BRONZE_PILLAR);
		wax(recipeOutput, RNBlocks.CORRODED_CUT_BRONZE_BRICKS);
		wax(recipeOutput, RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS);
		wax(recipeOutput, RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB);
		wax(recipeOutput, RNBlocks.CORRODED_BRONZE_BULB);

		wax(recipeOutput, RNBlocks.TARNISHED_BRONZE_BLOCK);
		wax(recipeOutput, RNBlocks.TARNISHED_CHISELED_BRONZE);
		wax(recipeOutput, RNBlocks.TARNISHED_CUT_BRONZE_PILLAR);
		wax(recipeOutput, RNBlocks.TARNISHED_CUT_BRONZE_BRICKS);
		wax(recipeOutput, RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS);
		wax(recipeOutput, RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB);
		wax(recipeOutput, RNBlocks.TARNISHED_BRONZE_BULB);

		wax(recipeOutput, RNBlocks.CRYSTALLIZED_BRONZE_BLOCK);
		wax(recipeOutput, RNBlocks.CRYSTALLIZED_CHISELED_BRONZE);
		wax(recipeOutput, RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR);
		wax(recipeOutput, RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS);
		wax(recipeOutput, RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS);
		wax(recipeOutput, RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB);
		wax(recipeOutput, RNBlocks.CRYSTALLIZED_BRONZE_BULB);
	}
}
