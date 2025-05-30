package corundum.rubinated_nether.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class BronzeScrapeParticle extends TextureSheetParticle {
    protected BronzeScrapeParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet spriteSet) {
        super(level, x, y, z, xd, yd, zd);
        this.pickSprite(spriteSet);

        this.gravity = 0.0F; // No gravity
        this.friction = 1.0F; // No drag, keeps velocity
        this.lifetime = 30 + level.random.nextInt(10); // Live 30–40 ticks
        this.quadSize = 0.1F + level.random.nextFloat() * 0.05F; // Small visible particle
        this.setAlpha(1.0F); // Fully visible
        this.rCol = 1.0F; // No red tint
        this.gCol = 1.0F;
        this.bCol = 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
