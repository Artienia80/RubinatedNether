package corundum.rubinated_nether.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import corundum.rubinated_nether.client.particles.RubyAuraParticle;
import corundum.rubinated_nether.content.RNParticleTypes;

@OnlyIn(Dist.CLIENT)
public class RubinatedNetherClient {
	public static void client(IEventBus bussin) {
		bussin.addListener(RubinatedNetherClient::registerParticleProviders);
	}

	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(RNParticleTypes.RUBY_AURA.get(), RubyAuraParticle.Provider::new);
	}
}
