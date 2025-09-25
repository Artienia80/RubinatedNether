package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.entities.BronzeLaserBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.ShapeUtils;

import java.util.Map;

public class BronzeLaserBlock extends DirectionalBlock implements BEBlock<BronzeLaserBlockEntity> {

	public static final MapCodec<BronzeLaserBlock> CODEC = simpleCodec(BronzeLaserBlock::new);

	public static final Map<Direction, VoxelShape> SHAPES = ShapeUtils.allDirections(Shapes.or(
			box(0, 0, 0, 16, 6, 16),
			box(2, 0, 2, 14, 16, 14)
	));

	public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
	public static final EnumProperty<LaserMode> MODE = EnumProperty.create("mode", LaserMode.class);

	public enum LaserMode implements StringRepresentable {
		SPECTRUM("spectrum"),     // Blocks + Entities (default)
		ULTRAVIOLET("uv"), // Blocks Only
		INFRARED("ir");       // Entities Only

		private final String name;

		LaserMode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public LaserMode cycle() {
			return switch (this) {
				case SPECTRUM -> ULTRAVIOLET;
				case ULTRAVIOLET -> INFRARED;
				case INFRARED -> SPECTRUM;
			};
		}

		public boolean detectsBlocks() {
			return this == SPECTRUM || this == ULTRAVIOLET;
		}

		public boolean detectsEntities() {
			return this == SPECTRUM || this == INFRARED;
		}
	}

	public BronzeLaserBlock(Properties properties) {
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
	public BlockEntityType<? extends BronzeLaserBlockEntity> getBlockEntityType() {
		return RNBlockEntities.BRONZE_LASER.get();
	}

	@Override
	public Class<? extends BronzeLaserBlockEntity> getBlockEntityClass() {
		return BronzeLaserBlockEntity.class;
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
		LaserMode currentMode = state.getValue(MODE);
		LaserMode nextMode = currentMode.cycle();

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
		if(!(be instanceof BronzeLaserBlockEntity laser)) return;
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

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return CODEC;
	}
}