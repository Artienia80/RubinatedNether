package corundum.rubinated_nether.content.blocks.entities;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class CofferItemHandler implements IItemHandler {
    private final CofferBlockEntity coffer;
    private final Container container;

    public CofferItemHandler(CofferBlockEntity coffer) {
        this.coffer = coffer;
        this.container = coffer.getContainer();
    }

    @Override
    public int getSlots() {
        return CofferBlockEntity.CONTAINER_SIZE;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.validSlot(slot) ? this.container.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !this.validSlot(slot) || !this.container.canPlaceItem(slot, stack)) {
            return stack;
        }

        ItemStack existing = this.container.getItem(slot);
        int limit = this.coffer.getMaxStackSize(stack);
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                return stack;
            }
            limit -= existing.getCount();
        }
        if (limit <= 0) {
            return stack;
        }

        int accepted = Math.min(limit, stack.getCount());
        if (!simulate) {
            if (existing.isEmpty()) {
                this.container.setItem(slot, stack.copyWithCount(accepted));
            } else {
                this.container.setItem(slot, existing.copyWithCount(existing.getCount() + accepted));
            }
        }

        return accepted >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - accepted);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0 || !this.validSlot(slot)) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = this.container.getItem(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (simulate) {
            return existing.copyWithCount(Math.min(toExtract, existing.getCount()));
        }
        return this.container.removeItem(slot, toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        ItemStack existing = this.getStackInSlot(slot);
        return existing.isEmpty() ? this.coffer.getMaxStackSize() : this.coffer.getMaxStackSize(existing);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.validSlot(slot) && this.container.canPlaceItem(slot, stack);
    }

    private boolean validSlot(int slot) {
        return slot >= 0 && slot < CofferBlockEntity.CONTAINER_SIZE;
    }
}
