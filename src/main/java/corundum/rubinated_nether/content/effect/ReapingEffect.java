package corundum.rubinated_nether.content.effect;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ReapingEffect extends MobEffect {
    public static final ResourceLocation BONUS_HEALTH_ID = RubinatedNether.id("reaping_bonus_health");

    public ReapingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xB3122B);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);
        AttributeInstance maxHealth = attributeMap.getInstance(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(BONUS_HEALTH_ID);
        }
    }
}
