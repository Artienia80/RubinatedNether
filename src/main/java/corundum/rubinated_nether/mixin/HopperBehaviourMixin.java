package corundum.rubinated_nether.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HopperBlockEntity.class)
public class HopperBehaviourMixin {

    @WrapOperation(method = "tryMoveInItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I", ordinal = 0))
    private static int getMaxContainerSize(ItemStack instance, Operation<Integer> original, @Local(index = 1) Container container) {
        return container.getMaxStackSize() - instance.getCount();
    }

    @ModifyExpressionValue(method = "tryMoveInItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;canMergeItems(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean containerSizeCheck(boolean original, @Local(index = 1) Container container, @Local(ordinal = 0) ItemStack stack, @Local(ordinal = 1) ItemStack itemStack ){
        return stack.getCount() <= container.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack, itemStack);
    }
}
