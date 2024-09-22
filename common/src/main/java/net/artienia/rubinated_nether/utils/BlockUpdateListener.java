package net.artienia.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Stream;

public interface BlockUpdateListener {

    void handleBlockUpdate(Level view, BlockPos pos, BlockState state);

    boolean shouldRemove();

    Stream<BlockPos> getListenedPositions();
}
