package corundum.rubinated_nether.client.particles;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class BronzeScrapeParticle extends TextureSheetParticle {
    protected BronzeScrapeParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        RubinatedNether.LOGGER.debug("BronzeScrapeParticle constructor hit.");
        this.pickSprite(sprites);
        this.gravity = 0.0F;
        this.lifetime = 20 + this.random.nextInt(10);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}


