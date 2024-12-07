package ruby.rubinated_nether;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import ruby.rubinated_nether.content.RubinatedNetherBlocks;
import ruby.rubinated_nether.content.RubinatedNetherItems;
import ruby.rubinated_nether.content.RubinatedNetherTabs;

@Mod(RubinatedNether.MODID)
public class RubinatedNether {
	public static final String MODID = "rubinated_nether";
	private static final Logger LOGGER = LogUtils.getLogger();

	public RubinatedNether(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(this::commonSetup);

		RubinatedNetherBlocks.BLOCKS.register(modEventBus);
		RubinatedNetherItems.ITEMS.register(modEventBus);
		RubinatedNetherTabs.CREATIVE_MODE_TABS.register(modEventBus);

		NeoForge.EVENT_BUS.register(this);

		modEventBus.addListener(this::addCreative);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("Rubinating all over your Nether");
	}

	// Add the example block item to the building blocks tab
	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
			event.accept(RubinatedNetherBlocks.EXAMPLE_BLOCK_ITEM);
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// Do something when the server starts
		LOGGER.info("HELLO from server starting");
	}

	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			// Some client setup code
			LOGGER.info("HELLO FROM CLIENT SETUP");
			LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
		}
	}
}
