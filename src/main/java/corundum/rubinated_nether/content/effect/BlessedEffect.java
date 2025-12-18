package corundum.rubinated_nether.content.effect;

import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BlessedEffect extends MobEffect {
    public BlessedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xffd700);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (RNConfig.blessedEffectGlowing) {
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, true, false, false));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 100 == 0;
    }
}