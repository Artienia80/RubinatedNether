package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class DrillHandler {
    private static final class Chain {
        long startTick;
        long lastActiveTick;
        BlockPos pos;
    }

    private static final Map<UUID, Chain> CHAINS = new HashMap<>();

    private DrillHandler() {}

    public static boolean isMining(ServerPlayer player, long gameTime) {
        Chain chain = CHAINS.get(player.getUUID());
        return chain != null && gameTime - chain.lastActiveTick <= DrillItem.GRACE_TICKS;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        long now = player.level().getGameTime();

        switch (event.getAction()) {
            case START -> {
                Level level = player.level();
                BlockPos pos = event.getPos();
                BlockState state = level.getBlockState(pos);
                if (isHoldingDrill(player) && player.getMainHandItem().getItem() instanceof DrillItem drill && drill.canWorkOn(state)
                        && !state.isAir() && state.getDestroySpeed(level, pos) >= 0f) {
                    begin(id, pos, now);
                } else {
                    Chain chain = CHAINS.get(id);
                    if (chain != null) chain.pos = null;
                }
            }
            case ABORT -> {
                Chain chain = CHAINS.get(id);
                if (chain != null) chain.pos = null;
            }
            case STOP -> {
                Chain chain = CHAINS.get(id);
                if (chain != null) {
                    chain.pos = null;
                    if (isHoldingDrill(player) && now - chain.lastActiveTick <= DrillItem.GRACE_TICKS) {
                        chain.lastActiveTick = now;
                    }
                }
            }
            default -> {}
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!isHoldingDrill(player)) return;
        begin(player.getUUID(), null, player.level().getGameTime());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        Chain chain = CHAINS.get(id);
        if (chain == null) return;

        long now = player.level().getGameTime();
        ItemStack held = player.getMainHandItem();
        boolean holdingDrill = held.getItem() instanceof DrillItem;

        if (chain.pos != null) {
            if (holdingDrill && isSessionValid(player, chain.pos)) {
                chain.lastActiveTick = now;
            } else {
                chain.pos = null;
            }
        }

        if (now - chain.lastActiveTick > DrillItem.GRACE_TICKS) {
            CHAINS.remove(id);
            if (holdingDrill && DrillItem.getBoostLevel(held) != 0) {
                DrillItem.setBoostLevel(held, 0);
            }
            return;
        }

        if (holdingDrill) {
            long elapsed = chain.lastActiveTick - chain.startTick;
            int level = (int) Math.min(DrillItem.MAX_BOOST_VALUE, elapsed / DrillItem.TICKS_PER_LEVEL);
            if (DrillItem.getBoostLevel(held) != level) {
                DrillItem.setBoostLevel(held, level);
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        forget(event.getEntity());
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        forget(event.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        forget(event.getEntity());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        CHAINS.clear();
    }

    private static void forget(Player player) {
        CHAINS.remove(player.getUUID());
    }

    private static void begin(UUID id, BlockPos pos, long now) {
        Chain chain = CHAINS.get(id);
        if (chain == null || now - chain.lastActiveTick > DrillItem.GRACE_TICKS) {
            chain = new Chain();
            chain.startTick = now;
            CHAINS.put(id, chain);
        }
        chain.lastActiveTick = now;
        chain.pos = pos != null ? pos.immutable() : chain.pos;
    }

    private static boolean isHoldingDrill(Player player) {
        return player.getMainHandItem().getItem() instanceof DrillItem;
    }

    private static boolean isSessionValid(ServerPlayer player, BlockPos pos) {
        ServerLevel level = player.serverLevel();
        return player.isAlive()
                && !player.isSpectator()
                && level.isLoaded(pos)
                && !level.getBlockState(pos).isAir()
                && player.canInteractWithBlock(pos, 1.0);
    }
}