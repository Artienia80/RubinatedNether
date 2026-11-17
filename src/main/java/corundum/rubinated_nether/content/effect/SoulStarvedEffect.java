package corundum.rubinated_nether.content.effect;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class SoulStarvedEffect extends MobEffect {
    public SoulStarvedEffect() {
        super(MobEffectCategory.HARMFUL, 0x4DD0E1);
        this.addAttributeModifier(
                Attributes.MAX_HEALTH,
                RubinatedNether.id("effect.soul_starved"),
                -2.0,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        cures.clear();
    }
}
