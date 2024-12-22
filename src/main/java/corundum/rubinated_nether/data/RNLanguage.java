package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.*;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RNLanguage extends LanguageProvider {
	public RNLanguage(PackOutput output) {
		super(output, RubinatedNether.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.addBlock(RNBlocks.NETHER_RUBY_ORE, "Nether Ruby Ore");
		this.addBlock(RNBlocks.MOLTEN_RUBY_ORE, "Molten Ruby Ore");
		this.addBlock(RNBlocks.RUBINATED_BLACKSTONE, "Rubinated Blackstone");

		this.addBlock(RNBlocks.RUBY_BLOCK, "Block of Ruby");
		this.addBlock(RNBlocks.MOLTEN_RUBY_BLOCK, "Block of Molten Ruby");
		this.addBlock(RNBlocks.BLEEDING_OBSIDIAN, "Bleeding Obsidian");

		this.addBlock(RNBlocks.RUBY_LANTERN, "Ruby Lantern");
		this.addBlock(RNBlocks.CHANDELIER, "Ruby Chandelier");
		this.addBlock(RNBlocks.LAVA_LAMP, "Molten Ruby Lava Lamp");
		this.addBlock(RNBlocks.DRY_ICE, "Dry Ice");
		this.addBlock(RNBlocks.SOAKSTONE, "Soakstone");

		this.addBlock(RNBlocks.RUBY_GLASS, "Ruby Glass");
		this.addBlock(RNBlocks.RUBY_GLASS_PANE, "Ruby Glass Pane");
		this.addBlock(RNBlocks.ORNATE_RUBY_GLASS, "Ornate Ruby Glass");
		this.addBlock(RNBlocks.ORNATE_RUBY_GLASS_PANE, "Ornate Ruby Glass Pane");
		this.addBlock(RNBlocks.MOLTEN_RUBY_GLASS, "Molten Ruby Glass");
		this.addBlock(RNBlocks.MOLTEN_RUBY_GLASS_PANE, "Molten Ruby Glass Pane");

		this.addBlock(RNBlocks.ALTAR_STONE, "Altar Stone");

		this.addBlock(RNBlocks.POLISHED_ALTAR_STONE, "Polished Altar Stone");
		this.addBlock(RNBlocks.POLISHED_ALTAR_STONE_STAIRS, "Polished Altar Stone Stairs");
		this.addBlock(RNBlocks.POLISHED_ALTAR_STONE_SLAB, "Polished Altar Stone Slab");
		this.addBlock(RNBlocks.POLISHED_ALTAR_STONE_WALL, "Polished Altar Stone Wall");

		this.addBlock(RNBlocks.ALTAR_STONE_TILES, "Altar Stone Tiles");
		this.addBlock(RNBlocks.ALTAR_STONE_TILES_STAIRS, "Altar Stone Tile Stairs");
		this.addBlock(RNBlocks.ALTAR_STONE_TILES_SLAB, "Altar Stone Tile Slab");
		this.addBlock(RNBlocks.ALTAR_STONE_TILES_WALL, "Altar Stone Tile Wall");

		this.addBlock(RNBlocks.ALTAR_STONE_PILLAR, "Altar Stone Pillar");

		this.addBlock(RNBlocks.ALTAR_STONE_BRICKS, "Altar Stone Bricks");
		this.addBlock(RNBlocks.ALTAR_STONE_BRICKS_STAIRS, "Altar Stone Brick Stairs");
		this.addBlock(RNBlocks.ALTAR_STONE_BRICKS_SLAB, "Altar Stone Brick Slab");
		this.addBlock(RNBlocks.ALTAR_STONE_BRICKS_WALL, "Altar Stone Brick Wall");

		this.addBlock(RNBlocks.CHISELED_ALTAR_STONE_BRICKS, "Chiseled Altar Stone Bricks");
		this.addBlock(RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS, "Rubinated Chiseled Altar Stone Bricks");
		this.addBlock(RNBlocks.RUNESTONE, "Runestone");
		this.addBlock(RNBlocks.FREEZER, "Freezer");

		this.addItem(RNItems.RUBY_ITEM, "Ruby");
		this.addItem(RNItems.MOLTEN_RUBY_ITEM, "Molten Ruby");
		this.addItem(RNItems.RUBY_SHARD_ITEM, "Ruby Shard");
		this.addItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM, "Molten Ruby Nugget");

		this.addItem(RNItems.MUSIC_DISC_SHIMMER, "Rubinated Music Disc");

		this.addBlock(RNBlocks.BRONZE_BLOCK, "Block of Bronze");
		this.addBlock(RNBlocks.DISCOLORED_BRONZE_BLOCK, "Discolored Bronze");
		this.addBlock(RNBlocks.CORRODED_BRONZE_BLOCK, "Corroded Bronze");
		this.addBlock(RNBlocks.TARNISHED_BRONZE_BLOCK, "Tarnished Bronze");
		this.addBlock(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK, "Crystallized Bronze");
		this.addBlock(RNBlocks.CUT_BRONZE, "Cut Bronze");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE, "Discolored Cut Bronze");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE, "Corroded Cut Bronze");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE, "Tarnished Cut Bronze");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE, "Crystallized Cut Bronze");


		this.add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		this.add("menu." + RubinatedNether.MODID + ".freezer", "Freezer");
		this.add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Showing Freezable");
	}
}
