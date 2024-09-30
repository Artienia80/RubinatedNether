package net.artienia.rubinated_nether.content.block.ruby_laser;

import net.artienia.rubinated_nether.content.RNBlockEntities;
import net.artienia.rubinated_nether.utils.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import uwu.serenity.critter.utils.BEBlock;

import java.util.Map;

@SuppressWarnings("deprecation")
public class RubyLaserBlock extends DirectionalBlock implements BEBlock<RubyLaserBlockEntity> {

    public static final Map<Direction, VoxelShape> SHAPES = ShapeUtils.allDirections(Shapes.or(
        box(0, 0, 0, 16, 6, 16),
        box(2, 0, 2, 14, 16, 14)
    ));

    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    public static final BooleanProperty TINTED = BooleanProperty.create("tinted");

    public RubyLaserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWER, 0)
            .setValue(TINTED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER, TINTED);
    }

    @Override
    public BlockEntityType<? extends RubyLaserBlockEntity> getBlockEntityType() {
        return RNBlockEntities.RUBY_LASER.get();
    }

    @Override
    public Class<? extends RubyLaserBlockEntity> getBlockEntityClass() {
        return RubyLaserBlockEntity.class;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean bl = state.getValue(TINTED);
        level.playLocalSound(pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.5f, bl ? 0.55f : 0.5f, true);
        level.setBlockAndUpdate(pos, state.cycle(TINTED));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof RubyLaserBlockEntity laser)) return;
        level.setBlockAndUpdate(pos, state.setValue(POWER, laser.getPowerLevel()));

        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        level.neighborChanged(blockPos, this, pos);
        level.updateNeighborsAtExceptFromFacing(blockPos, this, direction);
    }

    // Redstone output stuff
    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(FACING) == direction ? state.getValue(POWER) : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

}
