package corundum.rubinated_nether.content.effect;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BrazierPowerEffect extends MobEffect {
    public BrazierPowerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6B00);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (RNConfig.brazierPowerFireRes) {
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false, false));
        }
            if (RNConfig.brazierPowerLavaHealing && entity.isInLava()) {
            entity.heal(1.0F);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    public static boolean isFromBrazier(MobEffectInstance instance) {
        return instance != null && instance.isAmbient();
    }

    public static boolean isFromBrazier(LivingEntity entity) {
        return isFromBrazier(entity.getEffect(RNEffects.BRAZIER_POWER));
    }

    public static void removeBrazierPower(LivingEntity entity) {
        entity.removeEffect(RNEffects.BRAZIER_POWER);

        MobEffectInstance reaping = entity.getEffect(RNEffects.REAPING);
        if (reaping != null) {
            entity.addEffect(new MobEffectInstance(
                    RNEffects.BRAZIER_POWER,
                    reaping.getDuration(),
                    0,
                    false,
                    RNConfig.brazierEffectParticles,
                    true
            ));
        }
    }
}