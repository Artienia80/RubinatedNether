package net.artienia.rubinated_nether.fabric.mixin.client;

import net.artienia.rubinated_nether.client.hud.RubyLensOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z",
            shift = At.Shift.AFTER
        )
    )
    public void renderRubyLens(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        RubyLensOverlay.renderHud((Gui) (Object) this, guiGraphics);
    }
}
