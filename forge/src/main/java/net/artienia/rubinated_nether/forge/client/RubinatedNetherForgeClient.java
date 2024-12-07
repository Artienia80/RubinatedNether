package net.artienia.rubinated_nether.forge.client;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.client.config.RNConfigScreen;
import net.artienia.rubinated_nether.client.render.hud.RubyLensOverlay;
import net.artienia.rubinated_nether.forge.client.curios.CuriosRenderers;
import net.artienia.rubinated_nether.utils.ParticleFactoryConsumer;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RubinatedNether.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RubinatedNetherForgeClient {

	@SubscribeEvent
	public static void onInitializeClient(FMLClientSetupEvent event) {
		event.enqueueWork(RubinatedNetherClient::clientSetup);

		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
			() -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> RNConfigScreen.create(screen)));

		if(ModList.get().isLoaded("curios")) CuriosRenderers.register();
	}

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.VIGNETTE.id(), "ruby_lens_overlay",
			(forgeGui, arg, f, i, j) -> RubyLensOverlay.renderHud(forgeGui, arg));
	}

	@SubscribeEvent
	public static void registerEntityLayes(EntityRenderersEvent.AddLayers event) {
		RubinatedNetherClient.registerEntityLayers(event.getContext().getEntityRenderDispatcher(), event.getEntityModels(), event::getSkin);
	}

	@SubscribeEvent
	public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		RubinatedNetherClient.registeModelLayes(event::registerLayerDefinition);
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		RubinatedNetherClient.registerParticleFactories(new ParticleFactoryConsumer() {
			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, PendingProvider<T> factoryProvider) {
				event.registerSpriteSet(type, factoryProvider::create);
			}

			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
				event.registerSpecial(type, provider);
			}
		});
	}

}
