package corundum.rubinated_nether.content.menu;

import com.mojang.datafixers.util.Pair;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * The anvil's second slot. With a strongbox in the first slot it only takes winding keys and shows a key
 * as its empty-slot icon; otherwise it behaves like the normal slot.
 */
public class StrongboxKeySlot extends Slot {
    private static final ResourceLocation EMPTY_KEY_ICON = RubinatedNether.id("item/empty_slot_key");

    public StrongboxKeySlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    private boolean strongboxInFirstSlot() {
        return StrongboxLock.isStrongbox(this.container.getItem(0));
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !this.strongboxInFirstSlot() || StrongboxLock.isKey(stack);
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return this.strongboxInFirstSlot() ? Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_KEY_ICON) : null;
    }
}
