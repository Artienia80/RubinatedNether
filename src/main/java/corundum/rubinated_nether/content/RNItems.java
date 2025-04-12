package corundum.rubinated_nether.content;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.*;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class RNItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RubinatedNether.MODID);

	private static DeferredItem<Item> redItem(String name) {
		return ITEMS.register(name, () -> new Item(new Item.Properties().rarity(RNRarity.RUBINATED_NETHER_RUBY.get())));
	}

	public static final DeferredItem<Item> RUBY_ITEM = basicItem("ruby");
	public static final DeferredItem<Item> MOLTEN_RUBY_ITEM = basicItem("molten_ruby");
	public static final DeferredItem<Item> RUBY_SHARD_ITEM = basicItem("ruby_shard");
	public static final DeferredItem<Item> MOLTEN_RUBY_NUGGET_ITEM = basicItem("molten_ruby_nugget");

	// Runes
	public static final DeferredItem<Item> GREED_RUNE = makeRune(Rubination.GREED);
	public static final DeferredItem<Item> WRATH_RUNE = makeRune(Rubination.WRATH);
	public static final DeferredItem<Item> SLOTH_RUNE = makeRune(Rubination.SLOTH);
	public static final DeferredItem<Item> GLUTTONY_RUNE = makeRune(Rubination.GLUTTONY);
	public static final DeferredItem<Item> ENVY_RUNE = makeRune(Rubination.ENVY);
	public static final DeferredItem<Item> VAINGLORY_RUNE = makeRune(Rubination.VAINGLORY);
	public static final DeferredItem<Item> PRIDE_RUNE = makeRune(Rubination.PRIDE);
	public static final DeferredItem<Item> ACEDIA_RUNE = makeRune(Rubination.ACEDIA);
	public static final DeferredItem<Item> LUXURIA_RUNE = makeRune(Rubination.LUXURIA);
	public static final DeferredItem<Item> INSIDIAE_RUNE = makeRune(Rubination.INSIDIAE);
	public static final DeferredItem<Item> SUPERBIA_RUNE = makeRune(Rubination.SUPERBIA);
	public static final DeferredItem<Item> TRISTIA_RUNE = makeRune(Rubination.TRISTIA);
	public static final DeferredItem<Item> STUDIOSE_RUNE = makeRune(Rubination.STUDIOSE);
	public static final DeferredItem<Item> ARDENTER_RUNE = makeRune(Rubination.ARDENTER);
	public static final DeferredItem<Item> NIMIS_RUNE = makeRune(Rubination.NIMIS);


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
			.rarity(Rarity.RARE)
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
	public static final DeferredItem<Item> BRONZE_SCRAP = basicItem("bronze_scrap");

	public static final DeferredItem<Item> RITUAL_OFFERING = basicItem("ritual_offering");
	public static final DeferredItem<Item> GRAND_RITUAL_OFFERING = basicItem("grand_ritual_offering");



	public static final DeferredItem<Item> BRONZE_SHOT = ITEMS.register(
		"bronze_shot",
		() -> new BronzeShotItem(new Item.Properties())
	);

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

	private static @NotNull DeferredItem<Item> makeRune(Rubination rubination) {
		String name = rubination.name().toLowerCase();
		String tooltipKey = "tooltip.rune." + name;

		return ITEMS.register(
				rubination.name().toLowerCase().concat("_rune"), // Converts name to lowercase
				() -> new RuneItem(new Item.Properties().stacksTo(1).rarity(RNRarity.RUBINATED_NETHER_RUBY.get()), rubination, tooltipKey)
		);
	}
}
