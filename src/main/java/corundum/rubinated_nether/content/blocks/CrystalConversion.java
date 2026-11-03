package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class CrystalConversion {
    private static final double SMALL_MIN_RADIUS = 3.0;
    private static final double SMALL_MAX_RADIUS = 4.0;
    private static final double BIG_MIN_RADIUS = 5.0;
    private static final double BIG_MAX_RADIUS = 7.0;

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

        convertArea(level, center, big ? BIG_MIN_RADIUS : SMALL_MIN_RADIUS, big ? BIG_MAX_RADIUS : SMALL_MAX_RADIUS);
    }

    public static int convertArea(ServerLevel level, BlockPos center, double minRadius, double maxRadius) {
        RandomSource random = level.getRandom();
        int reach = Mth.ceil(maxRadius);
        int converted = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -reach; dx <= reach; dx++) {
            for (int dy = -reach; dy <= reach; dy++) {
                for (int dz = -reach; dz <= reach; dz++) {
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (distance > maxRadius) {
                        continue;
                    }
                    if (distance > minRadius && random.nextDouble() > (maxRadius - distance) / (maxRadius - minRadius)) {
                        continue;
                    }

                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (convertBlock(level, cursor)) {
                        converted++;
                        if (random.nextInt(3) == 0) {
                            level.sendParticles(ParticleTypes.GLOW, cursor.getX() + 0.5, cursor.getY() + 0.5, cursor.getZ() + 0.5, 2, 0.3, 0.3, 0.3, 0.02);
                        }
                    }
                }
            }
        }

        return converted;
    }

    public static boolean convertBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!TarnishingBronze.canCrystallize(block)) {
            return false;
        }

        BlockState converted = TarnishingBronze.getCrystallizedStage(block).withPropertiesOf(state);

        BlockEntity oldEntity = level.getBlockEntity(pos);
        CompoundTag saved = null;
        if (oldEntity != null) {
            saved = oldEntity.saveWithFullMetadata(level.registryAccess());
            level.removeBlockEntity(pos);
        }

        level.setBlock(pos, converted, Block.UPDATE_ALL);

        if (saved != null) {
            BlockEntity newEntity = level.getBlockEntity(pos);
            if (newEntity != null) {
                newEntity.loadWithComponents(saved, level.registryAccess());
                newEntity.setChanged();
                level.sendBlockUpdated(pos, converted, converted, Block.UPDATE_ALL);
            }
        }

        return true;
    }
}
