package corundum.rubinated_nether.content.blocks.bases;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.blocks.CopperLaserBlock;
import corundum.rubinated_nether.content.blocks.entities.CopperLaserBlockEntity;
import corundum.rubinated_nether.utils.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class AbstractLaserBlock extends DirectionalBlock {

    public static final Map<Direction, VoxelShape> SHAPES = ShapeUtils.allDirections(Shapes.or(
            box(0, 0, 0, 16, 6, 16),
            box(2, 0, 2, 14, 16, 14)
    ));

    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    public static final EnumProperty<LaserMode> MODE = EnumProperty.create("mode", LaserMode.class);

    public enum LaserMode implements StringRepresentable {
        SPECTRUM("spectrum"), // Blocks + Entities
        ULTRAVIOLET("uv"),    // Blocks Only
        INFRARED("ir");       // Entities Only

        private final String name;
        LaserMode(String name) { this.name = name; }

        @Override public String getSerializedName() { return this.name; }

        public LaserMode cycle() {
            return switch (this) {
                case SPECTRUM -> ULTRAVIOLET;
                case ULTRAVIOLET -> INFRARED;
                case INFRARED -> SPECTRUM;
            };
        }

        public boolean detectsBlocks() { return this == SPECTRUM || this == ULTRAVIOLET; }
        public boolean detectsEntities() { return this == SPECTRUM || this == INFRARED; }
    }

    protected AbstractLaserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWER, 0)
                .setValue(MODE, LaserMode.SPECTRUM)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER, MODE);
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getMainHandItem();

        // Check for honeycomb or axe - let Minecraft's default behavior handle both (highest priority)
        if (heldItem.is(net.minecraft.world.item.Items.HONEYCOMB) || heldItem.is(net.minecraft.tags.ItemTags.AXES)) {
            // PASS allows Minecraft's default copper interaction logic to run
            // This handles waxing, unwaxing, and scraping in the correct order
            return InteractionResult.PASS;
        }

        // Only do mode switching if no special items are held (lowest priority)
        LaserMode nextMode = state.getValue(MODE).cycle();
        float pitch = switch (nextMode) {
            case SPECTRUM -> 0.6f;
            case ULTRAVIOLET -> 0.7f;
            case INFRARED -> 0.5f;
        };

        level.playLocalSound(pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.5f, pitch, true);
        level.setBlockAndUpdate(pos, state.setValue(MODE, nextMode));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof CopperLaserBlockEntity laser)) return;
        level.setBlockAndUpdate(pos, state.setValue(POWER, laser.getPowerLevel()));

        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        level.neighborChanged(blockPos, this, pos);
        level.updateNeighborsAtExceptFromFacing(blockPos, this, direction);
    }

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
