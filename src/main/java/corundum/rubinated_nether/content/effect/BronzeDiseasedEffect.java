package corundum.rubinated_nether.content.effect;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class BronzeDiseasedEffect extends MobEffect {
    public BronzeDiseasedEffect() {
        super(MobEffectCategory.HARMFUL, 0xcff1d1);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        BlockState belowState = entity.level().getBlockState(entity.blockPosition().below());
        boolean onCrystallized = belowState.is(RNTags.Blocks.CRYSTALLIZED_BLOCKS);

        if (onCrystallized) {
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ResourceLocation.withDefaultNamespace("effect.weakness"),
                -0.4,
                AttributeModifier.Operation.ADD_VALUE
        );

        super.applyEffectTick(entity, amplifier);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = Math.max(20, 100 - amplifier * 20);
        return duration % interval == 0;
    }
}
