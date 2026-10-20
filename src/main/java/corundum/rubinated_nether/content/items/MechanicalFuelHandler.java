package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class MechanicalFuelHandler {
    private static final Map<UUID, Integer> RUN_TICKS = new HashMap<>();

    private MechanicalFuelHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof MechanicalTool tool) || !tool.isRunning(player)) return;
        if (player.getAbilities().instabuild) return;

        UUID id = player.getUUID();
        int ticks = RUN_TICKS.merge(id, 1, Integer::sum);
        if (ticks >= MechanicalFuel.TICKS_PER_DROPLET) {
            RUN_TICKS.put(id, ticks - MechanicalFuel.TICKS_PER_DROPLET);
            MechanicalFuel.burn(held);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        RUN_TICKS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        RUN_TICKS.clear();
    }
}