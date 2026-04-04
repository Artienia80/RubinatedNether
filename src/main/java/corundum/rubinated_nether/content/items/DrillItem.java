package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DrillItem extends PickShovelItem {
    public static final int RESET_THRESHOLD_TICKS = 60;
    public static final float MAX_BOOST_VALUE = 10.f;

	public DrillItem(Properties properties) {
		super(RNTiers.BRONZE, properties);
	}

    @Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!level.isClientSide() && state.is(RNBlocks.MOLTEN_RUBY_ORE.get())) {
			int count = 3 + level.random.nextInt(2); // 3-4 items
			ItemStack drops = new ItemStack(RNItems.MOLTEN_RUBY.get(), count);
			Block.popResource(level, pos, drops);
		}

        if (!level.isClientSide() && Boolean.TRUE.equals(stack.get(RNDataComponents.IS_COMBO))) {
            stack.set(RNDataComponents.LAST_TICK, level.getGameTime());
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
	}

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        try {
            if (!level.isClientSide() && stack.has(RNDataComponents.LAST_TICK)) {
                if (Boolean.TRUE.equals(stack.get(RNDataComponents.IS_COMBO))) {
                    this.boostMultiplier(stack);

                    long ticksPassed = level.getGameTime() - stack.get(RNDataComponents.LAST_TICK);

                    if (ticksPassed > RESET_THRESHOLD_TICKS) {
                        stack.set(RNDataComponents.IS_COMBO, false);
                        stack.set(RNDataComponents.DRILL_MULTIPLIER, 1.0f);
                        stack.remove(RNDataComponents.LAST_TICK);
                    }
                } else {
                    stack.remove(RNDataComponents.LAST_TICK);
                }
            }
        } catch (NullPointerException e) {
            RubinatedNether.LOGGER.warn("Drill DataComponents somehow broke???", e);
            stack.set(RNDataComponents.IS_COMBO, false);
            stack.set(RNDataComponents.DRILL_MULTIPLIER, 1.0f);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

    public float calcModifier(ItemStack stack, double baseValue) {
        return (float) (baseValue * stack.get(RNDataComponents.DRILL_MULTIPLIER));
    }

    public void boostMultiplier(ItemStack stack) {
        stack.set(RNDataComponents.DRILL_MULTIPLIER, Mth.clamp(stack.get(RNDataComponents.DRILL_MULTIPLIER) + 0.1f, 1.0f, MAX_BOOST_VALUE));
    }
}