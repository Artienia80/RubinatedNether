package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BronzeBulbBlock extends Block {
	public static final MapCodec<BronzeBulbBlock> CODEC = simpleCodec(BronzeBulbBlock::new);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	@Override
	protected MapCodec<? extends BronzeBulbBlock> codec() {
		return CODEC;
	}

	public BronzeBulbBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverlevel) {
			this.checkAndFlip(state, serverlevel, pos);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (level instanceof ServerLevel serverlevel) {
			this.checkAndFlip(state, serverlevel, pos);
		}
	}

	public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
		var flag = level.hasNeighborSignal(pos);
		if (flag != state.getValue(POWERED)) {
			BlockState blockstate = state;
			if (!state.getValue(POWERED)) {
				blockstate = state.cycle(LIT);
				level.playSound(
					null, 
					pos, 
					blockstate.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, 
					SoundSource.BLOCKS
				);
			}
			level.setBlock(pos, blockstate.setValue(POWERED, Boolean.valueOf(flag)), 3);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT, POWERED);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	/**
	 * Returns the analog signal this block emits. This is the signal a comparator can read from it.
	 */
	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return level.getBlockState(pos).getValue(LIT) ? 15 : 0;
	}
}
