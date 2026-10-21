package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.module.ToolModules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
            MechanicalFuel.burn(held, ToolModules.dropletsPerBurn(held)); // each module burns extra fuel
        }
    }

    // ---------- Tool cannot work: out of fuel, or wrong kind of block for this tool ----------
    // Either way it must behave identically: no break progress, no crack animation, nothing breaks.
    private static boolean cannotWork(Player player, BlockState state) {
        ItemStack held = player.getMainHandItem();
        return held.getItem() instanceof MechanicalTool tool
                && (!MechanicalFuel.hasFuel(held) || !tool.canWorkOn(state));
    }

    // Cancelled break speed = no progress and no crack animation (client and server)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (cannotWork(event.getEntity(), event.getState())) event.setCanceled(true);
    }

    // Stops the start of a dig, so no tool handler begins a session
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START
                && cannotWork(event.getEntity(), event.getLevel().getBlockState(event.getPos()))) {
            event.setCanceled(true);
        }
    }

    // Final safety net on the server: instant-break blocks, creative mode, other mods
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (cannotWork(event.getPlayer(), event.getState())) event.setCanceled(true);
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