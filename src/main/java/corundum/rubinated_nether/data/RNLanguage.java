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

		this.addBlock(RNBlocks.SHRINE_STONE, "Shrine Stone");

		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE, "Polished Shrine Stone");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_STAIRS, "Polished Shrine Stone Stairs");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_SLAB, "Polished Shrine Stone Slab");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_WALL, "Polished Shrine Stone Wall");

		this.addBlock(RNBlocks.SHRINE_STONE_TILES, "Shrine Stone Tiles");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_STAIRS, "Shrine Stone Tile Stairs");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_SLAB, "Shrine Stone Tile Slab");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_WALL, "Shrine Stone Tile Wall");

		this.addBlock(RNBlocks.SHRINE_STONE_PILLAR, "Shrine Stone Pillar");

		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS, "Shrine Stone Bricks");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_STAIRS, "Shrine Stone Brick Stairs");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_SLAB, "Shrine Stone Brick Slab");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_WALL, "Shrine Stone Brick Wall");

		this.addBlock(RNBlocks.CHISELED_SHRINE_STONE_BRICKS, "Chiseled Shrine Stone Bricks");
		this.addBlock(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS, "Rubinated Chiseled Shrine Stone Bricks");
		this.addBlock(RNBlocks.RUNESTONE, "Runestone");
		this.addBlock(RNBlocks.FREEZER, "Freezer");
		this.addBlock(RNBlocks.BRAZIER, "Ruby Brazier");

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

		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR, "Cut Bronze Pillar");
		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR_STAIRS, "Cut Bronze Pillar Stairs");
		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR_SLAB, "Cut Bronze Pillar Slab");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR, "Discolored Cut Bronze Pillar");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_STAIRS, "Discolored Cut Bronze Pillar Stairs");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_SLAB, "Discolored Cut Bronze Pillar Slab");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR, "Corroded Cut Bronze Pillar");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_STAIRS, "Corroded Cut Bronze Pillar Stairs");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_SLAB, "Corroded Cut Bronze Pillar Slab");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR, "Tarnished Cut Bronze Pillar");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_STAIRS, "Tarnished Cut Bronze Pillar Stairs");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_SLAB, "Tarnished Cut Bronze Pillar Slab");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR, "Crystallized Cut Bronze Pillar");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_STAIRS, "Crystallized Cut Bronze Pillar Stairs");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_SLAB, "Crystallized Cut Bronze Pillar Slab");

		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS, "Cut Bronze Bricks");
		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS_STAIRS, "Cut Bronze Brick Stairs");
		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS_SLAB, "Cut Bronze Brick Slab");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS, "Discolored Cut Bronze Bricks");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS, "Discolored Cut Bronze Brick Stairs");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB, "Discolored Cut Bronze Brick Slab");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS, "Corroded Cut Bronze Bricks");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS, "Corroded Cut Bronze Brick Stairs");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB, "Corroded Cut Bronze Brick Slab");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS, "Tarnished Cut Bronze Bricks");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS, "Tarnished Cut Bronze Brick Stairs");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB, "Tarnished Cut Bronze Brick Slab");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS, "Crystallized Cut Bronze Bricks");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS, "Crystallized Cut Bronze Brick Stairs");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB, "Crystallized Cut Bronze Brick Slab");


		this.add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		this.add("menu." + RubinatedNether.MODID + ".freezer", "Freezer");
		this.add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Showing Freezable");

		this.add(RubinatedNether.MODID + ".midnightconfig.category.chandelier", "Chandelier");
		this.add(RubinatedNether.MODID + ".midnightconfig.category.brazier", "Brazier");
		this.add(RubinatedNether.MODID + ".midnightconfig.category.client", "Client");
	}
}
