package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin {
    @Shadow
    private ItemStack input;

    @Shadow
    @org.spongepowered.asm.mixin.Final
    Slot inputSlot;

    /**
     * Vanilla only rebuilds the recipe list when the input ITEM changes. A blank rune and a carved rune are the
     * same item, so swapping one for the other would keep the stale list (letting a carved rune be carved again).
     * Forget the cached input when the rune's components differ so the list is rebuilt.
     */
    @Inject(method = "slotsChanged", at = @At("HEAD"))
    private void rubinated_nether$refreshOnRuneChange(Container container, CallbackInfo ci) {
        ItemStack current = this.inputSlot.getItem();
        if (current.is(RNItems.RUNE.get()) && this.input.is(RNItems.RUNE.get())
                && !ItemStack.isSameItemSameComponents(current, this.input)) {
            this.input = ItemStack.EMPTY;
        }
    }
}
