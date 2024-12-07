package net.artienia.rubinated_nether.utils;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface ParticleFactoryConsumer {

	<T extends ParticleOptions> void register(ParticleType<T> type, PendingProvider<T> factoryProvider);

	<T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);

	@FunctionalInterface
	interface PendingProvider<T extends ParticleOptions> {
		ParticleProvider<T> create(SpriteSet spriteSet);
	}
}
