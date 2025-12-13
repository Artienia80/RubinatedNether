package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderFlame", at = @At("HEAD"), cancellable = true)
    private void onRenderFlame(PoseStack poseStack, MultiBufferSource buffer, Entity entity, Quaternionf quaternion, CallbackInfo ci) {
        if (RNConfig.brazierPowerDisableFireOverlay &&
                entity instanceof LivingEntity livingEntity &&
                livingEntity.hasEffect(RNEffects.BRAZIER_POWER)) {
            ci.cancel();
        }
    }
}