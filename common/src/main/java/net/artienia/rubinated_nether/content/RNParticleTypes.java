package net.artienia.rubinated_nether.content;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

import static net.artienia.rubinated_nether.RubinatedNether.REGISTRIES;

public final class RNParticleTypes {

    public static final Registrar<ParticleType<?>> PARTICLES = REGISTRIES.getRegistrar(Registries.PARTICLE_TYPE);

    public static final RegistryEntry<SimpleParticleType> RUBY_AURA = PARTICLES.entry("ruby_aura", () -> new SimpleParticleType(true)).register();

    public static final RegistryEntry<SimpleParticleType> RUBINATE = PARTICLES.entry("rubinate", () -> new SimpleParticleType(false)).register();


    public static void register() {
        PARTICLES.register();
    }
}
