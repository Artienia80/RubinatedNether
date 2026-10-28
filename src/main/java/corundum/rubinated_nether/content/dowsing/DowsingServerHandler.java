package corundum.rubinated_nether.content.dowsing;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class DowsingServerHandler {
    private static final Map<UUID, Optional<BlockPos>> TRACKED = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID id = player.getUUID();

        if (!DowsingRods.isHolding(player)) {
            if (TRACKED.remove(id) != null) {
                broadcast(player, Optional.empty());
            }
            return;
        }

        boolean tracked = TRACKED.containsKey(id);
        if (tracked && player.tickCount % DowsingRods.SEARCH_INTERVAL != 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        BlockPos found = level.findNearestMapStructure(DowsingRods.TARGETS, player.blockPosition(), DowsingRods.SEARCH_RADIUS, false);
        Optional<BlockPos> result = Optional.ofNullable(found);

        TRACKED.put(id, result);
        broadcast(player, result);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target && event.getEntity() instanceof ServerPlayer observer) {
            Optional<BlockPos> current = TRACKED.get(target.getUUID());
            if (current != null) {
                PacketDistributor.sendToPlayer(observer, payload(target, current));
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        TRACKED.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        TRACKED.remove(event.getEntity().getUUID());
    }

    private static void broadcast(ServerPlayer player, Optional<BlockPos> target) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, payload(player, target));
    }

    private static DowsingTargetPayload payload(ServerPlayer player, Optional<BlockPos> target) {
        return new DowsingTargetPayload(player.getId(), target.isPresent(), target.orElse(BlockPos.ZERO));
    }
}
