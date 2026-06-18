package corundum.rubinated_nether.content.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class VaseMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 9;
    private final Container container;

    public VaseMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public VaseMenu(int containerId, Inventory playerInventory, Container container) {
        super(RNMenuTypes.VASE_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        this.container = container;
        container.startOpen(playerInventory.player);

        for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < 3; ++l) {
                this.addSlot(new ShulkerBoxSlot(container, l + k * 3, 62 + l * 18, 17 + k * 18));
            }
        }

        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 3; ++k1) {
                this.addSlot(new Slot(playerInventory, k1 + i1 * 3 + 3, 8 + k1 * 18, 84 + i1 * 18));
            }
        }

        for(int j1 = 0; j1 < 3; ++j1) {
            this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }

    }

    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}