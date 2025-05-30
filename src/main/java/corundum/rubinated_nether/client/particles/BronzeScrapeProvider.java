package corundum.rubinated_nether.client.particles;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class BronzeScrapeProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public BronzeScrapeProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                   double dx, double dy, double dz) {
        RubinatedNether.LOGGER.debug("Creating BronzeScrapeParticle at ({}, {}, {})", x, y, z);
        return new BronzeScrapeParticle(level, x, y, z, dx, dy, dz, spriteSet);
    }
}


