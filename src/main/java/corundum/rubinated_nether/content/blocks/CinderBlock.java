package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CinderBlock extends FallingBlock {
	public static final MapCodec<CinderBlock> CODEC = simpleCodec(CinderBlock::new);
	private static final int TRIGGER_DELAY = 5;
	private static final int CASCADE_DELAY = 2;
	private static final Direction[] CASCADE_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};

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
		if (entity instanceof LivingEntity && entity.isSprinting()) {
			trigger(level, pos);
		}
		super.stepOn(level, pos, state, entity);
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (entity instanceof Player player
				&& fallDistance > 0.5F
				&& !player.getItemBySlot(EquipmentSlot.FEET).is(Items.LEATHER_BOOTS)) {
			trigger(level, pos);
		}
		super.fallOn(level, state, pos, entity, fallDistance);
	}

	public void trigger(Level level, BlockPos pos) {
		if (level.isClientSide()) {
			return;
		}
		if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
			level.scheduleTick(pos, this, TRIGGER_DELAY);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (pos.getY() < level.getMinBuildHeight() || !isFree(level.getBlockState(pos.below()))) {
			return;
		}
		FallingBlockEntity.fall(level, pos, state);
		for (Direction direction : CASCADE_DIRECTIONS) {
			BlockPos neighborPos = pos.relative(direction);
			if (level.getBlockState(neighborPos).getBlock() instanceof CinderBlock neighbor
					&& !level.getBlockTicks().hasScheduledTick(neighborPos, neighbor)) {
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
