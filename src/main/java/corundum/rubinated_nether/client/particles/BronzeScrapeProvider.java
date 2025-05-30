package corundum.rubinated_nether.client.particles;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class BronzeScrapeProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public BronzeScrapeProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z, double dx, double dy, double dz) {

        double offsetX = (level.random.nextDouble() - 0.5) * 0.4; // +/- 0.2
        double offsetY = (level.random.nextDouble() - 0.5) * 0.4;
        double offsetZ = (level.random.nextDouble() - 0.5) * 0.4;

        double spawnX = x + offsetX;
        double spawnY = y + offsetY;
        double spawnZ = z + offsetZ;

        double motionX = level.random.nextGaussian() * 0.01;
        double motionY = level.random.nextGaussian() * 0.01;
        double motionZ = level.random.nextGaussian() * 0.01;

        RubinatedNether.LOGGER.debug("Spawning bronze_scrape at {},{},{} with motion {},{},{}",
                spawnX, spawnY, spawnZ, motionX, motionY, motionZ);

        return new BronzeScrapeParticle(level, spawnX, spawnY, spawnZ, motionX, motionY, motionZ, sprites);
    }
}
