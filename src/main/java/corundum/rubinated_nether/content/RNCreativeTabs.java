package corundum.rubinated_nether.content;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
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
						if (RNConfig.tabDisplay != RNConfig.TabDisplay.VANILLA_ONLY) {
							addItems(
									output,
									RNItems.MOLTEN_RUBY,
									RNItems.RUBY,
									RNItems.RUBY_SHARD,
									RNItems.MOLTEN_RUBY_NUGGET
							);
						}
						addItems(
								output,
								RNBlocks.MOLTEN_RUBY_ORE,
								RNBlocks.NETHER_RUBY_ORE,
								RNBlocks.RUBINATED_BLACKSTONE,
								RNBlocks.RUBY_BLOCK,
								RNBlocks.MOLTEN_RUBY_BLOCK,
								RNBlocks.BLEEDING_OBSIDIAN,
								RNBlocks.RUNESTONE,
								RNBlocks.SHRINE_STONE,
								RNBlocks.SHRINE_STONE_STAIRS,
								RNBlocks.SHRINE_STONE_SLAB,
								RNBlocks.SHRINE_STONE_WALL,
								RNBlocks.COBBLED_SHRINE_STONE,
								RNBlocks.COBBLED_SHRINE_STONE_STAIRS,
								RNBlocks.COBBLED_SHRINE_STONE_SLAB,
								RNBlocks.COBBLED_SHRINE_STONE_WALL,
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
								RNItems.MUSIC_DISC_SINNER,
								RNItems.BRONZE_ROD,
								RNItems.BRONZE_POWDER,
								RNItems.BRONZE_SCRAP,
								RNItems.BRONZE_SHOT,
								RNItems.CRYSTALLIZED_BRONZE_SHOT,
								RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL,
								RNItems.BRONZE_DRILL,
								RNItems.RITUAL_OFFERING,
								RNItems.WINDING_KEY,
								RNItems.COGWHEEL,
								RNItems.BRONZE_SPAWN_EGG,
								RNItems.DISCOLORED_BRONZE_SPAWN_EGG,
								RNItems.CORRODED_BRONZE_SPAWN_EGG,
								RNItems.TARNISHED_BRONZE_SPAWN_EGG,
								RNItems.CRYSTALLIZED_BRONZE_SPAWN_EGG
						);
						addWaxableItems(
								output,
								RNBlocks.BRONZE_LASER,
								RNBlocks.DISCOLORED_BRONZE_LASER,
								RNBlocks.CORRODED_BRONZE_LASER,
								RNBlocks.TARNISHED_BRONZE_LASER,
								RNBlocks.CRYSTALLIZED_BRONZE_LASER
						);
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
                                RNBlocks.GEARBOX,
                                RNBlocks.DISCOLORED_GEARBOX,
                                RNBlocks.CORRODED_GEARBOX,
                                RNBlocks.TARNISHED_GEARBOX,
                                RNBlocks.CRYSTALLIZED_GEARBOX
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
								RNItems.PHILARGYRIA_RUNE,
								RNItems.COGS_BANNER_PATTERN.get()
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
								RNBlocks.BRONZE_SPRING,
								RNBlocks.BRONZE_VENT,
								RNBlocks.BRONZE_LANTERN,
								RNBlocks.BRONZE_CHAIN,
								RNBlocks.BRONZE_CHANDELIER,
								RNBlocks.BRONZE_LAMP,
								RNBlocks.DISCOLORED_BRONZE_BLOCK,
								RNBlocks.DISCOLORED_CHISELED_BRONZE,
								RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR,
								RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
								RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.DISCOLORED_BRONZE_BULB,
								RNBlocks.DISCOLORED_BRONZE_GRATE,
								RNBlocks.DISCOLORED_BRONZE_SPRING,
								RNBlocks.DISCOLORED_BRONZE_VENT,
								RNBlocks.DISCOLORED_BRONZE_LANTERN,
								RNBlocks.DISCOLORED_BRONZE_CHAIN,
								RNBlocks.DISCOLORED_BRONZE_CHANDELIER,
								RNBlocks.DISCOLORED_BRONZE_LAMP,
								RNBlocks.CORRODED_BRONZE_BLOCK,
								RNBlocks.CORRODED_CHISELED_BRONZE,
								RNBlocks.CORRODED_CUT_BRONZE_PILLAR,
								RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
								RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.CORRODED_BRONZE_BULB,
								RNBlocks.CORRODED_BRONZE_GRATE,
								RNBlocks.CORRODED_BRONZE_SPRING,
								RNBlocks.CORRODED_BRONZE_VENT,
								RNBlocks.CORRODED_BRONZE_LANTERN,
								RNBlocks.CORRODED_BRONZE_CHAIN,
								RNBlocks.CORRODED_BRONZE_CHANDELIER,
								RNBlocks.CORRODED_BRONZE_LAMP,
								RNBlocks.TARNISHED_BRONZE_BLOCK,
								RNBlocks.TARNISHED_CHISELED_BRONZE,
								RNBlocks.TARNISHED_CUT_BRONZE_PILLAR,
								RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
								RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.TARNISHED_BRONZE_BULB,
								RNBlocks.TARNISHED_BRONZE_GRATE,
								RNBlocks.TARNISHED_BRONZE_SPRING,
								RNBlocks.TARNISHED_BRONZE_VENT,
								RNBlocks.TARNISHED_BRONZE_LANTERN,
								RNBlocks.TARNISHED_BRONZE_CHAIN,
								RNBlocks.TARNISHED_BRONZE_CHANDELIER,
								RNBlocks.TARNISHED_BRONZE_LAMP,
								RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
								RNBlocks.CRYSTALLIZED_CHISELED_BRONZE,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
								RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB,
								RNBlocks.CRYSTALLIZED_BRONZE_BULB,
								RNBlocks.CRYSTALLIZED_BRONZE_GRATE,
								RNBlocks.CRYSTALLIZED_BRONZE_SPRING,
								RNBlocks.CRYSTALLIZED_BRONZE_VENT,
								RNBlocks.CRYSTALLIZED_BRONZE_LANTERN,
								RNBlocks.CRYSTALLIZED_BRONZE_CHAIN,
								RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER,
								RNBlocks.CRYSTALLIZED_BRONZE_LAMP
						);
					})
					.build()
	);
	@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class VanillaTabInjector {
		private static final CreativeModeTab.TabVisibility BOTH_TABS =
				CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
		@SubscribeEvent
		public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
			if (RNConfig.tabDisplay == RNConfig.TabDisplay.MODDED_ONLY) return;
			injectIngredients(event);
			injectTools(event);
			injectCombat(event);
			injectNaturalBlocks(event);
			injectBuildingBlocks(event);
			injectColoredBlocks(event);
			injectFunctionalBlocks(event);
			injectRedstoneBlocks(event);
			injectSpawnEggs(event);
		}
		private static void injectIngredients(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.INGREDIENTS) return;
			event.insertAfter(Items.RAW_GOLD.getDefaultInstance(),
					RNItems.MOLTEN_RUBY.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.EMERALD.getDefaultInstance(),
					RNItems.RUBY.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.RUBY.get().getDefaultInstance(),
					RNItems.RUBY_SHARD.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.RUBY_SHARD.get().getDefaultInstance(),
					RNItems.MOLTEN_RUBY_NUGGET.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.BREEZE_ROD.getDefaultInstance(),
					RNItems.BRONZE_ROD.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.BRONZE_ROD.get().getDefaultInstance(),
					RNItems.BRONZE_POWDER.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.BRONZE_POWDER.get().getDefaultInstance(),
					RNItems.BRONZE_SCRAP.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.BRONZE_SCRAP.get().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.OMINOUS_TRIAL_KEY.getDefaultInstance(),
					RNItems.RITUAL_OFFERING.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.RITUAL_OFFERING.get().getDefaultInstance(),
					RNItems.WINDING_KEY.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.WINDING_KEY.get().getDefaultInstance(),
					RNItems.COGWHEEL.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.FLOWER_BANNER_PATTERN.getDefaultInstance(),
					RNItems.COGS_BANNER_PATTERN.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.SLOTH_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.GLUTTONY_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.GREED_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.VAINGLORY_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.WRATH_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.ENVY_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.PRIDE_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.ACEDIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.LUXURIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.INSIDIAE_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.SUPERBIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.TRISTIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.STUDIOSE_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.ARDENTER_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.NIMIS_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.IRA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.INVIDIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.GULA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.IGNAVIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.KENODOXIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
			event.accept(RNItems.PHILARGYRIA_RUNE.get().getDefaultInstance(), BOTH_TABS);
		}
		private static void injectTools(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.TOOLS_AND_UTILITIES) return;
			event.insertAfter(Items.POWDER_SNOW_BUCKET.getDefaultInstance(),
					RNItems.MOLTEN_RUBY_BUCKET.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(),
					RNItems.BRONZE_DRILL.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.MUSIC_DISC_PIGSTEP.getDefaultInstance(),
					RNItems.MUSIC_DISC_SHIMMER.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.MUSIC_DISC_SHIMMER.get().getDefaultInstance(),
					RNItems.MUSIC_DISC_SINNER.get().getDefaultInstance(), BOTH_TABS);
		}
		private static void injectCombat(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.COMBAT) return;
			event.insertAfter(Items.TURTLE_HELMET.getDefaultInstance(),
					RNItems.RUBY_LENS.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.WIND_CHARGE.getDefaultInstance(),
					RNItems.BRONZE_SHOT.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.BRONZE_SHOT.get().getDefaultInstance(),
					RNItems.CRYSTALLIZED_BRONZE_SHOT.get().getDefaultInstance(), BOTH_TABS);
		}
		private static void injectNaturalBlocks(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.NATURAL_BLOCKS) return;
			event.insertAfter(Items.ANCIENT_DEBRIS.getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_ORE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.MOLTEN_RUBY_ORE.get().asItem().getDefaultInstance(),
					RNBlocks.NETHER_RUBY_ORE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.NETHER_RUBY_ORE.get().asItem().getDefaultInstance(),
					RNBlocks.RUBINATED_BLACKSTONE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.CRYING_OBSIDIAN.getDefaultInstance(),
					RNBlocks.BLEEDING_OBSIDIAN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertBefore(Items.ICE.getDefaultInstance(),
					RNItems.POWDER_SNOW.get().getDefaultInstance(), BOTH_TABS);
			event.insertBefore(Items.PACKED_ICE.getDefaultInstance(),
					RNItems.FROSTED_ICE.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.RAW_GOLD_BLOCK.getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.BLUE_ICE.getDefaultInstance(),
					RNBlocks.DRY_ICE.get().asItem().getDefaultInstance(), BOTH_TABS);
		}
		private static void injectColoredBlocks(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.COLORED_BLOCKS) return;
			event.insertAfter(Items.TINTED_GLASS.getDefaultInstance(),
					RNBlocks.RUBY_GLASS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBY_GLASS.get().asItem().getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_GLASS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.MOLTEN_RUBY_GLASS.get().asItem().getDefaultInstance(),
					RNBlocks.ORNATE_RUBY_GLASS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.GLASS_PANE.getDefaultInstance(),
					RNBlocks.RUBY_GLASS_PANE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBY_GLASS_PANE.get().asItem().getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_GLASS_PANE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get().asItem().getDefaultInstance(),
					RNBlocks.ORNATE_RUBY_GLASS_PANE.get().asItem().getDefaultInstance(), BOTH_TABS);
		}
		private static void injectFunctionalBlocks(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.FUNCTIONAL_BLOCKS) return;
			event.insertAfter(Items.BEACON.getDefaultInstance(),
					RNBlocks.BRAZIER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.ENCHANTING_TABLE.getDefaultInstance(),
					RNBlocks.RUBINATION_ALTAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.BLAST_FURNACE.getDefaultInstance(),
					RNBlocks.FREEZER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.BARREL.getDefaultInstance(),
					RNBlocks.SHRINE_STONE_COFFER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.CHISELED_BOOKSHELF.getDefaultInstance(),
					RNBlocks.RUNESTONE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.CRYING_OBSIDIAN.getDefaultInstance(),
					RNBlocks.BLEEDING_OBSIDIAN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.MAGMA_BLOCK.getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_ORE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.TINTED_GLASS.getDefaultInstance(),
					RNBlocks.RUBY_GLASS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBY_GLASS.get().asItem().getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_GLASS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.MOLTEN_RUBY_GLASS.get().asItem().getDefaultInstance(),
					RNBlocks.ORNATE_RUBY_GLASS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.ORNATE_RUBY_GLASS.get().asItem().getDefaultInstance(),
					RNBlocks.RUBY_GLASS_PANE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBY_GLASS_PANE.get().asItem().getDefaultInstance(),
					RNBlocks.MOLTEN_RUBY_GLASS_PANE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get().asItem().getDefaultInstance(),
					RNBlocks.ORNATE_RUBY_GLASS_PANE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.CONDUIT.getDefaultInstance(),
					RNBlocks.SOAKSTONE.get().asItem().getDefaultInstance(), BOTH_TABS);

			event.insertAfter(Items.LANTERN.getDefaultInstance(),
					RNBlocks.BRONZE_LANTERN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_LANTERN.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_LANTERN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_LANTERN.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_LANTERN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_LANTERN.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_LANTERN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_LANTERN.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_LANTERN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_LANTERN),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_LANTERN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_LANTERN),
					getWaxed(RNBlocks.CORRODED_BRONZE_LANTERN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_LANTERN),
					getWaxed(RNBlocks.TARNISHED_BRONZE_LANTERN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_LANTERN),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_LANTERN), BOTH_TABS);

			event.insertAfter(Items.SOUL_LANTERN.getDefaultInstance(),
					RNBlocks.BRONZE_CHANDELIER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_CHANDELIER.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_CHANDELIER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_CHANDELIER),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_CHANDELIER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_CHANDELIER),
					getWaxed(RNBlocks.CORRODED_BRONZE_CHANDELIER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_CHANDELIER),
					getWaxed(RNBlocks.TARNISHED_BRONZE_CHANDELIER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_CHANDELIER),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER), BOTH_TABS);

			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER),
					RNBlocks.BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_LAMP),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_LAMP),
					getWaxed(RNBlocks.CORRODED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_LAMP),
					getWaxed(RNBlocks.TARNISHED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_LAMP),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_LAMP), BOTH_TABS);

			event.insertAfter(Items.CHAIN.getDefaultInstance(),
					RNBlocks.BRONZE_CHAIN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_CHAIN.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_CHAIN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_CHAIN.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_CHAIN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_CHAIN.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_CHAIN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_CHAIN.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_CHAIN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_CHAIN),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_CHAIN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_CHAIN),
					getWaxed(RNBlocks.CORRODED_BRONZE_CHAIN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_CHAIN),
					getWaxed(RNBlocks.TARNISHED_BRONZE_CHAIN), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_CHAIN),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_CHAIN), BOTH_TABS);

			event.insertAfter(Items.WAXED_OXIDIZED_COPPER_BULB.getDefaultInstance(),
					RNBlocks.BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_BULB),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_BULB),
					getWaxed(RNBlocks.CORRODED_BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_BULB),
					getWaxed(RNBlocks.TARNISHED_BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_BULB),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_BULB), BOTH_TABS);
		}
		private static void injectBuildingBlocks(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.BUILDING_BLOCKS) return;
			event.insertAfter(Items.NETHERITE_BLOCK.getDefaultInstance(),
					RNBlocks.RUBY_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.GILDED_BLACKSTONE.getDefaultInstance(),
					RNBlocks.RUBINATED_BLACKSTONE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultInstance(),
					RNBlocks.SHRINE_STONE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.SHRINE_STONE.get().asItem().getDefaultInstance(),
					RNBlocks.POLISHED_SHRINE_STONE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.POLISHED_SHRINE_STONE.get().asItem().getDefaultInstance(),
					RNBlocks.SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.SHRINE_STONE_TILES.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.SHRINE_STONE_TILES.get().asItem().getDefaultInstance(),
					RNBlocks.SHRINE_STONE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.SHRINE_STONE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.RUBINATED_SHRINE_STONE_TILES.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBINATED_SHRINE_STONE_TILES.get().asItem().getDefaultInstance(),
					RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);

			// UNAFFECTED
			event.insertAfter(Items.WAXED_OXIDIZED_COPPER_BULB.getDefaultInstance(),
					RNBlocks.BRONZE_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_BLOCK.get().asItem().getDefaultInstance(),
					RNBlocks.CHISELED_BRONZE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CHISELED_BRONZE.get().asItem().getDefaultInstance(),
					RNBlocks.BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(),
					RNBlocks.CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(),
					RNBlocks.CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			// DISCOLORED
			event.insertAfter(RNBlocks.BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_BLOCK.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_CHISELED_BRONZE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_CHISELED_BRONZE.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			// CORRODED
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_BLOCK.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_CHISELED_BRONZE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_CHISELED_BRONZE.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			// TARNISHED
			event.insertAfter(RNBlocks.CORRODED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_BLOCK.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_CHISELED_BRONZE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_CHISELED_BRONZE.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			// CRYSTALLIZED
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_BULB.get().asItem().getDefaultInstance(), BOTH_TABS);
			// WAXED UNAFFECTED
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_BULB.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_BLOCK), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_BLOCK),
					getWaxed(RNBlocks.CHISELED_BRONZE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CHISELED_BRONZE),
					getWaxed(RNBlocks.BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_GRATE),
					getWaxed(RNBlocks.CUT_BRONZE_BRICKS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CUT_BRONZE_BRICKS),
					getWaxed(RNBlocks.CUT_BRONZE_BRICKS_STAIRS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CUT_BRONZE_BRICKS_STAIRS),
					getWaxed(RNBlocks.CUT_BRONZE_BRICKS_SLAB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CUT_BRONZE_BRICKS_SLAB),
					getWaxed(RNBlocks.CUT_BRONZE_PILLAR), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CUT_BRONZE_PILLAR),
					getWaxed(RNBlocks.BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_LAMP),
					getWaxed(RNBlocks.BRONZE_BULB), BOTH_TABS);
			// WAXED DISCOLORED
			event.insertAfter(getWaxed(RNBlocks.BRONZE_BULB),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_BLOCK), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_BLOCK),
					getWaxed(RNBlocks.DISCOLORED_CHISELED_BRONZE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_CHISELED_BRONZE),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_GRATE),
					getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS),
					getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS),
					getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB),
					getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_LAMP),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_BULB), BOTH_TABS);
			// WAXED CORRODED
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_BULB),
					getWaxed(RNBlocks.CORRODED_BRONZE_BLOCK), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_BLOCK),
					getWaxed(RNBlocks.CORRODED_CHISELED_BRONZE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_CHISELED_BRONZE),
					getWaxed(RNBlocks.CORRODED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_GRATE),
					getWaxed(RNBlocks.CORRODED_CUT_BRONZE_BRICKS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_CUT_BRONZE_BRICKS),
					getWaxed(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS),
					getWaxed(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB),
					getWaxed(RNBlocks.CORRODED_CUT_BRONZE_PILLAR), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_CUT_BRONZE_PILLAR),
					getWaxed(RNBlocks.CORRODED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_LAMP),
					getWaxed(RNBlocks.CORRODED_BRONZE_BULB), BOTH_TABS);
			// WAXED TARNISHED
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_BULB),
					getWaxed(RNBlocks.TARNISHED_BRONZE_BLOCK), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_BLOCK),
					getWaxed(RNBlocks.TARNISHED_CHISELED_BRONZE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_CHISELED_BRONZE),
					getWaxed(RNBlocks.TARNISHED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_GRATE),
					getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS),
					getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS),
					getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB),
					getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR),
					getWaxed(RNBlocks.TARNISHED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_LAMP),
					getWaxed(RNBlocks.TARNISHED_BRONZE_BULB), BOTH_TABS);
			// WAXED CRYSTALLIZED
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_BULB),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK),
					getWaxed(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_GRATE),
					getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS),
					getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS),
					getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB),
					getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_LAMP), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_LAMP),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_BULB), BOTH_TABS);
		}
		private static void injectRedstoneBlocks(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.REDSTONE_BLOCKS) return;

			event.insertAfter(Items.WAXED_OXIDIZED_COPPER_BULB.getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_BULB),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_BULB),
					getWaxed(RNBlocks.CORRODED_BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_BULB),
					getWaxed(RNBlocks.TARNISHED_BRONZE_BULB), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_BULB),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_BULB), BOTH_TABS);

			event.insertAfter(Items.REDSTONE_ORE.getDefaultInstance(),
					RNBlocks.BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_GRATE),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_GRATE),
					getWaxed(RNBlocks.CORRODED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_GRATE),
					getWaxed(RNBlocks.TARNISHED_BRONZE_GRATE), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_GRATE),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_GRATE), BOTH_TABS);

			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_GRATE),
					RNBlocks.BRONZE_SPRING.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_SPRING.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_SPRING.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_SPRING.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_SPRING.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_SPRING.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_SPRING.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_SPRING.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_SPRING.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_SPRING.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_SPRING), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_SPRING),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_SPRING), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_SPRING),
					getWaxed(RNBlocks.CORRODED_BRONZE_SPRING), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_SPRING),
					getWaxed(RNBlocks.TARNISHED_BRONZE_SPRING), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_SPRING),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_SPRING), BOTH_TABS);

			event.insertAfter(getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_GRATE),
					RNBlocks.BRONZE_VENT.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.BRONZE_VENT.get().asItem().getDefaultInstance(),
					RNBlocks.DISCOLORED_BRONZE_VENT.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.DISCOLORED_BRONZE_VENT.get().asItem().getDefaultInstance(),
					RNBlocks.CORRODED_BRONZE_VENT.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CORRODED_BRONZE_VENT.get().asItem().getDefaultInstance(),
					RNBlocks.TARNISHED_BRONZE_VENT.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.TARNISHED_BRONZE_VENT.get().asItem().getDefaultInstance(),
					RNBlocks.CRYSTALLIZED_BRONZE_VENT.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.CRYSTALLIZED_BRONZE_VENT.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_VENT), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_VENT),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_VENT), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_VENT),
					getWaxed(RNBlocks.CORRODED_BRONZE_VENT), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_VENT),
					getWaxed(RNBlocks.TARNISHED_BRONZE_VENT), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_VENT),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_VENT), BOTH_TABS);

			event.insertAfter(Items.OBSERVER.getDefaultInstance(),
					RNBlocks.WAXED_COPPER_LASER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.WAXED_COPPER_LASER.get().asItem().getDefaultInstance(),
					RNBlocks.WAXED_EXPOSED_COPPER_LASER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.WAXED_EXPOSED_COPPER_LASER.get().asItem().getDefaultInstance(),
					RNBlocks.WAXED_WEATHERED_COPPER_LASER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.WAXED_WEATHERED_COPPER_LASER.get().asItem().getDefaultInstance(),
					RNBlocks.WAXED_OXIDIZED_COPPER_LASER.get().asItem().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNBlocks.WAXED_OXIDIZED_COPPER_LASER.get().asItem().getDefaultInstance(),
					getWaxed(RNBlocks.BRONZE_LASER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.BRONZE_LASER),
					getWaxed(RNBlocks.DISCOLORED_BRONZE_LASER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.DISCOLORED_BRONZE_LASER),
					getWaxed(RNBlocks.CORRODED_BRONZE_LASER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.CORRODED_BRONZE_LASER),
					getWaxed(RNBlocks.TARNISHED_BRONZE_LASER), BOTH_TABS);
			event.insertAfter(getWaxed(RNBlocks.TARNISHED_BRONZE_LASER),
					getWaxed(RNBlocks.CRYSTALLIZED_BRONZE_LASER), BOTH_TABS);
		}
		private static void injectSpawnEggs(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() != CreativeModeTabs.SPAWN_EGGS) return;
			event.insertAfter(Items.BLAZE_SPAWN_EGG.getDefaultInstance(),
					RNItems.BRONZE_SPAWN_EGG.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.BRONZE_SPAWN_EGG.get().getDefaultInstance(),
					RNItems.DISCOLORED_BRONZE_SPAWN_EGG.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.DISCOLORED_BRONZE_SPAWN_EGG.get().getDefaultInstance(),
					RNItems.CORRODED_BRONZE_SPAWN_EGG.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.CORRODED_BRONZE_SPAWN_EGG.get().getDefaultInstance(),
					RNItems.TARNISHED_BRONZE_SPAWN_EGG.get().getDefaultInstance(), BOTH_TABS);
			event.insertAfter(RNItems.TARNISHED_BRONZE_SPAWN_EGG.get().getDefaultInstance(),
					RNItems.CRYSTALLIZED_BRONZE_SPAWN_EGG.get().getDefaultInstance(), BOTH_TABS);
		}
	}
	private static net.minecraft.world.item.ItemStack getWaxed(DeferredBlock<?> block) {
		return BuiltInRegistries.ITEM
				.get(RubinatedNether.id("waxed_" + BuiltInRegistries.BLOCK.getKey(block.get()).getPath()))
				.getDefaultInstance();
	}
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