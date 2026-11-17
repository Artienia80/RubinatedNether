package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.effect.ReapingEffect;
import corundum.rubinated_nether.misc.RNAttachments;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class ReapingHandler {
    private static final int MAX_STEPS = 256;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getEntity() == player || !player.hasEffect(RNEffects.REAPING)) {
            return;
        }

        float damage = event.getNewDamage();
        if (damage <= 0.0F) {
            return;
        }

        float pool = player.getData(RNAttachments.REAPING_POOL) + damage;
        player.setData(RNAttachments.REAPING_POOL, convert(player, pool));
    }

    private static float convert(ServerPlayer player, float pool) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return 0.0F;
        }

        float healCost = Math.max(0.01F, RNConfig.reapingDamagePerHealth);
        float restoreCost = healCost * 2.0F;
        float overhealCost = Math.max(healCost, RNConfig.reapingOverhealDamagePerHealth);
        float bonusCap = (float) maxHealth.getBaseValue();

        for (int step = 0; step < MAX_STEPS; step++) {
            if (player.getHealth() < player.getMaxHealth()) {
                if (pool < healCost) {
                    return pool;
                }
                pool -= healCost;
                player.heal(1.0F);
                continue;
            }

            MobEffectInstance starved = player.getEffect(RNEffects.SOUL_STARVED);
            if (starved != null) {
                if (pool < restoreCost) {
                    return pool;
                }
                pool -= restoreCost;
                restoreHeart(player, starved);
                continue;
            }

            float bonus = getBonus(maxHealth);
            if (bonus >= bonusCap) {
                return 0.0F;
            }
            if (pool < overhealCost) {
                return pool;
            }
            pool -= overhealCost;
            grantBonusHealth(player, 1.0F);
        }

        return 0.0F;
    }

    public static float grantBonusHealth(LivingEntity entity, float amount) {
        if (amount <= 0.0F || !entity.hasEffect(RNEffects.REAPING) || entity.hasEffect(RNEffects.SOUL_STARVED)) {
            return 0.0F;
        }

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return 0.0F;
        }

        float bonus = getBonus(maxHealth);
        float granted = Math.min(amount, (float) maxHealth.getBaseValue() - bonus);
        if (granted <= 0.0F) {
            return 0.0F;
        }

        setBonus(maxHealth, bonus + granted);
        entity.heal(granted);
        return granted;
    }

    private static void restoreHeart(ServerPlayer player, MobEffectInstance starved) {
        lowerSoulStarved(player);
        player.heal(2.0F);
    }

    public static boolean lowerSoulStarved(LivingEntity entity) {
        MobEffectInstance starved = entity.getEffect(RNEffects.SOUL_STARVED);
        if (starved == null) {
            return false;
        }
        int amplifier = starved.getAmplifier();
        entity.removeEffect(RNEffects.SOUL_STARVED);
        if (amplifier > 0) {
            entity.addEffect(new MobEffectInstance(RNEffects.SOUL_STARVED, MobEffectInstance.INFINITE_DURATION, amplifier - 1, false, false, true));
        }
        return true;
    }

    private static float getBonus(AttributeInstance maxHealth) {
        AttributeModifier modifier = maxHealth.getModifier(ReapingEffect.BONUS_HEALTH_ID);
        return modifier == null ? 0.0F : (float) modifier.amount();
    }

    private static void setBonus(AttributeInstance maxHealth, float amount) {
        maxHealth.removeModifier(ReapingEffect.BONUS_HEALTH_ID);
        maxHealth.addPermanentModifier(new AttributeModifier(ReapingEffect.BONUS_HEALTH_ID, amount, AttributeModifier.Operation.ADD_VALUE));
    }
}
