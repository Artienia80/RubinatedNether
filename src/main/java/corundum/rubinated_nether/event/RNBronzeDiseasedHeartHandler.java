package corundum.rubinated_nether.event;

import corundum.rubinated_nether.client.RNClientEnumExtensions;
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
            Gui.HeartType bronzeDiseased = RNClientEnumExtensions.BRONZE_DISEASED_HEART_PROXY.getValue();
            event.setType(bronzeDiseased);
        }
    }
}