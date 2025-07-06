package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNCreativeTabs;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.data.providers.RNLanguageProvider;
import net.minecraft.data.PackOutput;

public class RNLanguage extends RNLanguageProvider {
	public RNLanguage(PackOutput output) {
		super(output, "en_us");
	}

	@Override
	protected void addTranslations() {
		addBlock(RNBlocks.NETHER_RUBY_ORE, "Nether Ruby Ore");
		addBlock(RNBlocks.MOLTEN_RUBY_ORE, "Molten Ruby Ore");
		addBlock(RNBlocks.RUBINATED_BLACKSTONE, "Rubinated Blackstone");

		addBlock(RNBlocks.RUBY_BLOCK, "Block of Ruby");
		addBlock(RNBlocks.MOLTEN_RUBY_BLOCK, "Block of Molten Ruby");
		addBlock(RNBlocks.BLEEDING_OBSIDIAN, "Bleeding Obsidian");

		addBlock(RNBlocks.RUBY_LANTERN, "Ruby Lantern");
		addBlock(RNBlocks.CHANDELIER, "Ruby Chandelier");
		addBlock(RNBlocks.LAVA_LAMP, "Molten Ruby Lava Lamp");
		addBlock(RNBlocks.DRY_ICE, "Dry Ice");
		addBlock(RNBlocks.SOAKSTONE, "Soakstone");

		addBlock(RNBlocks.RUBY_GLASS, "Ruby Glass");
		addBlock(RNBlocks.RUBY_GLASS_PANE, "Ruby Glass Pane");
		addBlock(RNBlocks.ORNATE_RUBY_GLASS, "Ornate Ruby Glass");
		addBlock(RNBlocks.ORNATE_RUBY_GLASS_PANE, "Ornate Ruby Glass Pane");
		addBlock(RNBlocks.MOLTEN_RUBY_GLASS, "Molten Ruby Glass");
		addBlock(RNBlocks.MOLTEN_RUBY_GLASS_PANE, "Molten Ruby Glass Pane");

		addBlock(RNBlocks.SHRINE_STONE, "Shrine Stone");

		addBlock(RNBlocks.POLISHED_SHRINE_STONE, "Polished Shrine Stone");
		addBlock(RNBlocks.POLISHED_SHRINE_STONE_STAIRS, "Polished Shrine Stone Stairs");
		addBlock(RNBlocks.POLISHED_SHRINE_STONE_SLAB, "Polished Shrine Stone Slab");
		addBlock(RNBlocks.POLISHED_SHRINE_STONE_WALL, "Polished Shrine Stone Wall");

		addBlock(RNBlocks.SHRINE_STONE_TILES, "Shrine Stone Tiles");
		addBlock(RNBlocks.SHRINE_STONE_TILES_STAIRS, "Shrine Stone Tile Stairs");
		addBlock(RNBlocks.SHRINE_STONE_TILES_SLAB, "Shrine Stone Tile Slab");
		addBlock(RNBlocks.SHRINE_STONE_TILES_WALL, "Shrine Stone Tile Wall");

		addBlock(RNBlocks.SHRINE_STONE_PILLAR, "Shrine Stone Pillar");

		addBlock(RNBlocks.SHRINE_STONE_BRICKS, "Shrine Stone Bricks");
		addBlock(RNBlocks.SHRINE_STONE_BRICKS_STAIRS, "Shrine Stone Brick Stairs");
		addBlock(RNBlocks.SHRINE_STONE_BRICKS_SLAB, "Shrine Stone Brick Slab");
		addBlock(RNBlocks.SHRINE_STONE_BRICKS_WALL, "Shrine Stone Brick Wall");

		addBlock(RNBlocks.CHISELED_SHRINE_STONE_BRICKS, "Chiseled Shrine Stone Bricks");
		addBlock(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS, "Rubinated Chiseled Shrine Stone Bricks");
		addBlock(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS, "Rubinated Shrine Stone Bricks");

		addBlock(RNBlocks.RUBY_LASER, "Ruby Laser");
		addItem(RNItems.RUBY_LENS, "Ruby Lens");

		addBlock(RNBlocks.RUNESTONE, "Runestone");
		addBlock(RNBlocks.RUBINATION_ALTAR, "Rubination Altar");

		addBlock(RNBlocks.FREEZER, "Freezer");
		addBlock(RNBlocks.BRAZIER, "Brazier");

		addItem(RNItems.RUBY_ITEM, "Ruby");
		addItem(RNItems.MOLTEN_RUBY_ITEM, "Molten Ruby");
		addItem(RNItems.RUBY_SHARD_ITEM, "Ruby Shard");
		addItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM, "Molten Ruby Nugget");

		addItem(RNItems.MUSIC_DISC_SHIMMER, "Rubinated Music Disc");

		addWaxableBlock(RNBlocks.BRONZE_BLOCK, "Block of Bronze");
		addWaxableBlock(RNBlocks.DISCOLORED_BRONZE_BLOCK, "Discolored Bronze");
		addWaxableBlock(RNBlocks.CORRODED_BRONZE_BLOCK, "Corroded Bronze");
		addWaxableBlock(RNBlocks.TARNISHED_BRONZE_BLOCK, "Tarnished Bronze");
		addWaxableBlock(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK, "Crystallized Bronze");

		addWaxableBlock(RNBlocks.CHISELED_BRONZE, "Chiseled Bronze");
		addWaxableBlock(RNBlocks.DISCOLORED_CHISELED_BRONZE, "Discolored Chiseled Bronze");
		addWaxableBlock(RNBlocks.CORRODED_CHISELED_BRONZE, "Corroded Chiseled Bronze");
		addWaxableBlock(RNBlocks.TARNISHED_CHISELED_BRONZE, "Tarnished Chiseled Bronze");
		addWaxableBlock(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE, "Crystallized Chiseled Bronze");

		addWaxableBlock(RNBlocks.CUT_BRONZE_PILLAR, "Cut Bronze Pillar");
		addWaxableBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR, "Discolored Cut Bronze Pillar");
		addWaxableBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR, "Corroded Cut Bronze Pillar");
		addWaxableBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR, "Tarnished Cut Bronze Pillar");
		addWaxableBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR, "Crystallized Cut Bronze Pillar");

		addWaxableBlock(RNBlocks.CUT_BRONZE_BRICKS, "Cut Bronze Bricks");
		addWaxableBlock(RNBlocks.CUT_BRONZE_BRICKS_STAIRS, "Cut Bronze Brick Stairs");
		addWaxableBlock(RNBlocks.CUT_BRONZE_BRICKS_SLAB, "Cut Bronze Brick Slab");

		addWaxableBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS, "Discolored Cut Bronze Bricks");
		addWaxableBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS, "Discolored Cut Bronze Brick Stairs");
		addWaxableBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB, "Discolored Cut Bronze Brick Slab");

		addWaxableBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS, "Corroded Cut Bronze Bricks");
		addWaxableBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS, "Corroded Cut Bronze Brick Stairs");
		addWaxableBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB, "Corroded Cut Bronze Brick Slab");

		addWaxableBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS, "Tarnished Cut Bronze Bricks");
		addWaxableBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS, "Tarnished Cut Bronze Brick Stairs");
		addWaxableBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB, "Tarnished Cut Bronze Brick Slab");

		addWaxableBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS, "Crystallized Cut Bronze Bricks");
		addWaxableBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS, "Crystallized Cut Bronze Brick Stairs");
		addWaxableBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB, "Crystallized Cut Bronze Brick Slab");

		addWaxableBlock(RNBlocks.BRONZE_BULB, "Bronze Bulb");
		addWaxableBlock(RNBlocks.DISCOLORED_BRONZE_BULB, "Discolored Bronze Bulb");
		addWaxableBlock(RNBlocks.CORRODED_BRONZE_BULB, "Corroded Bronze Bulb");
		addWaxableBlock(RNBlocks.TARNISHED_BRONZE_BULB, "Tarnished Bronze Bulb");
		addWaxableBlock(RNBlocks.CRYSTALLIZED_BRONZE_BULB, "Crystallized Bronze Bulb");


		addItem(RNItems.BRONZE_ROD, "Bronze Rod");
		addItem(RNItems.BRONZE_POWDER, "Bronze Scrap");
		addItem(RNItems.BRONZE_SHOT, "Bronze Shot");
		addItem(RNItems.BRONZE_DRILL, "Bronze Drill");

		addItem(RNItems.GREED_RUNE, "Rune of Greed");
		addItem(RNItems.SLOTH_RUNE, "Rune of Sloth");
		addItem(RNItems.GLUTTONY_RUNE, "Rune of Gluttony");

		addItem(RNItems.WRATH_RUNE, "Rune of Wrath");
		addItem(RNItems.ENVY_RUNE, "Rune of Envy");
		addItem(RNItems.VAINGLORY_RUNE, "Rune of Vainglory");

		addItem(RNItems.PRIDE_RUNE, "Rune of Pride");
		addItem(RNItems.ACEDIA_RUNE, "Rune of Acedia");
		addItem(RNItems.LUXURIA_RUNE, "Rune of Luxuria");

		addItem(RNItems.INSIDIAE_RUNE, "Rune of Insidiae");
		addItem(RNItems.SUPERBIA_RUNE, "Rune of Superbia");
		addItem(RNItems.TRISTIA_RUNE, "Rune of Tristia");

		addItem(RNItems.STUDIOSE_RUNE, "Rune of Studiose");
		addItem(RNItems.ARDENTER_RUNE, "Rune of Ardenter");
		addItem(RNItems.NIMIS_RUNE, "Rune of Nimis");

		addItem(RNItems.RITUAL_OFFERING, "Ritual Offering");
		addItem(RNItems.WINDING_KEY, "Winding Key");

		add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		add("menu." + RubinatedNether.MODID + ".freezer", "Freezer");
		add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Showing Freezable");

		add(RubinatedNether.MODID + ".midnightconfig.category.chandelier", "Chandelier");
		add(RubinatedNether.MODID + ".midnightconfig.category.brazier", "Brazier");
		add(RubinatedNether.MODID + ".midnightconfig.category.client", "Client");
		add("jukebox_song." + RubinatedNether.MODID + ".shimmer", "Quizzly - Shimmer");

		add("death.attack.fallingBlock", "%1$s was embraced by a falling chandelier");
		add("death.attack.fallingBlock.player", "%1$s was embraced by a falling chandelier whilst fighting %2$s");
		
		addAdvancement("obtain_bleeding_obsidian", "Blood For The Blood God!", "Obtain Bleeding Obsidian");
		addAdvancement("obtain_freezer", "Cold! Cold! Cold!", "Craft a Freezer");
		addAdvancement("obtain_frosted_ice", "Re-Obtainable", "Freeze Snow or Water into Frosted Ice");
		addAdvancement("obtain_rubinated_blackstone", "Legitimate Salvage", "Mine Rubinated Blackstone from a Bastion Remnant");
		addAdvancement("obtain_molten_ruby", "Hot! Hot! Hot!", "Acquire a Molten Ruby from Magma Veins");
		addAdvancement("obtain_ruby","Welcome back, Old Friend", "Acquire a Ruby from Nether Ruby Ore or freezing");
		addAdvancement("obtain_ruby_glass", "Over 9000!", "Craft the blast-resistant Ruby Glass");
		addAdvancement("obtain_ruby_laser", "Tag, You're It!", "Craft the Ruby Laser");		
		addAdvancement("obtain_ruby_lights", "Brighten Your Day!", "Have a Ruby Chandellier, Lantern and Lava Lamp at the same time in your inventory.");
		addAdvancement("obtain_brazier", "I'll Be Back", "Craft a Brazier");
		addAdvancement("wear_lens", "Rose-Tinted Glasses", "Equip a pair of Ruby Lenses to see lasers");
		addAdvancement("enter_shrine", "Dimension Expansion", "Undergo a Shrine Ritual");
		addAdvancement("rubinous_ritual", "Malevolent Shrine", "Undergo a Rubinous Ritual");
		addAdvancement("bronze_rod", "It's All Connected", "Obtain a Bronze Rod");
		addAdvancement("bronze_block", "The Emperor's New Ore", "Obtain any Bronze Block");
		addAdvancement("shrine_sentinel", "A Heavy Burden", "Obtain a Bronze Statue from a Shrine Sentinel");

		add("gui.rubinated_nether.jei.freezer", "Freezing");
	}
}
