package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.blocks.BrazierBlock;
import corundum.rubinated_nether.content.blocks.entities.BrazierBlockEntity;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public class BrazierRangeWarningHandler {
    private static final double REACH = 3.0;
    private static final double COVER_TOLERANCE = 0.1;
    private static final int ATTEMPTS_PER_TICK = 12;
    private static final int SCAN_INTERVAL = 5;

    private static int scanCooldown = 0;
    private static List<Vec3> centers = List.of();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if (player == null || level == null || mc.isPaused()) {
            centers = List.of();
            return;
        }

        if (!RNConfig.brazierBoundaryParticles || !player.hasEffect(RNEffects.BRAZIER_POWER) || !player.isInLava()) {
            centers = List.of();
            scanCooldown = 0;
            return;
        }

        if (--scanCooldown <= 0) {
            scanCooldown = SCAN_INTERVAL;
            centers = collectLitBraziers(level, player);
        }

        if (centers.isEmpty()) {
            return;
        }

        spawnBoundaryParticles(mc, level, player);
    }

    private static void spawnBoundaryParticles(Minecraft mc, ClientLevel level, LocalPlayer player) {
        double range = RNConfig.brazierEffectRange;
        double coverRadius = Math.max(0.0, range - COVER_TOLERANCE);
        double coverRadiusSq = coverRadius * coverRadius;
        Vec3 body = new Vec3(player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ());

        for (int i = 0; i < ATTEMPTS_PER_TICK; i++) {
            Vec3 center = centers.get(level.random.nextInt(centers.size()));

            double dx = level.random.nextGaussian();
            double dy = level.random.nextGaussian();
            double dz = level.random.nextGaussian();
            double dirLen = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dirLen < 1.0E-6) {
                continue;
            }
            double radius = REACH * Math.cbrt(level.random.nextDouble());
            Vec3 sample = body.add(dx / dirLen * radius, dy / dirLen * radius, dz / dirLen * radius);

            Vec3 offset = sample.subtract(center);
            double offsetLen = offset.length();
            if (offsetLen < 1.0E-4) {
                continue;
            }
            Vec3 surface = center.add(offset.scale(range / offsetLen));

            double distToPlayer = surface.distanceTo(body);
            if (distToPlayer > REACH) {
                continue;
            }
            if (level.random.nextDouble() > 1.0 - distToPlayer / REACH) {
                continue;
            }

            boolean insideAnother = false;
            for (Vec3 other : centers) {
                if (other != center && other.distanceToSqr(surface) < coverRadiusSq) {
                    insideAnother = true;
                    break;
                }
            }
            if (insideAnother) {
                continue;
            }

            mc.particleEngine.createParticle(RNParticleTypes.RUBY_SPIRIT.get(), surface.x, surface.y, surface.z, 0.0, 0.0, 0.0);
        }
    }

    private static List<Vec3> collectLitBraziers(ClientLevel level, LocalPlayer player) {
        double range = RNConfig.brazierEffectRange;
        double scanRadius = range + REACH;
        double scanRadiusSq = scanRadius * scanRadius;

        int minChunkX = Mth.floor(player.getX() - scanRadius) >> 4;
        int maxChunkX = Mth.floor(player.getX() + scanRadius) >> 4;
        int minChunkZ = Mth.floor(player.getZ() - scanRadius) >> 4;
        int maxChunkZ = Mth.floor(player.getZ() + scanRadius) >> 4;

        List<Vec3> result = new ArrayList<>();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) {
                    continue;
                }

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (!(be instanceof BrazierBlockEntity)) {
                        continue;
                    }
                    if (be.getBlockState().getValue(BrazierBlock.LEVEL) <= 0) {
                        continue;
                    }

                    Vec3 center = new Vec3(
                            be.getBlockPos().getX() + 0.5,
                            be.getBlockPos().getY() + 0.5,
                            be.getBlockPos().getZ() + 0.5
                    );

                    if (player.distanceToSqr(center) <= scanRadiusSq) {
                        result.add(center);
                    }
                }
            }
        }

        return result;
    }
}