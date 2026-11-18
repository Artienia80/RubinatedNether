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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoonphaseSensorBlock extends Block {
	public static final MapCodec<MoonphaseSensorBlock> CODEC = simpleCodec(MoonphaseSensorBlock::new);
	public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
	private static final int UPDATE_INTERVAL = 20;
	private static final long NIGHT_START = 13000L;
	private static final long NIGHT_END = 23000L;

	public MoonphaseSensorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(INVERTED, false).setValue(POWER, 0));
	}

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(INVERTED, POWER);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(POWER);
	}

	private static int computeOutput(Level level, BlockState state) {
		if (level.dimensionType().hasFixedTime()) {
			return 0;
		}
		long timeOfDay = level.getDayTime() % 24000L;
		if (timeOfDay < NIGHT_START || timeOfDay >= NIGHT_END) {
			return 0;
		}
		int phase = (level.getMoonPhase() + 4) % 8;
		return 1 + (state.getValue(INVERTED) ? 4 - Math.abs(phase - 4) : phase);
	}

	private static void refreshOutput(Level level, BlockPos pos, BlockState state) {
		int output = computeOutput(level, state);
		if (output != state.getValue(POWER)) {
			level.setBlock(pos, state.setValue(POWER, output), Block.UPDATE_ALL);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock()) && !level.isClientSide) {
			level.scheduleTick(pos, this, 1);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		refreshOutput(level, pos, state);
		level.scheduleTick(pos, this, UPDATE_INTERVAL);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockState cycled = state.cycle(INVERTED);
		level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, cycled.getValue(INVERTED) ? 0.55F : 0.5F);
		level.setBlock(pos, cycled, Block.UPDATE_ALL);
		refreshOutput(level, pos, cycled);
		return InteractionResult.CONSUME;
	}
}
