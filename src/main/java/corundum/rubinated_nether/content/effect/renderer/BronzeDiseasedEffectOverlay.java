package corundum.rubinated_nether.content.effect.renderer;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class BronzeDiseasedEffectOverlay {
    public static final int MAX_LEVEL = 5;

    private static final ResourceLocation[] PARANOIA_OVERLAYS = new ResourceLocation[MAX_LEVEL];

    static {
        for (int i = 0; i < MAX_LEVEL; i++) {
            PARANOIA_OVERLAYS[i] = ResourceLocation.fromNamespaceAndPath("rubinated_nether", "textures/gui/bronze_overlay_" + (i + 1) + ".png");
        }
    }

    public static ResourceLocation getOverlay(LocalPlayer player) {
        MobEffectInstance instance = player.getEffect(RNEffects.BRONZE_DISEASED);
        if (instance == null) return null;
        int level = Math.max(1, Math.min(MAX_LEVEL, instance.getAmplifier() + 1));
        return PARANOIA_OVERLAYS[level - 1];
    }

    public static void renderHud(Gui gui, GuiGraphics graphics) {
        if(RNConfig.bronzeDiseasedOverlayOpacity == 0) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player != null && minecraft.options.getCameraType().isFirstPerson()) {
            ResourceLocation overlay = getOverlay(player);
            if (overlay != null) {
                gui.renderTextureOverlay(graphics, overlay, 1.0f);
            }
        }
    }
}
