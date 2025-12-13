package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (!RNConfig.brazierPowerLavaVision) {
            return;
        }

        FogType fogtype = camera.getFluidInCamera();
        Entity entity = camera.getEntity();

        if (fogtype == FogType.LAVA && entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(RNEffects.BRAZIER_POWER)) {
                RenderSystem.setShaderFogStart(RNConfig.brazierPowerViewRange);
                RenderSystem.setShaderFogEnd(RNConfig.brazierPowerViewRange*2);
                RenderSystem.setShaderFogShape(FogShape.SPHERE);
                ci.cancel();
            }
        }
    }
}