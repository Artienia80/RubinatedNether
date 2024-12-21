package corundum.rubinated_nether.events;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.screen.FreezerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RNClientModBusEvents {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RNMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
    }
}
