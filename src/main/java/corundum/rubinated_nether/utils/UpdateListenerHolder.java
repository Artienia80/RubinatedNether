package corundum.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface UpdateListenerHolder {

	static void addUpdateListener(Level level, BlockUpdateListener listener) {
		((UpdateListenerHolder) level).rubinatedNether$addUpdateListener(listener);
	}

	void rubinatedNether$addUpdateListener(BlockUpdateListener listener);

	void rubinatedNether$handleBlockUpdate(BlockPos pos);
}
