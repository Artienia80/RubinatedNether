package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.blocks.CofferContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Inject(method = "safeInsert", at = @At("HEAD"), cancellable = true)
    private void injectSafeInsert(ItemStack stack, int count, CallbackInfoReturnable<Integer> cir) {
        Slot slot = (Slot)(Object)this;

        if (!(slot.container instanceof CofferContainer)) return;

        ItemStack slotStack = slot.getItem();

        if (slotStack.isEmpty() || !ItemStack.isSameItemSameComponents(slotStack, stack)) {
            return;
        }

        int max = 256;
        int transferable = Math.min(count, max - slotStack.getCount());

        if (transferable > 0) {
            slotStack.grow(transferable);
            stack.shrink(transferable);
            cir.setReturnValue(transferable);
        } else {
            cir.setReturnValue(0);
        }

        cir.cancel();
    }
}
