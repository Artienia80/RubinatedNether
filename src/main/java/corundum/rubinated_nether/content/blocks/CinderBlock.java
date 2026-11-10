package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CinderBlock extends FallingBlock {
	public static final MapCodec<CinderBlock> CODEC = simpleCodec(CinderBlock::new);

	public CinderBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return CODEC;
	}

	public static BlockState solidState() {
		return Blocks.MAGMA_BLOCK.defaultBlockState();
	}

	@Override
	public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
		if (replaceableState.getFluidState().is(FluidTags.LAVA) || touchesLava(level, pos)) {
			level.setBlock(pos, solidState(), 3);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (level.getFluidState(pos).is(FluidTags.LAVA) || touchesLava(level, pos)) {
			return solidState();
		}
		return super.getStateForPlacement(context);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (touchesLava(level, pos)) {
			return solidState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getMapColor(level, pos).col;
	}

	private static boolean touchesLava(BlockGetter level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (direction == Direction.DOWN) {
				continue;
			}
			if (level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
				return true;
			}
		}
		return false;
	}
}
