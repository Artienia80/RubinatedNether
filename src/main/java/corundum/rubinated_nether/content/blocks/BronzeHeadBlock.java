package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.BronzeHeadBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * The Bronze Head. It watches a cone in front of it (the shape depends on its tarnish stage) and
 * outputs a redstone signal that gets stronger the closer the nearest seen entity is. The winding key
 * on top turns with the signal.
 *
 * Shearing the head removes the key and turns it into a purely decorative block. A winding key turns it back.
 */
public class BronzeHeadBlock extends Block implements BEBlock<BronzeHeadBlockEntity> {
	public static final MapCodec<BronzeHeadBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeHeadBlock::getStage),
					propertiesCodec()
			).apply(instance, BronzeHeadBlock::new)
	);

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	public static final BooleanProperty KEYED = BooleanProperty.create("keyed");

	private static final VoxelShape SHAPE = box(3, 0, 3, 13, 10, 13);

	private final TarnishStage stage;

	public BronzeHeadBlock(TarnishStage stage, Properties props) {
		super(props);
		this.stage = stage;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWER, 0)
				.setValue(KEYED, true)
		);
	}

	public TarnishStage getStage() {
		return stage;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, KEYED);
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
		return SHAPE;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide) return;
		BronzeHeadBlockEntity head = getBlockEntity(level, pos);
		if (head != null) head.refreshPower();
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
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

	// ---- Redstone output: only into the block behind the head, like the lasers ----

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(FACING) == direction ? state.getValue(POWER) : 0;
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getSignal(state, level, pos, direction);
	}

	/** Tells the block behind the head (and its neighbours) that the head's signal changed. */
	public static void updateBehind(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		BlockPos behind = pos.relative(facing.getOpposite());
		level.neighborChanged(behind, state.getBlock(), pos);
		level.updateNeighborsAtExceptFromFacing(behind, state.getBlock(), facing);
	}

	@Override
	protected MapCodec<? extends BronzeHeadBlock> codec() {
		return CODEC;
	}
}
