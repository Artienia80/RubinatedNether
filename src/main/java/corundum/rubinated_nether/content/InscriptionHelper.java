package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class InscriptionHelper {
    private static final Map<Block, Block> RUBINATED_TO_NORMAL_MAP = Map.of(
            RNBlocks.RUBINATED_SHRINE_STONE_TILES.get(), RNBlocks.SHRINE_STONE_TILES.get(),
            RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get(), RNBlocks.SHRINE_STONE_PILLAR.get(),
            RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(), RNBlocks.SHRINE_STONE_BRICKS.get(),
            RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(), RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get()
    );

    private static long lastDebugTime = 0;
    private static final long DEBUG_INTERVAL = 1000; // 1 second in milliseconds

    public static int countRubinatedBlocks(Level level, BlockPos centerPos, int radius) {
        int count = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    Block block = level.getBlockState(pos).getBlock();
                    if (RUBINATED_TO_NORMAL_MAP.containsKey(block)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static boolean hasEnoughBlocksForInscription(Level level, BlockPos centerPos, int radius) {
        return countRubinatedBlocks(level, centerPos, radius) >= 100;
    }

    public static boolean hasEnoughBlocksForInscription(Level level, BlockPos centerPos) {
        return hasEnoughBlocksForInscription(level, centerPos, 20);
    }


    public static void debugLogRubinatedBlocks(Level level, BlockPos centerPos, String source) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDebugTime >= DEBUG_INTERVAL) {
            lastDebugTime = currentTime;
            int blockCount = countRubinatedBlocks(level, centerPos, 20);
            System.out.println(source + ": Blocks (" + blockCount + ")");
        }
    }
}