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
				output.accept(RNBlocks.CHANDELIER.get());
			})
			.build()
	);

	public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(RNBlocks.MOLTEN_RUBY_ORE);
		}
	}
}
