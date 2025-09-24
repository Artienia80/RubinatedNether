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

	@SuppressWarnings("unchecked") // TODO: Make Checked 
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

				RNBlocks.BRONZE_LANTERN.get(),
				RNBlocks.DISCOLORED_BRONZE_LANTERN.get(),
				RNBlocks.CORRODED_BRONZE_LANTERN.get(),
				RNBlocks.TARNISHED_BRONZE_LANTERN.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get(),

				RNBlocks.BRONZE_CHAIN.get(),
				RNBlocks.DISCOLORED_BRONZE_CHAIN.get(),
				RNBlocks.CORRODED_BRONZE_CHAIN.get(),
				RNBlocks.TARNISHED_BRONZE_CHAIN.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get(),

				RNBlocks.BRONZE_CHANDELIER.get(),
                RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get(),
                RNBlocks.CORRODED_BRONZE_CHANDELIER.get(),
                RNBlocks.TARNISHED_BRONZE_CHANDELIER.get(),
                RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get(),
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
				RNBlocks.BRAZIER.get(),

				RNBlocks.SHRINE_STONE.get(),
				RNBlocks.SHRINE_STONE_STAIRS.get(),
				RNBlocks.SHRINE_STONE_SLAB.get(),
				RNBlocks.SHRINE_STONE_WALL.get(),

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
				RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get(),
				RNBlocks.RUBINATED_SHRINE_STONE_TILES.get(),
				RNBlocks.RUNESTONE.get(),
				RNBlocks.SHRINE_STONE_COFFER.get(),

				RNBlocks.BRAZIER.get(),
				RNBlocks.RUBINATION_ALTAR.get(),

				RNBlocks.BRONZE_BLOCK.get(),
				RNBlocks.DISCOLORED_BRONZE_BLOCK.get(),
				RNBlocks.CORRODED_BRONZE_BLOCK.get(),
				RNBlocks.TARNISHED_BRONZE_BLOCK.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),

				RNBlocks.CHISELED_BRONZE.get(),
				RNBlocks.DISCOLORED_CHISELED_BRONZE.get(),
				RNBlocks.CORRODED_CHISELED_BRONZE.get(),
				RNBlocks.TARNISHED_CHISELED_BRONZE.get(),
				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get(),

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
				RNBlocks.CRYSTALLIZED_BRONZE_BULB.get(),

				RNBlocks.BRONZE_GRATE.get(),
				RNBlocks.DISCOLORED_BRONZE_GRATE.get(),
				RNBlocks.CORRODED_BRONZE_GRATE.get(),
				RNBlocks.TARNISHED_BRONZE_GRATE.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get()
		);

		this.tag(BlockTags.NEEDS_STONE_TOOL).add(
				RNBlocks.RUBY_GLASS.get(),
				RNBlocks.RUBY_GLASS_PANE.get(),
				RNBlocks.ORNATE_RUBY_GLASS.get(),
				RNBlocks.ORNATE_RUBY_GLASS_PANE.get(),
				RNBlocks.MOLTEN_RUBY_GLASS.get(),
				RNBlocks.MOLTEN_RUBY_GLASS_PANE.get()

		);

		this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
				RNBlocks.NETHER_RUBY_ORE.get(),
				RNBlocks.MOLTEN_RUBY_ORE.get(),
				RNBlocks.RUBINATED_BLACKSTONE.get(),

				RNBlocks.RUBY_BLOCK.get(),
				RNBlocks.MOLTEN_RUBY_BLOCK.get(),
				RNBlocks.BLEEDING_OBSIDIAN.get(),

				RNBlocks.FREEZER.get(),

				RNBlocks.SHRINE_STONE.get(),
				RNBlocks.SHRINE_STONE_STAIRS.get(),
				RNBlocks.SHRINE_STONE_SLAB.get(),
				RNBlocks.SHRINE_STONE_WALL.get(),

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
				RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get(),
				RNBlocks.RUBINATED_SHRINE_STONE_TILES.get(),
				RNBlocks.RUNESTONE.get(),
				RNBlocks.SHRINE_STONE_COFFER.get(),

				RNBlocks.BRAZIER.get(),
				RNBlocks.RUBINATION_ALTAR.get(),

				RNBlocks.BRONZE_BLOCK.get(),
				RNBlocks.DISCOLORED_BRONZE_BLOCK.get(),
				RNBlocks.CORRODED_BRONZE_BLOCK.get(),
				RNBlocks.TARNISHED_BRONZE_BLOCK.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),

				RNBlocks.CHISELED_BRONZE.get(),
				RNBlocks.DISCOLORED_CHISELED_BRONZE.get(),
				RNBlocks.CORRODED_CHISELED_BRONZE.get(),
				RNBlocks.TARNISHED_CHISELED_BRONZE.get(),
				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get(),

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
				RNBlocks.CRYSTALLIZED_BRONZE_BULB.get(),

				RNBlocks.BRONZE_GRATE.get(),
				RNBlocks.DISCOLORED_BRONZE_GRATE.get(),
				RNBlocks.CORRODED_BRONZE_GRATE.get(),
				RNBlocks.TARNISHED_BRONZE_GRATE.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get(),

				RNBlocks.BRONZE_LANTERN.get(),
				RNBlocks.DISCOLORED_BRONZE_LANTERN.get(),
				RNBlocks.CORRODED_BRONZE_LANTERN.get(),
				RNBlocks.TARNISHED_BRONZE_LANTERN.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get(),

				RNBlocks.BRONZE_CHAIN.get(),
				RNBlocks.DISCOLORED_BRONZE_CHAIN.get(),
				RNBlocks.CORRODED_BRONZE_CHAIN.get(),
				RNBlocks.TARNISHED_BRONZE_CHAIN.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get(),

				RNBlocks.BRONZE_CHANDELIER.get(),
				RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get(),
				RNBlocks.CORRODED_BRONZE_CHANDELIER.get(),
				RNBlocks.TARNISHED_BRONZE_CHANDELIER.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get()
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

		this.tag(RNTags.Blocks.CRYSTALLIZED_BLOCKS).add(
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get(),
				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_BULB.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get(),
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get()
		);

		this.tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).addTag(RNTags.Blocks.CRYSTALLIZED_BLOCKS);

		this.tag(RNTags.Blocks.CRYSTALLIZATION_CATALYST).addTag(RNTags.Blocks.LIT_SOUL_BLOCKS);
		this.tag(RNTags.Blocks.CRYSTALLIZATION_CATALYST).addTag(RNTags.Blocks.CRYSTALLIZED_BLOCKS);



		this.tag(RNTags.Blocks.RUBY_LASER_NO_SIGNAL).add(
				Blocks.TINTED_GLASS
		);

		this.tag(RNTags.Blocks.RUBY_GLASS).add(
				RNBlocks.RUBY_GLASS.get(),
				RNBlocks.MOLTEN_RUBY_GLASS.get(),
				RNBlocks.ORNATE_RUBY_GLASS.get()
		);
		this.tag(RNTags.Blocks.RUBY_GLASS_PANES).add(
				RNBlocks.RUBY_GLASS_PANE.get(),
				RNBlocks.MOLTEN_RUBY_GLASS_PANE.get(),
				RNBlocks.ORNATE_RUBY_GLASS_PANE.get()
		);

		this.tag(RNTags.Blocks.GRATES).add(
				Blocks.COPPER_GRATE,
				Blocks.EXPOSED_COPPER_GRATE,
				Blocks.WEATHERED_COPPER_GRATE,
				Blocks.OXIDIZED_COPPER_GRATE,
				Blocks.WAXED_COPPER_GRATE,
				Blocks.WAXED_EXPOSED_COPPER_GRATE,
				Blocks.WAXED_WEATHERED_COPPER_GRATE,
				Blocks.WAXED_OXIDIZED_COPPER_GRATE,

				RNBlocks.BRONZE_GRATE.get(),
				RNBlocks.DISCOLORED_BRONZE_GRATE.get(),
				RNBlocks.CORRODED_BRONZE_GRATE.get(),
				RNBlocks.TARNISHED_BRONZE_GRATE.get(),
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get()
				);


		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).addTag(Tags.Blocks.GLASS_BLOCKS_CHEAP);
		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).addTag(Tags.Blocks.GLASS_PANES);
		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).addTag(RNTags.Blocks.RUBY_GLASS);
		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).addTag(RNTags.Blocks.RUBY_GLASS_PANES);
		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).addTag(RNTags.Blocks.GRATES);

		this.tag(RNTags.Blocks.RUBY_LASER_TRANSPARENT).add(
				Blocks.IRON_BARS
		);

		this.tag(RNTags.Blocks.SILLY_LASER).addTag(RNTags.Blocks.RUBY_GLASS);
		this.tag(RNTags.Blocks.SILLY_LASER).addTag(RNTags.Blocks.RUBY_GLASS_PANES);

		this.tag(RNTags.Blocks.MINEABLE_WITH_DRILL).addTags(
				BlockTags.MINEABLE_WITH_PICKAXE,
				BlockTags.MINEABLE_WITH_SHOVEL
		);

		this.tag(BlockTags.WALLS).add(
				RNBlocks.SHRINE_STONE_WALL.get(),
				RNBlocks.SHRINE_STONE_TILES_WALL.get(),
				RNBlocks.SHRINE_STONE_BRICKS_WALL.get(),
				RNBlocks.POLISHED_SHRINE_STONE_WALL.get()
		);


		this.tag(RNTags.Blocks.SHRINE_STONE_CANDIDATE).add(
				Blocks.STONE,
				Blocks.DEEPSLATE,
				Blocks.BLACKSTONE,
				Blocks.ANDESITE,
				Blocks.DIORITE,
				Blocks.GRANITE,
				Blocks.TUFF
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_STAIRS_CANDIDATE).add(
				Blocks.STONE_STAIRS,
				Blocks.BLACKSTONE_STAIRS,
				Blocks.ANDESITE_STAIRS,
				Blocks.DIORITE_STAIRS,
				Blocks.GRANITE_STAIRS,
				Blocks.TUFF_STAIRS
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_SLAB_CANDIDATE).add(
				Blocks.STONE_SLAB,
				Blocks.BLACKSTONE_SLAB,
				Blocks.ANDESITE_SLAB,
				Blocks.DIORITE_SLAB,
				Blocks.GRANITE_SLAB,
				Blocks.TUFF_SLAB
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_WALL_CANDIDATE).add(
				Blocks.BLACKSTONE_WALL,
				Blocks.ANDESITE_WALL,
				Blocks.DIORITE_WALL,
				Blocks.GRANITE_WALL,
				Blocks.TUFF_WALL
		);

		this.tag(RNTags.Blocks.POLISHED_SHRINE_STONE_CANDIDATE).add(
				Blocks.SMOOTH_STONE,
				Blocks.POLISHED_DEEPSLATE,
				Blocks.POLISHED_BLACKSTONE,
				Blocks.POLISHED_ANDESITE,
				Blocks.POLISHED_DIORITE,
				Blocks.POLISHED_GRANITE,
				Blocks.POLISHED_TUFF
		);

		this.tag(RNTags.Blocks.POLISHED_SHRINE_STONE_STAIRS_CANDIDATE).add(
				Blocks.POLISHED_DEEPSLATE_STAIRS,
				Blocks.POLISHED_BLACKSTONE_STAIRS,
				Blocks.POLISHED_ANDESITE_STAIRS,
				Blocks.POLISHED_DIORITE_STAIRS,
				Blocks.POLISHED_GRANITE_STAIRS,
				Blocks.POLISHED_TUFF_STAIRS
		);

		this.tag(RNTags.Blocks.POLISHED_SHRINE_STONE_SLAB_CANDIDATE).add(
				Blocks.SMOOTH_STONE_SLAB,
				Blocks.POLISHED_DEEPSLATE_SLAB,
				Blocks.POLISHED_BLACKSTONE_SLAB,
				Blocks.POLISHED_ANDESITE_SLAB,
				Blocks.POLISHED_DIORITE_SLAB,
				Blocks.POLISHED_GRANITE_SLAB,
				Blocks.POLISHED_TUFF_SLAB
		);

		this.tag(RNTags.Blocks.POLISHED_SHRINE_STONE_WALL_CANDIDATE).add(
				Blocks.POLISHED_DEEPSLATE_WALL,
				Blocks.POLISHED_BLACKSTONE_WALL,
				Blocks.POLISHED_TUFF_WALL
		);

		// No vanilla blocks, this is for modcompat
		this.tag(RNTags.Blocks.SHRINE_STONE_PILLAR_CANDIDATE);

		this.tag(RNTags.Blocks.CHISELED_SHRINE_STONE_BRICKS_CANDIDATE).add(
				Blocks.CHISELED_STONE_BRICKS,
				Blocks.CHISELED_DEEPSLATE,
				Blocks.CHISELED_POLISHED_BLACKSTONE,
				Blocks.CHISELED_TUFF,
				Blocks.CHISELED_TUFF_BRICKS
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_BRICKS_CANDIDATE).add(
				Blocks.STONE_BRICKS,
				Blocks.MOSSY_STONE_BRICKS,
				Blocks.CRACKED_STONE_BRICKS,
				Blocks.DEEPSLATE_BRICKS,
				Blocks.CRACKED_DEEPSLATE_BRICKS,
				Blocks.POLISHED_BLACKSTONE_BRICKS,
				Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
				Blocks.TUFF_BRICKS
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_BRICKS_STAIRS_CANDIDATE).add(
				Blocks.STONE_BRICK_STAIRS,
				Blocks.MOSSY_STONE_BRICK_STAIRS,
				Blocks.DEEPSLATE_BRICK_STAIRS,
				Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS,
				Blocks.TUFF_BRICK_STAIRS
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_BRICKS_SLAB_CANDIDATE).add(
				Blocks.STONE_BRICK_SLAB,
				Blocks.MOSSY_STONE_BRICK_SLAB,
				Blocks.DEEPSLATE_BRICK_SLAB,
				Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
				Blocks.TUFF_BRICK_SLAB
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_BRICKS_WALL_CANDIDATE).add(
				Blocks.STONE_BRICK_WALL,
				Blocks.MOSSY_STONE_BRICK_WALL,
				Blocks.DEEPSLATE_BRICK_WALL,
				Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
				Blocks.TUFF_BRICK_WALL
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_TILES_CANDIDATE).add(
				Blocks.DEEPSLATE_TILES,
				Blocks.CRACKED_DEEPSLATE_TILES
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_TILES_STAIRS_CANDIDATE).add(
				Blocks.DEEPSLATE_TILE_STAIRS
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_TILES_SLAB_CANDIDATE).add(
				Blocks.DEEPSLATE_TILE_SLAB
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_TILES_WALL_CANDIDATE).add(
				Blocks.DEEPSLATE_TILE_WALL
		);

		this.tag(RNTags.Blocks.SHRINE_STONE_BLOCKS).add(
				RNBlocks.SHRINE_STONE.get(),
				RNBlocks.POLISHED_SHRINE_STONE.get(),
				RNBlocks.SHRINE_STONE_TILES.get(),
				RNBlocks.SHRINE_STONE_PILLAR.get(),
				RNBlocks.SHRINE_STONE_BRICKS.get(),
				RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(),
				RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(),
				RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(),
				RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get(),
				RNBlocks.RUBINATED_SHRINE_STONE_TILES.get(),
				RNBlocks.SHRINE_STONE_STAIRS.get(),
				RNBlocks.SHRINE_STONE_SLAB.get(),
				RNBlocks.SHRINE_STONE_WALL.get(),
				RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get(),
				RNBlocks.POLISHED_SHRINE_STONE_SLAB.get(),
				RNBlocks.POLISHED_SHRINE_STONE_WALL.get(),
				RNBlocks.SHRINE_STONE_TILES_SLAB.get(),
				RNBlocks.SHRINE_STONE_TILES_STAIRS.get(),
				RNBlocks.SHRINE_STONE_TILES_WALL.get(),
				RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get(),
				RNBlocks.SHRINE_STONE_BRICKS_WALL.get(),
				RNBlocks.SHRINE_STONE_BRICKS_SLAB.get(),
				RNBlocks.RUNESTONE.get()
		);
	}
}