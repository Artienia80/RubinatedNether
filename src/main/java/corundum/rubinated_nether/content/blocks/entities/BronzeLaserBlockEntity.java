package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.mixin.accessors.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Blocks;
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
import corundum.rubinated_nether.content.blocks.BronzeLaserBlock;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.utils.BlockUpdateListener;
import corundum.rubinated_nether.utils.ShapeUtils;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import corundum.rubinated_nether.utils.UpdateListenerHolder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class BronzeLaserBlockEntity extends BlockEntity implements BlockUpdateListener, TickableBlockEntity {

	// Shapes representing a 1 block long beam segment
	private static final Map<Direction, VoxelShape> BEAM_SEGMENT_SHAPES = ShapeUtils.allDirections(
			Shapes.box(.4, 0, .4, .6, 1, .6)
	);

	private static final int BASE_LASER_RANGE = 15;
	private int powerLevel;
	private int blockRange = -1;
	private int currentRange = BASE_LASER_RANGE;
	private double rangeRemnant;
	private boolean visible = false;
	private Optional<Integer> color;
	private boolean silly = false;

	public BronzeLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRONZE_LASER.get(), pos, blockState);
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

		BronzeLaserBlock.LaserMode mode = getBlockState().getValue(BronzeLaserBlock.MODE);

		// If mode doesn't detect entities, skip entity detection
		if (!mode.detectsEntities()) {
			// For ULTRAVIOLET mode, power is based only on block range
			if (mode == BronzeLaserBlock.LaserMode.ULTRAVIOLET) {
				powerLevel = calculatePowerLevel(blockRange);
			}

			if (powerLevel != getBlockState().getValue(BronzeLaserBlock.POWER)) {
				level.scheduleTick(getBlockPos(), RNBlocks.BRONZE_LASER.get(), 2);
			}
			return;
		}

		Direction facing = getBlockState().getValue(BronzeLaserBlock.FACING);

		AABB range = getLaserRangeAABB(worldPosition, facing);

		AtomicInteger entityPowerBonus = new AtomicInteger(0);
		MutableDouble lastDistance = new MutableDouble(blockRange);
		((LevelAccessor) level).invokeGetEntities().get(range, entity -> {
			double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) - 1;
			if(distance < lastDistance.getValue()) {
				lastDistance.setValue(distance);
				entityPowerBonus.set(BASE_LASER_RANGE - this.currentRange);
			}
		});

		int closestEntityDistance = Mth.clamp(Mth.floor(lastDistance.getValue()), 0, this.currentRange);

		// Calculate power based on mode
		if (mode == BronzeLaserBlock.LaserMode.INFRARED) {
			// For INFRARED mode, only consider entity distance
			powerLevel = calculatePowerLevel(closestEntityDistance) + entityPowerBonus.get();
		} else {
			// For SPECTRUM mode, consider both blocks and entities (use whichever is closer)
			int closestDistance = Math.min(blockRange, closestEntityDistance);
			powerLevel = calculatePowerLevel(closestDistance) + entityPowerBonus.get();
		}

		if(powerLevel != getBlockState().getValue(BronzeLaserBlock.POWER)) {
			level.scheduleTick(getBlockPos(), RNBlocks.BRONZE_LASER.get(), 2);
		}
	}

	/**
	 * Calculate power level based on distance and tarnish state accuracy grouping
	 * @param distance The distance to the obstruction (0 = maximum power)
	 * @return Power level from 0-15
	 */
	private int calculatePowerLevel(int distance) {
		// Get the tarnish state from the block
		TarnishingBronze.TarnishState tarnishState = getTarnishState();

		// Calculate accuracy (blocks per power level)
		int blocksPerPowerLevel = getBlocksPerPowerLevel(tarnishState);

		// Calculate the accuracy group (0-based)
		// For normal accuracy: distance 0 = group 0, distance 1 = group 1, etc.
		// For other accuracies: distances 0-(blocksPerPowerLevel-1) = group 0, etc.
		int accuracyGroup = distance / blocksPerPowerLevel;

		// Power starts at 15 and decreases by 1 for each accuracy group
		int power = BASE_LASER_RANGE - accuracyGroup;

		// Clamp to valid redstone power range
		return Mth.clamp(power, 0, BASE_LASER_RANGE);
	}

	/**
	 * Get the maximum range for this tarnish state
	 */
	private int calculateMaxRange(TarnishingBronze.TarnishState tarnishState) {
		return switch (tarnishState) {
			case UNAFFECTED -> 15;
			case DISCOLORED -> 30;
			case CORRODED -> 45;
			case TARNISHED -> 60;
			case CRYSTALLIZED -> 15;
		};
	}

	/**
	 * Get the blocks per power level (accuracy) for this tarnish state
	 */
	private int getBlocksPerPowerLevel(TarnishingBronze.TarnishState tarnishState) {
		return switch (tarnishState) {
			case UNAFFECTED -> 1;    // Normal accuracy
			case DISCOLORED -> 2;    // Half accuracy
			case CORRODED -> 3;      // Third accuracy
			case TARNISHED -> 4;     // Quarter accuracy
			case CRYSTALLIZED -> 1;  // Normal accuracy
		};
	}

	/**
	 * Get the tarnish state from the current block
	 */
	private TarnishingBronze.TarnishState getTarnishState() {
		if (getBlockState().getBlock() instanceof BronzeLaserBlock laserBlock) {
			return laserBlock.getAge();
		}
		// Fallback to UNAFFECTED if we can't determine the state
		return TarnishingBronze.TarnishState.UNAFFECTED;
	}

	@SuppressWarnings({"DataFlowIssue"})
	@Override
	public void handleBlockUpdate(Level view, BlockPos pos, BlockState bs) {
		TarnishingBronze.TarnishState tarnishState = getTarnishState();
		this.currentRange = calculateMaxRange(tarnishState);

		Direction facing = getBlockState().getValue(BronzeLaserBlock.FACING);
		BronzeLaserBlock.LaserMode mode = getBlockState().getValue(BronzeLaserBlock.MODE);

		// BlockPos that is being checked
		BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();

		// Only check blocks if mode detects blocks
		if (mode.detectsBlocks()) {
			// Iterating the range of the Laser to check each position
			for (int i = 0; i <= currentRange; i++) {
				mutableBlockPos.move(facing);
				blockRange = i;

				BlockState state = level.getBlockState(mutableBlockPos);

				boolean blockCheck = state.is(RNTags.Blocks.LASER_NO_SIGNAL);
				if (!blockCheck && state.is(RNTags.Blocks.LASER_TRANSPARENT)) continue;

				VoxelShape shape = Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND);

				if(!shape.isEmpty()) {
					if(level.isClientSide) {
						Direction.Axis axis = facing.getAxis();
						rangeRemnant = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? shape.min(axis) : 1.0 - shape.max(axis);
					}
					// In case of block obstruction, the laser range is shortened
					if(blockCheck) this.currentRange = blockRange;
					break;
				}
			}
		} else {
			// For INFRARED mode, blocks don't stop the laser
			blockRange = currentRange;
		}

		// Visual properties
		BlockState state = level.getBlockState(worldPosition.relative(facing));
		silly = state.is(RNTags.Blocks.SILLY_LASER);
		visible = silly || state.is(Tags.Blocks.GLASS_BLOCKS) || state.is(Tags.Blocks.GLASS_BLOCKS_TINTED) || state.is(Tags.Blocks.GLASS_PANES) || state.is(Blocks.IRON_BARS) || state.is(Blocks.IRON_BARS)  || state.is(Blocks.COPPER_GRATE)  || state.is(RNTags.Blocks.GRATES);

		if (visible && !silly && state.getBlock() instanceof BeaconBeamBlock) {
			DyeColor dye = ((BeaconBeamBlock) state.getBlock()).getColor();
			color = Optional.of(dye.getTextureDiffuseColor());
		} else {
			color = Optional.empty();
		}

		// Final power calculation for ULTRAVIOLET mode
		if(mode == BronzeLaserBlock.LaserMode.ULTRAVIOLET) {
			powerLevel = calculatePowerLevel(blockRange);
			if (powerLevel != getBlockState().getValue(BronzeLaserBlock.POWER)) {
				level.scheduleTick(getBlockPos(), RNBlocks.BRONZE_LASER.get(), 2);
			}
		}
	}

	@Override
	public boolean shouldRemove() {
		return this.isRemoved();
	}

	@Override
	public Stream<BlockPos> getListenedPositions() {
		TarnishingBronze.TarnishState tarnishState = getTarnishState();
		int maxRange = calculateMaxRange(tarnishState);
		Vec3i offset = getBlockState().getValue(BronzeLaserBlock.FACING).getNormal().multiply(maxRange);
		return BlockPos.betweenClosedStream(worldPosition, worldPosition.offset(offset));
	}

	// This overrides a forge thing
	public AABB getRenderBoundingBox() {
		Direction facing = getBlockState().getValue(BronzeLaserBlock.FACING);
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

	public int getCurrentRange() {
		return currentRange;
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
		return RNBlockEntities.BRONZE_LASER.get();
	}
}