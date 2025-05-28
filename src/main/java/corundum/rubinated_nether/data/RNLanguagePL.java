package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.data.providers.RNLanguageProvider;
import net.minecraft.data.PackOutput;

public class RNLanguagePL extends RNLanguageProvider {
	public RNLanguagePL(PackOutput output) {
		super(output, "pl_pl");
	}

	@Override
	protected void addTranslations() {
		this.addBlock(RNBlocks.NETHER_RUBY_ORE, "Netherowe złoże rubinu");
		this.addBlock(RNBlocks.MOLTEN_RUBY_ORE, "Stopione złoże rubinu");
		this.addBlock(RNBlocks.RUBINATED_BLACKSTONE, "Rubinowy Czernit");

		this.addBlock(RNBlocks.RUBY_BLOCK, "Blok Rubinu");
		this.addBlock(RNBlocks.MOLTEN_RUBY_BLOCK, "Blok stopionego");
		this.addBlock(RNBlocks.BLEEDING_OBSIDIAN, "Krwawiący Obsydian");

		this.addBlock(RNBlocks.RUBY_LANTERN, "Rubinowa Latarenka");
		this.addBlock(RNBlocks.CHANDELIER, "Rubinowy Żyrandol");
		this.addBlock(RNBlocks.LAVA_LAMP, "Rubinowa Lampa Lawowa");
		this.addBlock(RNBlocks.DRY_ICE, "Suchy lód");
		this.addBlock(RNBlocks.SOAKSTONE, "Nasiąkamień");

		this.addBlock(RNBlocks.RUBY_GLASS, "Rubinowe Szkło");
		this.addBlock(RNBlocks.RUBY_GLASS_PANE, "Rubinowa Szyba");
		this.addBlock(RNBlocks.ORNATE_RUBY_GLASS, "Ozdobne Rubinowe Szkło");
		this.addBlock(RNBlocks.ORNATE_RUBY_GLASS_PANE, "Ozdobna Rubinowa Szyba");
		this.addBlock(RNBlocks.MOLTEN_RUBY_GLASS, "Szkło z Stopionego Rubinu");
		this.addBlock(RNBlocks.MOLTEN_RUBY_GLASS_PANE, "Szyba z Stopionego");

		this.addBlock(RNBlocks.SHRINE_STONE, "Kamień Świątynny");

		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE, "Wypolerowany Kamień Świątynny");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_STAIRS, "Wypolerowane Kamienne Świątynne Schody");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_SLAB, "Wypolerowana Kamienna Świątynna Płyta ");
		this.addBlock(RNBlocks.POLISHED_SHRINE_STONE_WALL, "Wypolerowany Kamienny Świątynny Murek");

		this.addBlock(RNBlocks.SHRINE_STONE_TILES, "Kamienne Świątynne Kafelki");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_STAIRS, "Kamienne Świątynne Kafelkowe Schody");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_SLAB, "Kamienna Świątynna Kafelkowa Płyta");
		this.addBlock(RNBlocks.SHRINE_STONE_TILES_WALL, "Kamienny Świątynny Kafelkowy Murek");

		this.addBlock(RNBlocks.SHRINE_STONE_PILLAR, "Kamienny Świątynny Filar");

		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS, "Kamienne Świątynne Cegły");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_STAIRS, "Kamienne Świątynne Ceglane Schody");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_SLAB, "Kamienna Świątynna Ceglana Płyta");
		this.addBlock(RNBlocks.SHRINE_STONE_BRICKS_WALL, "Kamienny Świątynny Ceglany Murek");

		this.addBlock(RNBlocks.CHISELED_SHRINE_STONE_BRICKS, "Rzeźbione Kamienne Świątynne Cegły");
		this.addBlock(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS, "Rubinowe Rzeźbione Kamienne Świątynne Cegły");
		this.addBlock(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS, "Rubinowe Kamienne Świątynne Cegły");

		this.addBlock(RNBlocks.RUBY_LASER, "Rubinowy Laser");
		this.addItem(RNItems.RUBY_LENS, "Rubinowa Soczewka");

		this.addBlock(RNBlocks.RUNESTONE, "Runiczny Kamień");
		this.addBlock(RNBlocks.FREEZER, "Zamrażarka");
		this.addBlock(RNBlocks.BRAZIER, "Palenisko");

		this.addItem(RNItems.RUBY_ITEM, "Rubin");
		this.addItem(RNItems.MOLTEN_RUBY_ITEM, "Stopiony Rubin");
		this.addItem(RNItems.RUBY_SHARD_ITEM, "Odłamek Rubinu");
		this.addItem(RNItems.MOLTEN_RUBY_NUGGET_ITEM, "Bryłka Stopionego rubinu");

		this.addItem(RNItems.MUSIC_DISC_SHIMMER, "Rubinowa Płyta Muzyczna");

		this.addBlock(RNBlocks.BRONZE_BLOCK, "Blok Brązu");
		this.addBlock(RNBlocks.DISCOLORED_BRONZE_BLOCK, "Przebarwiony Brąz");
		this.addBlock(RNBlocks.CORRODED_BRONZE_BLOCK, "Zardzewiały Brąz");
		this.addBlock(RNBlocks.TARNISHED_BRONZE_BLOCK, "Wyniszczony Brąz");
		this.addBlock(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK, "Skrystalizowany Brąz");

		this.addBlock(RNBlocks.CUT_BRONZE_PILLAR, "Przycięty Brązowy Filar");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR, "Przebarwiony Przycięty Brązowy Filar");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_PILLAR, "Zardzewiały Przycięty Brązowy Filar");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR, "Wyniszczony Przycięty Brązowy Filar");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR, "Skrystalizowany Przycięty Brązowy Filar");

		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS, "Przycięte Brązowe Cegły");
		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS_STAIRS, "Przycięte Brązowe Ceglane Schody");
		this.addBlock(RNBlocks.CUT_BRONZE_BRICKS_SLAB, "Przycięta Brązowa Ceglana Płyta");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS, "Przebarwione Przycięte Brązowe Cegły");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS, "Przebarwione Przycięte Brązowe Ceglane Schody");
		this.addBlock(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB, "Przebarwiona Przycięta Brązowa Ceglana Płyta");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS, "Zardzewiałe Przycięte Brązowe Cegły");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS, "Zardzewiałe Przycięte Brązowe Ceglane Schody");
		this.addBlock(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB, "Zardzewiała Przycięta Brązowa Ceglana Płyta");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS, "Wyniszczone Przycięte Brązowe Cegły");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS, "Wyniszczone Przycięte Brązowe Ceglane Schody");
		this.addBlock(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB, "Wyniszczona Przycięta Brązowa Ceglana Płyta");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS, "Skrystalizowane");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS, "Crystallized Cut Bronze Brick Stairs");
		this.addBlock(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB, "Crystallized Cut Bronze Brick Slab");

		this.addItem(RNItems.BRONZE_ROD, "Brązowa Różdzka");
		this.addItem(RNItems.BRONZE_POWDER, "Brązowy Odłamek");

		this.add(RNCreativeTabs.RN_TAB.get().getDisplayName().getString(), "Rubinated Nether");

		this.add("menu." + RubinatedNether.MODID + ".freezer", "Zamrażarka");
		this.add("gui." + RubinatedNether.MODID + ".recipebook.toggleRecipes.freezable", "Pokazuje Zamrażalne");

		this.add(RubinatedNether.MODID + ".midnightconfig.category.chandelier", "Żyrandol");
		this.add(RubinatedNether.MODID + ".midnightconfig.category.brazier", "Palenisko");
		this.add(RubinatedNether.MODID + ".midnightconfig.category.client", "Client");
		this.add("jukebox_song." + RubinatedNether.MODID + ".shimmer", "Quizzly - Shimmer");

		this.add("death.attack.fallingBlock", "%1$s został uwolniony z tego świata przez spadający żyrandol");
		this.add("death.attack.fallingBlock.player", "%1$s został uwolniony z tego świata przez spadający żyrando podczas walki z %2$s");
		
		this.add("advancements.rubinated_nether.obtain_bleeding_obsidian.title","Krew dla boga krwi!");
		this.add("advancements.rubinated_nether.obtain_bleeding_obsidian.description","Zdobądz Krwawiący Obsydian");
		this.add("advancements.rubinated_nether.obtain_freezer.title","Zimne! Zimne! Zimne!");
		this.add("advancements.rubinated_nether.obtain_freezer.description","Stwórz Zamrażarke");
		this.add("advancements.rubinated_nether.obtain_frosted_ice.title","Re-Obtainable");
		this.add("advancements.rubinated_nether.obtain_frosted_ice.description","Zamróź Śnieg lub Wode w Oszroniony Lód ");
		this.add("advancements.rubinated_nether.obtain_rubinated_blackstone.title","Legitimate Salvage");
		this.add("advancements.rubinated_nether.obtain_rubinated_blackstone.description","Wykop Rubinowy Czernit w Pozostałościach Bastionu");
		this.add("advancements.rubinated_nether.obtain_molten_ruby.title","Gorące! Gorące! Gorące!");
		this.add("advancements.rubinated_nether.obtain_molten_ruby.description","Zdobądź Stopiony Rubin z żył magmy lub z przepalania");
		this.add("advancements.rubinated_nether.obtain_ruby.title","Witaj z Powrotem Stary Przyjacielu");
		this.add("advancements.rubinated_nether.obtain_ruby.description","Zdobądź Rubin z Rud Rubinu lub mrożenia");
		this.add("advancements.rubinated_nether.obtain_ruby_glass.title","Grubo Ponad 8000!");
		this.add("advancements.rubinated_nether.obtain_ruby_glass.description","Stwórz Rubinowe Szkło Odporne na Eksplozje");
		this.add("advancements.rubinated_nether.obtain_ruby_laser.title","Tag, You're It!");
		this.add("advancements.rubinated_nether.obtain_ruby_laser.description","Stwórz Rubinową soczewke");
		this.add("advancements.rubinated_nether.obtain_ruby_lights.title","Aż promieniejesz!");
		this.add("advancements.rubinated_nether.obtain_ruby_lights.description","Posiadaj Rubinowy Żyrandol, Latarenke i Lampe Lawową w tym samym czasie w swoim ekwipunku!");
		this.add("advancements.rubinated_nether.obtain_brazier.title","I'll Be Back");
		this.add("advancements.rubinated_nether.obtain_brazier.description","Stwórz Palenisko");
		this.add("advancements.rubinated_nether.wear_lens.title","Różowe Okulary");
		this.add("advancements.rubinated_nether.wear_lens.description","Załóż Rubinowe Soczewki żeby zobaczyć laserowe promienie.");
		this.add("advancements.rubinated_nether.enter_shrine.title","Dimension Expansion");
		this.add("advancements.rubinated_nether.enter_shrine.description","Przejdź Świątynny Rytuał");
		this.add("advancements.rubinated_nether.rubinous_ritual.title","Wroga Świątynia");
		this.add("advancements.rubinated_nether.rubinous_ritual.description","Przejdź Rubinowy Rytuał");
		this.add("advancements.rubinated_nether.bronze_rod.title","It's All Connected");
		this.add("advancements.rubinated_nether.bronze_rod.description","Zdobądź Brązową Różdżke");
		this.add("advancements.rubinated_nether.bronze_block.title","Nowe Złoża Króla");
		this.add("advancements.rubinated_nether.bronze_block.description","Zdobądz Jakikolwiek Blok Brązu");
		this.add("advancements.rubinated_nether.shrine_sentinel.title","Ogromny Ciężar");
		this.add("advancements.rubinated_nether.shrine_sentinel.description","Zdobądź Brązową Statue z Świątynnego Wartownika");
	
		this.add("gui.rubinated_nether.jei.freezer", "TODO!");
	}
}
