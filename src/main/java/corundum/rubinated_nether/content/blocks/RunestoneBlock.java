package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlockStateProperties;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.entities.RunestoneBlockEntity;
import corundum.rubinated_nether.mixin.accessors.DoublePlantBlockAccessor;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class RunestoneBlock extends BaseEntityBlock {

	private MapCodec<? extends BaseEntityBlock> codec = simpleCodec(RunestoneBlock::new);

	public static final BooleanProperty HAS_RUNE = RNBlockStateProperties.HAS_RUNE;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	protected static final VoxelShape SHAPE_TOP = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0));
	protected static final VoxelShape SHAPE_BOTTOM = Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(1.0, 10.0, 1.0, 15.0, 16.0, 15.0));

	public RunestoneBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(HAS_RUNE, Boolean.FALSE));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return codec;
	}

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_BOTTOM : SHAPE_TOP;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable("tooltip.rubinated_nether.wip.tooltip"));
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
		if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP))
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		else
			return facingState.getBlock() instanceof RunestoneBlock && facingState.getValue(HALF) != doubleblockhalf
				? facingState.setValue(HALF, doubleblockhalf)
				: Blocks.AIR.defaultBlockState();
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos)))
			DoublePlantBlockAccessor.invokePreventDropFromBottomPart(level, pos, state, player);

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();

		if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context))
			return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(HAS_RUNE, Boolean.FALSE);
		else
			return null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
		CustomData customdata = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
		if (customdata.contains("RuneItem")) {
			level.setBlock(pos, state.setValue(HAS_RUNE, Boolean.TRUE), 2);
		}
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState blockstate = level.getBlockState(blockpos);

		return state.getValue(HALF) == DoubleBlockHalf.LOWER 
			? blockstate.isFaceSturdy(level, blockpos, Direction.UP) 
			: blockstate.is(this);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF);
		builder.add(HAS_RUNE);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (state.getValue(HAS_RUNE) && level.getBlockEntity(getCorrectBlockPos(pos, state)) instanceof RunestoneBlockEntity runestoneBlockEntity) {

			runestoneBlockEntity.popOutTheItem(getCorrectBlockPos(pos, state));
			return InteractionResult.sidedSuccess(level.isClientSide);
		} else {
			return InteractionResult.PASS;
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (state.getValue(HAS_RUNE)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else {
			ItemStack itemstack = player.getItemInHand(hand);
			ItemInteractionResult iteminteractionresult = tryInsertIntoRunestone(level, pos, itemstack, player);
			return !iteminteractionresult.consumesAction() ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : iteminteractionresult;
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof RunestoneBlockEntity runestoneBlockEntity) {
				runestoneBlockEntity.popOutTheItem(getCorrectBlockPos(pos, state));
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public ItemInteractionResult tryInsertIntoRunestone(Level level, BlockPos pos, ItemStack stack, Player player) {
		if (!stack.is(RNItems.RUBY_ITEM.asItem()))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		BlockState blockstate = level.getBlockState(pos);
		if (blockstate.is(RNBlocks.RUNESTONE) && !blockstate.getValue(HAS_RUNE)) {
			if (!level.isClientSide) {
				ItemStack itemstack = stack.consumeAndReturn(1, player);
				BlockEntity blockEntity = level.getBlockEntity(getCorrectBlockPos(pos, blockstate));
				if (blockEntity instanceof RunestoneBlockEntity runestoneBlockEntity) {
					runestoneBlockEntity.setTheItem(itemstack);
					blockstate.setValue(HAS_RUNE, Boolean.TRUE);
					level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
				}
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		} else {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new RunestoneBlockEntity(blockPos, blockState);
	}

	/**
	 * Always ensures that the position of the upper half of the block is returned.
	 * This is used mainly for the BlockEntity checks.
	 */
	private BlockPos getCorrectBlockPos(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
	}
}
