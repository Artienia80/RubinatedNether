package corundum.rubinated_nether.content.blocks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CofferSlot extends Slot {
    public CofferSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 256;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 256;
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }

    @Override
    public void set(ItemStack stack) {
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        super.set(stack);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if (!stack.isEmpty() && this.mayPlace(stack)) {
            ItemStack itemstack = this.getItem();
            int i = Math.min(Math.min(increment, stack.getCount()), this.getMaxStackSize(stack) - itemstack.getCount());
            if (itemstack.isEmpty()) {
                this.setByPlayer(stack.split(i));
            } else if (ItemStack.isSameItemSameComponents(itemstack, stack)) {
                stack.shrink(i);
                itemstack.grow(i);
                this.setByPlayer(itemstack);
            }

            return stack;
        } else {
            return stack;
        }
    }
}

