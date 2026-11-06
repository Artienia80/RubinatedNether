package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;

public class CrystallizedBronzeCrystalBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final Map<Direction, BooleanProperty> FACES = PipeBlock.PROPERTY_BY_DIRECTION;

    private static final VoxelShape[] PLATES = new VoxelShape[]{
            box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
            box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0)
    };

    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    static {
        for (int mask = 0; mask < SHAPES.length; mask++) {
            VoxelShape shape = Shapes.empty();
            for (Direction direction : Direction.values()) {
                if ((mask & (1 << direction.ordinal())) != 0) {
                    shape = Shapes.or(shape, PLATES[direction.ordinal()]);
                }
            }
            SHAPES[mask] = shape;
        }
    }

    public CrystallizedBronzeCrystalBlock(Properties properties) {
        super(properties);

        BlockState state = this.stateDefinition.any().setValue(WATERLOGGED, false);
        for (Direction direction : Direction.values()) {
            state = state.setValue(FACES.get(direction), direction == Direction.UP);
        }
        this.registerDefaultState(state);
    }

    public static BooleanProperty faceProperty(Direction direction) {
        return FACES.get(direction);
    }

    public BlockState stateFacing(Direction facing) {
        BlockState state = this.defaultBlockState();
        for (Direction direction : Direction.values()) {
            state = state.setValue(FACES.get(direction), direction == facing);
        }
        return state;
    }

    private static int faceMask(BlockState state) {
        int mask = 0;
        for (Direction direction : Direction.values()) {
            if (state.getValue(FACES.get(direction))) {
                mask |= 1 << direction.ordinal();
            }
        }
        return mask;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
        for (Direction direction : Direction.values()) {
            builder.add(FACES.get(direction));
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[faceMask(state)];
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        boolean any = false;
        for (Direction direction : Direction.values()) {
            if (state.getValue(FACES.get(direction))) {
                any = true;
                BlockPos supportPos = pos.relative(direction.getOpposite());
                if (!level.getBlockState(supportPos).isFaceSturdy(level, supportPos, direction)) {
                    return false;
                }
            }
        }
        return any;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getItemInHand().is(this.asItem())) {
            return !state.getValue(FACES.get(context.getClickedFace()));
        }
        return super.canBeReplaced(state, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState existing = level.getBlockState(pos);
        Direction face = context.getClickedFace();

        BlockState state;
        if (existing.is(this)) {
            state = existing.setValue(FACES.get(face), true);
        } else {
            state = this.stateFacing(face)
                    .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        }

        return state.canSurvive(level, pos) ? state : null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        Direction face = direction.getOpposite();
        if (state.getValue(FACES.get(face)) && !neighborState.isFaceSturdy(level, neighborPos, face)) {
            BlockState reduced = state.setValue(FACES.get(face), false);
            if (faceMask(reduced) == 0) {
                return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            }
            if (level instanceof ServerLevel serverLevel) {
                popResource(serverLevel, pos, new ItemStack(this));
            }
            return reduced;
        }

        return state;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(this, Integer.bitCount(faceMask(state))));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState result = state;
        for (Direction direction : Direction.values()) {
            result = result.setValue(FACES.get(rotation.rotate(direction)), state.getValue(FACES.get(direction)));
        }
        return result;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState result = state;
        for (Direction direction : Direction.values()) {
            result = result.setValue(FACES.get(mirror.mirror(direction)), state.getValue(FACES.get(direction)));
        }
        return result;
    }
}
