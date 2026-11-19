package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

public class RedstoneAccumulatorBlock extends DiodeBlock {
	public static final MapCodec<RedstoneAccumulatorBlock> CODEC = simpleCodec(RedstoneAccumulatorBlock::new);
	public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public RedstoneAccumulatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(
				this.stateDefinition.any()
						.setValue(FACING, Direction.NORTH)
						.setValue(POWERED, false)
						.setValue(MODE, Mode.CLAMPED)
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

	@Override
	protected boolean sideInputDiodesOnly() {
		return true;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return this.getOutputSignal(level, pos, state);
	}

	@Override
	protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
		int signal = super.getInputSignal(level, pos, state);
		Direction facing = state.getValue(FACING);
		BlockPos behind = pos.relative(facing);
		BlockState behindState = level.getBlockState(behind);
		if (behindState.hasAnalogOutputSignal()) {
			signal = this.containerInput(level, behind, behindState);
		} else if (signal < Redstone.SIGNAL_MAX && behindState.isRedstoneConductor(level, behind)) {
			behind = behind.relative(facing);
			behindState = level.getBlockState(behind);
			ItemFrame frame = this.getItemFrame(level, facing, behind);
			int analog = Math.max(
					frame == null ? Integer.MIN_VALUE : frame.getAnalogOutput(),
					behindState.hasAnalogOutputSignal() ? this.containerInput(level, behind, behindState) : Integer.MIN_VALUE
			);
			if (analog != Integer.MIN_VALUE) {
				signal = analog;
			}
		}
		return signal;
	}

	private int rawCount(Level level, BlockPos pos, BlockState state) {
		if (!state.hasAnalogOutputSignal()) {
			return Integer.MIN_VALUE;
		}
		if (state.getBlock() instanceof ChestBlock chest) {
			Container chestContainer = ChestBlock.getContainer(chest, state, level, pos, false);
			return chestContainer == null ? 0 : this.countItems(chestContainer);
		}
		if (level.getBlockEntity(pos) instanceof Container container) {
			return this.countItems(container);
		}
		return Integer.MIN_VALUE;
	}

	private int containerInput(Level level, BlockPos pos, BlockState state) {
		int raw = this.rawCount(level, pos, state);
		return raw != Integer.MIN_VALUE ? raw : state.getAnalogOutputSignal(level, pos);
	}

	private int countItems(Container container) {
		int total = 0;
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			total += container.getItem(slot).getCount();
		}
		return total;
	}

	private ItemFrame getItemFrame(Level level, Direction facing, BlockPos pos) {
		List<ItemFrame> frames = level.getEntitiesOfClass(
				ItemFrame.class,
				new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
				frame -> frame != null && frame.getDirection() == facing
		);
		return frames.size() == 1 ? frames.get(0) : null;
	}

	private int sideInput(Level level, BlockPos pos, Direction side) {
		BlockPos adjacent = pos.relative(side);
		int signal = level.getControlInputSignal(adjacent, side, this.sideInputDiodesOnly());
		BlockState adjacentState = level.getBlockState(adjacent);
		int raw = this.rawCount(level, adjacent, adjacentState);
		if (raw != Integer.MIN_VALUE) {
			return raw;
		}
		if (signal < Redstone.SIGNAL_MAX && adjacentState.isRedstoneConductor(level, adjacent)) {
			BlockPos far = adjacent.relative(side);
			raw = this.rawCount(level, far, level.getBlockState(far));
			if (raw != Integer.MIN_VALUE) {
				return raw;
			}
		}
		return signal;
	}

	private int computeOutput(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		int sum = this.getInputSignal(level, pos, state)
				+ this.sideInput(level, pos, facing.getClockWise())
				+ this.sideInput(level, pos, facing.getCounterClockWise());
		return state.getValue(MODE) == Mode.WRAPPED
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
		int output = this.computeOutput(level, pos, state);
		if (output != state.getValue(POWER) || state.getValue(POWERED) != this.shouldTurnOn(level, pos, state)) {
			TickPriority priority = this.shouldPrioritize(level, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
			level.scheduleTick(pos, this, this.getDelay(state), priority);
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
		float pitch = cycled.getValue(MODE) == Mode.WRAPPED ? 0.55F : 0.5F;
		level.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
		level.setBlock(pos, cycled, Block.UPDATE_CLIENTS);
		this.refreshOutput(level, pos, cycled);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
		return true;
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		if (pos.getY() == neighbor.getY() && level instanceof Level real && !real.isClientSide()) {
			state.handleNeighborChanged(real, pos, level.getBlockState(neighbor).getBlock(), neighbor, false);
		}
	}

	public enum Mode implements StringRepresentable {
		CLAMPED("clamped"),
		WRAPPED("wrapped");

		private final String name;

		Mode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
