package corundum.rubinated_nether.utils;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class BrazierTracker {
    private static final Map<Level, LongSet> LIT = Collections.synchronizedMap(new WeakHashMap<>());

    private BrazierTracker() {
    }

    public static void set(Level level, BlockPos pos, boolean lit) {
        if (lit) {
            LIT.computeIfAbsent(level, key -> new LongOpenHashSet()).add(pos.asLong());
            return;
        }
        LongSet set = LIT.get(level);
        if (set != null) {
            set.remove(pos.asLong());
        }
    }

    public static boolean anyInRange(Level level, double x, double y, double z, double range) {
        return anyInRange(level, x, y, z, range, null);
    }

    public static boolean anyInRange(Level level, double x, double y, double z, double range, @Nullable BlockPos exclude) {
        LongSet set = LIT.get(level);
        if (set == null || set.isEmpty()) {
            return false;
        }

        boolean hasExclude = exclude != null;
        long excludeKey = hasExclude ? exclude.asLong() : 0L;
        double rangeSq = range * range;

        LongIterator iterator = set.iterator();
        while (iterator.hasNext()) {
            long key = iterator.nextLong();
            if (hasExclude && key == excludeKey) {
                continue;
            }
            double dx = BlockPos.getX(key) + 0.5 - x;
            double dy = BlockPos.getY(key) + 0.5 - y;
            double dz = BlockPos.getZ(key) + 0.5 - z;
            if (dx * dx + dy * dy + dz * dz <= rangeSq) {
                return true;
            }
        }
        return false;
    }
}
