package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.ChainsawItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Landing rules for the falling blocks the chainsaw spawns (tagged {@link ChainsawItem#HEAVY_TAG}), and only those.
 * <ul>
 *   <li>They turn farmland into dirt.</li>
 *   <li>They never break on landing. Vanilla breaks a falling block into an item when it comes to rest on a partial
 *       block (slab, chest, carpet...), because the block it rests in is that partial block and cannot be replaced.
 *       Here such a block is placed on top of the partial block instead.</li>
 * </ul>
 * Everything is worked out on the tick <i>before</i> the block would touch down, using the same movement vanilla will
 * apply, so no mixin is needed.
 */
@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class HeavyFallingBlockHandler {
    /** FallingBlockEntity's gravity per tick */
    private static final double GRAVITY = 0.04;
    private static final double EPS = 1.0E-5;

    private HeavyFallingBlockHandler() {}

    @SubscribeEvent
    public static void landHeavyBlocks(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof FallingBlockEntity falling)) return;
        Level level = falling.level();
        if (level.isClientSide() || !falling.getTags().contains(ChainsawItem.HEAVY_TAG)) return;

        AABB box = falling.getBoundingBox();
        double nextDy = Math.min(falling.getDeltaMovement().y, 0.0) - GRAVITY;
        AABB swept = box.expandTowards(0.0, nextDy, 0.0);

        // The highest surface under the block that it will reach this tick
        double bestTop = -Double.MAX_VALUE;
        BlockPos supportPos = null;
        BlockPos min = BlockPos.containing(swept.minX, swept.minY, swept.minZ);
        BlockPos max = BlockPos.containing(swept.maxX, swept.maxY, swept.maxZ);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;
            VoxelShape shape = state.getCollisionShape(level, pos);
            if (shape.isEmpty()) continue;
            for (AABB part : shape.toAabbs()) {
                if (box.maxX <= pos.getX() + part.minX || box.minX >= pos.getX() + part.maxX) continue;
                if (box.maxZ <= pos.getZ() + part.minZ || box.minZ >= pos.getZ() + part.maxZ) continue;
                double top = pos.getY() + part.maxY;
                if (top > box.minY + EPS || top < box.minY + nextDy - EPS) continue;
                if (top > bestTop) {
                    bestTop = top;
                    supportPos = pos.immutable();
                }
            }
        }
        if (supportPos == null) return;

        BlockState support = level.getBlockState(supportPos);
        if (support.getBlock() instanceof FarmBlock) {
            // Trample it now; next tick the block lands on the resulting full dirt block like on any other block
            FarmBlock.turnToDirt(falling, support, level, supportPos);
            return;
        }

        // Where vanilla will consider the block to have landed: the block its feet end up in
        BlockPos land = new BlockPos(Mth.floor(falling.getX()), Mth.floor(bestTop + EPS), Mth.floor(falling.getZ()));
        if (canReplace(level, land) && !FallingBlock.isFree(level.getBlockState(land.below()))) {
            return; // an ordinary landing, vanilla places the block
        }

        // Vanilla would break it. Put it on top instead.
        BlockPos target = land;
        for (int i = 0; i < 3 && !canReplace(level, target); i++) {
            target = target.above();
        }
        if (!canReplace(level, target)) return;
        if (level.setBlock(target, falling.getBlockState(), Block.UPDATE_ALL)) {
            falling.discard();
        }
    }

    private static boolean canReplace(Level level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced(
                new DirectionalPlaceContext(level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
    }
}
