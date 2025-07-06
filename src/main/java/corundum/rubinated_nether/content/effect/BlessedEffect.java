package corundum.rubinated_nether.content.effect;

import corundum.rubinated_nether.content.RNEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BlessedEffect extends MobEffect {
    public BlessedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xffd700); // Golden color
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        long dayCount = entity.level().getDayTime() / 24000L;
        int moonPhase = (int) (dayCount % 8);

        int baseDuration = 120;

        MobEffectInstance blessedInstance = entity.getEffect(RNEffects.BLESSED);
        int blessedDuration = blessedInstance != null ? blessedInstance.getDuration() : 0;
        int effectAmplifier = blessedDuration > 384000 ? 1 : 0;

        switch (moonPhase) {
            case 0: // Full Moon
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, baseDuration, effectAmplifier, true, false, false));
                break;
            case 1: // Waning Gibbous
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, baseDuration, effectAmplifier, true, false, false));
                break;
            case 2: // Last Quarter
                entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, baseDuration, effectAmplifier, true, false, false));
                break;
            case 3: // Waning Crescent
                entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, baseDuration, effectAmplifier, true, false, false));
                break;
            case 4: // New Moon
                entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, effectAmplifier, true, false, false));
                break;
            case 5: // Waxing Crescent
                entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, baseDuration, effectAmplifier, true, false, false));
                break;
            case 6: // First Quarter
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, baseDuration, effectAmplifier, true, false, false));
                break;
            case 7: // Waxing Gibbous
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, baseDuration, effectAmplifier, true, false, false));
                break;
        }

        super.applyEffectTick(entity, amplifier);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 100 == 0;
    }
}