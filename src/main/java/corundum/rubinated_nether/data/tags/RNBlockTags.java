package corundum.rubinated_nether.data.tags;

import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RNBlockTags extends BlockTagsProvider {
	public RNBlockTags(
		PackOutput output, 
		CompletableFuture<HolderLookup.Provider> lookupProvider, 
		ExistingFileHelper existingFileHelper
	) {
		super(output, lookupProvider, RubinatedNether.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		// Mining tags 
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
			RNBlocks.NETHER_RUBY_ORE.get(),
			RNBlocks.MOLTEN_RUBY_ORE.get(),
			RNBlocks.RUBINATED_BLACKSTONE.get(),

			RNBlocks.RUBY_BLOCK.get(),
			RNBlocks.MOLTEN_RUBY_BLOCK.get(),
			RNBlocks.BLEEDING_OBSIDIAN.get(),

			RNBlocks.RUBY_LANTERN.get(),
			RNBlocks.CHANDELIER.get(),
			RNBlocks.LAVA_LAMP.get(),
			RNBlocks.DRY_ICE.get(),
			RNBlocks.SOAKSTONE.get(),

			RNBlocks.RUBY_GLASS.get(),
			RNBlocks.RUBY_GLASS_PANE.get(),
			RNBlocks.ORNATE_RUBY_GLASS.get(),
			RNBlocks.ORNATE_RUBY_GLASS_PANE.get(),
			RNBlocks.MOLTEN_RUBY_GLASS.get(),
			RNBlocks.MOLTEN_RUBY_GLASS_PANE.get(),
			
			RNBlocks.ALTAR_STONE.get(),

			RNBlocks.POLISHED_ALTAR_STONE.get(),
			RNBlocks.POLISHED_ALTAR_STONE_STAIRS.get(),
			RNBlocks.POLISHED_ALTAR_STONE_SLAB.get(),
			RNBlocks.POLISHED_ALTAR_STONE_WALL.get(),

			RNBlocks.ALTAR_STONE_TILES.get(),
			RNBlocks.ALTAR_STONE_TILES_SLAB.get(),
			RNBlocks.ALTAR_STONE_TILES_STAIRS.get(),
			RNBlocks.ALTAR_STONE_TILES_WALL.get(),

			RNBlocks.ALTAR_STONE_PILLAR.get(),

			RNBlocks.ALTAR_STONE_BRICKS.get(),
			RNBlocks.ALTAR_STONE_BRICKS_STAIRS.get(),
			RNBlocks.ALTAR_STONE_BRICKS_WALL.get(),
			RNBlocks.ALTAR_STONE_BRICKS_SLAB.get(),

			RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get(),
			RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS.get(),
			RNBlocks.RUNESTONE.get(),

			RNBlocks.BRONZE_BLOCK.get(),
			RNBlocks.DISCOLORED_BRONZE_BLOCK.get(),
			RNBlocks.CORRODED_BRONZE_BLOCK.get(),
			RNBlocks.TARNISHED_BRONZE_BLOCK.get(),
			RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),
			RNBlocks.CUT_BRONZE.get(),

			RNBlocks.DISCOLORED_CUT_BRONZE.get(),
			RNBlocks.CORRODED_CUT_BRONZE.get(),
			RNBlocks.TARNISHED_CUT_BRONZE.get(),
			RNBlocks.CRYSTALLIZED_CUT_BRONZE.get(),

			RNBlocks.FREEZER.get()
		);
//
//		this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
//				Blocks.FROSTED_ICE
//		);

		this.tag(BlockTags.NEEDS_STONE_TOOL).add(
			RNBlocks.RUBY_GLASS.get(),
			RNBlocks.RUBY_GLASS_PANE.get(),
			RNBlocks.ORNATE_RUBY_GLASS.get(),
			RNBlocks.ORNATE_RUBY_GLASS_PANE.get(),
			RNBlocks.MOLTEN_RUBY_GLASS.get(),
			RNBlocks.MOLTEN_RUBY_GLASS_PANE.get(),

			RNBlocks.CHANDELIER.get()
		);

		this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
			RNBlocks.NETHER_RUBY_ORE.get(),
			RNBlocks.MOLTEN_RUBY_ORE.get(),
			RNBlocks.RUBINATED_BLACKSTONE.get(),

			RNBlocks.RUBY_BLOCK.get(),
			RNBlocks.MOLTEN_RUBY_BLOCK.get(),
			RNBlocks.BLEEDING_OBSIDIAN.get(),

			RNBlocks.ALTAR_STONE.get(),

			RNBlocks.POLISHED_ALTAR_STONE.get(),
			RNBlocks.POLISHED_ALTAR_STONE_STAIRS.get(),
			RNBlocks.POLISHED_ALTAR_STONE_SLAB.get(),
			RNBlocks.POLISHED_ALTAR_STONE_WALL.get(),

			RNBlocks.ALTAR_STONE_TILES.get(),
			RNBlocks.ALTAR_STONE_TILES_SLAB.get(),
			RNBlocks.ALTAR_STONE_TILES_STAIRS.get(),
			RNBlocks.ALTAR_STONE_TILES_WALL.get(),

			RNBlocks.ALTAR_STONE_PILLAR.get(),

			RNBlocks.ALTAR_STONE_BRICKS.get(),
			RNBlocks.ALTAR_STONE_BRICKS_STAIRS.get(),
			RNBlocks.ALTAR_STONE_BRICKS_WALL.get(),
			RNBlocks.ALTAR_STONE_BRICKS_SLAB.get(),
			RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get(),
			RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS.get(),
			RNBlocks.RUNESTONE.get(),

			RNBlocks.BRONZE_BLOCK.get(),
			RNBlocks.DISCOLORED_BRONZE_BLOCK.get(),
			RNBlocks.CORRODED_BRONZE_BLOCK.get(),
			RNBlocks.TARNISHED_BRONZE_BLOCK.get(),
			RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),

			RNBlocks.CUT_BRONZE.get(),
			RNBlocks.DISCOLORED_CUT_BRONZE.get(),
			RNBlocks.CORRODED_CUT_BRONZE.get(),
			RNBlocks.TARNISHED_CUT_BRONZE.get(),
			RNBlocks.CRYSTALLIZED_CUT_BRONZE.get(),

			RNBlocks.FREEZER.get()
		);

		this.tag(RNTags.Blocks.COLDEST_ICE).add(
			Blocks.BLUE_ICE	
		);

		this.tag(BlockTags.WALLS).add(
			RNBlocks.ALTAR_STONE_TILES_WALL.get(),
			RNBlocks.ALTAR_STONE_BRICKS_WALL.get(),
			RNBlocks.POLISHED_ALTAR_STONE_WALL.get()
		);
	}
}
