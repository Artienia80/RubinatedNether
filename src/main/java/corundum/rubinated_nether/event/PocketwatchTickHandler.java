package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.items.PocketwatchData;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class PocketwatchTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        long gameTime = player.level().getGameTime();
        ItemStack tickingWatch = null;

        for (ItemStack stack : PocketwatchItem.findPocketwatches(player)) {
            PocketwatchData data = stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
            if (PocketwatchItem.isEffectivelyTicking(data, gameTime)) {
                tickingWatch = stack;
                break;
            }
        }

        if (tickingWatch == null) {
            if (player.hasEffect(RNEffects.BLESSED)) {
                player.removeEffect(RNEffects.BLESSED);
            }
            return;
        }

        if (!player.hasEffect(RNEffects.BLESSED)) {
            PocketwatchData data = tickingWatch.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
            int remaining = PocketwatchItem.getRemainingTicks(data, gameTime);
            player.addEffect(new MobEffectInstance(RNEffects.BLESSED, remaining, 0, false, false, true));
        }
    }
}