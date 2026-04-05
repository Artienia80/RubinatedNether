package corundum.rubinated_nether.event;

import com.mojang.blaze3d.systems.RenderSystem;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.render.BronzeLaserRenderer;
import corundum.rubinated_nether.client.render.CofferRenderer;
import corundum.rubinated_nether.client.render.CopperLaserRenderer;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.effect.renderer.BronzeDiseasedEffectOverlay;
import corundum.rubinated_nether.content.entity.client.BronzeShotProjectileModel;
import corundum.rubinated_nether.content.entity.client.BronzeShotProjectileRenderer;
import corundum.rubinated_nether.content.gui.RubyLensOverlay;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.screen.CofferScreen;
import corundum.rubinated_nether.content.screen.FreezerScreen;
import corundum.rubinated_nether.content.screen.RubinationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public class RNClientEvents {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
	}

	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(
			BronzeShotProjectileModel.LAYER_LOCATION, 
			BronzeShotProjectileModel::createBodyLayer
		);
        event.registerLayerDefinition(RNModelLayers.COFFER,
                CofferRenderer::createSingleBodyLayer
        );
    }

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(RNMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
		event.register(RNMenuTypes.RUBINATION_MENU.get(), RubinationScreen::new);
		event.register(RNMenuTypes.COFFER_MENU.get(), CofferScreen::new);
	}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(RNBlockEntities.COFFER.get(), CofferRenderer::new);
        event.registerBlockEntityRenderer(RNBlockEntities.BRONZE_LASER.get(), BronzeLaserRenderer::new);
        event.registerBlockEntityRenderer(RNBlockEntities.COPPER_LASER.get(), CopperLaserRenderer::new);

        event.registerEntityRenderer(RNEntityCreator.BRONZE_SHOT.get(), BronzeShotProjectileRenderer::new);
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



    @SubscribeEvent
    public static void onRenderWorldOverlay(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !player.hasEffect(RNEffects.BRONZE_DISEASED)) return;

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        RenderSystem.setShaderTexture(0, BronzeDiseasedEffectOverlay.PARANOIA_OVERLAY);

        blitFullScreen();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void blitFullScreen() {
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        guiGraphics.blit(BronzeDiseasedEffectOverlay.PARANOIA_OVERLAY, 0, 0, 0, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);

        guiGraphics.flush();
    }
}
