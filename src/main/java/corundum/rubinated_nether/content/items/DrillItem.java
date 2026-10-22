package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.module.Flywheel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DrillItem extends PickShovelItem {
    public static final int MAX_BOOST_VALUE = 50;

    public static final int TICKS_PER_LEVEL = 8;

    public static final int GRACE_TICKS = 15;

    public DrillItem(Properties properties) {
        super(RNTiers.BRONZE, properties);
    }

    @Override
    protected void appendStats(ItemStack stack, List<Component> tooltip) {
        ToolStatsTooltip.breaking(tooltip, maxBoost(stack), 20.0 / ticksPerLevel(stack));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide() && state.is(RNBlocks.MOLTEN_RUBY_ORE.get())) {
            int count = 3 + level.random.nextInt(2);
            ItemStack drops = new ItemStack(RNItems.MOLTEN_RUBY.get(), count);
            Block.popResource(level, pos, drops);
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() && isSelected && entity instanceof Player player) {
            player.displayClientMessage(
                    Component.literal(String.format("Drill Combo: %d", getBoostLevel(stack))), true
            );
        }

        if (!level.isClientSide()
                && entity instanceof ServerPlayer player
                && getBoostLevel(stack) > 0
                && !DrillHandler.isMining(player, level.getGameTime())) {
            setBoostLevel(stack, 0);
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

    @Override
    public boolean isRunning(ServerPlayer player) {
        return DrillHandler.isMining(player, player.level().getGameTime());
    }

    public float calcModifier(ItemStack stack, double baseValue) {
        if (!MechanicalFuel.hasFuel(stack)) return (float) baseValue;
        int res = getBoostLevel(stack);
        return res > 0 ? (float) (baseValue * res) : (float) baseValue;
    }

    public static int maxBoost(ItemStack stack) {
        return Math.max(1, Math.round(MAX_BOOST_VALUE * Flywheel.peakMult(stack)));
    }

    public static float ticksPerLevel(ItemStack stack) {
        return TICKS_PER_LEVEL / Flywheel.accelMult(stack);
    }

    public static int getBoostLevel(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.BLOCKS_BROKEN, 0);
    }

    public static void setBoostLevel(ItemStack stack, int level) {
        stack.set(RNDataComponents.BLOCKS_BROKEN, Mth.clamp(level, 0, maxBoost(stack)));
    }
}