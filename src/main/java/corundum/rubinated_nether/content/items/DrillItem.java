package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DrillItem extends PickShovelItem {
    public static final int RESET_THRESHOLD_TICKS = 40;
    public static final int MAX_BOOST_VALUE = 50;

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

        if (!level.isClientSide()) {
            this.boostMultiplier(stack);
            stack.set(RNDataComponents.LAST_TICK, level.getGameTime());
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // Is this just for debugging purposes??
        // Client-side: show action bar message while combo is active and drill is held
        if (level.isClientSide() && isSelected && entity instanceof Player player) {
            int multiplier = getCurrentBlockCombo(stack);
            player.displayClientMessage(
                    Component.literal(String.format("Drill Combo: %d", multiplier)), true
            );
        }

        // Server-side: manage combo state and multiplier
        if (!level.isClientSide() && stack.has(RNDataComponents.LAST_TICK)) {
            // NullPointerException suppressed, as component is clearly checked
            long ticksPassed = level.getGameTime() - stack.get(RNDataComponents.LAST_TICK);

            if (ticksPassed > RESET_THRESHOLD_TICKS) {
                stack.set(RNDataComponents.BLOCKS_BROKEN, 0);
                stack.remove(RNDataComponents.LAST_TICK);
            }
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
        int res = getCurrentBlockCombo(stack);
        return res > 0 ? (float) (baseValue * res) : (float) baseValue;
    }

    public void boostMultiplier(ItemStack stack) {
        stack.set(RNDataComponents.BLOCKS_BROKEN, Mth.clamp(getCurrentBlockCombo(stack) + 1, 0, MAX_BOOST_VALUE));
    }

    private int getCurrentBlockCombo(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.BLOCKS_BROKEN, 0);
    }
}