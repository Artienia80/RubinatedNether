package corundum.rubinated_nether.content.items;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChainsawItem extends AxeItem {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** Prints what the chainsaw is doing to the log. Set to false once it works. */
    public static final boolean DEBUG = true;

    /** Entity tag marking falling logs that crush non-solid blocks (torches etc.) instead of breaking on them. */
    private static final String HEAVY_TAG = "rn_heavy_log";

    /** Safety cap on how many branch logs one cut can affect. */
    private static final int MAX_BRANCH_LOGS = 128;

    public ChainsawItem(Properties properties) {
        super(RNTiers.BRONZE, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (DEBUG) {
            LOGGER.info("[Chainsaw] mineBlock side={} block={} inLogsTag={}",
                    level.isClientSide() ? "client" : "server", state.getBlock(), state.is(BlockTags.LOGS));
        }

        // The broken block is already gone from the world here, so pos is air.
        if (!level.isClientSide() && state.is(BlockTags.LOGS)) {
            dropLogsAbove(level, pos);
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    private static void dropLogsAbove(Level level, BlockPos pos) {
        // --- 1. trunk column directly above the cut ---
        List<BlockPos> column = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = pos.above().mutable();
        while (cursor.getY() < level.getMaxBuildHeight() && level.getBlockState(cursor).is(BlockTags.LOGS)) {
            column.add(cursor.immutable());
            cursor.move(Direction.UP);
        }
        if (column.isEmpty()) return;

        // --- 2. branches: logs connected to the trunk (incl. diagonals), above the cut ---
        Set<BlockPos> visited = new HashSet<>(column);
        Set<BlockPos> branches = new LinkedHashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>(column);
        while (!queue.isEmpty() && branches.size() < MAX_BRANCH_LOGS) {
            BlockPos p = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos n = p.offset(dx, dy, dz);
                        if (n.getY() <= pos.getY()) continue;
                        if (visited.contains(n) || !level.getBlockState(n).is(BlockTags.LOGS)) continue;
                        // Has its own support (another trunk column, a neighbouring tree)? Leave it alone.
                        BlockPos under = n.below();
                        if (!visited.contains(under) && level.getBlockState(under).is(BlockTags.LOGS)) continue;
                        visited.add(n);
                        branches.add(n);
                        queue.add(n);
                    }
                }
            }
        }

        if (DEBUG) {
            LOGGER.info("[Chainsaw] cut at {}: trunk logs={} branch logs={}", pos, column.size(), branches.size());
        }

        // --- 3. trunk falls one block (it lands on whatever is under the gap) ---
        for (BlockPos p : column) {
            spawnHeavyLog(level, p);
        }

        // --- 4. branches: only fall if there's open space below, otherwise they stay put ---
        for (BlockPos p : branches) {
            BlockState below = level.getBlockState(p.below());
            if (below.isAir() || below.canBeReplaced()) {
                spawnHeavyLog(level, p);
            }
        }
    }

    private static void spawnHeavyLog(Level level, BlockPos p) {
        BlockState s = level.getBlockState(p);
        if (!s.is(BlockTags.LOGS)) return;
        FallingBlockEntity log = FallingBlockEntity.fall(level, p, s); // turns p into air
        log.addTag(HEAVY_TAG);
    }

    /** Register once in the mod constructor: NeoForge.EVENT_BUS.addListener(ChainsawItem::onEntityTick); */
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof FallingBlockEntity falling)) return;
        Level level = falling.level();
        if (level.isClientSide() || !falling.getTags().contains(HEAVY_TAG)) return;

        BlockPos feet = falling.blockPosition();
        crush(level, feet);
        crush(level, feet.below());
    }

    /** Destroys blocks with no collision (torches, flowers, buttons...) so the log lands cleanly. */
    private static void crush(Level level, BlockPos p) {
        BlockState s = level.getBlockState(p);
        if (s.isAir() || !s.getFluidState().isEmpty()) return;
        if (s.getDestroySpeed(level, p) < 0) return;                 // bedrock etc.
        if (!s.getCollisionShape(level, p).isEmpty()) return;        // solid blocks stop the log normally
        level.destroyBlock(p, true);
    }
}