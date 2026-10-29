package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.MusicBoxItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Keeps a playing music box's song attached to wherever the box actually is. */
@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class MusicBoxHandler {
    private MusicBoxHandler() {}

    @SubscribeEvent
    public static void onToss(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player && event.getEntity().getItem().getItem() instanceof MusicBoxItem) {
            MusicBoxItem.onTossed(player, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MusicBoxItem.checkPlayerSound(player);
        }
    }

    @SubscribeEvent
    public static void onItemEntityTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof ItemEntity itemEntity && itemEntity.level() instanceof ServerLevel level) {
            MusicBoxItem.tickDropped(itemEntity, level);
        }
    }

    @SubscribeEvent
    public static void onItemEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity && event.getLevel() instanceof ServerLevel level) {
            MusicBoxItem.onDroppedRemoved(itemEntity, level);
        }
    }

    /** Post: only fires once the item really went into the inventory. */
    @SubscribeEvent
    public static void onPickup(ItemEntityPickupEvent.Post event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            MusicBoxItem.onPickedUp(player, event.getItemEntity());
        }
    }
}
