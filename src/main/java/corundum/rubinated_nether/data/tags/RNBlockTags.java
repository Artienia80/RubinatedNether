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
import net.neoforged.neoforge.common.Tags;
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

			RNBlocks.FREEZER.get(),
			RNBlocks.BRAZIER.get()
		);

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

			RNBlocks.FREEZER.get()
		);

		this.tag(RNTags.Blocks.MINEABLE_WITH_DRILL).add(
				RNBlocks.SHRINE_STONE.get(),

				RNBlocks.POLISHED_SHRINE_STONE.get(),
				RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get(),
				RNBlocks.POLISHED_SHRINE_STONE_SLAB.get(),
				RNBlocks.POLISHED_SHRINE_STONE_WALL.get(),

				RNBlocks.SHRINE_STONE_TILES.get(),
				RNBlocks.SHRINE_STONE_TILES_SLAB.get(),
				RNBlocks.SHRINE_STONE_TILES_STAIRS.get(),
				RNBlocks.SHRINE_STONE_TILES_WALL.get(),

				RNBlocks.SHRINE_STONE_PILLAR.get(),

				RNBlocks.SHRINE_STONE_BRICKS.get(),
				RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get(),
				RNBlocks.SHRINE_STONE_BRICKS_WALL.get(),
				RNBlocks.SHRINE_STONE_BRICKS_SLAB.get(),
				RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(),
				RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(),
				RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(),
				RNBlocks.RUNESTONE.get(),
				RNBlocks.BRAZIER.get(),

				RNBlocks.BRONZE_BLOCK.get(),
				RNBlocks.DISCOLORED_BRONZE_BLOCK.get(),
				RNBlocks.CORRODED_BRONZE_BLOCK.get(),
				RNBlocks.TARNISHED_BRONZE_BLOCK.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),

				RNBlocks.CUT_BRONZE_PILLAR.get(),
				RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get(),
				RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get(),
				RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get(),

				RNBlocks.CUT_BRONZE_BRICKS.get(),
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.CUT_BRONZE_BRICKS_SLAB.get(),
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get(),
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get(),
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get(),
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get(),
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get(),
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get(),

				RNBlocks.BRONZE_BULB.get(),
				RNBlocks.DISCOLORED_BRONZE_BULB.get(),
				RNBlocks.CORRODED_BRONZE_BULB.get(),
				RNBlocks.TARNISHED_BRONZE_BULB.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_BULB.get()
		);

				this.tag(RNTags.Blocks.COLDEST_ICE).add(
			Blocks.BLUE_ICE	
		);

		this.tag(RNTags.Blocks.LIT_SOUL_BLOCKS).add(
				Blocks.SOUL_CAMPFIRE,
				Blocks.SOUL_FIRE,
				Blocks.SOUL_LANTERN,
				Blocks.SOUL_TORCH,
				Blocks.SOUL_WALL_TORCH
				);

		this.tag(RNTags.Blocks.CRYSTALLIZATION_CATALYST).add(
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get()
				);

		this.tag(RNTags.Blocks.CRYSTALLIZATION_CATALYST).addTag(RNTags.Blocks.LIT_SOUL_BLOCKS);

		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).addTag(Tags.Blocks.GLASS_BLOCKS_CHEAP);
		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).add(
				RNBlocks.RUBY_GLASS.get(),
				RNBlocks.MOLTEN_RUBY_GLASS.get(),
				RNBlocks.ORNATE_RUBY_GLASS.get()
		);

		this.tag(RNTags.Blocks.RUBY_LASER_NO_SIGNAL).add(
				Blocks.TINTED_GLASS
		);

		this.tag(RNTags.Blocks.RUBY_GLASS).add(
				RNBlocks.RUBY_GLASS.get(),
				RNBlocks.MOLTEN_RUBY_GLASS.get(),
				RNBlocks.ORNATE_RUBY_GLASS.get()
		);

		this.tag(RNTags.Blocks.RAINBOW_LASER).addTag(RNTags.Blocks.RUBY_GLASS);

		this.tag(RNTags.Blocks.MINEABLE_WITH_DRILL).addTags(
				BlockTags.MINEABLE_WITH_PICKAXE,
				BlockTags.MINEABLE_WITH_SHOVEL
		);

		this.tag(BlockTags.WALLS).add(
			RNBlocks.SHRINE_STONE_TILES_WALL.get(),
			RNBlocks.SHRINE_STONE_BRICKS_WALL.get(),
			RNBlocks.POLISHED_SHRINE_STONE_WALL.get()
		);
	}
}
