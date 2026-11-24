package corundum.rubinated_nether.content.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

public class PetrifiedSignBlockEntity extends SignBlockEntity {

	public PetrifiedSignBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	protected SignText createDefaultSignText() {
		return super.createDefaultSignText().setColor(DyeColor.WHITE);
	}
}
