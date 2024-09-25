package net.artienia.rubinated_nether.client.render.hud;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class RubyLensOverlay {

    private static final ResourceLocation RUBY_OVERLAY = RubinatedNether.id("textures/misc/ruby_overlay.png");
    private static final ResourceLocation RUBY_OVERLAY_FAST = RubinatedNether.id("textures/misc/ruby_overlay_fast.png");

    public static void renderHud(Gui gui, GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player != null && minecraft.options.getCameraType().isFirstPerson() && Platform.rubyLensEquipped(player)) {
            gui.renderTextureOverlay(graphics, Minecraft.useFancyGraphics() ? RUBY_OVERLAY : RUBY_OVERLAY_FAST, 1.0f);
        }
    }
}
