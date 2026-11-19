package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class CrystalConversion {
    private CrystalConversion() {
    }

    public static void detonate(ServerLevel level, BlockPos center, boolean big) {
        Vec3 at = Vec3.atCenterOf(center);
        RandomSource random = level.getRandom();

        if (big) {
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
            level.sendParticles(ParticleTypes.GLOW, at.x, at.y, at.z, 60, 2.5, 1.5, 2.5, 0.15);
            level.playSound(null, center, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 3.0F, 0.7F);
            level.playSound(null, center, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.HOSTILE, 3.0F, 0.6F);
        } else {
            level.sendParticles(ParticleTypes.EXPLOSION, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
            level.sendParticles(ParticleTypes.GLOW, at.x, at.y, at.z, 25, 1.2, 0.8, 1.2, 0.1);
            level.playSound(null, center, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1.5F, 0.8F + random.nextFloat() * 0.3F);
            level.playSound(null, center, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.HOSTILE, 2.0F, 0.8F);
        }
    }
}
