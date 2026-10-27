package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.CopperLaserBlock;
import corundum.rubinated_nether.content.blocks.LaserMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class CopperLaserBlockEntity extends AbstractLaserBlockEntity {

	// Timer-specific fields
	private long obstructionStartTime = -1; // -1 means no obstruction
	private int obstructionTimer = 0; // in seconds
	private boolean wasObstructed = false;

	public CopperLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.COPPER_LASER.get(), pos, blockState);
	}

	@Override
	public LaserMode getMode() {
		return getBlockState().getValue(CopperLaserBlock.MODE);
	}

	@Override
	protected int getMaxRange() {
		return calculateMaxRange(getWeatherState());
	}

	@Override
	public void tick() {
		handleBlockUpdate(level, worldPosition, getBlockState());

		LaserMode mode = getMode();

		boolean isCurrentlyObstructed = false;

		// Check for obstruction based on mode
		if (mode == LaserMode.ULTRAVIOLET) {
			// UV mode: blocks only
			isCurrentlyObstructed = blockRange != -1 && !isNoSignalBlock(blockRange);
		} else if (mode.detectsEntities()) {
			// SPECTRUM and INFRARED modes: check entities
			Direction facing = getFacing();
			AABB range = getLaserRangeAABB(worldPosition, facing);

			MutableBoolean hasEntity = new MutableBoolean(false);
			level.getEntities().get(range, entity -> {
				// Calculate distance to entity along the laser's facing axis
				double blockDistance = getEntityBlockDistance(entity, facing);

				hasEntity.setTrue();

				// Grant advancement when entity is detected AND within unobstructed range
				if (!level.isClientSide) {
					// Only grant if entity is closer than any block obstruction
					if (blockRange == -1 || blockDistance < blockRange) {
						grantLaserDetectionAdvancement(entity);
					}
				}
			});

			if (mode == LaserMode.INFRARED) {
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

	@Override
	@SuppressWarnings({"DataFlowIssue"})
	public void handleBlockUpdate(Level view, BlockPos pos, BlockState bs) {
		refreshBeam();
	}

	private WeatheringCopper.WeatherState getWeatherState() {
		if (getBlockState().getBlock() instanceof CopperLaserBlock copperLaser) {
			return copperLaser.getAge();
		}
		return WeatheringCopper.WeatherState.UNAFFECTED;
	}

	private int calculateMaxRange(WeatheringCopper.WeatherState weatherState) {
		// Fixed: Copper lasers should have consistent range regardless of oxidation
		// Only bronze lasers should have variable range based on weathering
		return 15;
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

	public int getObstructionTimer() {
		return obstructionTimer;
	}
}
