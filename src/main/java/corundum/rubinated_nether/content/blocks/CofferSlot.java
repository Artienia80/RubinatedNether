package corundum.rubinated_nether.content.blocks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
    public boolean mayPlace(ItemStack stack) {
        return true;
    }
}

