package corundum.rubinated_nether.content.gui;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.mixin.accessors.GuiAccessor;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class RubyLensOverlay {
	private static final ResourceLocation RUBY_OVERLAY = RubinatedNether.id("textures/misc/ruby_overlay.png");
	private static final ResourceLocation RUBY_OVERLAY_FAST = RubinatedNether.id("textures/misc/ruby_overlay_fast.png");

	public static void renderHud(Gui gui, GuiGraphics graphics) {
		if(RNConfig.rubyLensOpacity == 0) return;
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if(player != null && minecraft.options.getCameraType().isFirstPerson() && player.getItemBySlot(EquipmentSlot.HEAD).is(RNItems.RUBY_LENS.get())) {
			((GuiAccessor) gui).invokeRenderTextureOverlay(graphics, Minecraft.useFancyGraphics() ? RUBY_OVERLAY : RUBY_OVERLAY_FAST, RNConfig.rubyLensOpacity);
		}
	}
}
