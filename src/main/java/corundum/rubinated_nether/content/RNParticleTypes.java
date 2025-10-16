package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

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

	public static final Supplier<SimpleParticleType> BRONZE_SCRAPE = PARTICLES.register(
			"bronze_scrape", () -> new SimpleParticleType(true));

}
