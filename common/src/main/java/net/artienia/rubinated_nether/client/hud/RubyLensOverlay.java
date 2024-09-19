package net.artienia.rubinated_nether.client.hud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class RubyLensOverlay {

    public static final ResourceLocation RUBY_OVERLAY = RubinatedNether.id("textures/misc/ruby_overlay.png");

    public static void renderHud(Gui gui, GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get())) {
            gui.renderTextureOverlay(graphics, RUBY_OVERLAY, 1.0f);
        }
    }
}
