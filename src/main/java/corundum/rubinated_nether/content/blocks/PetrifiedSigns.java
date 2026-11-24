package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;

final class PetrifiedSigns {

	private PetrifiedSigns() {
	}

	static void whitenText(Level level, BlockPos pos) {
		if (level.isClientSide || !(level.getBlockEntity(pos) instanceof SignBlockEntity sign)) {
			return;
		}
		sign.setText(sign.getFrontText().setColor(DyeColor.WHITE), true);
		sign.setText(sign.getBackText().setColor(DyeColor.WHITE), false);
	}
}
