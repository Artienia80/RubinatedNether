package corundum.rubinated_nether.content.items;

import net.minecraft.server.level.ServerPlayer;

public interface MechanicalTool {
    default boolean isRunning(ServerPlayer player) {
        return false;
    }
}