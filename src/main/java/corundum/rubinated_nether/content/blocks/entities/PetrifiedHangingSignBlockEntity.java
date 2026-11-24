package corundum.rubinated_nether.content.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

public class PetrifiedHangingSignBlockEntity extends HangingSignBlockEntity {

	public PetrifiedHangingSignBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	protected SignText createDefaultSignText() {
		return super.createDefaultSignText().setColor(DyeColor.WHITE);
	}
}
