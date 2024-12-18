package net.artienia.rubinated_nether.client;


import net.artienia.rubinated_nether.client.particles.RubinationAltarParticle;
import net.artienia.rubinated_nether.client.particles.RubyAuraParticle;
import net.artienia.rubinated_nether.client.render.entity.RubyLensModel;
import net.artienia.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import net.artienia.rubinated_nether.content.RNParticleTypes;
import net.artienia.rubinated_nether.integrations.RNModCompat;
import net.artienia.rubinated_nether.utils.ParticleFactoryConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RubinatedNetherClient {
	public static void clientSetup() {
		RNModCompat.clientSetup();
	}

	public static void registeModelLayes(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> layers) {
		layers.accept(RubyLensModel.LAYER_LOCATION, RubyLensModel::createBodyLayer);
	}

	@SuppressWarnings("unchecked")
	public static void registerEntityLayers(EntityRenderDispatcher dispatcher, EntityModelSet models, Function<String, PlayerRenderer> skinGetter) {
		dispatcher.renderers.forEach((type, renderer) -> {
			if(renderer instanceof LivingEntityRenderer<?,?> livingRenderer && livingRenderer.getModel() instanceof HeadedModel) {
				livingRenderer.addLayer(new RubyLensRenderLayer(livingRenderer, models, livingRenderer.getModel()));
			}
		});

		PlayerRenderer defaultSkin = skinGetter.apply("default");
		PlayerRenderer slimSkin = skinGetter.apply("slim");
		defaultSkin.addLayer(new RubyLensRenderLayer<>(defaultSkin, models, defaultSkin.getModel()));
		slimSkin.addLayer(new RubyLensRenderLayer<>(slimSkin, models, slimSkin.getModel()));
	}

	public static void registerParticleFactories(ParticleFactoryConsumer registrar) {
		registrar.register(RNParticleTypes.RUBY_AURA.get(), RubyAuraParticle.Provider::new);
		registrar.register(RNParticleTypes.RUBINATE.get(), RubinationAltarParticle.Provider::new);
	}
}
