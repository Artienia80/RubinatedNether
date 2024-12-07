package net.artienia.rubinated_nether.fabric.client;

import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.fabric.client.trinkets.TrinketsRenderers;
import net.artienia.rubinated_nether.utils.ParticleFactoryConsumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import uwu.serenity.critter.platform.PlatformUtils;

public final class RubinatedNetherFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		RubinatedNetherClient.clientSetup();
		RubinatedNetherClient.registeModelLayes((location, definition) -> EntityModelLayerRegistry.registerModelLayer(location, definition::get));
		RubinatedNetherClient.registerParticleFactories(new ParticleFactoryConsumer() {
			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, PendingProvider<T> factoryProvider) {
				ParticleFactoryRegistry.getInstance().register(type, factoryProvider::create);
			}

			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
				ParticleFactoryRegistry.getInstance().register(type, provider);
			}
		});

		if(PlatformUtils.modLoaded("trinkets")) TrinketsRenderers.register();
	}
}
