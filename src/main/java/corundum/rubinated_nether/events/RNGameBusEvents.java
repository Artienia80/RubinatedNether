package corundum.rubinated_nether.events;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.DrillItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.GAME)
public class RNGameBusEvents {

    // Stores the last tick a mining event occurred for each player
    private static final Map<UUID, Long> lastMiningTick = new HashMap<>();

    @SubscribeEvent
    public static void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getMainHandItem();

        if (!event.isCanceled()) {
            if (itemStack.getItem() instanceof DrillItem drillItem) {
                // Update the last mining tick for the player
                long currentTick = player.level().getGameTime();
                lastMiningTick.put(player.getUUID(), currentTick);

                // Retrieve the current counter and calculate a multiplier
                int ticksUsed = drillItem.getNBT().getInt("ticksUsed");

                float multiplier = 1.0f + ((float) ticksUsed / DrillItem.MAX_USE_TICKS)
                        * (DrillItem.MAX_MULTIPLIER_BOOST - 1.0f);

                // If the tick count goes over the max, it doesn't get incremented - darksonic300
                if(ticksUsed < DrillItem.MAX_USE_TICKS)
                    drillItem.getNBT().putInt("ticksUsed", ticksUsed + 1);

                event.setNewSpeed(event.getNewSpeed() * multiplier);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // We're only interested in the end phase to avoid duplicate work
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();

            // Only act if the player is holding the drill
            if (stack.getItem() instanceof DrillItem drill) {
                UUID playerId = player.getUUID();
                long currentTick = player.level().getGameTime();

                // Check when the last mining event occurred for this player
                if (lastMiningTick.containsKey(playerId)) {
                    long lastTick = lastMiningTick.get(playerId);
                    // If the player hasn't mined for a while, reset the multiplier
                    if (currentTick - lastTick > 15) {
                        CompoundTag tag = drill.getNBT();
                        if(tag != null) {
                            tag.putInt("ticksUsed", 0); // Reset multiplier progress
                            // Optionally remove the player from the map if no longer needed
                            lastMiningTick.remove(playerId);
                        }
                    }
                }
            }
        }
}
