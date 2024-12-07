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

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			float f = (float)this.age / (float)this.lifetime;
			f = 1.0F - f;
			float g = 1.0F - f;
			g *= g;
			g *= g;
			this.x = this.xStart + this.xd * (double)f;
			this.y = this.yStart + this.yd * (double)f - (double)(g * 1.5F);
			this.z = this.zStart + this.zd * (double)f;
		}
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
