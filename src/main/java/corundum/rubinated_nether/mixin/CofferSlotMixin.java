package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.blocks.CofferContainer;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// mixin/CofferSlotMixin.java
@Mixin(Slot.class)
public abstract class CofferSlotMixin {
    @Shadow @Final private Container container;

    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void rubinated$increaseStackLimit(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (container != null && container.getClass().getSimpleName().contains("Coffer")) {
            cir.setReturnValue(256);
        }
    }
}
