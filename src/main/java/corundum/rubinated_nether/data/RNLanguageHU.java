package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNCreativeTabs;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RNLanguageHU extends LanguageProvider {
	public RNLanguageHU(PackOutput output) {
		super(output, RubinatedNether.MODID, "hu_hu");
	}

	@Override
	protected void addTranslations() {
		this.addBlock(RNBlocks.NETHER_RUBY_ORE, "Netherrubinérc");
		this.addBlock(RNBlocks.MOLTEN_RUBY_ORE, "Olvadt rubinérc");
		this.addBlock(RNBlocks.RUBINATED_BLACKSTONE, "Rubinozott feketekő");

		this.addBlock(RNBlocks.RUBY_BLOCK, "Rubinblokk");
		this.addBlock(RNBlocks.MOLTEN_RUBY_BLOCK, "Olvadt rubinblokk");
		this.addBlock(RNBlocks.BLEEDING_OBSIDIAN, "Vérző obszidián");

		this.addBlock(RNBlocks.RUBY_LANTERN, "Rubinlámpás");
		this.addBlock(RNBlocks.CHANDELIER, "Rubincsillár");
		this.addBlock(RNBlocks.LAVA_LAMP, "Olvadt rubin lávalámpa");
		this.addBlock(RNBlocks.DRY_ICE, "Szárazjég");
		this.addBlock(RNBlocks.SOAKSTONE, "Ázókő");

		this.addBlock(RNBlocks.RUBY_GLASS, "Rubinüveg");
		this.addBlock(RNBlocks.RUBY_GLASS_PANE, "Rubinüveglap");
		this.addBlock(RNBlocks.ORNATE_RUBY_GLASS, "Dísz rubinüveg");
		this.addBlock(RNBlocks.ORNATE_RUBY_GLASS_PANE, "Dísz rubinüveglap");
		this.addBlock(RNBlocks.MOLTEN_RUBY_GLASS, "Olvadt rubinüveg");
		this.addBlock(RNBlocks.MOLTEN_RUBY_GLASS_PANE, "Olvadt rubinüveglap");

		this.addBlock(RNBlocks.SHRINE_STONE, "Templomkő");

		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE, "Csiszolt templomkő");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_STAIRS, "Csiszolt templomkőlépcső");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_SLAB, "Csiszolt templomkőlap");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_WALL, "Csiszolt templomkőfal");

		this.addBlock(RNBlocks.SHRINE_STONE_TILES, "Templomkő csempe");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_STAIRS, "Templomkő csempelépcső");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_SLAB, "Templomkő csempelap");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_WALL, "Templomkő csempefal");

		this.addBlock(RNBlocks.SHRINE_STONE_PILLAR, "Templomkőoszlop");

		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS, "Templomkőtégla");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_STAIRS, "Templomkőtégla-lépcső");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_SLAB, "Templomkőtégla-lap");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_WALL, "Templomkőtégla-fal");

		this.addBlock(RNBlocks.CHISELED_SHRINE_STONE_BRICKS, "Vésett templomkőtégla");
		this.addBlock(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS, "Rubinozott Vésett Templomkőtégla");
		this.addBlock(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS, "binozott templomkőtégla");

		this.addBlock(RNBlocks.RUBY_LASER, "Rubinlézer");
		this.addItem(RNItems.RUBY_LENS, "Rubinlencse");

		this.addBlock(RNBlocks.RUNESTONE, "Rúnakő");
		this.addBlock(RNBlocks.FREEZER, "Fagyasztó");
		this.addBlock(RNBlocks.BRAZIER, "Parázstartó");

		this.addItem(RNItems.RUBY_ITEM, "Rubin");
		this.addItem(RNItems.MOLTEN_RUBY_ITEM, "Olvadt rubin");
		this.addItem(RNItems.RUBY_SHARD_ITEM, "Rubinszilánk");
		this.addItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM, "Olvadt rubinrög");

		this.addItem(RNItems.MUSIC_DISC_SHIMMER, "Rubinozott hanglemez");

		this.addBlock(RNBlocks.DISCOLORED_BRONZE_BLOCK, "Elszíneződött bronzblokk");
		this.addBlock(RNBlocks.CORRODED_BRONZE_BLOCK, "Rozsdásodott bronzblokk");
		this.addBlock(RNBlocks.TARNISHED_BRONZE_BLOCK, "Kopott bronzblokk");
		this.addBlock(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK, "Kristályosodott bronzblokk");

		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR, "Vágott bronzoszlop");
		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR_STAIRS, "Vágott bronzoszlop-lépcső");
		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR_SLAB, "Vágott bronzoszlop-lap");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR, "Elszíneződött vágott bronzoszlop");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_STAIRS, "Elszíneződött vágott bronzoszlop-lépcső");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_SLAB, "Elszíneződött vágott bronzoszlop-lap");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR, "Rozsdásodott vágott bronzoszlop");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_STAIRS, "Rozsdásodott vágott bronzoszlop-lépcső");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_SLAB, "Rozsdásodott vágott bronzoszlop-lap");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR, "Kopott vágott bronzoszlop");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_STAIRS, "Kopott vágott bronzoszlop-lépcső");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_SLAB, "Kopott vágott bronzoszlop-lap");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR, "Kristályosodott vágott bronzoszlop");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_STAIRS, "Kristályosodott vágott bronzoszlop-lépcső");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_SLAB, "Kristályosodott vágott bronzoszlop-lap");

		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS, "Vágott bronztégla");
		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS_STAIRS, "Vágott bronztégla-lépcső");
		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS_SLAB, "Vágott bronztégla-lap");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS, "Elszíneződött vágott bronztégla");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS, "Elszíneződött vágott bronztégla-lépcső");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB, "Elszíneződött vágott bronztégla-lap");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS, "Rozsdásodott vágott bronztégla");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS, "Rozsdásodott vágott bronztégla-lépcső");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB, "Rozsdásodott vágott bronztégla-lap");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS, "Kopott vágott bronztégla");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS, "Kopott vágott bronztégla-lépcső");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB, "Kopott vágott bronztégla-lap");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS, "Kristályosodott vágott bronztégla");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS, "Kristályosodott vágott bronztégla-lépcső");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB, "Kristályosodott vágott bronztégla-lap");

		this.addItem(RNItems.BRONZE_ROD, "Bronzrúd");
		this.addItem(RNItems.BRONZE_SCRAP, "Bronzdarab");

		this.add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		this.add("menu." + RubinatedNether.MODID + ".freezer", "Fagyasztó");
		this.add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Fagyaszthatók");

		this.add(RubinatedNether.MODID + ".midnightconfig.category.chandelier", "Rubincsillár");
		this.add(RubinatedNether.MODID + ".midnightconfig.category.brazier", "Parázstartó");
		this.add(RubinatedNether.MODID + ".midnightconfig.category.client", "Kliens");
		this.add("jukebox_song." + RubinatedNether.MODID + ".shimmer", "Quizzly - Shimmer");

		this.add("death.attack.fallingBlock", "%1$s megölelt egy hulló rubincsillárt");
		this.add("death.attack.fallingBlock.player", "%1$s megölelt egy hulló rubincsillárt miközbén %2$s támadta");

		this.add("advancements.rubinated_nether.obtain_bleeding_obsidian.title", "Vért a véristennek!");
		this.add("advancements.rubinated_nether.obtain_bleeding_obsidian.description", "Szerezz vérző obszidiánt");
		this.add("advancements.rubinated_nether.ruby_barter.title", "...jobb mint a semmi");
		this.add("advancements.rubinated_nether.ruby_barter.description", "Cserélj Piglinekkel egy rubinszilánkkal");
		this.add("advancements.rubinated_nether.obtain_freezer.title", "Hideg! Hideg! Hideg!");
		this.add("advancements.rubinated_nether.obtain_freezer.description", "Barkácsolj egy fagyasztót");
		this.add("advancements.rubinated_nether.obtain_molten_ruby.title", "Forró! Forró! Forró!");
		this.add("advancements.rubinated_nether.obtain_molten_ruby.description", "Szerezz egy olvadt rubint");
		this.add("advancements.rubinated_nether.obtain_ruby.title", "Üdv újra, régi cimborám.");
		this.add("advancements.rubinated_nether.obtain_ruby.description", "Szerezz egy rubin");
		this.add("advancements.rubinated_nether.obtain_ruby_lights.title", "Ne legyés borús!");
		this.add("advancements.rubinated_nether.obtain_ruby_lights.description", "Legyen egy rubincsillár és rubin lávalámpa az eszköztáradban");
		this.add("advancements.rubinated_nether.obtain_rubinated_blackstone.title", "Jogos zsákmány");
		this.add("advancements.rubinated_nether.obtain_rubinated_blackstone.description", "Bányássz egy rubinozott feketekövet egy bástyaromból");
		this.add("advancements.rubinated_nether.obtain_ruby_glass.title", "Messze 8000 fölött!");
		this.add("advancements.rubinated_nether.obtain_ruby_glass.description", "Barkácsold a páncélozott Rubinüveget");
		this.add("advancements.rubinated_nether.obtain_frosted_ice.title", "Újra-szerezhető");
		this.add("advancements.rubinated_nether.obtain_frosted_ice.description", "Fagyassz egy hóblokkot gyorsfagyasztott jéggé");
		this.add("advancements.rubinated_nether.wear_lens.title", "Rózsás kilátás");
		this.add("advancements.rubinated_nether.wear_lens.description", "Vegyél fel egy Rubin lencsét és lásd a Rubinlézert!");
		this.add("advancements.rubinated_nether.obtain_ruby_laser.title", "Ha én cica volnék...");
		this.add("advancements.rubinated_nether.obtain_ruby_laser.description", "Barkácsolj egy Rubinlézert");
		this.add("advancements.rubinated_nether.obtain_brazier.title", "Még élni akarok!");
		this.add("advancements.rubinated_nether.obtain_brazier.description", "Varkécsold a parázstartót");
		this.add("advancements.rubinated_nether.enter_shrine.title", "Szezám tárulj");
		this.add("advancements.rubinated_nether.enter_shrine.description", "Vegyél részt a templom-rituáléban");
		this.add("advancements.rubinated_nether.rubinous_ritual.title", "Vöröseső hulljon nékem");
		this.add("advancements.rubinated_nether.rubinous_ritual.description", "Vegyél részt a rubinozott-rituáléban");
		this.add("advancements.rubinated_nether.bronze_rod.title", "Mindennek mindenhez köze van");
		this.add("advancements.rubinated_nether.bronze_rod.description", "Szerezz egy bronzrúdat");
		this.add("advancements.rubinated_nether.bronze_block.title", "Egyszer volt templomba bronzvásár");
		this.add("advancements.rubinated_nether.bronze_block.description", "Szerezz bármi bronzblokkot");
		this.add("advancements.rubinated_nether.shrine_sentinel.title", "Nehéz súly");
		this.add("advancements.rubinated_nether.shrine_sentinel.description", "Szerezz egy bronz szobrot a templomőröktől");
	
		this.add("gui.rubinated_nether.jei.freezer", "TODO!");
	}
}
