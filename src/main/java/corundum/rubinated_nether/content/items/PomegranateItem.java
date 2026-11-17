package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.event.ReapingHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PomegranateItem extends Item {
	public static final float HEAL_AMOUNT = 2.0F;

	public PomegranateItem(Properties properties) {
		super(properties);
	}

	protected void applyEffects(LivingEntity entity) {
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide) {
			applyEffects(entity);
			ReapingHandler.lowerSoulStarved(entity);

			float room = Math.max(0.0F, entity.getMaxHealth() - entity.getHealth());
			float healed = Math.min(HEAL_AMOUNT, room);
			if (healed > 0.0F) {
				entity.heal(healed);
			}
			ReapingHandler.grantBonusHealth(entity, HEAL_AMOUNT - healed);
		}

		return super.finishUsingItem(stack, level, entity);
	}
}
