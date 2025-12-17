package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderFlame", at = @At("HEAD"), cancellable = true)
    private void onRenderFlame(PoseStack poseStack, MultiBufferSource buffer, Entity entity, Quaternionf quaternion, CallbackInfo ci) {
        // Cancel fire rendering for living entities with Brazier Power effect
        if (RNConfig.brazierPowerDisableFireOverlay &&
                entity instanceof LivingEntity livingEntity &&
                livingEntity.hasEffect(RNEffects.BRAZIER_POWER)) {
            ci.cancel();
            return;
        }

        // Cancel fire rendering for items near braziers (when lava immunity is enabled)
        if (RNConfig.brazierPowerLavaImmuneItems && RNConfig.brazierPowerDisableFireOverlay &&
                entity instanceof ItemEntity itemEntity &&
                isItemNearBrazier(itemEntity)) {
            ci.cancel();
        }
    }

    private boolean isItemNearBrazier(ItemEntity itemEntity) {
        if (itemEntity.level() == null) {
            return false;
        }

        BlockPos itemPos = itemEntity.blockPosition();
        double range = RNConfig.brazierEffectRange;

        for (BlockPos pos : BlockPos.betweenClosed(
                itemPos.offset((int)-range, (int)-range, (int)-range),
                itemPos.offset((int)range, (int)range, (int)range))) {

            BlockEntity blockEntity = itemEntity.level().getBlockEntity(pos);

            if (blockEntity != null && blockEntity.getType() == RNBlockEntities.BRAZIER.get()) {
                double distanceSq = itemPos.distSqr(pos);

                if (distanceSq <= range * range) {
                    return true;
                }
            }
        }

        return false;
    }
}