package net.artienia.rubinated_nether.screen;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FreezerFuelSlot extends Slot {
    private FreezerMenu menu;

    public FreezerFuelSlot(FreezerMenu menu, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.menu.isFuel(stack);
    }
}
