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

        this.friction = 0.975f;

        float speed = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        // With friction 0.975, max distance = speed / (1 - 0.975) = speed * 40
        // initialSpeed = signal / 40, so max distance = (signal / 40) * 40 = signal blocks
        // Ticks until speed decays to ~0.01: 0.975^n = 0.01 / speed
        int travelTicks = speed > 0
                ? Math.max(30, (int)(Math.log(0.01 / speed) / Math.log(0.975)) + 10)
                : 80;

        this.fadeStartTick = (int)(travelTicks * 0.5f);
        this.lifetime = travelTicks;

        this.initialAlpha = 0.9f;
        this.alpha = initialAlpha;

        this.scale(3.0f);
        this.gravity = 0.0f;
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