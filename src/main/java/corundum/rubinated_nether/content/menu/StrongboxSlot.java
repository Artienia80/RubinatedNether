package corundum.rubinated_nether.content.menu;

import corundum.rubinated_nether.content.NestingRules;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class StrongboxSlot extends Slot {
    public StrongboxSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return NestingRules.strongboxAccepts(stack);
    }
}
