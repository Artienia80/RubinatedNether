package corundum.rubinated_nether.client;

import corundum.rubinated_nether.client.particles.RubyAuraParticle;
import corundum.rubinated_nether.client.render.entity.RubyLensModel;
import corundum.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.mixin.accessors.EntityRenderDispatcherAccessor;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@OnlyIn(Dist.CLIENT)
public class RubinatedNetherClient {
	public static final int WHITE = 0xFFFFFFFF;

	public static void client(IEventBus bussin) {
		bussin.addListener(RubinatedNetherClient::registerParticleProviders);
		bussin.addListener(RubinatedNetherClient::registerEntityLayers);
		bussin.addListener(RubinatedNetherClient::registeModelLayers);
	}

	public static void registeModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(
			RubyLensModel.LAYER_LOCATION, 
			RubyLensModel::createBodyLayer
		);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerEntityLayers(EntityRenderersEvent.AddLayers event) {
		var dispatcher = event.getContext().getEntityRenderDispatcher();
		var renderers = ((EntityRenderDispatcherAccessor)dispatcher).getRenderers();
		var models = event.getEntityModels();
		
		renderers.forEach((type, renderer) -> {
			if(renderer instanceof LivingEntityRenderer<?,?> livingRenderer && livingRenderer.getModel() instanceof HeadedModel) {
				livingRenderer.addLayer(new RubyLensRenderLayer(livingRenderer, models, livingRenderer.getModel()));
			}
		});

		((EntityRenderDispatcherAccessor)dispatcher).setRenderers(renderers);

		PlayerRenderer defaultSkin = event.getSkin(Model.WIDE);
		PlayerRenderer slimSkin = event.getSkin(Model.SLIM);
		defaultSkin.addLayer(new RubyLensRenderLayer<>(defaultSkin, models, defaultSkin.getModel()));
		slimSkin.addLayer(new RubyLensRenderLayer<>(slimSkin, models, slimSkin.getModel()));
	}

	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(RNParticleTypes.RUBY_AURA.get(), RubyAuraParticle.Provider::new);
	}
}
