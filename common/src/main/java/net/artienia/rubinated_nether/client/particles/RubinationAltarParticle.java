package net.artienia.rubinated_nether.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class RubinationAltarParticle extends TextureSheetParticle {

    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final double angleOffset;
    private double currentAngle; // Current angle for smooth interpolation


    RubinationAltarParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.xStart = x;
        this.yStart = y;
        this.zStart = z;
        this.xo = x + xSpeed;
        this.yo = y + ySpeed;
        this.zo = z + zSpeed;
        this.angleOffset = this.random.nextDouble() * Math.PI * 2; // Random initial angle
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f;
        this.gCol = 0.3F * f;
        this.bCol = 0.3F * f;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0) + 75;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        float f = (float)this.age / (float)this.lifetime;
        f *= f;
        f *= f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += (int)(f * 15.0F * 16.0F);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }


    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float progress = (float) this.age / (float) this.lifetime; // Progress through lifetime (0 to 1)
            float radius = (1.0F - progress) * 1.5F; // Decreasing radius
            float f = (float)this.age / (float)this.lifetime;
            f = 1.0F - f;

            // Gradually adjust angle for smooth rotation
            double targetAngle = this.angleOffset + Math.PI * 2 * progress * 2; // Desired angle
            this.currentAngle = this.currentAngle + (targetAngle - this.currentAngle) * 0.05F; // Interpolate smoothly

            double spiralX = Math.cos(this.currentAngle) * radius;
            double spiralZ = Math.sin(this.currentAngle) * radius;

            // Adjusted height to start low and end at y=0
            float heightChange = (float) Math.sin(Math.PI * progress) * 0.15F; // Reduced height scaling
            this.y = this.yStart + heightChange + 0.1F - progress; // Gradually decreases to y=0

            // Update x and z for unique spiraling inward
            this.x = this.xStart + (spiralX*progress) * (double)f;
            this.z = this.zStart + (spiralX*progress) * (double)f;
        }
    }




    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            RubinationAltarParticle rubinationAltarParticle = new RubinationAltarParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            rubinationAltarParticle.pickSprite(this.sprite);
            return rubinationAltarParticle;
        }
    }
}
