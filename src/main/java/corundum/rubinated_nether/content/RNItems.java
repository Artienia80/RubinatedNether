package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.*;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

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

	public static final Supplier<Item> COGS_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.COGS);
//	public static final Supplier<Item> RUNE_TOOL_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_TOOL);
//	public static final Supplier<Item> RUNE_ARMOR_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_ARMOR);
//	public static final Supplier<Item> RUNE_WEAPON_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_WEAPON);
//	public static final Supplier<Item> RUNE_BOW_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_BOW);
//	public static final Supplier<Item> RUNE_CROSSBOW_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_CROSSBOW);
//	public static final Supplier<Item> RUNE_MACE_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_MACE);
//	public static final Supplier<Item> RUNE_TRIDENT_BANNER_PATTERN = registerBannerPattern(RNBannerPatterns.RUNE_TRIDENT);

	private static Supplier<Item> registerBannerPattern(RNBannerPatterns.BannerPatternEntry entry) {
		String name = entry.key().location().getPath() + "_banner_pattern";
		return ITEMS.register(
				name,
				() -> new BannerPatternItem(
						entry.tag(),
						new Item.Properties()
								.stacksTo(1)
								.rarity(Rarity.UNCOMMON)
				)
		);
	}

	public static final DeferredItem<Item> BRONZE_DRILL = ITEMS.register(
			"bronze_drill",
			() -> new DrillItem(
					new Item.Properties()
                            .stacksTo(1)
                            .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
                            .component(RNDataComponents.IS_COMBO, false)
                            .component(RNDataComponents.DRILL_MULTIPLIER, 1.0f)
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

	public static final DeferredItem<Item> CRYSTALLIZED_BRONZE_SHOT = ITEMS.register(
			"crystallized_bronze_shot",
			() -> new CrystallizedBronzeShotItem(new Item.Properties())
	);

	public static final DeferredItem<Item> BRONZE_SPAWN_EGG = ITEMS.register("bronze_spawn_egg",
			() -> new BronzeSpawnEgg(RNEntityCreator.BRONZE, TarnishStage.UNAFFECTED,
					0xbf8142, 0x76422c, new Item.Properties()));

	public static final DeferredItem<Item> DISCOLORED_BRONZE_SPAWN_EGG = ITEMS.register("discolored_bronze_spawn_egg",
			() -> new BronzeSpawnEgg(RNEntityCreator.BRONZE, TarnishStage.DISCOLORED,
					0xC77459, 0x6D411C, new Item.Properties()));

	public static final DeferredItem<Item> CORRODED_BRONZE_SPAWN_EGG = ITEMS.register("corroded_bronze_spawn_egg",
			() -> new BronzeSpawnEgg(RNEntityCreator.BRONZE, TarnishStage.CORRODED,
					0xB25B4E, 0x662D1B, new Item.Properties()));

	public static final DeferredItem<Item> TARNISHED_BRONZE_SPAWN_EGG = ITEMS.register("tarnished_bronze_spawn_egg",
			() -> new BronzeSpawnEgg(RNEntityCreator.BRONZE, TarnishStage.TARNISHED,
					0x6F4A4F, 0x352227, new Item.Properties()));

	public static final DeferredItem<Item> CRYSTALLIZED_BRONZE_SPAWN_EGG = ITEMS.register("crystallized_bronze_spawn_egg",
			() -> new BronzeSpawnEgg(RNEntityCreator.BRONZE, TarnishStage.CRYSTALLIZED,
					0xACD1B0, 0x738E79, new Item.Properties()));

	public static final DeferredItem<Item> MOLTEN_RUBY_BUCKET = ITEMS.register(
			"molten_ruby_bucket",
			() -> new SolidBucketItem(
					RNBlocks.MOLTEN_RUBY_BLOCK.get(),
					SoundEvents.BUCKET_EMPTY_LAVA,
					new Item.Properties().stacksTo(1)
			)
	);

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

		// Get the corresponding banner pattern based on the rubination's item tag
		RNBannerPatterns.BannerPatternEntry bannerPattern = getBannerPatternForRubination(rubination);

		return ITEMS.register(
				rubination.name().toLowerCase(Locale.ROOT).concat("_rune"), // Converts name to lowercase
				() -> new RuneItem(
						rubination,
						tooltipKey,
						bannerPattern,
						new Item.Properties().stacksTo(1).rarity(RNRarity.RUBINATED_NETHER_RUBY.get())
				)
		);
	}

	private static RNBannerPatterns.BannerPatternEntry getBannerPatternForRubination(Rubination rubination) {
		TagKey<Item> itemTag = rubination.getItemTag();

		if (itemTag == RNTags.Items.RUBINATION_TOOL) {
			return RNBannerPatterns.RUNE_TOOL;
		} else if (itemTag == RNTags.Items.RUBINATION_WEAPON) {
			return RNBannerPatterns.RUNE_WEAPON;
		} else if (itemTag == RNTags.Items.RUBINATION_ARMOR) {
			return RNBannerPatterns.RUNE_ARMOR;
		} else if (itemTag == RNTags.Items.RUBINATION_BOW) {
			return RNBannerPatterns.RUNE_BOW;
		} else if (itemTag == RNTags.Items.RUBINATION_CROSSBOW) {
			return RNBannerPatterns.RUNE_CROSSBOW;
		} else if (itemTag == RNTags.Items.RUBINATION_TRIDENT) {
			return RNBannerPatterns.RUNE_TRIDENT;
		} else if (itemTag == RNTags.Items.RUBINATION_MACE) {
			return RNBannerPatterns.RUNE_MACE;
		}

		// Default to RUNE_TOOL if no match
		return RNBannerPatterns.RUNE_TOOL;
	}
}