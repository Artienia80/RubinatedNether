package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.blocks.entities.PetrifiedSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class PetrifiedWallSignBlock extends WallSignBlock {

	public PetrifiedWallSignBlock(WoodType type, Properties properties) {
		super(type, properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PetrifiedSignBlockEntity(pos, state);
	}
}
