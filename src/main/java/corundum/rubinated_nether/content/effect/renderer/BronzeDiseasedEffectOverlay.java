package corundum.rubinated_nether.content.effect.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class BronzeDiseasedEffectOverlay {
    public static final ResourceLocation PARANOIA_OVERLAY = ResourceLocation.fromNamespaceAndPath("rubinated_nether", "textures/gui/bronze_overlay.png");

    public static void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(shaderLocation, 0, 0, -90, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
