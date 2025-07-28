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
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = slot.getItem();
        ItemStack copy = sourceStack.copy();

        boolean fromCoffer = index < 8;
        if (fromCoffer) {
            if (!this.moveItemStackTo(sourceStack, 8, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, 0, 8, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean moved = false;

        int index = reverseDirection ? endIndex - 1 : startIndex;

        while (!stack.isEmpty()) {
            if (reverseDirection ? index < startIndex : index >= endIndex) break;

            Slot slot = this.slots.get(index);
            ItemStack slotStack = slot.getItem();

            if (!slotStack.isEmpty() && ItemStack.isSameItem(stack, slotStack)) {
                int maxSize = (slot instanceof CofferSlot) ? 256 : slot.getMaxStackSize();

                int newCount = slotStack.getCount() + stack.getCount();
                if (newCount <= maxSize) {
                    stack.setCount(0);
                    slotStack.setCount(newCount);
                    slot.setChanged();
                    moved = true;
                } else if (slotStack.getCount() < maxSize) {
                    int diff = maxSize - slotStack.getCount();
                    stack.shrink(diff);
                    slotStack.grow(diff);
                    slot.setChanged();
                    moved = true;
                }
            }

            index += reverseDirection ? -1 : 1;
        }

        index = reverseDirection ? endIndex - 1 : startIndex;
        while (!stack.isEmpty()) {
            if (reverseDirection ? index < startIndex : index >= endIndex) break;

            Slot slot = this.slots.get(index);
            if (slot.getItem().isEmpty() && slot.mayPlace(stack)) {
                int maxSize = (slot instanceof CofferSlot) ? 256 : slot.getMaxStackSize();
                ItemStack copy = stack.copy();
                copy.setCount(Math.min(stack.getCount(), maxSize));
                slot.set(copy);
                slot.setChanged();
                stack.shrink(copy.getCount());
                moved = true;
            }

            index += reverseDirection ? -1 : 1;
        }

        return moved;
    }


}
