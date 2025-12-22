package corundum.rubinated_nether.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RubySpiritParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float baseSize;

    protected RubySpiritParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;

        // Slow upward movement with slight randomness
        this.xd = (this.random.nextDouble() - 0.5) * 0.01;
        this.yd = 0.03 + this.random.nextDouble() * 0.02;
        this.zd = (this.random.nextDouble() - 0.5) * 0.01;

        // Particle properties
        this.lifetime = 80 + this.random.nextInt(40);
        this.gravity = -0.005F;
        this.friction = 0.96F;

        // Size and appearance
        this.baseSize = 0.08F + this.random.nextFloat() * 0.04F;
        this.quadSize = this.baseSize;
        this.alpha = 1.0F;

        // Color - brighter ruby spirit glow
        this.rCol = 1.0F;
        this.gCol = 0.4F + this.random.nextFloat() * 0.2F;
        this.bCol = 0.4F + this.random.nextFloat() * 0.2F;

        // Set initial sprite
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Custom sprite animation - stay on first frame for 90% of life, then animate through rest
        float ageRatio = (float)this.age / (float)this.lifetime;
        if (ageRatio < 0.9F) {
            this.setSprite(this.sprites.get(0, 4));
        } else {
            float dissipateProgress = (ageRatio - 0.9F) / 0.1F;
            int targetFrame = (int)(dissipateProgress * 4);
            targetFrame = Math.min(targetFrame, 3);
            this.setSprite(this.sprites.get(targetFrame, 4));
        }

        // Keep alpha constant
        this.alpha = 1.0F;

        // Smooth size increase over time
        this.quadSize = this.baseSize * (1.0F + ageRatio * 0.3F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        // Make it glow (emissive) - full brightness
        return 240 | 240 << 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new RubySpiritParticle(level, x, y, z, this.sprites);
        }
    }
}