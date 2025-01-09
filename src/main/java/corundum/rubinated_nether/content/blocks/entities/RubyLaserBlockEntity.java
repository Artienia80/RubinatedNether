package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.mixin.accessors.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import org.apache.commons.lang3.mutable.MutableDouble;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.blocks.RubyLaserBlock;
import corundum.rubinated_nether.utils.BlockUpdateListener;
import corundum.rubinated_nether.utils.ShapeUtils;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import corundum.rubinated_nether.utils.UpdateListenerHolder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class RubyLaserBlockEntity extends BlockEntity implements BlockUpdateListener, TickableBlockEntity {

	// Shapes representing a 1 block long beam segment
	private static final Map<Direction, VoxelShape> BEAM_SEGMENT_SHAPES = ShapeUtils.allDirections(
		Shapes.box(.4, 0, .4, .6, 1, .6)
	);

	private static final int LASER_RANGE = 15;
	private int powerLevel;
	private int blockRange = -1;
	private int currentRange = LASER_RANGE;
	private double rangeRemnant;
	private boolean visible = false;
	private Optional<Integer> color;
	private boolean silly = false;

	public RubyLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.RUBY_LASER.get(), pos, blockState);
	}

	@Override
	public void setLevel(Level level) {
		if(!hasLevel()) UpdateListenerHolder.addUpdateListener(level, this);
		super.setLevel(level);
	}

	@Override
	public void clientTick() {
		handleBlockUpdate(level, worldPosition, getBlockState());
	}

	@Override
	public void tick() {
		handleBlockUpdate(level, worldPosition, getBlockState());

		if(getBlockState().getValue(RubyLaserBlock.TINTED)) return;

		Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);

		AABB range = getLaserRangeAABB(worldPosition, facing);

		AtomicInteger i = new AtomicInteger();
		MutableDouble lastDistance = new MutableDouble(blockRange);
		((LevelAccessor) level).invokeGetEntities().get(range, entity -> {
			double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) - 1;
			if(distance < lastDistance.getValue()) {
				lastDistance.setValue(distance);
				i.set(LASER_RANGE - this.currentRange);
			}
		});

		int blockDistance = Mth.clamp(Mth.floor(lastDistance.getValue()), 0, this.currentRange);
		powerLevel = this.currentRange - blockDistance + i.get();

		if(powerLevel != getBlockState().getValue(RubyLaserBlock.POWER)) {
			level.scheduleTick(getBlockPos(), RNBlocks.RUBY_LASER.get(), 2);
		}
	}

	@SuppressWarnings({"DataFlowIssue"})
	@Override
	public void handleBlockUpdate(Level view, BlockPos pos, BlockState bs) {
		this.currentRange = LASER_RANGE;
		Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);
		// BlockPos that is being checked
		BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();

		// Iterating the range of the Laser to check each position
		for (int i = 0; i <= LASER_RANGE; i++) {
			mutableBlockPos.move(facing);
			blockRange = i;

			BlockState state = level.getBlockState(mutableBlockPos);

			boolean blockCheck = state.is(RNTags.Blocks.RUBY_LASER_NO_SIGNAL);
			if (!blockCheck && state.is(RNTags.Blocks.RUBY_LASER_TRANSPARENT)) continue;

			VoxelShape shape = Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND);

			if(!shape.isEmpty()) {
				if(level.isClientSide) {
					Direction.Axis axis = facing.getAxis();
					rangeRemnant = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? shape.min(axis) : 1.0 - shape.max(axis);
				}
				// In case of Tinted Glass the laser range is shortened
				if(blockCheck) this.currentRange = blockRange;
				break;
			}
		}

		// Ignore what IDEA says its stupid
		//
		BlockState state = level.getBlockState(worldPosition.relative(facing));
		silly = state.is(RNTags.Blocks.RAINBOW_LASER);
		visible = silly || state.is(Tags.Blocks.GLASS_BLOCKS);

		if (visible && !silly && state.getBlock() instanceof BeaconBeamBlock) {
			DyeColor dye = ((BeaconBeamBlock) state.getBlock()).getColor();
			color = Optional.of(dye.getTextureDiffuseColor());
		} else {
			color = Optional.empty();
		}

		if(getBlockState().getValue(RubyLaserBlock.TINTED)) {
			powerLevel = Mth.clamp(currentRange - blockRange, 0, LASER_RANGE);
			if (powerLevel != getBlockState().getValue(RubyLaserBlock.POWER)) {
				level.scheduleTick(getBlockPos(), RNBlocks.RUBY_LASER.get(), 2);
			}
		}
	}

	@Override
	public boolean shouldRemove() {
		return this.isRemoved();
	}

	@Override
	public Stream<BlockPos> getListenedPositions() {
		Vec3i offset = getBlockState().getValue(RubyLaserBlock.FACING).getNormal().multiply(LASER_RANGE);
		return BlockPos.betweenClosedStream(worldPosition, worldPosition.offset(offset));
	}

	// This overrides a forge thing
	public AABB getRenderBoundingBox() {
		Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);
		Vec3i end = facing.getNormal().multiply(currentRange + 1);
		return new AABB(worldPosition).expandTowards(end.getX(), end.getY(), end.getZ());
	}

	private AABB getLaserRangeAABB(BlockPos worldPosition, Direction facing) {
		Vec3i rangeVec = facing.getNormal().multiply(blockRange);
		return new AABB(0, 0, 0, 1, 1, 1)
				.expandTowards(rangeVec.getX(), rangeVec.getY(), rangeVec.getZ())
				.move(worldPosition.relative(facing));
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	public int getBlockRange() {
		return (blockRange == -1) ? currentRange : Mth.clamp(blockRange, 0, currentRange);
	}

	public double getRenderRange() {
		return getBlockRange() + rangeRemnant;
	}

	public boolean alwaysVisible() {
		return visible;
	}

	public boolean isColored() {
		return color.isPresent();
	}

	public boolean isSilly() {
		return silly;
	}

	public Optional<Integer> getColor() {
		return color;
	}

	@Override
	public BlockEntityType<?> getType() {
		return RNBlockEntities.RUBY_LASER.get();
	}
}
