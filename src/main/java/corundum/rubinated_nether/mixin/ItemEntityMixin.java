package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.utils.BrazierTracker;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void rubinated_nether$onFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (!RNConfig.brazierPowerLavaImmuneItems) {
            return;
        }

        ItemEntity itemEntity = (ItemEntity) (Object) this;
        Level level = itemEntity.level();
        if (level == null || level.isClientSide) {
            return;
        }

        if (BrazierTracker.anyInRange(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), RNConfig.brazierEffectRange)) {
            cir.setReturnValue(true);
        }
    }
}
