package corundum.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BeamScanCache {
    private static final long FULL_RESCAN_INTERVAL_TICKS = 20;

    private BlockState[] states = new BlockState[0];
    private int count = -1;
    private Direction facing;
    private int maxRange;
    private long scannedAt;

    public boolean isValid(Level level, BlockPos origin, Direction facing, int maxRange) {
        if (count < 0 || this.facing != facing || this.maxRange != maxRange) return false;

        long now = level.getGameTime();
        if (now < scannedAt || now - scannedAt >= FULL_RESCAN_INTERVAL_TICKS) return false;

        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int i = 0; i < count; i++) {
            pos.move(facing);
            if (level.getBlockState(pos) != states[i]) return false;
        }
        return true;
    }

    public void begin(Level level, Direction facing, int maxRange) {
        this.facing = facing;
        this.maxRange = maxRange;
        this.count = -1;
        this.scannedAt = level.getGameTime();
        if (states.length < maxRange) {
            states = new BlockState[maxRange];
        }
    }

    public void record(int index, BlockState state) {
        states[index] = state;
    }

    public void finish(int count) {
        this.count = count;
    }

    public void invalidate() {
        this.count = -1;
    }
}
