package corundum.rubinated_nether.content.enchantment.custom;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class SinkholeScheduler {
    private static final int BLOCKS_PER_TICK = 8;
    private static final float DROP_CHANCE = 0.5f;
    private static final float SKIP_CHANCE = 0.05f;

    private static final List<Job> JOBS = new ArrayList<>();

    private SinkholeScheduler() {}

    static void enqueue(ServerLevel level, long[] positions) {
        if (positions.length > 0) {
            JOBS.add(new Job(level, positions));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (JOBS.isEmpty()) return;

        for (int i = 0; i < JOBS.size(); i++) {
            if (JOBS.get(i).runBatch()) {
                JOBS.remove(i--);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        JOBS.clear();
    }

    private static final class Job {
        private final ServerLevel level;
        private final long[] positions;
        private int next;

        private Job(ServerLevel level, long[] positions) {
            this.level = level;
            this.positions = positions;
        }

        private boolean runBatch() {
            RandomSource random = level.getRandom();
            int end = Math.min(next + BLOCKS_PER_TICK, positions.length);

            for (; next < end; next++) {
                if (random.nextFloat() < SKIP_CHANCE) continue;

                BlockPos pos = BlockPos.of(positions[next]);
                if (!level.isLoaded(pos)) continue;

                BlockState state = level.getBlockState(pos);
                if (!state.isAir()) {
                    level.destroyBlock(pos, random.nextFloat() < DROP_CHANCE);
                }
            }

            return next >= positions.length;
        }
    }
}
