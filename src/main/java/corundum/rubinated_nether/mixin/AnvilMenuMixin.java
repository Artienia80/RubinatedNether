package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.menu.StrongboxKeySlot;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Shadow
    private String itemName;

    @Unique
    private ItemStack rn$keyBeingRenamed = ItemStack.EMPTY;

    /** The name the strongbox and its keys are being renamed to; null when the name is being removed. */
    @Unique
    private String rn$newName = null;

    /** Swaps the second input slot for one that only takes keys next to a strongbox. */
    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void rn$useKeySlot(int containerId, Inventory inventory, ContainerLevelAccess access, CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        Slot old = menu.slots.get(1);
        Slot replacement = new StrongboxKeySlot(old.container, old.getContainerSlot(), old.x, old.y);
        replacement.index = old.index;
        menu.slots.set(1, replacement);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void rn$captureKey(Player player, ItemStack stack, CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack left = menu.getSlot(0).getItem();
        ItemStack right = menu.getSlot(1).getItem();
        this.rn$keyBeingRenamed = StrongboxLock.isStrongbox(left) && StrongboxLock.isKey(right)
                ? right.copy()
                : ItemStack.EMPTY;
        // Taken now, from what was typed, because by the end of the take the result stack may already be
        // moved or emptied and the input slots are cleared.
        this.rn$newName = this.itemName == null || this.itemName.isBlank() ? null : this.itemName;
    }

    /** The keys are not used up: they stay in the second slot, all renamed to the strongbox's new name. */
    @Inject(method = "onTake", at = @At("TAIL"))
    private void rn$returnRenamedKey(Player player, ItemStack stack, CallbackInfo ci) {
        if (this.rn$keyBeingRenamed.isEmpty()) {
            return;
        }
        ItemStack key = this.rn$keyBeingRenamed;
        this.rn$keyBeingRenamed = ItemStack.EMPTY;

        AnvilMenu menu = (AnvilMenu) (Object) this;
        String newName = this.rn$newName;
        this.rn$newName = null;
        menu.getSlot(1).set(StrongboxLock.renamedKey(key, newName));
    }
}
