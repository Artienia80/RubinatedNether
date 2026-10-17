package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.PocketwatchData;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class PocketwatchDeathHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        long gameTime = player.level().getGameTime();
        long thresholdTicks = RNConfig.altarFullBlessingThreshold * 60L * 20L;

        for (ItemStack watch : PocketwatchItem.findPocketwatches(player)) {
            PocketwatchData data = watch.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);

            if (!PocketwatchItem.isEffectivelyTicking(data, gameTime)) {
                continue;
            }

            int remaining = PocketwatchItem.getRemainingTicks(data, gameTime);

            if (remaining > thresholdTicks) {
                watch.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.BROKEN);

                // The watch is broken: drop any leftover stop-lock cooldown.
                player.getCooldowns().removeCooldown(RNItems.POCKETWATCH.get());

                event.setCanceled(true);
                player.setHealth(1.0F);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, PocketwatchSavePayload.INSTANCE);
            } else {
                watch.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
            }

            PocketwatchItem.syncModelFrame(watch, gameTime);
            break;
        }
    }
}