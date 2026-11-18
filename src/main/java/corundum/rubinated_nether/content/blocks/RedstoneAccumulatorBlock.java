package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.level.ticks.TickPriority;
import net.minecraft.world.phys.BlockHitResult;

public class RedstoneAccumulatorBlock extends DiodeBlock {
	public static final MapCodec<RedstoneAccumulatorBlock> CODEC = simpleCodec(RedstoneAccumulatorBlock::new);
	public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.MODE_COMPARATOR;
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public RedstoneAccumulatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(
				this.stateDefinition.any()
						.setValue(FACING, Direction.NORTH)
						.setValue(POWERED, false)
						.setValue(MODE, ComparatorMode.COMPARE)
						.setValue(POWER, 0)
		);
	}

	@Override
	protected MapCodec<? extends DiodeBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, MODE, POWER);
	}

	@Override
	protected int getDelay(BlockState state) {
		return 2;
	}

	@Override
	protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
		return state.getValue(POWER);
	}

	private int sideInput(Level level, BlockPos pos, Direction side) {
		BlockPos neighborPos = pos.relative(side);
		BlockState neighbor = level.getBlockState(neighborPos);
		if (neighbor.is(Blocks.REDSTONE_BLOCK)) {
			return Redstone.SIGNAL_MAX;
		}
		if (neighbor.is(Blocks.REDSTONE_WIRE)) {
			return neighbor.getValue(RedStoneWireBlock.POWER);
		}
		if (neighbor.getBlock() instanceof DiodeBlock) {
			return level.getSignal(neighborPos, side);
		}
		return 0;
	}

	private int computeOutput(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		int sum = this.getInputSignal(level, pos, state)
				+ this.sideInput(level, pos, facing.getClockWise())
				+ this.sideInput(level, pos, facing.getCounterClockWise());
		return state.getValue(MODE) == ComparatorMode.SUBTRACT
				? sum % (Redstone.SIGNAL_MAX + 1)
				: Math.min(Redstone.SIGNAL_MAX, sum);
	}

	@Override
	protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
		return this.computeOutput(level, pos, state) > 0;
	}

	@Override
	protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
		if (level.getBlockTicks().willTickThisTick(pos, this)) {
			return;
		}
		if (this.computeOutput(level, pos, state) != state.getValue(POWER)) {
			level.scheduleTick(pos, this, this.getDelay(state), TickPriority.HIGH);
		}
	}

	private void refreshOutput(Level level, BlockPos pos, BlockState state) {
		int output = this.computeOutput(level, pos, state);
		if (output != state.getValue(POWER)) {
			BlockState updated = state.setValue(POWER, output).setValue(POWERED, output > 0);
			level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
			this.updateNeighborsInFront(level, pos, updated);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.refreshOutput(level, pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		}
		BlockState cycled = state.cycle(MODE);
		float pitch = cycled.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
		level.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
		level.setBlock(pos, cycled, Block.UPDATE_CLIENTS);
		this.refreshOutput(level, pos, cycled);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
