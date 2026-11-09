package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.BronzeHeadBlockEntity;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.HeadOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BronzeHeadBlock extends Block implements BEBlock<BronzeHeadBlockEntity>, TarnishingBronze, Equipable {
	public static final MapCodec<BronzeHeadBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeHeadBlock::getStage),
					propertiesCodec()
			).apply(instance, BronzeHeadBlock::new)
	);

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	public static final BooleanProperty KEYED = BooleanProperty.create("keyed");
	public static final BooleanProperty DIAGONAL = BooleanProperty.create("diagonal");
	public static final DirectionProperty ATTACH = DirectionProperty.create("attach");

	private static final VoxelShape SHAPE_UP = box(3, 0, 3, 13, 10, 13);
	private static final VoxelShape SHAPE_DOWN = box(3, 6, 3, 13, 16, 13);
	private static final VoxelShape SHAPE_NORTH = box(3, 3, 6, 13, 13, 16);
	private static final VoxelShape SHAPE_SOUTH = box(3, 3, 0, 13, 13, 10);
	private static final VoxelShape SHAPE_EAST = box(0, 3, 3, 10, 13, 13);
	private static final VoxelShape SHAPE_WEST = box(6, 3, 3, 16, 13, 13);

	private final TarnishStage stage;

	public BronzeHeadBlock(TarnishStage stage, Properties props) {
		super(props);
		this.stage = stage;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWER, 0)
				.setValue(KEYED, true)
				.setValue(DIAGONAL, false)
				.setValue(ATTACH, Direction.UP)
				.setValue(WAXED, false)
		);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	public TarnishStage getStage() {
		return stage;
	}

	@Override
	public TarnishStage getAge() {
		return stage;
	}

	public static int getStep(BlockState state) {
		return state.getValue(FACING).get2DDataValue() * 2 + (state.getValue(DIAGONAL) ? 1 : 0);
	}

	public static BlockState withStep(BlockState state, int step) {
		int wrapped = step & 7;
		return state
				.setValue(FACING, Direction.from2DDataValue(wrapped / 2))
				.setValue(DIAGONAL, (wrapped & 1) == 1);
	}

	public static Direction[] outputDirections(BlockState state) {
		return new Direction[]{state.getValue(ATTACH)};
	}

	public static boolean outputsTo(BlockState state, Direction direction) {
		for (Direction output : outputDirections(state)) {
			if (output == direction) return true;
		}
		return false;
	}

	private static BlockState orient(BlockState state, Direction attach, Vec3 look) {
		return withStep(state.setValue(ATTACH, attach), HeadOrientation.stepFor(attach, look.x, look.y, look.z));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, KEYED, DIAGONAL, ATTACH, WAXED);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		Direction attach = state.getValue(ATTACH);
		Vec3 look = HeadOrientation.look(attach, getStep(state));
		Vec3 rotated = switch (rotation) {
			case CLOCKWISE_90 -> new Vec3(-look.z, look.y, look.x);
			case CLOCKWISE_180 -> new Vec3(-look.x, look.y, -look.z);
			case COUNTERCLOCKWISE_90 -> new Vec3(look.z, look.y, -look.x);
			default -> look;
		};
		return orient(state, rotation.rotate(attach), rotated);
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		Direction attach = state.getValue(ATTACH);
		Vec3 look = HeadOrientation.look(attach, getStep(state));
		Vec3 mirrored = switch (mirror) {
			case LEFT_RIGHT -> new Vec3(look.x, look.y, -look.z);
			case FRONT_BACK -> new Vec3(-look.x, look.y, look.z);
			default -> look;
		};
		return orient(state, mirror.mirror(attach), mirrored);
	}

	@Override
	public BlockEntityType<? extends BronzeHeadBlockEntity> getBlockEntityType() {
		return RNBlockEntities.BRONZE_HEAD.get();
	}

	@Override
	public Class<? extends BronzeHeadBlockEntity> getBlockEntityClass() {
		return BronzeHeadBlockEntity.class;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(ATTACH)) {
			case UP -> SHAPE_UP;
			case DOWN -> SHAPE_DOWN;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case EAST -> SHAPE_EAST;
			case WEST -> SHAPE_WEST;
		};
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction attach = context.getClickedFace();
		Player player = context.getPlayer();
		int step = 0;
		if (attach.getAxis().isVertical()) {
			step = Mth.floor((context.getRotation() + 180.0F) / 45.0F + 0.5F);
		} else if (player != null) {
			Vec3 look = player.getLookAngle();
			step = HeadOrientation.stepFor(attach, -look.x, -look.y, -look.z);
		}
		return withStep(defaultBlockState().setValue(ATTACH, attach), step);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide) return;
		BronzeHeadBlockEntity head = getBlockEntity(level, pos);
		if (head != null) head.refreshPower();
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!level.isClientSide && !movedByPiston && !state.is(newState.getBlock()) && state.getValue(POWER) > 0) {
			updateBehind(level, pos, state);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(WAXED)) return;

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
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(
				state.getValue(WAXED)
						? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
						: this
		);
		if (!state.getValue(KEYED)) {
			stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(KEYED, false));
		}
		return stack;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (waxing(stack, state, level, pos, player, hand, hitResult)) {
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		boolean keyed = state.getValue(KEYED);

		if (keyed && stack.is(Items.SHEARS)) {
			if (!level.isClientSide) {
				level.setBlock(pos, state.setValue(KEYED, false).setValue(POWER, 0), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
				if (!player.isCreative()) {
					ItemEntity key = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, new ItemStack(RNItems.WINDING_KEY.get()));
					key.setDefaultPickUpDelay();
					level.addFreshEntity(key);
				}
				BronzeHeadBlockEntity sheared = getBlockEntity(level, pos);
				if (sheared != null) sheared.refreshPower();
				updateBehind(level, pos, state);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		if (!keyed && stack.is(RNItems.WINDING_KEY.get())) {
			if (!level.isClientSide) {
				BlockState keyedState = state.setValue(KEYED, true);
				level.setBlock(pos, keyedState, Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
				stack.consume(1, player);
				BronzeHeadBlockEntity head = getBlockEntity(level, pos);
				if (head != null) head.refreshPower();
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return outputsTo(state, direction) ? state.getValue(POWER) : 0;
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getSignal(state, level, pos, direction);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return state.getValue(POWER);
	}

	public static void updateBehind(Level level, BlockPos pos, BlockState state) {
		for (Direction output : outputDirections(state)) {
			BlockPos behind = pos.relative(output.getOpposite());
			level.neighborChanged(behind, state.getBlock(), pos);
			level.updateNeighborsAtExceptFromFacing(behind, state.getBlock(), output);
		}
	}

	@Override
	protected MapCodec<? extends BronzeHeadBlock> codec() {
		return CODEC;
	}
}
