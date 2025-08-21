package corundum.rubinated_nether.events;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.effect.renderer.BronzeDiseasedEffectOverlay;
import corundum.rubinated_nether.content.entity.client.BronzeChargeProjectileModel;
import corundum.rubinated_nether.content.entity.client.BronzeChargeProjectileRenderer;
import corundum.rubinated_nether.content.gui.RubyLensOverlay;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.screen.CofferScreen;
import corundum.rubinated_nether.content.screen.FreezerScreen;
import corundum.rubinated_nether.content.screen.RubinationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RNClientModBusEvents {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		EntityRenderers.register(
			RNEntityCreator.BRONZE_SHOT.get(),
			BronzeChargeProjectileRenderer::new
		);
	}

	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(
			BronzeChargeProjectileModel.LAYER_LOCATION, 
			BronzeChargeProjectileModel::createBodyLayer
		);
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(RNMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
		event.register(RNMenuTypes.RUBINATION_MENU.get(), RubinationScreen::new);
		event.register(RNMenuTypes.COFFER_MENU.get(), CofferScreen::new);
	}


	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent event) {
		Minecraft minecraft = Minecraft.getInstance();

		event.registerAbove(
			VanillaGuiLayers.DEMO_OVERLAY,
			RubinatedNether.id("ruby_lens_overlay"),
			(guiGraphics, deltaTracker) -> RubyLensOverlay.renderHud(new Gui(minecraft), guiGraphics)
		);
		event.registerBelowAll(
				RubinatedNether.id("bronze_overlay"),
				(guiGraphics, deltaTracker)  -> BronzeDiseasedEffectOverlay.renderHud(new Gui(minecraft), guiGraphics)
		);
	}

}
