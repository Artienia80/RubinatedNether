package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.BronzeShotItem;
import corundum.rubinated_nether.content.items.DrillItem;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RubyLensItem;
import corundum.rubinated_nether.content.items.RuneItem;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class RNItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RubinatedNether.MODID);

	public static final DeferredItem<Item> RUBY = basicItem("ruby");
	public static final DeferredItem<Item> MOLTEN_RUBY = basicItem("molten_ruby");
	public static final DeferredItem<Item> RUBY_SHARD = basicItem("ruby_shard");
	public static final DeferredItem<Item> MOLTEN_RUBY_NUGGET = basicItem("molten_ruby_nugget");

	// Runes
	public static final DeferredItem<Item> RUNE = basicItem("rune");
	public static final DeferredItem<RuneItem> GREED_RUNE = makeRune(Rubination.GREED);
	public static final DeferredItem<RuneItem> WRATH_RUNE = makeRune(Rubination.WRATH);
	public static final DeferredItem<RuneItem> SLOTH_RUNE = makeRune(Rubination.SLOTH);
	public static final DeferredItem<RuneItem> GLUTTONY_RUNE = makeRune(Rubination.GLUTTONY);
	public static final DeferredItem<RuneItem> ENVY_RUNE = makeRune(Rubination.ENVY);
	public static final DeferredItem<RuneItem> VAINGLORY_RUNE = makeRune(Rubination.VAINGLORY);
	public static final DeferredItem<RuneItem> PRIDE_RUNE = makeRune(Rubination.PRIDE);
	public static final DeferredItem<RuneItem> ACEDIA_RUNE = makeRune(Rubination.ACEDIA);
	public static final DeferredItem<RuneItem> LUXURIA_RUNE = makeRune(Rubination.LUXURIA);
	public static final DeferredItem<RuneItem> INSIDIAE_RUNE = makeRune(Rubination.INSIDIAE);
	public static final DeferredItem<RuneItem> SUPERBIA_RUNE = makeRune(Rubination.SUPERBIA);
	public static final DeferredItem<RuneItem> TRISTIA_RUNE = makeRune(Rubination.TRISTIA);
	public static final DeferredItem<RuneItem> STUDIOSE_RUNE = makeRune(Rubination.STUDIOSE);
	public static final DeferredItem<RuneItem> ARDENTER_RUNE = makeRune(Rubination.ARDENTER);
	public static final DeferredItem<RuneItem> NIMIS_RUNE = makeRune(Rubination.NIMIS);
	public static final DeferredItem<RuneItem> IRA_RUNE = makeRune(Rubination.IRA);
	public static final DeferredItem<RuneItem> INVIDIA_RUNE = makeRune(Rubination.INVIDIA);
	public static final DeferredItem<RuneItem> GULA_RUNE = makeRune(Rubination.GULA);
	public static final DeferredItem<RuneItem> IGNAVIA_RUNE = makeRune(Rubination.IGNAVIA);
	public static final DeferredItem<RuneItem> KENODOXIA_RUNE = makeRune(Rubination.KENODOXIA);
	public static final DeferredItem<RuneItem> PHILARGYRIA_RUNE = makeRune(Rubination.PHILARGYRIA);


	public static final DeferredItem<Item> BRONZE_DRILL = ITEMS.register(
			"bronze_drill",
			() -> new DrillItem(
					new Item.Properties().stacksTo(1)
			)
	);

	public static final DeferredItem<Item> RUBY_LENS = ITEMS.register(
		"ruby_lens", 
		() -> new RubyLensItem(
			new Item.Properties()
				.stacksTo(1)
		)
	);

	public static final DeferredItem<Item> MUSIC_DISC_SHIMMER = ITEMS.registerSimpleItem(
		"music_disc_shimmer", 
		new Item.Properties()
			.stacksTo(1)
			.rarity(RNRarity.RUBINATED_NETHER_RUBY.get())
			.jukeboxPlayable(RNJukeboxSongs.SHIMMER)
	);

	public static final DeferredItem<BlockItem> FROSTED_ICE = ITEMS.registerSimpleBlockItem(
		"frosted_ice",
		() -> Blocks.FROSTED_ICE
	);

	public static final DeferredItem<BlockItem> POWDER_SNOW = ITEMS.registerSimpleBlockItem(
		"powder_snow", 
		() -> Blocks.POWDER_SNOW
	);

	public static final DeferredItem<Item> BRONZE_ROD = basicItem("bronze_rod");
	public static final DeferredItem<Item> BRONZE_POWDER = basicItem("bronze_powder");
	public static final DeferredItem<Item> BRONZE_SCRAP = basicItem("bronze_scrap");

	public static final DeferredItem<Item> RITUAL_OFFERING = basicItem("ritual_offering");
	public static final DeferredItem<Item> COGWHEEL = basicItem("cogwheel");
	public static final DeferredItem<Item> WINDING_KEY = basicItem("winding_key");

	public static final DeferredItem<Item> BRONZE_SHOT = ITEMS.register(
		"bronze_shot",
		() -> new BronzeShotItem(new Item.Properties())
	);

	public static final DeferredItem<Item> BRONZE_SPAWN_EGG = ITEMS.register("bronze_spawn_egg",
			() -> new DeferredSpawnEggItem(RNEntityCreator.BRONZE, 0xbf8142, 0x76422c,
					new Item.Properties()));

	//Not Actual Items

	public static final DeferredItem<Item> BLESSED_ICON = ITEMS.register("blessed_icon",
			() -> new Item(new Item.Properties()));

	public static final DeferredItem<Item> RUBY_ICON = ITEMS.register("ruby_icon",
			() -> new Item(new Item.Properties()));

	public static DeferredItem<Item> basicItem(String name) {
		return ITEMS.registerSimpleItem(
			name, 
			new Item.Properties()
		);
	}

	public static DeferredItem<Item> basicItem(String name, int stacksTo) {
		return ITEMS.registerSimpleItem(
				name,
				new Item.Properties().stacksTo(stacksTo)
		);
	}

	private static @NotNull DeferredItem<RuneItem> makeRune(Rubination rubination) {
		String name = rubination.name().toLowerCase(Locale.ROOT);
		String tooltipKey = "tooltip.rune." + name;

		return ITEMS.register(
				rubination.name().toLowerCase(Locale.ROOT).concat("_rune"), // Converts name to lowercase
				() -> new RuneItem(new Item.Properties().stacksTo(1).rarity(RNRarity.RUBINATED_NETHER_RUBY.get()), rubination, tooltipKey)
		);
	}
}
