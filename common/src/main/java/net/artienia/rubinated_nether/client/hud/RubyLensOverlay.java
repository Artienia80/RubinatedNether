package net.artienia.rubinated_nether.client.hud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public class RubyLensOverlay {

    private static final ResourceLocation RUBY_OVERLAY = RubinatedNether.id("textures/misc/ruby_overlay.png");

    public static void renderHud(GuiGraphics graphics, float tickDelta) {
        Player player = Minecraft.getInstance().player;
        if(player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get())) {
            Window window = Minecraft.getInstance().getWindow();
            RenderSystem.enableBlend();
            graphics.blit(RUBY_OVERLAY, 0, 0, 0, 0, window.getWidth(), window.getHeight(), 512, 256);
        }
    }
}
