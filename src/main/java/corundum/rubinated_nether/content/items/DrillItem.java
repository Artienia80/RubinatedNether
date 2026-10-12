package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
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

/**
 * Speed boost is driven by how long the player has been mining nonstop, NOT by how many blocks broke.
 * DrillHandler tracks the mining chain and writes the resulting level (0..MAX_BOOST_VALUE) into the
 * BLOCKS_BROKEN component. That component now means "boost level"; it is only written when the level
 * changes (at most once every TICKS_PER_LEVEL ticks), never per tick.
 */
public class DrillItem extends PickShovelItem {
    public static final int MAX_BOOST_VALUE = 50;

    /** Ticks of continuous mining per boost level. 8 * 50 = 400 ticks = 20 s to reach full speed. */
    public static final int TICKS_PER_LEVEL = 8;

    /**
     * Max gap (ticks) between mining activity before the chain is considered broken and the boost resets.
     * Must stay above the client's 5-tick delay between blocks, plus some room for latency.
     */
    public static final int GRACE_TICKS = 15;

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
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // Is this just for debugging purposes??
        // Client-side: show action bar message while drill is held
        if (level.isClientSide() && isSelected && entity instanceof Player player) {
            player.displayClientMessage(
                    Component.literal(String.format("Drill Combo: %d", getBoostLevel(stack))), true
            );
        }

        // Server-side: if the player isn't mining right now, any leftover boost on this stack is stale
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

    /** Unchanged behaviour: base speed * boost level (or just base speed at level 0). */
    public float calcModifier(ItemStack stack, double baseValue) {
        int res = getBoostLevel(stack);
        return res > 0 ? (float) (baseValue * res) : (float) baseValue;
    }

    public static int getBoostLevel(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.BLOCKS_BROKEN, 0);
    }

    public static void setBoostLevel(ItemStack stack, int level) {
        stack.set(RNDataComponents.BLOCKS_BROKEN, Mth.clamp(level, 0, MAX_BOOST_VALUE));
    }
}