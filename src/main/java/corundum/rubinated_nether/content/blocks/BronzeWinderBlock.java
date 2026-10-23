package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
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

public class BronzeWinderBlock extends DirectionalBlock implements TarnishingBronze {

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
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, WAXED);
	}

	@Override
	protected MapCodec<? extends BronzeWinderBlock> codec() {
		return CODEC;
	}

	@Override
	public TarnishStage getAge() {
		return tarnishStage;
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

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace());
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
		if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
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
			int current = state.getValue(POWER);
			int next = player.isShiftKeyDown() ? (current + 15) % 16 : (current + 1) % 16;
			level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.5F, 0.5F + next / 30.0F);
			setPower(level, pos, state, next);
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	private void setPower(Level level, BlockPos pos, BlockState state, int power) {
		BlockState next = state.setValue(POWER, power);
		level.setBlock(pos, next, Block.UPDATE_ALL);
		updateNeighbours(level, pos, next);
		scheduleUnwind(level, pos, next);
	}

	private void updateNeighbours(Level level, BlockPos pos, BlockState state) {
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
	}

	private void scheduleUnwind(Level level, BlockPos pos, BlockState state) {
		int delay = getUnwindTicks();
		if (!level.isClientSide() && delay > 0 && state.getValue(POWER) > 0) {
			level.scheduleTick(pos, this, delay);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock())) {
			scheduleUnwind(level, pos, state);
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
		int power = state.getValue(POWER);
		if (power > 0 && getUnwindTicks() > 0) {
			setPower(level, pos, state, power - 1);
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
