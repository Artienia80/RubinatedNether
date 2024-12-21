package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RubinatedNether.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RN_TAB = CREATIVE_MODE_TABS.register(
		"rubinated_nether_tab", ()
		 -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.examplemod"))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> RNItems.RUBY_ITEM.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				output.accept(RNItems.RUBY_ITEM.get());
				output.accept(RNItems.MOLTEN_RUBY_ITEM.get());
				output.accept(RNItems.RUBY_SHARD_ITEM.get());
				output.accept(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get());

				output.accept(RNBlocks.NETHER_RUBY_ORE.get());
				output.accept(RNBlocks.MOLTEN_RUBY_ORE.get());
				output.accept(RNBlocks.RUBINATED_BLACKSTONE.get());
				output.accept(RNBlocks.RUBY_BLOCK.get());
				output.accept(RNBlocks.MOLTEN_RUBY_BLOCK.get());

				output.accept(RNBlocks.BLEEDING_OBSIDIAN.get());

				output.accept(RNBlocks.ALTAR_STONE.get());
				output.accept(RNBlocks.ALTAR_STONE_BRICKS.get());

				output.accept(RNBlocks.ALTAR_STONE_TILES.get());
				output.accept(RNBlocks.ALTAR_STONE_TILES_STAIRS.get());
				output.accept(RNBlocks.ALTAR_STONE_TILES_SLAB.get());
				output.accept(RNBlocks.ALTAR_STONE_TILES_WALL.get());




				output.accept(RNBlocks.ALTAR_STONE_PILLAR.get());
				output.accept(RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
				output.accept(RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS.get());

				output.accept(RNBlocks.SOAKSTONE.get());

				output.accept(RNBlocks.RUBY_GLASS.get());
				output.accept(RNBlocks.RUBY_GLASS_PANE.get());
				output.accept(RNBlocks.MOLTEN_RUBY_GLASS.get());
				output.accept(RNBlocks.ORNATE_RUBY_GLASS.get());
				output.accept(RNBlocks.ORNATE_RUBY_GLASS_PANE.get());

				output.accept(RNBlocks.RUBY_LANTERN.get());
				output.accept(RNBlocks.CHANDELIER.get());
				output.accept(RNBlocks.LAVA_LAMP.get());
//				output.accept(RNBlocks.BRAZIER.get());

				output.accept(RNBlocks.FREEZER.get());
//				output.accept(Blocks.FROSTED_ICE);
				output.accept(RNBlocks.DRY_ICE.get());

//				output.accept(RNBlocks.RUBY_LASER.get());
//				output.accept(RNItems.RUBY_LENS.get());
//
//				output.accept(RNBlocks.RUBINATION_ALTAR.get());
				output.accept(RNBlocks.RUNESTONE.get());

				output.accept(RNItems.MUSIC_DISC_SHIMMER.get());
			})
			.build()
	);

	public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(RNBlocks.MOLTEN_RUBY_ORE);
		}
	}
}
