package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RuneAnvilHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().is(RNTags.Items.RUNES) || event.getRight().is(RNTags.Items.RUNES)) {
            event.setCanceled(true);
        }
    }
}
