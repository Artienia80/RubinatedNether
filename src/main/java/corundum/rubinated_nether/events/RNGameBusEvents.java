package corundum.rubinated_nether.events;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.items.DrillItem;
import corundum.rubinated_nether.misc.DatapackRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RNGameBusEvents {

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<UUID, Long> lastMiningTick = new HashMap<>();
    private static final Map<UUID, Integer> decayTicks = new HashMap<>();
    private static final Map<UUID, Long> lastLoggedTick = new HashMap<>();

    @SubscribeEvent
    public static void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getMainHandItem();

        if (!event.isCanceled() && itemStack.getItem() instanceof DrillItem drillItem) {
            long currentTick = player.level().getGameTime();
            UUID playerId = player.getUUID();
            lastMiningTick.put(playerId, currentTick);

            // Retrieve current counter
            CompoundTag tag = drillItem.getNBT();
            int ticksUsed = tag.getInt("ticksUsed");

            // Calculate multiplier
            float multiplier = 1.0f + ((float) ticksUsed / DrillItem.MAX_USE_TICKS) *
                    (DrillItem.MAX_MULTIPLIER_BOOST - 1.0f);

            // Increment ticksUsed but cap at MAX_USE_TICKS
            if (ticksUsed < DrillItem.MAX_USE_TICKS) {
                tag.putInt("ticksUsed", ticksUsed + 1);
            }

            event.setNewSpeed(event.getNewSpeed() * multiplier);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        UUID playerId = player.getUUID();
        long currentTick = player.level().getGameTime();

        if (stack.getItem() instanceof DrillItem drill) {
            CompoundTag tag = drill.getNBT();
            if (tag == null) return;

            // Log multiplier every 2 seconds (40 ticks)
            if (!lastLoggedTick.containsKey(playerId) || currentTick - lastLoggedTick.get(playerId) >= 40) {
                int ticksUsed = tag.getInt("ticksUsed");
                float multiplier = 1.0f + ((float) ticksUsed / DrillItem.MAX_USE_TICKS) *
                        (DrillItem.MAX_MULTIPLIER_BOOST - 1.0f);
                player.sendSystemMessage(Component.literal("Current Drill Speed Multiplier: " + multiplier));
                lastLoggedTick.put(playerId, currentTick);
            }

            // Handle multiplier decay when stopping
            if (lastMiningTick.containsKey(playerId)) {
                long lastTick = lastMiningTick.get(playerId);

                if (currentTick - lastTick > 20) { // Raised from 15 to 20 ticks
                    if (!decayTicks.containsKey(playerId)) {
                        decayTicks.put(playerId, 0);
                    }

                    int decayCount = decayTicks.get(playerId);

                    if (currentTick % 20 == 0) { // Every 20 ticks
                        int ticksUsed = tag.getInt("ticksUsed");

                        if (ticksUsed > 0) {
                            int reduction = (int) Math.ceil(ticksUsed * 0.25);
                            tag.putInt("ticksUsed", Math.max(ticksUsed - reduction, 0));
                        } else {
                            lastMiningTick.remove(playerId);
                            decayTicks.remove(playerId);
                        }

                        decayTicks.put(playerId, decayCount + 1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void freezerFuel(ServerAboutToStartEvent event) {
        FreezerBlockEntity.cleanFreezingTimes();

        var entries = event.getServer().registryAccess().registryOrThrow(DatapackRegistry.FREEZER_FUELS).entrySet();
        LOGGER.info("Registered Freezer Fuels: {}", entries.size());

        for (var entry : entries) {
            var x = entry.getValue();
            var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(x.item()));

            LOGGER.info(x.toString());
            FreezerBlockEntity.addItemFreezingTime(item, x.freezeTime());
        }
    }
}
