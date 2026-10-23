package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.BronzeWinderBlockEntity;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BronzeWinderBlock extends DirectionalBlock implements TarnishingBronze, BEBlock<BronzeWinderBlockEntity>, SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final MapCodec<BronzeWinderBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeWinderBlock::getAge),
					propertiesCodec()
			).apply(instance, BronzeWinderBlock::new)
	);

	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public static final Map<Direction, VoxelShape> SHAPES = ShapeUtils.allDirections(Shapes.or(
			box(0, 0, 0, 16, 6, 16),
			box(3, 6, 3, 13, 12, 13)
	));

	// how long a winder holds its charge unchanged after its last input before it starts unwinding
	private static final int UNAFFECTED_IDLE_TICKS = 5 * 20;
	private static final int DISCOLORED_IDLE_TICKS = 15 * 20;
	private static final int CORRODED_IDLE_TICKS = 30 * 20;
	private static final int TARNISHED_IDLE_TICKS = 60 * 20;
	private static final int UNAFFECTED_UNWIND_TICKS = 20;
	private static final int DISCOLORED_UNWIND_TICKS = 40;
	private static final int CORRODED_UNWIND_TICKS = 80;
	private static final int TARNISHED_UNWIND_TICKS = 160;

	private final TarnishStage tarnishStage;

	public BronzeWinderBlock(TarnishStage stage, Properties props) {
		super(props);
		this.tarnishStage = stage;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.UP)
				.setValue(POWER, 0)
				.setValue(WAXED, false)
				.setValue(WATERLOGGED, false)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, WAXED, WATERLOGGED);
	}

	@Override
	protected MapCodec<? extends BronzeWinderBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntityType<? extends BronzeWinderBlockEntity> getBlockEntityType() {
		return RNBlockEntities.BRONZE_WINDER.get();
	}

	@Override
	public Class<? extends BronzeWinderBlockEntity> getBlockEntityClass() {
		return BronzeWinderBlockEntity.class;
	}

	@Override
	public TarnishStage getAge() {
		return tarnishStage;
	}

	public int getIdleTicks() {
		return switch (tarnishStage) {
			case UNAFFECTED -> UNAFFECTED_IDLE_TICKS;
			case DISCOLORED -> DISCOLORED_IDLE_TICKS;
			case CORRODED -> CORRODED_IDLE_TICKS;
			case TARNISHED -> TARNISHED_IDLE_TICKS;
			case CRYSTALLIZED -> 0; // never unwinds, so unused
		};
	}

	public int getUnwindTicks() {
		return switch (tarnishStage) {
			case UNAFFECTED -> UNAFFECTED_UNWIND_TICKS;
			case DISCOLORED -> DISCOLORED_UNWIND_TICKS;
			case CORRODED -> CORRODED_UNWIND_TICKS;
			case TARNISHED -> TARNISHED_UNWIND_TICKS;
			case CRYSTALLIZED -> -1;
		};
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		BlockState state = this.defaultBlockState()
				.setValue(FACING, context.getClickedFace())
				.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
		return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		BlockPos supportPos = pos.relative(facing.getOpposite());
		return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED))
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) {
			return state.getFluidState().createLegacyBlock();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return waxing(stack, state, level, pos, player, hand, hitResult)
				? ItemInteractionResult.SUCCESS
				: super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			if (getExternalSignal(level, pos, state) > 0) {
				// held by an external signal, can't be wound by hand
				level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, 0.3F);
				return InteractionResult.sidedSuccess(false);
			}
			int current = state.getValue(POWER);
			int next = player.isShiftKeyDown() ? (current + 15) % 16 : (current + 1) % 16;
			level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.5F, 0.5F + next / 30.0F);
			BronzeWinderBlockEntity blockEntity = getBlockEntity(level, pos);
			if (blockEntity != null) {
				blockEntity.setLastInput(level.getGameTime());
			}
			setPower(level, pos, state, next);
			scheduleIfAbsent(level, pos, getIdleTicks());
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	private void setPower(Level level, BlockPos pos, BlockState state, int power) {
		BlockState next = state.setValue(POWER, power);
		level.setBlock(pos, next, Block.UPDATE_ALL);
		updateNeighbours(level, pos, next);
	}

	private void updateNeighbours(Level level, BlockPos pos, BlockState state) {
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
	}

	private void scheduleIfAbsent(Level level, BlockPos pos, int delay) {
		if (!level.isClientSide() && getUnwindTicks() > 0 && !level.getBlockTicks().hasScheduledTick(pos, this)) {
			level.scheduleTick(pos, this, delay);
		}
	}

	/**
	 * Strongest redstone signal arriving from outside. The winder reads its own output back through its support
	 * face and through adjacent redstone dust, so those are ignored to avoid it holding itself on. Power it with
	 * a repeater, comparator, observer, lever, powered block, etc.
	 */
	private int getExternalSignal(Level level, BlockPos pos, BlockState state) {
		Direction support = state.getValue(FACING).getOpposite();
		int best = 0;
		for (Direction direction : Direction.values()) {
			if (direction == support) {
				continue;
			}
			BlockPos neighborPos = pos.relative(direction);
			if (level.getBlockState(neighborPos).is(Blocks.REDSTONE_WIRE)) {
				continue;
			}
			best = Math.max(best, level.getSignal(neighborPos, direction));
			if (best >= 15) {
				break;
			}
		}
		return best;
	}

	/**
	 * While an external signal is present the winder copies its strength and holds it. When the signal goes away
	 * the winder keeps its last strength and unwinds normally.
	 *
	 * @return true if an external signal is currently holding the winder
	 */
	private boolean syncExternalSignal(Level level, BlockPos pos, BlockState state) {
		BronzeWinderBlockEntity blockEntity = getBlockEntity(level, pos);
		if (blockEntity == null) {
			return false;
		}

		int external = getExternalSignal(level, pos, state);
		long now = level.getGameTime();

		if (external > 0) {
			blockEntity.setExternallyLocked(true);
			blockEntity.setLastInput(now);
			if (state.getValue(POWER) != external) {
				setPower(level, pos, state, external);
			}
			scheduleIfAbsent(level, pos, getIdleTicks());
			return true;
		}

		if (blockEntity.isExternallyLocked()) {
			// signal was just cut: restart the idle timer so the unwind starts from now
			blockEntity.setExternallyLocked(false);
			blockEntity.setLastInput(now);
			scheduleIfAbsent(level, pos, getIdleTicks());
		}
		return false;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (!level.isClientSide()) {
			syncExternalSignal(level, pos, state);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock()) && state.getValue(POWER) > 0) {
			scheduleIfAbsent(level, pos, getIdleTicks());
		}
		if (!level.isClientSide() && !oldState.is(state.getBlock())
				&& getExternalSignal(level, pos, state) > 0
				&& !level.getBlockTicks().hasScheduledTick(pos, this)) {
			// the block entity doesn't exist yet during onPlace, so sync on the next tick
			level.scheduleTick(pos, this, 1);
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!movedByPiston && !state.is(newState.getBlock()) && state.getValue(POWER) > 0) {
			updateNeighbours(level, pos, state);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (syncExternalSignal(level, pos, state)) {
			return;
		}
		state = level.getBlockState(pos);
		int power = state.getValue(POWER);
		int unwindTicks = getUnwindTicks();
		if (power <= 0 || unwindTicks <= 0) {
			return;
		}

		BronzeWinderBlockEntity blockEntity = getBlockEntity(level, pos);
		if (blockEntity != null) {
			long now = level.getGameTime();
			long lastInput = blockEntity.getLastInput();
			if (lastInput < 0 || lastInput > now) {
				lastInput = now;
				blockEntity.setLastInput(now);
			}
			long remaining = lastInput + getIdleTicks() - now;
			if (remaining > 0) {
				level.scheduleTick(pos, this, (int) remaining);
				return;
			}
		}

		setPower(level, pos, state, power - 1);
		if (power - 1 > 0) {
			level.scheduleTick(pos, this, unwindTicks);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(WAXED)) {
			return;
		}

		boolean hasCatalystNearby = BlockPos.betweenClosedStream(
				pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
		).anyMatch(neighbor -> level.getBlockState(neighbor).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

		if (hasCatalystNearby) {
			this.getCrystallized(state).ifPresent(next -> level.setBlockAndUpdate(pos, next));
		} else {
			this.changeOverTime(state, level, pos, random);
		}
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.canCrystallize(state.getBlock());
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(POWER);
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(FACING) == direction ? state.getValue(POWER) : 0;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return new ItemStack(
				state.getValue(WAXED)
						? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
						: this
		);
	}
}
