package net.artienia.rubinated_nether.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class RubyAuraParticle extends RisingParticle {

	protected RubyAuraParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	protected int getLightColor(float partialTick) {
		return LightTexture.FULL_BRIGHT;
	}

	public static final class Provider implements ParticleProvider<SimpleParticleType> {

		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public @NotNull Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			RubyAuraParticle particle = new RubyAuraParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(sprites);
			return particle;
		}
	}
}
