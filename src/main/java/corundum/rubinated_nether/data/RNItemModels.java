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

		this.simpleBlockItem(RNBlocks.ALTAR_STONE.get());
		this.simpleBlockItem(RNBlocks.ALTAR_STONE_TILES.get());
		this.simpleBlockItem(RNBlocks.ALTAR_STONE_TILES_STAIRS.get());
		this.simpleBlockItem(RNBlocks.ALTAR_STONE_TILES_SLAB.get());
		wallInventory(
				RNBlocks.ALTAR_STONE_TILES_WALL.getId().toString(),
				modLoc("block/altar_stone_tiles")
		);

		this.simpleBlockItem(RNBlocks.ALTAR_STONE_PILLAR.get());

		this.simpleBlockItem(RNBlocks.ALTAR_STONE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.ALTAR_STONE_BRICKS_STAIRS.get());
		this.simpleBlockItem(RNBlocks.ALTAR_STONE_BRICKS_SLAB.get());
		wallInventory(
				RNBlocks.ALTAR_STONE_BRICKS_WALL.getId().toString(),
				modLoc("block/altar_stone_bricks")
		);
		this.simpleBlockItem(RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
		this.simpleBlockItem(RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS.get());
		this.basicItem(RNBlocks.RUNESTONE.asItem());

		// Non-block items
		this.basicItem(RNItems.RUBY_ITEM.get());
		this.basicItem(RNItems.MOLTEN_RUBY_ITEM.get());
		this.basicItem(RNItems.RUBY_SHARD_ITEM.get());
		this.basicItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get());
		this.basicItem(RNItems.MUSIC_DISC_SHIMMER.get());



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
