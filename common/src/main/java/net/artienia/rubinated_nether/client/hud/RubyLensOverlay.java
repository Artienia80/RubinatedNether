package net.artienia.rubinated_nether.client.hud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class RubyLensOverlay {

    private static final ResourceLocation RUBY_OVERLAY = RubinatedNether.id("textures/misc/ruby_overlay.png");

    public static void renderHud(GuiGraphics graphics, float tickDelta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player != null && minecraft.options.getCameraType().isFirstPerson() && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get())) {
            Window window = minecraft.getWindow();
            RenderSystem.enableBlend();
            graphics.blit(RUBY_OVERLAY, 0, 0, -150, 0, 0, window.getWidth(), window.getHeight(), 512, 256);
        }
    }
}
