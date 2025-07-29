package corundum.rubinated_nether.content.menu;

import corundum.rubinated_nether.content.blocks.CofferContainer;
import corundum.rubinated_nether.content.blocks.CofferSlot;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CofferMenu extends AbstractContainerMenu {
    private final Container container;

    public CofferMenu(int i, Inventory inventory) {
        this(i, inventory, new CofferContainer());
    }

    public CofferMenu(int id, Inventory playerInv, Container container) {
        super(RNMenuTypes.COFFER_MENU.get(), id);
        this.container = container;

        // Coffer slots (8 slots)
        for (int i = 0; i < 8; i++) {
            this.addSlot(new CofferSlot(container, i, 20 + i * 18, 20) {
                @Override
                public int getMaxStackSize() {
                    return 256;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    if (!(this.container instanceof CofferBlockEntity)) {
                        if (stack.getCount() > 64) {
                            stack.setCount(64);
                        }
                    }
                    super.onTake(player, stack);
                }
            });


        }

        int offsetY = 50 + 36;

        // Player inventory (3 rows x 9 columns)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, offsetY + row * 18));
            }
        }

        // Hotbar (1 row)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, offsetY + 3 * 18));
        }
    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack copy = stack.copy();

            if (index < 8) {
                if (!this.moveItemStackTo(stack, 8, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, 8, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            return copy;
        }
        return ItemStack.EMPTY;
    }


    public boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        boolean flag = false;
        int i = startIndex;

        if (reverse) i = endIndex - 1;

        while (!stack.isEmpty() && (reverse ? i >= startIndex : i < endIndex)) {
            Slot slot = this.slots.get(i);
            ItemStack existing = slot.getItem();

            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(stack, existing)) {
                int maxStack = 256;
                int newCount = existing.getCount() + stack.getCount();
                if (newCount <= maxStack) {
                    stack.setCount(0);
                    existing.setCount(newCount);
                    slot.setChanged();
                    flag = true;
                } else if (existing.getCount() < maxStack) {
                    stack.shrink(maxStack - existing.getCount());
                    existing.setCount(maxStack);
                    slot.setChanged();
                    flag = true;
                }
            }

            if (reverse) --i;
            else ++i;
        }

        i = reverse ? endIndex - 1 : startIndex;
        while (!stack.isEmpty() && (reverse ? i >= startIndex : i < endIndex)) {
            Slot slot = this.slots.get(i);
            if (!slot.hasItem() && slot.mayPlace(stack)) {
                ItemStack placed = stack.copy();
                placed.setCount(Math.min(stack.getCount(), 256));
                slot.set(placed);
                stack.shrink(placed.getCount());
                flag = true;
                break;
            }

            if (reverse) --i;
            else ++i;
        }

        return flag;
    }

}
