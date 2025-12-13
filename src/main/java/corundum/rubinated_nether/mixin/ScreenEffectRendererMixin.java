package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void onRenderFire(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        if (RNConfig.brazierPowerDisableFireOverlay &&
                minecraft.player != null &&
                minecraft.player.hasEffect(RNEffects.BRAZIER_POWER)) {
            ci.cancel();
        }
    }
}