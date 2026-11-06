package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class GrateFluidScheduler {
    private static final int MAX_UPDATES_PER_TICK = 65536;
    private static final Map<ServerLevel, LevelQueue> QUEUES = new IdentityHashMap<>();
    private static long sequence;

    private GrateFluidScheduler() {}

    public static void schedule(LevelAccessor accessor, BlockPos pos, int delay) {
        if (!(accessor instanceof ServerLevel level)) return;

        LevelQueue queue = QUEUES.computeIfAbsent(level, key -> new LevelQueue());
        long packed = pos.asLong();
        if (queue.pending.add(packed)) {
            queue.entries.add(new Entry(level.getGameTime() + Math.max(1, delay), sequence++, packed));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (QUEUES.isEmpty()) return;

        for (ServerLevel level : new ArrayList<>(QUEUES.keySet())) {
            LevelQueue queue = QUEUES.get(level);
            long time = level.getGameTime();

            for (int budget = MAX_UPDATES_PER_TICK; budget > 0; budget--) {
                Entry head = queue.entries.peek();
                if (head == null || head.due() > time) break;

                queue.entries.poll();
                queue.pending.remove(head.pos());

                BlockPos pos = BlockPos.of(head.pos());
                if (level.isLoaded(pos)) {
                    BronzeGrateBlock.updateFluid(level, pos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        QUEUES.clear();
    }

    private record Entry(long due, long order, long pos) {}

    private static final class LevelQueue {
        private final PriorityQueue<Entry> entries = new PriorityQueue<>(
                Comparator.comparingLong(Entry::due).thenComparingLong(Entry::order));
        private final Set<Long> pending = new HashSet<>();
    }
}
