package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.effect.BrazierPowerEffect;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RubinatedPomegranateItem extends Item {
	public RubinatedPomegranateItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, level, entity);

		if (!level.isClientSide) {
			int duration = RNConfig.getRubinatedPomegranateEffectTicks();
			if (!BrazierPowerEffect.isFromBrazier(entity)) {
				entity.addEffect(new MobEffectInstance(RNEffects.BRAZIER_POWER, duration, 0, false, RNConfig.brazierEffectParticles, true));
			}
			entity.addEffect(new MobEffectInstance(RNEffects.REAPING, duration, 0, false, true, true));
		}

		return result;
	}
}
