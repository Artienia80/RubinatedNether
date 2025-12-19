package corundum.rubinated_nether.event;

import corundum.rubinated_nether.content.RNEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class RNBronzeDiseasedHeartHandler {

    @SubscribeEvent
    public static void onPlayerHeartType(PlayerHeartTypeEvent event) {
        Player player = event.getEntity();

        if (player.hasEffect(RNEffects.BRONZE_DISEASED)) {
            // Get the custom heart type from the enum (with mod ID prefix)
            Gui.HeartType bronzeDiseased = Gui.HeartType.valueOf("RUBINATED_NETHER_BRONZE_DISEASED");
            event.setType(bronzeDiseased);
        }
    }
}