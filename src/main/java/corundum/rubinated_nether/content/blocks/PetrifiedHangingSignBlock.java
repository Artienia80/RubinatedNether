package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.blocks.entities.PetrifiedHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class PetrifiedHangingSignBlock extends CeilingHangingSignBlock {

	public PetrifiedHangingSignBlock(WoodType type, Properties properties) {
		super(type, properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PetrifiedHangingSignBlockEntity(pos, state);
	}
}
