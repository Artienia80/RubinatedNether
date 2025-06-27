package corundum.rubinated_nether.content.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;

public class DrillItem extends PickShovelItem {
	public CompoundTag data;

	public static int MAX_USE_TICKS = 600;
	public static int MAX_MULTIPLIER_BOOST = 100;

	public DrillItem(Properties properties) {
		super(RNTiers.BRONZE, properties);
		serializeNBT();
	}

	public void serializeNBT() {
		data = new CompoundTag();
		data.putInt("ticksUsed", 0);
	}

	public CompoundTag getNBT() {
		return data;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!level.isClientSide() && state.is(RNBlocks.MOLTEN_RUBY_ORE.get())) {
			int count = 3 + level.random.nextInt(2); // 3-4 items
			ItemStack drops = new ItemStack(RNItems.MOLTEN_RUBY_ITEM.get(), count);
			Block.popResource(level, pos, drops);
		}

		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
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
}