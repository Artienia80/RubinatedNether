package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.BrazierTracker;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class BrazierPowerCleanupHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 20 != 0 || !player.hasEffect(RNEffects.BRAZIER_POWER)) {
            return;
        }
        if (!BrazierTracker.anyInRange(player.level(), player.getX(), player.getY(), player.getZ(), RNConfig.brazierEffectRange)) {
            player.removeEffect(RNEffects.BRAZIER_POWER);
        }
    }
}
