package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.utils.FallingWaterCarry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

public final class QuasiGravity {
    private static final int TICK_DELAY = 2;
    private static final int MAX_CLUSTER_SIZE = 2048;

    private QuasiGravity() {}

    public static void schedule(LevelAccessor level, BlockPos pos, Block block) {
        if (!level.isClientSide()) {
            level.scheduleTick(pos, block, TICK_DELAY);
        }
    }

    public static void settle(ServerLevel level, BlockPos origin) {
        if (!(level.getBlockState(origin).getBlock() instanceof QuasiGravityBlock)) return;

        Set<BlockPos> cluster = new LinkedHashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        cluster.add(origin);
        toCheck.add(origin);

        while (!toCheck.isEmpty()) {
            BlockPos current = toCheck.poll();
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (cluster.contains(next)) continue;

                BlockState nextState = level.getBlockState(next);
                if (nextState.getBlock() instanceof QuasiGravityBlock) {
                    if (cluster.size() >= MAX_CLUSTER_SIZE) return;
                    cluster.add(next);
                    toCheck.add(next);
                } else if (nextState.isFaceSturdy(level, next, direction.getOpposite())) {
                    return;
                }
            }
        }

        for (BlockPos pos : cluster) {
            BlockState state = level.getBlockState(pos);
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
            FallingWaterCarry.pickUpWater(level, pos, state, fallingBlock);
        }
    }
}
