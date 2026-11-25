package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ShrinePillarBlock extends RotatedPillarBlock {
	public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");

	public ShrinePillarBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(INVERTED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(INVERTED);
	}

	public static Direction getFacing(BlockState state) {
		boolean inverted = state.getValue(INVERTED);
		return switch (state.getValue(AXIS)) {
			case X -> inverted ? Direction.WEST : Direction.EAST;
			case Z -> inverted ? Direction.SOUTH : Direction.NORTH;
			default -> inverted ? Direction.DOWN : Direction.UP;
		};
	}

	public static BlockState withFacing(BlockState state, Direction facing) {
		boolean inverted = switch (facing) {
			case WEST, SOUTH, DOWN -> true;
			default -> false;
		};
		return state.setValue(AXIS, facing.getAxis()).setValue(INVERTED, inverted);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return withFacing(this.defaultBlockState(), context.getClickedFace());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return withFacing(state, rotation.rotate(getFacing(state)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(getFacing(state)));
	}
}
