//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.artienia.rubinated_nether.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RubyRailBlock extends BaseRailBlock {
	public static final EnumProperty<RailShape> SHAPE;
	public static final BooleanProperty POWERED;

	public RubyRailBlock(BlockBehaviour.Properties properties) {
		super(true, properties);
		this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
	}

	protected boolean findPoweredRailSignal(Level level, BlockPos pos, BlockState state, boolean searchForward, int recursionCount) {
		if (recursionCount >= 8) {
			return false;
		} else {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			boolean bl = true;
			RailShape railShape = (RailShape)state.getValue(SHAPE);
			switch (railShape) {
				case NORTH_SOUTH:
					if (searchForward) {
						++k;
					} else {
						--k;
					}
					break;
				case EAST_WEST:
					if (searchForward) {
						--i;
					} else {
						++i;
					}
					break;
				case ASCENDING_EAST:
					if (searchForward) {
						--i;
					} else {
						++i;
						++j;
						bl = false;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_WEST:
					if (searchForward) {
						--i;
						++j;
						bl = false;
					} else {
						++i;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_NORTH:
					if (searchForward) {
						++k;
					} else {
						--k;
						++j;
						bl = false;
					}

					railShape = RailShape.NORTH_SOUTH;
					break;
				case ASCENDING_SOUTH:
					if (searchForward) {
						++k;
						++j;
						bl = false;
					} else {
						--k;
					}

					railShape = RailShape.NORTH_SOUTH;
				default:
					break;
			}

			if (this.isSameRailWithPower(level, new BlockPos(i, j, k), searchForward, recursionCount, railShape)) {
				return true;
			} else {
				return bl && this.isSameRailWithPower(level, new BlockPos(i, j - 1, k), searchForward, recursionCount, railShape);
			}
		}
	}

	protected boolean isSameRailWithPower(Level level, BlockPos state, boolean searchForward, int recursionCount, RailShape shape) {
		BlockState blockState = level.getBlockState(state);
		if (!blockState.is(this)) {
			return false;
		} else {
			RailShape railShape = (RailShape)blockState.getValue(SHAPE);
			if (shape == RailShape.EAST_WEST && (railShape == RailShape.NORTH_SOUTH || railShape == RailShape.ASCENDING_NORTH || railShape == RailShape.ASCENDING_SOUTH)) {
				return false;
			} else if (shape == RailShape.NORTH_SOUTH && (railShape == RailShape.EAST_WEST || railShape == RailShape.ASCENDING_EAST || railShape == RailShape.ASCENDING_WEST)) {
				return false;
			} else if ((Boolean)blockState.getValue(POWERED)) {
				return level.hasNeighborSignal(state) ? true : this.findPoweredRailSignal(level, state, blockState, searchForward, recursionCount + 1);
			} else {
				return false;
			}
		}
	}

	protected void updateState(BlockState state, Level level, BlockPos pos, Block neighborBlock) {
		boolean bl = (Boolean)state.getValue(POWERED);
		boolean bl2 = level.hasNeighborSignal(pos) || this.findPoweredRailSignal(level, pos, state, true, 0) || this.findPoweredRailSignal(level, pos, state, false, 0);
		if (bl2 != bl) {
			level.setBlock(pos, (BlockState)state.setValue(POWERED, bl2), 3);
			level.updateNeighborsAt(pos.below(), this);
			if (((RailShape)state.getValue(SHAPE)).isAscending()) {
				level.updateNeighborsAt(pos.above(), this);
			}
		}

	}

	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	public BlockState rotate(BlockState state, Rotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((RailShape)state.getValue(SHAPE)) {
					case ASCENDING_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_WEST);
					}
					case ASCENDING_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_EAST);
					}
					case ASCENDING_NORTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
					}
					case ASCENDING_SOUTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
					}
					case SOUTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_WEST);
					}
					case SOUTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_EAST);
					}
					case NORTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_EAST);
					}
					case NORTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_WEST);
					}
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)state.getValue(SHAPE)) {
					case NORTH_SOUTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.EAST_WEST);
					}
					case EAST_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_SOUTH);
					}
					case ASCENDING_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
					}
					case ASCENDING_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
					}
					case ASCENDING_NORTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_WEST);
					}
					case ASCENDING_SOUTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_EAST);
					}
					case SOUTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_EAST);
					}
					case SOUTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_EAST);
					}
					case NORTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_WEST);
					}
					case NORTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_WEST);
					}
				}
			case CLOCKWISE_90:
				switch ((RailShape)state.getValue(SHAPE)) {
					case NORTH_SOUTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.EAST_WEST);
					}
					case EAST_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_SOUTH);
					}
					case ASCENDING_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
					}
					case ASCENDING_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
					}
					case ASCENDING_NORTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_EAST);
					}
					case ASCENDING_SOUTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_WEST);
					}
					case SOUTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_WEST);
					}
					case SOUTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_WEST);
					}
					case NORTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_EAST);
					}
					case NORTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_EAST);
					}
				}
			default:
				return state;
		}
	}

	public BlockState mirror(BlockState state, Mirror mirror) {
		RailShape railShape = (RailShape)state.getValue(SHAPE);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
					}
					case ASCENDING_SOUTH -> {
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
					}
					case SOUTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_EAST);
					}
					case SOUTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_WEST);
					}
					case NORTH_WEST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_WEST);
					}
					case NORTH_EAST -> {
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_EAST);
					}
					default -> {
						return super.mirror(state, mirror);
					}
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return (BlockState)state.setValue(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return (BlockState)state.setValue(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return (BlockState)state.setValue(SHAPE, RailShape.NORTH_WEST);
				}
			default:
				break;
		}

		return super.mirror(state, mirror);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{SHAPE, POWERED, WATERLOGGED});
	}

	static {
		SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
		POWERED = BlockStateProperties.POWERED;
	}
}
