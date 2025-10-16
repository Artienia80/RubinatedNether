package corundum.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class RNParticleUtils {

    /**
     * - CODE COPY of {@link net.minecraft.util.ParticleUtils#spawnParticles(LevelAccessor, BlockPos, int, double, double, boolean, ParticleOptions)}
     *  Uses Vec3 in place of the BlockPos for more precise particle spawning
     */
    public static void spawnParticles(LevelAccessor level, Vec3 pos, int count, double xzSpread, double ySpread, ParticleOptions particle) {
        RandomSource randomsource = level.getRandom();

        for(int i = 0; i < count; ++i) {
            double d0 = randomsource.nextGaussian() * 0.02;
            double d1 = randomsource.nextGaussian() * 0.02;
            double d2 = randomsource.nextGaussian() * 0.02;
            double d3 = (double)0.5F - xzSpread;
            double d4 = pos.get(Direction.Axis.X) - 0.5F + randomsource.nextDouble() * xzSpread * (double)2.0F;
            double d5 = pos.get(Direction.Axis.Y) + randomsource.nextDouble() * ySpread;
            double d6 = pos.get(Direction.Axis.Z) - 0.5F + randomsource.nextDouble() * xzSpread * (double)2.0F;
            level.addParticle(particle, d4, d5, d6, d0, d1, d2);
        }
    }
}
