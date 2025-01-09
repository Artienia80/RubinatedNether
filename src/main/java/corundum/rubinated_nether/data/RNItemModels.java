package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RNItemModels extends ItemModelProvider {
	public RNItemModels(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, RubinatedNether.MODID, fileHelper);
	}

	@Override
	protected void registerModels() {
		// Block items
		this.simpleBlockItem(RNBlocks.NETHER_RUBY_ORE.get());
		this.simpleBlockItem(RNBlocks.MOLTEN_RUBY_ORE.get());
		this.simpleBlockItem(RNBlocks.RUBINATED_BLACKSTONE.get());

		this.simpleBlockItem(RNBlocks.RUBY_BLOCK.get());
		this.simpleBlockItem(RNBlocks.MOLTEN_RUBY_BLOCK.get());

		this.simpleBlockItem(RNBlocks.BLEEDING_OBSIDIAN.get());

		this.basicItem(RNBlocks.RUBY_LANTERN.asItem());
		this.basicItem(RNBlocks.CHANDELIER.asItem());
		this.simpleBlockItem(RNBlocks.LAVA_LAMP.get());
		this.simpleBlockItem(RNBlocks.DRY_ICE.get());
		this.simpleBlockItem(RNBlocks.SOAKSTONE.get());

		this.simpleBlockItem(RNBlocks.BRAZIER.get());

		this.simpleBlockItem(RNBlocks.RUBY_GLASS.get());
		paneItem(
			RNBlocks.RUBY_GLASS_PANE.getId().toString(),
			"block/ruby_glass"
		);
		this.simpleBlockItem(RNBlocks.ORNATE_RUBY_GLASS.get());
		paneItem(
			RNBlocks.ORNATE_RUBY_GLASS_PANE.getId().toString(),
			"block/ornate_ruby_glass"
		);
		this.simpleBlockItem(RNBlocks.MOLTEN_RUBY_GLASS.get());
		paneItem(
			RNBlocks.MOLTEN_RUBY_GLASS_PANE.getId().toString(),
			"block/molten_ruby_glass"
		);

		this.simpleBlockItem(RNBlocks.SHRINE_STONE.get());

		this.simpleBlockItem(RNBlocks.POLISHED_SHRINE_STONE.get());
		this.simpleBlockItem(RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get());
		this.simpleBlockItem(RNBlocks.POLISHED_SHRINE_STONE_SLAB.get());
		wallInventory(
				RNBlocks.POLISHED_SHRINE_STONE_WALL.getId().toString(),
				modLoc("block/polished_shrine_stone")
		);

		this.simpleBlockItem(RNBlocks.SHRINE_STONE_TILES.get());
		this.simpleBlockItem(RNBlocks.SHRINE_STONE_TILES_STAIRS.get());
		this.simpleBlockItem(RNBlocks.SHRINE_STONE_TILES_SLAB.get());
		wallInventory(
				RNBlocks.SHRINE_STONE_TILES_WALL.getId().toString(),
				modLoc("block/shrine_stone_tiles")
		);

		this.simpleBlockItem(RNBlocks.SHRINE_STONE_PILLAR.get());

		this.simpleBlockItem(RNBlocks.SHRINE_STONE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.SHRINE_STONE_BRICKS_SLAB.get());
		wallInventory(
				RNBlocks.SHRINE_STONE_BRICKS_WALL.getId().toString(),
				modLoc("block/shrine_stone_bricks")
		);
		this.simpleBlockItem(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get());
		this.basicItem(RNBlocks.RUNESTONE.asItem());
		this.basicItem(RNItems.RUBY_LENS.asItem());

		this.simpleBlockItem(RNBlocks.BRONZE_BLOCK.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_BRONZE_BLOCK.get());
		this.simpleBlockItem(RNBlocks.CORRODED_BRONZE_BLOCK.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_BRONZE_BLOCK.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());

		this.simpleBlockItem(RNBlocks.CUT_BRONZE_PILLAR.get());
		this.simpleBlockItem(RNBlocks.CUT_BRONZE_PILLAR_STAIRS.get());
		this.simpleBlockItem(RNBlocks.CUT_BRONZE_PILLAR_SLAB.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_SLAB.get());
		this.simpleBlockItem(RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get());
		this.simpleBlockItem(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.simpleBlockItem(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_SLAB.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_SLAB.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_SLAB.get());

		this.simpleBlockItem(RNBlocks.CUT_BRONZE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.CUT_BRONZE_BRICKS_SLAB.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get());
		this.simpleBlockItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get());


		// Non-block items
		this.basicItem(RNItems.RUBY_ITEM.get());
		this.basicItem(RNItems.MOLTEN_RUBY_ITEM.get());
		this.basicItem(RNItems.RUBY_SHARD_ITEM.get());
		this.basicItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get());
		this.basicItem(RNItems.MUSIC_DISC_SHIMMER.get());
		this.basicItem(RNItems.BRONZE_ROD.get());
		this.basicItem(RNItems.BRONZE_SCRAP.get());


	}

	private void paneItem(String block, String texture) {
		this.withExistingParent(
			block, 
			mcLoc("item/generated")
		)
		.texture("layer0", texture)
		.renderType(mcLoc("translucent"));
	}
}
