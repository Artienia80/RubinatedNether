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
    private static int particleCount = 0; // Track how many particles are created

    protected RubySpiritParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;

        particleCount++;
        System.out.println("*** RUBY SPIRIT PARTICLE CREATED #" + particleCount + " ***");
        System.out.println("Position: " + x + ", " + y + ", " + z);
        System.out.println("Sprites available: " + (sprites != null ? "YES" : "NO"));

        // Slow upward movement with slight randomness
        this.xd = (this.random.nextDouble() - 0.5) * 0.01; // Slight horizontal drift
        this.yd = 0.03 + this.random.nextDouble() * 0.02; // Upward velocity (0.03-0.05)
        this.zd = (this.random.nextDouble() - 0.5) * 0.01; // Slight horizontal drift

        // Particle properties
        this.lifetime = 80 + this.random.nextInt(40); // 4-6 seconds
        this.gravity = -0.005F; // Reduced upward drift
        this.friction = 0.96F; // More air resistance

        // Size and appearance - INCREASED SIZE
        this.quadSize = 0.5F + this.random.nextFloat() * 0.3F; // 0.5-0.8 blocks
        this.alpha = 1.0F; // Full opacity to start

        // Color - brighter ruby spirit glow
        this.rCol = 1.0F;
        this.gCol = 0.4F + this.random.nextFloat() * 0.2F;
        this.bCol = 0.4F + this.random.nextFloat() * 0.2F;

        System.out.println("Lifetime: " + this.lifetime + " ticks");
        System.out.println("Size: " + this.quadSize);
        System.out.println("Color: R=" + this.rCol + ", G=" + this.gCol + ", B=" + this.bCol);
        System.out.println("Alpha: " + this.alpha);

        // Set initial sprite
        this.setSpriteFromAge(sprites);
        System.out.println("Initial sprite set!");
        System.out.println("*********************************************");
    }

    @Override
    public void tick() {
        super.tick();

        // Only log every 20 ticks (once per second) to avoid spam
        if (this.age % 20 == 0) {
            System.out.println("Ruby Spirit Particle #" + particleCount + " - Age: " + this.age + "/" + this.lifetime +
                    " Pos: " + this.x + ", " + this.y + ", " + this.z +
                    " Alpha: " + this.alpha);
        }

        // Animate through sprite frames
        this.setSpriteFromAge(this.sprites);

        // Gentle pulsing effect
        float ageRatio = (float)this.age / (float)this.lifetime;
        if (ageRatio < 0.2F) {
            // Fade in during first 20% of lifetime
            this.alpha = ageRatio * 5.0F;
        } else if (ageRatio > 0.8F) {
            // Fade out during last 20% of lifetime
            this.alpha = (1.0F - ageRatio) * 5.0F;
        } else {
            // Full opacity in middle
            this.alpha = 1.0F;
        }

        // Slight size increase over time (spirits grow as they rise)
        this.quadSize = (0.5F + this.random.nextFloat() * 0.3F) * (1.0F + ageRatio * 0.3F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        // Make it glow (emissive) - full brightness
        return 240 | 240 << 16; // Both sky and block light at max
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
            System.out.println("@@@ RUBY SPIRIT PARTICLE PROVIDER CREATED @@@");
            System.out.println("SpriteSet: " + (sprites != null ? "VALID" : "NULL"));
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            System.out.println(">>> PROVIDER: Creating Ruby Spirit particle at " + x + ", " + y + ", " + z);
            return new RubySpiritParticle(level, x, y, z, this.sprites);
        }
    }
}