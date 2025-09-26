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

	private int powerLevel;
	private int blockRange = -1;
	private int currentRange;
	private double rangeRemnant;
	private boolean visible = false;
	private Optional<Integer> color;
	private boolean silly = false;

	public BronzeLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRONZE_LASER.get(), pos, blockState);
		this.currentRange = calculateMaxRange(getTarnishState());
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

		// ULTRAVIOLET mode (equivalent to old TINTED) - blocks only, no entity detection
		if (mode == BronzeLaserBlock.LaserMode.ULTRAVIOLET) {
			double effectiveDistance = (blockRange == -1) ? currentRange : blockRange;
			powerLevel = calculatePowerLevel(effectiveDistance);
		} else if (mode.detectsEntities()) {
			// SPECTRUM and INFRARED modes - include entity detection
			Direction facing = getBlockState().getValue(BronzeLaserBlock.FACING);
			AABB range = getLaserRangeAABB(worldPosition, facing);

			AtomicInteger entityHitPowerBonus = new AtomicInteger();
			MutableDouble lastDistance = new MutableDouble(blockRange == -1 ? currentRange : blockRange);
			((LevelAccessor) level).invokeGetEntities().get(range, entity -> {
				double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) - 1;
				if(distance < lastDistance.getValue()) {
					lastDistance.setValue(distance);
					entityHitPowerBonus.set(calculateMaxRange(getTarnishState()) - this.currentRange);
				}
			});

			// Calculate final power level
			double effectiveDistance = Math.min(lastDistance.getValue(), this.currentRange);
			powerLevel = calculatePowerLevel(effectiveDistance) + entityHitPowerBonus.get();
		}

		if(powerLevel != getBlockState().getValue(BronzeLaserBlock.POWER)) {
			level.scheduleTick(getBlockPos(), RNBlocks.BRONZE_LASER.get(), 2);
		}
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

		// Reset block range
		blockRange = -1;

		// Block detection based on mode
		if (mode.detectsBlocks()) {
			// Iterating the range of the Laser to check each position
			for (int i = 0; i < currentRange; i++) {
				mutableBlockPos.move(facing);
				int currentDistance = i + 1; // We're now at distance i+1 from laser

				BlockState state = level.getBlockState(mutableBlockPos);

				boolean blockCheck = state.is(RNTags.Blocks.LASER_NO_SIGNAL);
				if (!blockCheck && state.is(RNTags.Blocks.LASER_TRANSPARENT)) continue;

				VoxelShape shape = Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND);

				if(!shape.isEmpty()) {
					if(level.isClientSide) {
						Direction.Axis axis = facing.getAxis();
						rangeRemnant = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? shape.min(axis) : 1.0 - shape.max(axis);
					}
					// In case of Tinted Glass the laser range is shortened
					if(blockCheck) this.currentRange = currentDistance;
					blockRange = currentDistance;
					break;
				}
			}
		} else {
			// INFRARED mode - blocks don't stop the laser
			blockRange = currentRange;
		}

		// Visual properties check
		BlockState state = level.getBlockState(worldPosition.relative(facing));
		silly = state.is(RNTags.Blocks.SILLY_LASER);
		visible = silly || state.is(Tags.Blocks.GLASS_BLOCKS) || state.is(Tags.Blocks.GLASS_BLOCKS_TINTED) || state.is(Tags.Blocks.GLASS_PANES) || state.is(Blocks.IRON_BARS) || state.is(Blocks.COPPER_GRATE) || state.is(RNTags.Blocks.GRATES);

		if (visible && !silly && state.getBlock() instanceof BeaconBeamBlock) {
			DyeColor dye = ((BeaconBeamBlock) state.getBlock()).getColor();
			color = Optional.of(dye.getTextureDiffuseColor());
		} else {
			color = Optional.empty();
		}

		// Calculate power level for ULTRAVIOLET mode (blocks only)
		BronzeLaserBlock.LaserMode currentMode = getBlockState().getValue(BronzeLaserBlock.MODE);
		if (currentMode == BronzeLaserBlock.LaserMode.ULTRAVIOLET) {
			double effectiveDistance = (blockRange == -1) ? currentRange : blockRange;
			powerLevel = calculatePowerLevel(effectiveDistance);
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
		Vec3i offset = getBlockState().getValue(BronzeLaserBlock.FACING).getNormal().multiply(currentRange);
		return BlockPos.betweenClosedStream(worldPosition, worldPosition.offset(offset));
	}

	// This overrides a forge thing
	public AABB getRenderBoundingBox() {
		Direction facing = getBlockState().getValue(BronzeLaserBlock.FACING);
		Vec3i end = facing.getNormal().multiply(currentRange + 1);
		return new AABB(worldPosition).expandTowards(end.getX(), end.getY(), end.getZ());
	}

	private AABB getLaserRangeAABB(BlockPos worldPosition, Direction facing) {
		int effectiveRange = (blockRange == -1) ? currentRange : blockRange;
		Vec3i rangeVec = facing.getNormal().multiply(effectiveRange);
		return new AABB(0, 0, 0, 1, 1, 1)
				.expandTowards(rangeVec.getX(), rangeVec.getY(), rangeVec.getZ())
				.move(worldPosition.relative(facing));
	}

	private TarnishingBronze.TarnishState getTarnishState() {
		if (getBlockState().getBlock() instanceof BronzeLaserBlock bronzeLaser) {
			return bronzeLaser.getAge();
		}
		return TarnishingBronze.TarnishState.UNAFFECTED;
	}

	private int calculateMaxRange(TarnishingBronze.TarnishState tarnishState) {
		return switch (tarnishState) {
			case UNAFFECTED, CRYSTALLIZED -> 15;
			case DISCOLORED -> 30;
			case CORRODED -> 45;
			case TARNISHED -> 60;
		};
	}

	private int getBlocksPerPowerLevel(TarnishingBronze.TarnishState tarnishState) {
		return switch (tarnishState) {
			case UNAFFECTED, CRYSTALLIZED -> 1;
			case DISCOLORED -> 2;
			case CORRODED -> 3;
			case TARNISHED -> 4;
		};
	}

	private int calculatePowerLevel(double distance) {
		TarnishingBronze.TarnishState tarnishState = getTarnishState();
		int maxRange = calculateMaxRange(tarnishState);
		int blocksPerPowerLevel = getBlocksPerPowerLevel(tarnishState);

		// No obstruction found - power level 0
		if (distance >= maxRange) {
			if (!level.isClientSide) {
				System.out.println("DISTANCE: " + distance + ", NO OBSTRUCTION, OUTPUT: 0");
			}
			return 0;
		}

		// Calculate power based on distance and accuracy
		// Subtract 1 from distance so distance 1 = max power (15)
		double formula = (distance - 1) / blocksPerPowerLevel;
		int power = 15 - (int)(formula);
		power = Mth.clamp(power, 0, 15);

		if (!level.isClientSide) {
			System.out.println("DISTANCE: " + distance + ", FORMULA: 15 - (int)((" + distance + " - 1) / " + blocksPerPowerLevel + ") = 15 - " + (int)formula + " = " + power);
		}

		return power;
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