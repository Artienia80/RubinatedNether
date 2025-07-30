package corundum.rubinated_nether.content.menu.coffer;

import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerMenu;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedSimpleContainer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CofferMenu extends LimitlessContainerMenu {
    private final MultipliedContainer container;

    public CofferMenu(int containerId, Inventory inventory) {
        this(containerId, inventory,
                new MultipliedSimpleContainer(4,
                        CofferBlockEntity.COINTAINER_SIZE
                )
        );
    }

    public CofferMenu(int id, Inventory playerInv, MultipliedContainer container) {
        super(RNMenuTypes.COFFER_MENU.get(), id);
        this.container = container;

        // Coffer slots (4x2 grid, centered)
        // Calculate center position: (176 - (4 * 18 + 3 * spacing)) / 2
        // With standard 18px slot size and no extra spacing between slots: (176 - 72) / 2 = 52
        int startX = 53;
        int startY = 20;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int slotIndex = row * 4 + col;
                this.addSlot(new MultipliedSlot(this.container, slotIndex, startX + col * 18, startY + row * 18));
            }
        }

        // Move inventory up by 19 pixels
        int offsetY = 50 + 36 - 19;

        // Player inventory (3 rows x 9 columns)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, offsetY + row * 18));
            }
        }

        // Hotbar (1 row) - moved down 4 pixels
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, offsetY + 3 * 18 + 4));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < 8) {
                if (!this.moveItemStackTo(itemStack2, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, 8, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public boolean is(Container container) {
        return this.container == container;
    }
}