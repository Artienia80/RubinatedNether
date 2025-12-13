package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void onFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (!RNConfig.brazierPowerLavaImmuneItems) {
            return;
        }

        ItemEntity itemEntity = (ItemEntity) (Object) this;

        if (itemEntity.level() != null && !itemEntity.level().isClientSide) {
            BlockPos itemPos = itemEntity.blockPosition();
            double range = RNConfig.brazierEffectRange;

            for (BlockPos pos : BlockPos.betweenClosed(
                    itemPos.offset((int)-range, (int)-range, (int)-range),
                    itemPos.offset((int)range, (int)range, (int)range))) {

                BlockEntity blockEntity = itemEntity.level().getBlockEntity(pos);

                if (blockEntity != null && blockEntity.getType() == RNBlockEntities.BRAZIER.get()) {
                    double distanceSq = itemPos.distSqr(pos);

                    if (distanceSq <= range * range) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
    }
}