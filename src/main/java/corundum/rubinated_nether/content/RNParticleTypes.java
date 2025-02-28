package corundum.rubinated_nether.content;

import java.util.function.Supplier;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RNParticleTypes {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(
		BuiltInRegistries.PARTICLE_TYPE,
		RubinatedNether.MODID
	);

	public static final Supplier<SimpleParticleType> RUBY_AURA = PARTICLES.register(
		"ruby_aura",
		() -> new SimpleParticleType(true)
	);

	public static final Supplier<SimpleParticleType> RUBINATE = PARTICLES.register(
			"rubinate",
			() -> new SimpleParticleType(false)
	);
}
