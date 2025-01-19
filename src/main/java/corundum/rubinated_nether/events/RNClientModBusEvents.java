package corundum.rubinated_nether.events;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.client.BronzeChargeProjectileModel;
import corundum.rubinated_nether.content.gui.RubyLensOverlay;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.screen.FreezerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RNClientModBusEvents {

	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(
			BronzeChargeProjectileModel.LAYER_LOCATION, 
			BronzeChargeProjectileModel::createBodyLayer
		);
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(
			RNMenuTypes.FREEZER_MENU.get(), 
			FreezerScreen::new
		);
	}


	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent event) {
		Minecraft minecraft = Minecraft.getInstance();

		event.registerAbove(
			VanillaGuiLayers.DEMO_OVERLAY, 
			RubinatedNether.id("ruby_lens_overlay"),
			(guiGraphics, deltaTracker) -> RubyLensOverlay.renderHud(new Gui(minecraft), guiGraphics)
		);
	}
}
