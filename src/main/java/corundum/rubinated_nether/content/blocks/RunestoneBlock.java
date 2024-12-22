package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.mixin.DoublePlantBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class RunestoneBlock extends Block {

	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

//	protected static final VoxelShape SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0);
//	protected static final VoxelShape SHAPE_MIDDLE = Block.box(1.0, 10.0, 1.0, 15.0, 16.0, 15.0);
//	protected static final VoxelShape SHAPE_TOP = Block.box(0.0, 24.0, 0.0, 16.0, 32.0, 16.0);

	protected static final VoxelShape SHAPE_TOP = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0));
	protected static final VoxelShape SHAPE_BOTTOM = Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(1.0, 10.0, 1.0, 15.0, 16.0, 15.0));

	public RunestoneBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
	}

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_BOTTOM : SHAPE_TOP;
	}

	@Override
	public void appendHoverText(
		ItemStack stack, 
		TooltipContext context, 
		List<Component> tooltipComponents,
		TooltipFlag tooltipFlag
	) {
		tooltipComponents.add(Component.translatable("tooltip.rubinated_nether.wip.tooltip"));
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = level.getBlockEntity(pos);
				player.openMenu((BarrelBlockEntity)blockEntity);

			return InteractionResult.CONSUME;
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
		if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)) {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
					? Blocks.AIR.defaultBlockState()
					: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		} else {
			return facingState.getBlock() instanceof RunestoneBlock && facingState.getValue(HALF) != doubleblockhalf
					? facingState.setValue(HALF, doubleblockhalf)
					: Blocks.AIR.defaultBlockState();
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) {
			if (player.isCreative()) {
				DoublePlantBlockAccessor.invokePreventDropFromBottomPart(level, pos, state, player);
			} else {
				dropResources(state, level, pos, null, player, player.getMainHandItem());
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
			return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState blockstate = level.getBlockState(blockpos);
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(level, blockpos, Direction.UP) : blockstate.is(this);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF);
	}
}
