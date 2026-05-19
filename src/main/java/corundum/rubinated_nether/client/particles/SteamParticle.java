package corundum.rubinated_nether.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SteamParticle extends TextureSheetParticle {

    private final float initialAlpha;
    private final int fadeStartTick;

    protected SteamParticle(ClientLevel level, double x, double y, double z,
                            double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        float speed = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        int travelTicks = speed > 0 ? (int)(1.0f / (1.0f - 0.96f) * (1 - (float)Math.pow(0.96, 60))) : 60;
        this.fadeStartTick = (int)(travelTicks * 0.5f);
        this.lifetime = travelTicks;

        this.initialAlpha = 0.9f;
        this.alpha = initialAlpha;

        this.scale(3.0f);
        this.gravity = 0.0f;
        this.friction = 0.96f;
    }

    @Override
    public void tick() {
        super.tick();

        if (age >= fadeStartTick) {
            float fadeFraction = (float)(age - fadeStartTick) / (lifetime - fadeStartTick);
            this.alpha = initialAlpha * (1.0f - fadeFraction);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            SteamParticle particle = new SteamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprites);
            return particle;
        }
    }
}