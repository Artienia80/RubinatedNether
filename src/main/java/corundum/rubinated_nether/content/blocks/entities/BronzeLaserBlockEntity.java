package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.BronzeLaserBlock;
import corundum.rubinated_nether.content.blocks.LaserMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.concurrent.atomic.AtomicInteger;

public class BronzeLaserBlockEntity extends AbstractLaserBlockEntity {

	public BronzeLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRONZE_LASER.get(), pos, blockState);
	}

	@Override
	public LaserMode getMode() {
		return getBlockState().getValue(BronzeLaserBlock.MODE);
	}

	@Override
	protected int getMaxRange() {
		return calculateMaxRange(getTarnishState());
	}

	@Override
	public void tick() {
		handleBlockUpdate(level, worldPosition, getBlockState());

		LaserMode mode = getMode();

		// ULTRAVIOLET mode (equivalent to old TINTED) - blocks only, no entity detection
		if (mode == LaserMode.ULTRAVIOLET) {
			powerLevel = calculateBlockPower();
		} else if (mode.detectsEntities()) {
			// SPECTRUM and INFRARED modes - include entity detection
			Direction facing = getFacing();
			AABB range = getLaserRangeAABB(worldPosition, facing);

			AtomicInteger entityPower = new AtomicInteger(0);
			MutableDouble lastDistance = new MutableDouble(blockRange == -1 ? Double.MAX_VALUE : blockRange);
			level.getEntities().get(range, entity -> {
				double blockDistance = getEntityBlockDistance(entity, facing);

				if(blockDistance < lastDistance.getValue()) {
					lastDistance.setValue(blockDistance);
					// Calculate entity power based on distance
					entityPower.set(calculatePowerLevel(blockDistance));

					// Grant advancement when entity is detected AND within unobstructed range
					if (!level.isClientSide) {
						// Only grant if entity is closer than any block obstruction
						if (blockRange == -1 || blockDistance < blockRange) {
							grantLaserDetectionAdvancement(entity);
						}
					}
				}
			});

			// Calculate block power (if any) - but not for LASER_NO_SIGNAL blocks
			int blockPower = calculateBlockPower();

			// For INFRARED mode, only give power if entity is closer than block obstruction
			if (mode == LaserMode.INFRARED) {
				if (blockRange != -1 && lastDistance.getValue() >= blockRange) {
					// Block obstructs and no entity is closer than the block
					powerLevel = 0;
				} else {
					// Either no block obstruction, or entity is closer than block obstruction
					powerLevel = entityPower.get();
				}
			} else {
				// SPECTRUM mode - take the maximum of block power and entity power
				powerLevel = Math.max(blockPower, entityPower.get());
			}
		}

		// Ensure power level is always within valid range (0-15) to prevent crashes
		powerLevel = Mth.clamp(powerLevel, 0, 15);

		if(powerLevel != getBlockState().getValue(BronzeLaserBlock.POWER)) {
			level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 2);
		}
	}

	@Override
	@SuppressWarnings({"DataFlowIssue"})
	public void handleBlockUpdate(Level view, BlockPos pos, BlockState bs) {
		refreshBeam();

		// Calculate power level for ULTRAVIOLET mode (blocks only)
		if (getMode() == LaserMode.ULTRAVIOLET) {
			powerLevel = calculateBlockPower();
			// Ensure power level is within valid range
			powerLevel = Mth.clamp(powerLevel, 0, 15);
			if (powerLevel != getBlockState().getValue(BronzeLaserBlock.POWER)) {
				level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 2);
			}
		}
		// INFRARED and SPECTRUM modes are handled in tick() due to entity detection
	}

	private int calculateBlockPower() {
		if (blockRange == -1 || isNoSignalBlock(blockRange)) {
			// Tinted glass or similar - no power output
			return 0;
		}
		// Normal block obstruction - give power based on distance
		return calculatePowerLevel(blockRange);
	}

	private TarnishStage getTarnishState() {
		if (getBlockState().getBlock() instanceof BronzeLaserBlock bronzeLaser) {
			return bronzeLaser.getAge();
		}
		return TarnishStage.UNAFFECTED;
	}

	private int calculateMaxRange(TarnishStage tarnishStage) {
		return switch (tarnishStage) {
			case UNAFFECTED, CRYSTALLIZED -> 15;
			case DISCOLORED -> 30;
			case CORRODED -> 45;
			case TARNISHED -> 60;
        };
	}

	private int getBlocksPerPowerLevel(TarnishStage tarnishStage) {
		return switch (tarnishStage) {
			case UNAFFECTED, CRYSTALLIZED -> 1;
			case DISCOLORED -> 2;
			case CORRODED -> 3;
			case TARNISHED -> 4;
		};
	}

	private int calculatePowerLevel(double distance) {
		TarnishStage tarnishStage = getTarnishState();
		int maxRange = calculateMaxRange(tarnishStage);
		int blocksPerPowerLevel = getBlocksPerPowerLevel(tarnishStage);

		// No obstruction found - power level 0 (when beyond max range or no obstruction)
		if (distance > maxRange) {
			return 0;
		}

		// Calculate power based on distance and accuracy
		// Subtract 1 from distance so distance 1 = max power (15)
		double formula = (distance - 1) / blocksPerPowerLevel;
		int power = 15 - (int)(formula);
		power = Mth.clamp(power, 0, 15);

		return power;
	}

	@Override
	public BlockEntityType<?> getType() {
		return RNBlockEntities.BRONZE_LASER.get();
	}
}
