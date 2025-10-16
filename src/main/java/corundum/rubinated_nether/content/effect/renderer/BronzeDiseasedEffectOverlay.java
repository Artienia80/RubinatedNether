package corundum.rubinated_nether.content.effect.renderer;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.mixin.accessors.GuiAccessor;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class BronzeDiseasedEffectOverlay {
    public static final ResourceLocation PARANOIA_OVERLAY =
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "textures/gui/bronze_overlay.png");

    public static void renderHud(Gui gui, GuiGraphics graphics) {
        if(RNConfig.rubyLensOpacity == 0) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player != null && minecraft.options.getCameraType().isFirstPerson() && player.hasEffect(RNEffects.BRONZE_DISEASED)) {
            ((GuiAccessor) gui).invokeRenderTextureOverlay(graphics, PARANOIA_OVERLAY, 1.0f);
        }
    }
}
