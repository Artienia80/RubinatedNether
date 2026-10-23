package corundum.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Lets a waterlogged falling block take its water along with it.
 * <p>
 * Vanilla {@link FallingBlockEntity#fall} strips the waterlogged state and leaves a water source behind.
 * Call {@link #pickUpWater} right after {@code FallingBlockEntity.fall(...)} to remove that water and
 * remember it on the entity, and {@link #dropOffWater} from the block's {@code Fallable#onLand} to put
 * the water back where the block lands.
 */
public final class FallingWaterCarry {
	private static final String TAG = "RNCarriesWater";

	private FallingWaterCarry() {}

	/**
	 * @param originalState the block state before {@code FallingBlockEntity.fall(...)} was called
	 */
	public static void pickUpWater(ServerLevel level, BlockPos pos, BlockState originalState, FallingBlockEntity entity) {
		if (!originalState.hasProperty(BlockStateProperties.WATERLOGGED)
				|| !originalState.getValue(BlockStateProperties.WATERLOGGED))
			return;

		entity.getPersistentData().putBoolean(TAG, true);
		// vanilla left a water source where the block was; the water now travels with the block
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	public static void dropOffWater(Level level, BlockPos pos, FallingBlockEntity entity) {
		if (!entity.getPersistentData().getBoolean(TAG))
			return;

		BlockState landed = level.getBlockState(pos);
		if (landed.hasProperty(BlockStateProperties.WATERLOGGED) && !landed.getValue(BlockStateProperties.WATERLOGGED))
			level.setBlock(pos, landed.setValue(BlockStateProperties.WATERLOGGED, true), 3);
	}
}
