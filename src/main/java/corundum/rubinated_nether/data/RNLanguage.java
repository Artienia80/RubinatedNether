package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

public class RNLanguage extends LanguageProvider {
	public RNLanguage(PackOutput output) {
		super(output, RubinatedNether.MODID, "en_us");
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

		addItem(RNItems.BRONZE_ROD, "Bronze Rod");
		addItem(RNItems.BRONZE_SCRAP, "Bronze Scrap");
		addItem(RNItems.BRONZE_SHOT, "Bronze Shot");


		add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		add("menu." + RubinatedNether.MODID + ".freezer", "Freezer");
		add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Showing Freezable");

		add(RubinatedNether.MODID + ".midnightconfig.category.chandelier", "Chandelier");
		add(RubinatedNether.MODID + ".midnightconfig.category.brazier", "Brazier");
		add(RubinatedNether.MODID + ".midnightconfig.category.client", "Client");
		add("jukebox_song." + RubinatedNether.MODID + ".shimmer", "Quizzly - Shimmer");

		add("death.attack.fallingBlock", "%1$s was embraced by a falling chandelier");
		add("death.attack.fallingBlock.player", "%1$s was embraced by a falling chandelier whilst fighting %2$s");
		
		add("advancements.rubinated_nether.obtain_bleeding_obsidian.title","Blood For The Blood God!");
		add("advancements.rubinated_nether.obtain_bleeding_obsidian.description","Obtain Bleeding Obsidian");
		add("advancements.rubinated_nether.obtain_freezer.title","Cold! Cold! Cold!");
		add("advancements.rubinated_nether.obtain_freezer.description","Craft the Freezer");
		add("advancements.rubinated_nether.obtain_frosted_ice.title","Re-Obtainable");
		add("advancements.rubinated_nether.obtain_frosted_ice.description","Freeze Snow or Water into Frosted Ice");
		add("advancements.rubinated_nether.obtain_rubinated_blackstone.title","Legitimate Salvage");
		add("advancements.rubinated_nether.obtain_rubinated_blackstone.description","Mine Rubinated Blackstone from a Bastion Remnant");
		add("advancements.rubinated_nether.obtain_molten_ruby.title","Hot! Hot! Hot!");
		add("advancements.rubinated_nether.obtain_molten_ruby.description"," Acquire a Molten Ruby from Magma Veins");
		add("advancements.rubinated_nether.obtain_ruby.title","Welcome back, Old Friend");
		add("advancements.rubinated_nether.obtain_ruby.description","Acquire a Ruby from Nether Ruby Ore or freezing");
		add("advancements.rubinated_nether.obtain_ruby_glass.title","Over 9000!");
		add("advancements.rubinated_nether.obtain_ruby_glass.description","Craft the blast-resistant Ruby Glass");
		add("advancements.rubinated_nether.obtain_ruby_laser.title","Tag, You're It!");
		add("advancements.rubinated_nether.obtain_ruby_laser.description","Craft the Ruby Laser");
		add("advancements.rubinated_nether.obtain_ruby_lights.title","Brighten Your Day!");
		add("advancements.rubinated_nether.obtain_ruby_lights.description","Have a Ruby Chandellier, Lantern and Lava Lamp at the same time in your inventory.");
		add("advancements.rubinated_nether.obtain_brazier.title","I'll Be Back");
		add("advancements.rubinated_nether.obtain_brazier.description","Craft the Brazier");
		add("advancements.rubinated_nether.wear_lens.title","Rose-Tinted Glasses");
		add("advancements.rubinated_nether.wear_lens.description","Equip a pair of Ruby Lenses to see lasers");
		add("advancements.rubinated_nether.enter_shrine.title","Dimension Expansion");
		add("advancements.rubinated_nether.enter_shrine.description","Undergo a Shrine Ritual");
		add("advancements.rubinated_nether.rubinous_ritual.title","Malevolent Shrine");
		add("advancements.rubinated_nether.rubinous_ritual.description","Undergo a Rubinous Ritual");
		add("advancements.rubinated_nether.bronze_rod.title","It's All Connected");
		add("advancements.rubinated_nether.bronze_rod.description","Obtain a Bronze Rod");
		add("advancements.rubinated_nether.bronze_block.title","The Emperor's New Ore");
		add("advancements.rubinated_nether.bronze_block.description","Obtain any Bronze Block");
		add("advancements.rubinated_nether.shrine_sentinel.title","A Heavy Burden");
		add("advancements.rubinated_nether.shrine_sentinel.description","Obtain a Bronze Statue from a Shrine Sentinel");
	
		add("gui.rubinated_nether.jei.freezer", "Freezing");
	}

	private void addWaxableBlock(DeferredBlock<?> block, String name) {
		addBlock(block, name);
		add("item." + RubinatedNether.MODID + "." + WaxableBlockItem.getWaxableItem(block), "Waxed " + name);
	}
}
