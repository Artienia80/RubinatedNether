package corundum.rubinated_nether.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/**
 * Rubin script glyph. Spawned at an origin; the spawn velocity is interpreted as the
 * target offset from that origin. It shoots out to the offset (ease-out), then stays
 * roughly in place drifting slightly, then slowly fades out.
 *
 * Styled like the RUBINATE particle: small, deep red, and fully bright regardless of
 * world light.
 */
public class RubinGlyphParticle extends TextureSheetParticle {
    private static final int EXPAND_TICKS = 14;
    private static final int BASE_LIFETIME = 85;
    private static final int FADE_TICKS = 40;

    // Size: previously 0.2-0.3, now roughly half (RUBINATE uses 0.05-0.1).
    private static final float MIN_SIZE = 0.09F;
    private static final float SIZE_VARIANCE = 0.06F;

    // Same red as RubinationAltarParticle (RUBINATE). Raise R for a brighter, hotter glow.
    private static final float RED = 0.667F;
    private static final float GREEN = 0.0F;
    private static final float BLUE = 0.0F;

    // Full block + sky light (same value as 240 | 240 << 16).
    private static final int FULL_BRIGHT = 15728880;

    private final double originX, originY, originZ;
    private final double offsetX, offsetY, offsetZ;
    private final double driftVelX, driftVelY, driftVelZ;
    private double driftX, driftY, driftZ;

    protected RubinGlyphParticle(ClientLevel level, double x, double y, double z,
                                 double offsetX, double offsetY, double offsetZ, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.driftVelX = (this.random.nextDouble() - 0.5) * 0.008;
        this.driftVelY = (this.random.nextDouble() - 0.5) * 0.008;
        this.driftVelZ = (this.random.nextDouble() - 0.5) * 0.008;

        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.hasPhysics = false;
        this.lifetime = BASE_LIFETIME + this.random.nextInt(11) - 5;
        this.quadSize = MIN_SIZE + this.random.nextFloat() * SIZE_VARIANCE;
        this.rCol = RED;
        this.gCol = GREEN;
        this.bCol = BLUE;
        this.alpha = 1.0F;
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float u = Math.min(1.0F, this.age / (float) EXPAND_TICKS);
        double p = 1.0 - Math.pow(1.0 - u, 3.0);

        if (this.age > EXPAND_TICKS) {
            this.driftX += this.driftVelX;
            this.driftY += this.driftVelY;
            this.driftZ += this.driftVelZ;
        }

        this.setPos(
                this.originX + this.offsetX * p + this.driftX,
                this.originY + this.offsetY * p + this.driftY,
                this.originZ + this.offsetZ * p + this.driftZ
        );

        int fadeStart = this.lifetime - FADE_TICKS;
        if (this.age > fadeStart) {
            this.alpha = Mth.clamp(1.0F - (this.age - fadeStart) / (float) FADE_TICKS, 0.0F, 1.0F);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new RubinGlyphParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}