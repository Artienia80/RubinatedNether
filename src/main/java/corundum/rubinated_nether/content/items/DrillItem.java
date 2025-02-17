package corundum.rubinated_nether.content.items;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DrillItem extends PickShovelItem {
    public CompoundTag data;

    public static int MAX_USE_TICKS = 800;
    public static int MAX_MULTIPLIER_BOOST = 90;

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
