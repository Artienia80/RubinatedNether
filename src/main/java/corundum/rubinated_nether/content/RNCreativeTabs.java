package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
			Registries.CREATIVE_MODE_TAB,
			RubinatedNether.MODID
	);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RN_GENERAL_TAB = CREATIVE_MODE_TABS.register(
			"rubinated_nether_general_tab",
			() -> CreativeModeTab.builder()
					.title(net.minecraft.network.chat.Component.translatable("itemGroup.rubinated_nether_general"))
					.withTabsBefore(net.minecraft.world.item.CreativeModeTabs.COMBAT)
					.icon(() -> RNItems.RUBY_ICON.get().getDefaultInstance())
					.displayItems((parameters, output) -> {

						addItems(
								output,

								RNItems.RUBY_ITEM,
								RNItems.MOLTEN_RUBY_ITEM,
								RNItems.RUBY_SHARD_ITEM,
								RNItems.MOLTEN_RUBY_NUGGET_ITEM,

								RNBlocks.NETHER_RUBY_ORE,
								RNBlocks.MOLTEN_RUBY_ORE,
								RNBlocks.RUBINATED_BLACKSTONE,
								RNBlocks.RUBY_BLOCK,
								RNBlocks.MOLTEN_RUBY_BLOCK,

								RNBlocks.BLEEDING_OBSIDIAN,
								RNBlocks.RUNESTONE,

								RNBlocks.SHRINE_STONE,
								RNBlocks.SHRINE_STONE_STAIRS,
								RNBlocks.SHRINE_STONE_SLAB,
								RNBlocks.SHRINE_STONE_WALL,

								RNBlocks.POLISHED_SHRINE_STONE,
								RNBlocks.POLISHED_SHRINE_STONE_STAIRS,
								RNBlocks.POLISHED_SHRINE_STONE_SLAB,
								RNBlocks.POLISHED_SHRINE_STONE_WALL,

								RNBlocks.SHRINE_STONE_BRICKS,
								RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
								RNBlocks.SHRINE_STONE_BRICKS_SLAB,
								RNBlocks.SHRINE_STONE_BRICKS_WALL,

								RNBlocks.SHRINE_STONE_TILES,
								RNBlocks.SHRINE_STONE_TILES_STAIRS,
								RNBlocks.SHRINE_STONE_TILES_SLAB,
								RNBlocks.SHRINE_STONE_TILES_WALL,

								RNBlocks.SHRINE_STONE_PILLAR,
								RNBlocks.CHISELED_SHRINE_STONE_BRICKS,

								RNBlocks.RUBINATED_SHRINE_STONE_BRICKS,
								RNBlocks.RUBINATED_SHRINE_STONE_TILES,
								RNBlocks.RUBINATED_SHRINE_STONE_PILLAR,
								RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS,

								RNBlocks.SHRINE_STONE_COFFER,

								RNBlocks.SOAKSTONE,

								RNBlocks.RUBY_GLASS,
								RNBlocks.RUBY_GLASS_PANE,
								RNBlocks.MOLTEN_RUBY_GLASS,
								RNBlocks.MOLTEN_RUBY_GLASS_PANE,
								RNBlocks.ORNATE_RUBY_GLASS,
								RNBlocks.ORNATE_RUBY_GLASS_PANE,

								RNBlocks.BRAZIER,
								RNBlocks.RUBINATION_ALTAR,

								RNBlocks.FREEZER,
								RNItems.POWDER_SNOW,
								RNItems.FROSTED_ICE,
								RNBlocks.DRY_ICE,

								RNItems.RUBY_LENS,

								RNItems.MUSIC_DISC_SHIMMER,

								RNItems.BRONZE_ROD,
								RNItems.BRONZE_POWDER,
								RNItems.BRONZE_SCRAP,
								RNItems.BRONZE_SHOT,
								RNItems.BRONZE_DRILL,

								RNItems.RITUAL_OFFERING,
								RNItems.WINDING_KEY,

								RNItems.BRONZE_SPAWN_EGG
						);

						// Add Bronze Lasers
						addWaxableItems(
								output,
								RNBlocks.BRONZE_LASER,
								RNBlocks.DISCOLORED_BRONZE_LASER,
								RNBlocks.CORRODED_BRONZE_LASER,
								RNBlocks.TARNISHED_BRONZE_LASER,
								RNBlocks.CRYSTALLIZED_BRONZE_LASER
						);

						// Add Copper Lasers
						addItems(
								output,
								RNBlocks.COPPER_LASER,
								RNBlocks.EXPOSED_COPPER_LASER,
								RNBlocks.WEATHERED_COPPER_LASER,
								RNBlocks.OXIDIZED_COPPER_LASER,
								RNBlocks.WAXED_COPPER_LASER,
								RNBlocks.WAXED_EXPOSED_COPPER_LASER,
								RNBlocks.WAXED_WEATHERED_COPPER_LASER,
								RNBlocks.WAXED_OXIDIZED_COPPER_LASER
						);

						addItems(
								output,
								RNItems.RUNE,

								RNItems.SLOTH_RUNE,
								RNItems.GLUTTONY_RUNE,
								RNItems.GREED_RUNE,

								RNItems.VAINGLORY_RUNE,
								RNItems.WRATH_RUNE,
								RNItems.ENVY_RUNE,

								RNItems.PRIDE_RUNE,
								RNItems.ACEDIA_RUNE,
								RNItems.LUXURIA_RUNE,

								RNItems.INSIDIAE_RUNE,
								RNItems.SUPERBIA_RUNE,
								RNItems.TRISTIA_RUNE,

								RNItems.STUDIOSE_RUNE,
								RNItems.ARDENTER_RUNE,
								RNItems.NIMIS_RUNE,

								RNItems.IRA_RUNE,
								RNItems.INVIDIA_RUNE,
								RNItems.GULA_RUNE,

								RNItems.IGNAVIA_RUNE,
								RNItems.KENODOXIA_RUNE,
								RNItems.PHILARGYRIA_RUNE
						);
					})
					.build()
	);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RN_BRONZE_TAB = CREATIVE_MODE_TABS.register(
			"rubinated_nether_bronze_tab",
			() -> CreativeModeTab.builder()
					.title(net.minecraft.network.chat.Component.translatable("itemGroup.rubinated_nether_bronze"))
					.withTabsBefore(RN_GENERAL_TAB.getId())
					.icon(() -> RNItems.BRONZE_POWDER.get().getDefaultInstance())
					.displayItems((parameters, output) -> {

						addWaxableItems(
								output,

								RNBlocks.BRONZE_BLOCK,
								RNBlocks.CHISELED_BRONZE,
								RNBlocks.CUT_BRONZE_PILLAR,
								RNBlocks.CUT_BRONZE_BRICKS,
								RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.BRONZE_BULB,
								RNBlocks.BRONZE_GRATE,

								RNBlocks.DISCOLORED_BRONZE_BLOCK,
								RNBlocks.DISCOLORED_CHISELED_BRONZE,
								RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR,
								RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
								RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.DISCOLORED_BRONZE_BULB,
								RNBlocks.DISCOLORED_BRONZE_GRATE,

								RNBlocks.CORRODED_BRONZE_BLOCK,
								RNBlocks.CORRODED_CHISELED_BRONZE,
								RNBlocks.CORRODED_CUT_BRONZE_PILLAR,
								RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
								RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.CORRODED_BRONZE_BULB,
								RNBlocks.CORRODED_BRONZE_GRATE,

								RNBlocks.TARNISHED_BRONZE_BLOCK,
								RNBlocks.TARNISHED_CHISELED_BRONZE,
								RNBlocks.TARNISHED_CUT_BRONZE_PILLAR,
								RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
								RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.TARNISHED_BRONZE_BULB,
								RNBlocks.TARNISHED_BRONZE_GRATE,

								RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
								RNBlocks.CRYSTALLIZED_CHISELED_BRONZE,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.CRYSTALLIZED_BRONZE_BULB,
								RNBlocks.CRYSTALLIZED_BRONZE_GRATE,

								RNBlocks.BRONZE_LANTERN,
								RNBlocks.DISCOLORED_BRONZE_LANTERN,
								RNBlocks.CORRODED_BRONZE_LANTERN,
								RNBlocks.TARNISHED_BRONZE_LANTERN,
								RNBlocks.CRYSTALLIZED_BRONZE_LANTERN,

								RNBlocks.BRONZE_CHAIN,
								RNBlocks.DISCOLORED_BRONZE_CHAIN,
								RNBlocks.CORRODED_BRONZE_CHAIN,
								RNBlocks.TARNISHED_BRONZE_CHAIN,
								RNBlocks.CRYSTALLIZED_BRONZE_CHAIN,

								RNBlocks.BRONZE_CHANDELIER,
								RNBlocks.DISCOLORED_BRONZE_CHANDELIER,
								RNBlocks.CORRODED_BRONZE_CHANDELIER,
								RNBlocks.TARNISHED_BRONZE_CHANDELIER,
								RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER,

								RNBlocks.BRONZE_LAMP,
								RNBlocks.DISCOLORED_BRONZE_LAMP,
								RNBlocks.CORRODED_BRONZE_LAMP,
								RNBlocks.TARNISHED_BRONZE_LAMP,
								RNBlocks.CRYSTALLIZED_BRONZE_LAMP
						);
					})
					.build()
	);

	private static void addItems(Output output, ItemLike... items) {
		for (var item : items)
			output.accept(item);
	}

	private static void addWaxableItems(Output output, ItemLike... items) {
		addItems(output, items);

		for (var item : items)
			output.accept(BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(item))));
	}
}