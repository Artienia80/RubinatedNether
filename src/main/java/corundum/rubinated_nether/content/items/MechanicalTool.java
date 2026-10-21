package corundum.rubinated_nether.content.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public interface MechanicalTool {
    default boolean isRunning(ServerPlayer player) {
        return false;
    }

    default boolean canWorkOn(BlockState state) {
        return true;
    }
}