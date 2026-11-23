package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class FrostedGlassBlock extends TransparentBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final int MAX_GROUP_SIZE = 256;

	public FrostedGlassBlock(BlockBehaviour.Properties properties) {
		super(properties);
		BlockState state = this.stateDefinition.any().setValue(POWERED, false);
		for (Direction direction : Direction.values()) {
			state = state.setValue(property(direction), false);
		}
		this.registerDefaultState(state);
	}

	public static BooleanProperty property(Direction direction) {
		return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(
				BlockStateProperties.NORTH,
				BlockStateProperties.EAST,
				BlockStateProperties.SOUTH,
				BlockStateProperties.WEST,
				BlockStateProperties.UP,
				BlockStateProperties.DOWN,
				POWERED
		);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(property(context.getNearestLookingDirection()), true);
	}

	private static void refreshGroup(Level level, BlockPos origin) {
		if (level.isClientSide) {
			return;
		}
		BlockPos start = origin.immutable();
		Set<BlockPos> group = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		group.add(start);
		queue.add(start);
		boolean powered = false;
		while (!queue.isEmpty()) {
			BlockPos pos = queue.poll();
			powered |= level.hasNeighborSignal(pos);
			for (Direction direction : Direction.values()) {
				if (group.size() >= MAX_GROUP_SIZE) {
					break;
				}
				BlockPos next = pos.relative(direction);
				if (!group.contains(next) && level.getBlockState(next).getBlock() instanceof FrostedGlassBlock) {
					group.add(next);
					queue.add(next);
				}
			}
		}
		for (BlockPos pos : group) {
			BlockState current = level.getBlockState(pos);
			if (current.getValue(POWERED) != powered) {
				level.setBlock(pos, current.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
			}
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(this)) {
			refreshGroup(level, pos);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
		refreshGroup(level, pos);
	}

	@Override
	protected ItemInteractionResult useItemOn(
			ItemStack stack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hit
	) {
		if (state.getValue(POWERED)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		Direction face = hit.getDirection();
		BooleanProperty property = property(face);
		boolean frosted = state.getValue(property);

		if (!frosted && stack.is(Tags.Items.GEMS_QUARTZ)) {
			if (!level.isClientSide) {
				level.setBlock(pos, state.setValue(property, true), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
				stack.consume(1, player);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		if (frosted && stack.canPerformAction(ItemAbilities.AXE_SCRAPE)) {
			level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3005, pos, 0);
			if (!level.isClientSide) {
				level.setBlock(pos, state.setValue(property, false), Block.UPDATE_ALL);
				Block.popResourceFromFace(level, pos, face, new ItemStack(Items.QUARTZ));
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		BlockState result = state;
		for (Direction direction : Direction.values()) {
			result = result.setValue(property(rotation.rotate(direction)), state.getValue(property(direction)));
		}
		return result;
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return switch (mirror) {
			case LEFT_RIGHT -> state
					.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH))
					.setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH));
			case FRONT_BACK -> state
					.setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST))
					.setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST));
			default -> state;
		};
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
		if (adjacentState.is(this)) {
			if (state.getValue(POWERED) && adjacentState.getValue(POWERED)) {
				return true;
			}
			return !state.getValue(property(direction)) && !adjacentState.getValue(property(direction.getOpposite()));
		}
		return super.skipRendering(state, adjacentState, direction);
	}
}
