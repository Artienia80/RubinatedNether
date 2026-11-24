package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CinderBlock extends FallingBlock {
	public static final MapCodec<CinderBlock> CODEC = simpleCodec(CinderBlock::new);
	private static final int TRIGGER_DELAY = 5;
	private static final int CASCADE_DELAY = 2;
	private static final int SUPPORT_DEPTH = 2;
	private static final float FALL_TRIGGER_DISTANCE = 3.0F;
	private static final BlockPos[] CASCADE_OFFSETS = buildCascadeOffsets();

	public CinderBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return CODEC;
	}

	public static BlockState solidState() {
		return Blocks.MAGMA_BLOCK.defaultBlockState();
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		onStep(level, pos, entity);
		super.stepOn(level, pos, state, entity);
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		onFall(level, pos, entity, fallDistance);
		super.fallOn(level, state, pos, entity, fallDistance);
	}

	public void trigger(Level level, BlockPos pos) {
		triggerAt(level, pos);
	}

	public static void onStep(Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity && entity.isSprinting()) {
			triggerAt(level, pos);
		}
	}

	public static void onFall(Level level, BlockPos pos, Entity entity, float fallDistance) {
		if (entity instanceof LivingEntity && fallDistance >= FALL_TRIGGER_DISTANCE) {
			triggerAt(level, pos);
		}
	}

	public static void triggerAt(Level level, BlockPos pos) {
		if (level.isClientSide()) {
			return;
		}
		Block block = level.getBlockState(pos).getBlock();
		if (!level.getBlockTicks().hasScheduledTick(pos, block)) {
			level.scheduleTick(pos, block, TRIGGER_DELAY);
		}
	}

	public static boolean isCinder(BlockState state) {
		return state.getBlock() instanceof CinderBlock || state.getBlock() instanceof SuspiciousCinderBlock;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		collapse(level, pos, state);
	}

	public static void collapse(ServerLevel level, BlockPos pos, BlockState state) {
		if (pos.getY() < level.getMinBuildHeight() || !shouldFall(level, pos)) {
			return;
		}
		FallingBlockEntity.fall(level, pos, state);
		for (BlockPos offset : CASCADE_OFFSETS) {
			BlockPos neighborPos = pos.offset(offset);
			Block neighbor = level.getBlockState(neighborPos).getBlock();
			if (isCinder(level.getBlockState(neighborPos)) && !level.getBlockTicks().hasScheduledTick(neighborPos, neighbor)) {
				level.scheduleTick(neighborPos, neighbor, CASCADE_DELAY);
			}
		}
	}

	@Override
	public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
		if (replaceableState.getFluidState().is(FluidTags.LAVA) || touchesLava(level, pos)) {
			level.setBlock(pos, solidState(), 3);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (level.getFluidState(pos).is(FluidTags.LAVA) || touchesLava(level, pos)) {
			return solidState();
		}
		return super.getStateForPlacement(context);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (touchesLava(level, pos)) {
			return solidState();
		}
		return state;
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getMapColor(level, pos).col;
	}

	private static BlockPos[] buildCascadeOffsets() {
		java.util.List<BlockPos> offsets = new java.util.ArrayList<>();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			for (int dy = -1; dy <= 1; dy++) {
				offsets.add(new BlockPos(direction.getStepX(), dy, direction.getStepZ()));
			}
		}
		offsets.add(new BlockPos(0, 1, 0));
		return offsets.toArray(new BlockPos[0]);
	}

	private static boolean shouldFall(ServerLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos cursor = pos.mutable();
		for (int i = 0; i <= SUPPORT_DEPTH; i++) {
			cursor.move(Direction.DOWN);
			if (cursor.getY() < level.getMinBuildHeight()) {
				return false;
			}
			BlockState below = level.getBlockState(cursor);
			if (isFree(below)) {
				return true;
			}
			if (!isCinder(below)) {
				return false;
			}
		}
		return false;
	}

	private static boolean touchesLava(BlockGetter level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (direction == Direction.DOWN) {
				continue;
			}
			if (level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
				return true;
			}
		}
		return false;
	}
}
