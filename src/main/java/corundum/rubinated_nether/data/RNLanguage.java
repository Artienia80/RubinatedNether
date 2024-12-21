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
		this.addBlock(RNBlocks.ALTAR_STONE_TILES, "Altar Stone Tiles");
		this.addBlock(RNBlocks.ALTAR_STONE_PILLAR, "Altar Stone Pillar");
		this.addBlock(RNBlocks.ALTAR_STONE_BRICKS, "Altar Stone Bricks");
		this.addBlock(RNBlocks.CHISELED_ALTAR_STONE_BRICKS, "Chiseled Altar Stone Bricks");
		this.addBlock(RNBlocks.BLEEDING_CHISELED_ALTAR_STONE_BRICKS, "Bleeding Chiseled Altar Stone Bricks");
		this.addBlock(RNBlocks.RUNESTONE, "Runestone");
		this.addBlock(RNBlocks.FREEZER, "Freezer");

		this.addItem(RNItems.RUBY_ITEM, "Ruby");
		this.addItem(RNItems.MOLTEN_RUBY_ITEM, "Molten Ruby");
		this.addItem(RNItems.RUBY_SHARD_ITEM, "Ruby Shard");
		this.addItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM, "Molten Ruby Nugget");

		this.addItem(RNItems.MUSIC_DISC_SHIMMER, "Rubinated Music Disc");




		this.add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		this.add("menu." + RubinatedNether.MODID + ".freezer", "Freezer");
		this.add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Showing Freezable");
	}
}
