package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.mixin.accessors.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import org.apache.commons.lang3.mutable.MutableBoolean;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.blocks.CopperLaserBlock;
import corundum.rubinated_nether.utils.BlockUpdateListener;
import corundum.rubinated_nether.utils.ShapeUtils;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import corundum.rubinated_nether.utils.UpdateListenerHolder;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CopperLaserBlockEntity extends BlockEntity implements BlockUpdateListener, TickableBlockEntity {

	// Shapes representing a 1 block long beam segment
	private static final Map<Direction, VoxelShape> BEAM_SEGMENT_SHAPES = ShapeUtils.allDirections(
			Shapes.box(.4, 0, .4, .6, 1, .6)
	);

	private int powerLevel;
	private int blockRange = -1;
	private int currentRange;
	private double rangeRemnant;
	private boolean visible = false;
	private Optional<Integer> color = Optional.empty();
	private boolean silly = false;

	// Timer-specific fields
	private long obstructionStartTime = -1; // -1 means no obstruction
	private int obstructionTimer = 0; // in seconds
	private boolean wasObstructed = false;

	public CopperLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.COPPER_LASER.get(), pos, blockState);
		this.currentRange = calculateMaxRange(getWeatherState());
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

		CopperLaserBlock.LaserMode mode = getBlockState().getValue(CopperLaserBlock.MODE);

		boolean isCurrentlyObstructed = false;

		// Check for obstruction based on mode
		if (mode == CopperLaserBlock.LaserMode.ULTRAVIOLET) {
			// UV mode: blocks only
			isCurrentlyObstructed = blockRange != -1 && !isNoSignalBlock(blockRange);
		} else if (mode.detectsEntities()) {
			// SPECTRUM and INFRARED modes: check entities
			Direction facing = getBlockState().getValue(CopperLaserBlock.FACING);
			AABB range = getLaserRangeAABB(worldPosition, facing);

			MutableBoolean hasEntity = new MutableBoolean(false);
			((LevelAccessor) level).invokeGetEntities().get(range, entity -> {
				hasEntity.setTrue();
			});

			if (mode == CopperLaserBlock.LaserMode.INFRARED) {
				// INFRARED: entities only, but blocked by blocks
				if (blockRange != -1 && !hasEntity.booleanValue()) {
					// Block obstructs and no entity closer than block
					isCurrentlyObstructed = false;
				} else {
					// Entity present (closer than block or no block obstruction)
					isCurrentlyObstructed = hasEntity.booleanValue();
				}
			} else {
				// SPECTRUM: blocks OR entities
				boolean blockObstruction = blockRange != -1 && !isNoSignalBlock(blockRange);
				isCurrentlyObstructed = blockObstruction || hasEntity.booleanValue();
			}
		}

		// Update timer based on obstruction state
		updateObstructionTimer(isCurrentlyObstructed);

		// Calculate power level based on timer
		int newPowerLevel = calculatePowerFromTimer();

		if (newPowerLevel != powerLevel) {
			powerLevel = newPowerLevel;
			if (level != null && !level.isClientSide) {
				level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 2);
			}
		}
	}

	private void updateObstructionTimer(boolean isObstructed) {
		long currentTime = level.getGameTime();

		if (isObstructed) {
			if (!wasObstructed) {
				// Obstruction just started
				obstructionStartTime = currentTime;
				obstructionTimer = 0;
			} else {
				// Obstruction continuing - update timer (20 ticks = 1 second)
				if (obstructionStartTime != -1) {
					long ticksObstructed = currentTime - obstructionStartTime;
					obstructionTimer = (int) (ticksObstructed / 20); // Convert to seconds
				}
			}
		} else {
			// No obstruction - reset timer
			obstructionStartTime = -1;
			obstructionTimer = 0;
		}

		wasObstructed = isObstructed;
	}

	private int calculatePowerFromTimer() {
		if (obstructionTimer == 0) {
			return 0;
		}

		WeatheringCopper.WeatherState weatherState = getWeatherState();
		int secondsPerPowerLevel = getSecondsPerPowerLevel(weatherState);
		int maxSeconds = getMaxSeconds(weatherState);

		// Clamp timer to max seconds
		int clampedTimer = Math.min(obstructionTimer, maxSeconds);

		// Calculate power level (1 second minimum for power level 1)
		int power = Math.min(15, clampedTimer / secondsPerPowerLevel);
		if (clampedTimer > 0 && power == 0) {
			power = 1; // Minimum 1 power if there's any obstruction time
		}

		return power;
	}

	private boolean isNoSignalBlock(int range) {
		BlockPos obstructionPos = worldPosition.relative(getBlockState().getValue(CopperLaserBlock.FACING), range);
		BlockState obstructionState = level.getBlockState(obstructionPos);
		return obstructionState.is(RNTags.Blocks.LASER_NO_SIGNAL);
	}

	@SuppressWarnings({"DataFlowIssue"})
	@Override
	public void handleBlockUpdate(Level view, BlockPos pos, BlockState bs) {
		WeatheringCopper.WeatherState weatherState = getWeatherState();
		this.currentRange = calculateMaxRange(weatherState);

		Direction facing = getBlockState().getValue(CopperLaserBlock.FACING);

		// BlockPos that is being checked
		BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();

		// Reset block range
		blockRange = -1;

		// Block detection for obstruction/rendering
		for (int i = 0; i < currentRange; i++) {
			mutableBlockPos.move(facing);
			int currentDistance = i + 1;

			BlockState state = level.getBlockState(mutableBlockPos);

			boolean blockCheck = state.is(RNTags.Blocks.LASER_NO_SIGNAL);
			if (!blockCheck && state.is(RNTags.Blocks.LASER_TRANSPARENT)) continue;

			VoxelShape shape = Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND);

			if(!shape.isEmpty()) {
				if(level.isClientSide) {
					Direction.Axis axis = facing.getAxis();
					rangeRemnant = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? shape.min(axis) : 1.0 - shape.max(axis);
				}
				if(blockCheck) this.currentRange = currentDistance;
				blockRange = currentDistance;
				break;
			}
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
	}

	@Override
	public boolean shouldRemove() {
		return this.isRemoved();
	}

	@Override
	public Stream<BlockPos> getListenedPositions() {
		Vec3i offset = getBlockState().getValue(CopperLaserBlock.FACING).getNormal().multiply(currentRange);
		return BlockPos.betweenClosedStream(worldPosition, worldPosition.offset(offset));
	}

	public AABB getRenderBoundingBox() {
		Direction facing = getBlockState().getValue(CopperLaserBlock.FACING);
		Vec3i end = facing.getNormal().multiply(currentRange + 1);
		return new AABB(worldPosition).expandTowards(end.getX(), end.getY(), end.getZ());
	}

	private AABB getLaserRangeAABB(BlockPos worldPosition, Direction facing) {
		int effectiveRange = (blockRange == -1) ? currentRange : Math.min(blockRange, currentRange);
		Vec3i rangeVec = facing.getNormal().multiply(effectiveRange);
		return new AABB(0, 0, 0, 1, 1, 1)
				.expandTowards(rangeVec.getX(), rangeVec.getY(), rangeVec.getZ())
				.move(worldPosition.relative(facing));
	}

	private WeatheringCopper.WeatherState getWeatherState() {
		if (getBlockState().getBlock() instanceof CopperLaserBlock copperLaser) {
			return copperLaser.getAge();
		}
		return WeatheringCopper.WeatherState.UNAFFECTED;
	}

	private int calculateMaxRange(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 15;
			case EXPOSED -> 30;
			case WEATHERED -> 45;
			case OXIDIZED -> 60;
		};
	}

	private int getSecondsPerPowerLevel(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1;
			case EXPOSED -> 2;
			case WEATHERED -> 3;
			case OXIDIZED -> 4;
		};
	}

	private int getMaxSeconds(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 15;
			case EXPOSED -> 30;
			case WEATHERED -> 45;
			case OXIDIZED -> 60;
		};
	}

	// NBT Persistence
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putLong("ObstructionStartTime", obstructionStartTime);
		tag.putInt("ObstructionTimer", obstructionTimer);
		tag.putBoolean("WasObstructed", wasObstructed);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		obstructionStartTime = tag.getLong("ObstructionStartTime");
		obstructionTimer = tag.getInt("ObstructionTimer");
		wasObstructed = tag.getBoolean("WasObstructed");

		// Recalculate power level from loaded timer
		powerLevel = calculatePowerFromTimer();
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

	public int getObstructionTimer() {
		return obstructionTimer;
	}
}