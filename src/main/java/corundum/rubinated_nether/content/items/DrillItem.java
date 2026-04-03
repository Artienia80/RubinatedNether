package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
    public static int MAX_USE_TICKS = 400;
    public static int MAX_MULTIPLIER_BOOST = 50;
    public boolean shouldIncrease;
    public float currentMultiplier;

	public DrillItem(Properties properties) {
		super(RNTiers.BRONZE, properties);
        this.currentMultiplier = 1.0F;
        this.shouldIncrease = false;
	}

    @Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!level.isClientSide() && state.is(RNBlocks.MOLTEN_RUBY_ORE.get())) {
			int count = 3 + level.random.nextInt(2); // 3-4 items
			ItemStack drops = new ItemStack(RNItems.MOLTEN_RUBY.get(), count);
			Block.popResource(level, pos, drops);
		}

		return true;
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

    public float calcModifier(double baseValue) {
        return (float) (baseValue * currentMultiplier);
    }
}