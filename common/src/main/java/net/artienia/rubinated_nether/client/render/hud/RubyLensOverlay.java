package net.artienia.rubinated_nether.client.render.hud;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.platform.PlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class RubyLensOverlay {

    public static final ResourceLocation RUBY_OVERLAY = RubinatedNether.id("textures/misc/ruby_overlay.png");

    public static void renderHud(Gui gui, GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player != null && PlatformUtils.rubyLensEquipped(player)) {
            gui.renderTextureOverlay(graphics, RUBY_OVERLAY, 1.0f);
        }
    }
}
