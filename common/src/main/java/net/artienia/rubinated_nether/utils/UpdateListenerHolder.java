package net.artienia.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public interface UpdateListenerHolder {

    static void addUpdateListener(Level level, BlockUpdateListener listener) {
        ((UpdateListenerHolder) level).rn$addUpdateListener(listener);
    }

    void rn$addUpdateListener(BlockUpdateListener listener);

    void rn$handleBlockUpdate(BlockPos pos);
}
