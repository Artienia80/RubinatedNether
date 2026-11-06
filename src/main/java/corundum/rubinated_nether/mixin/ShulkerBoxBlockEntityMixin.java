package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.NestingRules;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin {
    @Inject(method = "canPlaceItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void rubinated_nether$rejectNestedContainers(int index, ItemStack stack, @Nullable Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (NestingRules.isNestedContainer(stack)) {
            cir.setReturnValue(false);
        }
    }
}
