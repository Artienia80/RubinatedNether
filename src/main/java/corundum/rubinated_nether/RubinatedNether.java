package corundum.rubinated_nether;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import corundum.rubinated_nether.content.RubinatedNetherItems;
import corundum.rubinated_nether.content.RubinatedNetherTabs;
import corundum.rubinated_nether.data.Datagen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(RubinatedNether.MODID)
public class RubinatedNether {
	public static final String MODID = "rubinated_nether";
	private static final Logger LOGGER = LogUtils.getLogger();

	public RubinatedNether(IEventBus modEventBus, ModContainer modContainer) {
		LOGGER.info("Rubinating all over your Nether...");

		RubinatedNetherBlocks.BLOCKS.register(modEventBus);
		RubinatedNetherItems.ITEMS.register(modEventBus);
		RubinatedNetherTabs.CREATIVE_MODE_TABS.register(modEventBus);

		modEventBus.addListener(Datagen::datagen);
		modEventBus.addListener(RubinatedNetherTabs::addCreative);
	}
}
