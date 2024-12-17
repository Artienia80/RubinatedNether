package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RubinatedNetherCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RubinatedNether.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register(
		"example_tab", ()
		 -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.examplemod"))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> RubinatedNetherItems.RUBY_ITEM.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				output.accept(RubinatedNetherItems.RUBY_ITEM.get());
				output.accept(RubinatedNetherItems.MOLTEN_RUBY_ITEM.get());
				output.accept(RubinatedNetherBlocks.CHANDELIER.get());
			})
			.build()
	);

	public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(RubinatedNetherBlocks.MOLTEN_RUBY_ORE);
		}
	}
}
