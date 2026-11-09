package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNSoundEvents;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.BronzeHeadBlock;
import corundum.rubinated_nether.utils.HeadCone;
import corundum.rubinated_nether.utils.HeadConeCache;
import corundum.rubinated_nether.utils.HeadOrientation;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BronzeHeadBlockEntity extends BlockEntity implements TickableBlockEntity {
	public static final float DEGREES_PER_POWER = 22.5F;

	public static final int MAX_RANGE = 15;

	private static final float TICK_VOLUME = 0.6F;

	private float keyAngle;
	private float prevKeyAngle;
	private boolean synced;
	private int lastPower;

	private int visionPower;
	private boolean initialized;
	private long observationStart = -1;
	private int observationSeconds;
	private boolean wasObserving;
	private final HeadConeCache cone = new HeadConeCache();

	public BronzeHeadBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRONZE_HEAD.get(), pos, blockState);
	}

	@Override
	public void tick() {
		if (level == null) return;

		if (!initialized) {
			initialized = true;
			refreshPower();
		}

		BlockState state = getBlockState();
		if (!state.getValue(BronzeHeadBlock.KEYED)) {
			resetObservation();
			return;
		}

		if ((level.getGameTime() + worldPosition.hashCode()) % 2 != 0) return;

		int seen = updateObservation(scanVision(state));
		if (seen != visionPower) {
			visionPower = seen;
			refreshPower();
		}
	}

	public void refreshPower() {
		if (level == null || level.isClientSide) return;
		BlockState state = getBlockState();
		if (!(state.getBlock() instanceof BronzeHeadBlock)) return;

		int target = 0;
		if (state.getValue(BronzeHeadBlock.KEYED)) {
			target = Math.max(visionPower, redstoneInput(state));
		} else {
			visionPower = 0;
			resetObservation();
		}

		if (state.getValue(BronzeHeadBlock.POWER) != target) {
			level.setBlock(worldPosition, state.setValue(BronzeHeadBlock.POWER, target), Block.UPDATE_ALL);
			BronzeHeadBlock.updateBehind(level, worldPosition, state);
			level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		}
	}

	private int redstoneInput(BlockState state) {
		int best = 0;
		for (Direction dir : Direction.values()) {
			if (BronzeHeadBlock.outputsTo(state, dir.getOpposite())) continue;
			best = Math.max(best, level.getSignal(worldPosition.relative(dir), dir));
		}
		return best;
	}

	private TarnishStage getStage(BlockState state) {
		return state.getBlock() instanceof BronzeHeadBlock head ? head.getStage() : TarnishStage.UNAFFECTED;
	}

	private static double halfAngle(TarnishStage stage) {
		return switch (stage) {
			case UNAFFECTED, CRYSTALLIZED -> 0.0D;
			case DISCOLORED -> Math.atan(0.5D);
			case CORRODED -> Math.atan(1.0D);
			case TARNISHED -> Math.atan(2.0D);
		};
	}

	private boolean scanVision(BlockState state) {
		TarnishStage stage = getStage(state);
		boolean crystallized = stage == TarnishStage.CRYSTALLIZED;
		Direction attach = state.getValue(BronzeHeadBlock.ATTACH);
		int step = BronzeHeadBlock.getStep(state);
		Direction.Axis plane = attach.getAxis();
		Vec3 look = HeadOrientation.look(attach, step);

		cone.refresh(level, worldPosition,
				look.get(HeadOrientation.uAxis(plane)), look.get(HeadOrientation.vAxis(plane)),
				halfAngle(stage), crystallized,
				(attach.get3DDataValue() * HeadOrientation.STEPS + step) * 8 + stage.getId(), plane);

		int range = HeadCone.RANGE;
		AABB area = new AABB(
				worldPosition.getX() - (plane == Direction.Axis.X ? 0 : range),
				worldPosition.getY() - (plane == Direction.Axis.Y ? 0 : range),
				worldPosition.getZ() - (plane == Direction.Axis.Z ? 0 : range),
				worldPosition.getX() + (plane == Direction.Axis.X ? 0 : range) + 1,
				worldPosition.getY() + (plane == Direction.Axis.Y ? 0 : range) + 1,
				worldPosition.getZ() + (plane == Direction.Axis.Z ? 0 : range) + 1
		);

		boolean observed = false;
		for (Entity entity : level.getEntities((Entity) null, area,
				candidate -> !candidate.isSpectator() && (!crystallized || candidate instanceof Player))) {
			if (observed && !(entity instanceof ServerPlayer)) continue;
			if (!cone.sees(entity.getBoundingBox(), worldPosition)) continue;

			observed = true;
			grantLaserDetectionAdvancement(entity);
		}

		return observed;
	}

	private void resetObservation() {
		observationStart = -1;
		observationSeconds = 0;
		wasObserving = false;
	}

	private int updateObservation(boolean observing) {
		if (observing) {
			long now = level.getGameTime();
			if (!wasObserving) {
				observationStart = now;
				observationSeconds = 0;
			} else if (observationStart != -1) {
				observationSeconds = (int) ((now - observationStart) / 20);
			}
			wasObserving = true;
		} else {
			resetObservation();
		}
		return powerFromSeconds(getStage(getBlockState()), observationSeconds);
	}

	private static int secondsPerPowerLevel(TarnishStage stage) {
		return switch (stage) {
			case UNAFFECTED, CRYSTALLIZED -> 1;
			case DISCOLORED -> 2;
			case CORRODED -> 3;
			case TARNISHED -> 4;
		};
	}

	private static int powerFromSeconds(TarnishStage stage, int seconds) {
		if (seconds <= 0) return 0;
		int perLevel = secondsPerPowerLevel(stage);
		int clamped = Math.min(seconds, perLevel * 15);
		return Math.max(1, Math.min(15, clamped / perLevel));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putLong("ObservationStart", observationStart);
		tag.putInt("ObservationSeconds", observationSeconds);
		tag.putBoolean("WasObserving", wasObserving);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		observationStart = tag.contains("ObservationStart") ? tag.getLong("ObservationStart") : -1;
		observationSeconds = tag.getInt("ObservationSeconds");
		wasObserving = tag.getBoolean("WasObserving");
		visionPower = powerFromSeconds(getStage(getBlockState()), observationSeconds);
	}

	private void grantLaserDetectionAdvancement(Entity entity) {
		if (level.isClientSide || !(entity instanceof ServerPlayer serverPlayer)) return;
		var advancementHolder = serverPlayer.server.getAdvancements().get(RubinatedNether.id("laser_detection"));
		if (advancementHolder == null) return;

		var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
		if (!progress.isDone()) {
			for (String criterion : progress.getRemainingCriteria()) {
				serverPlayer.getAdvancements().award(advancementHolder, criterion);
			}
		}
	}

	@Override
	public void clientTick() {
		BlockState state = getBlockState();
		int power = state.getValue(BronzeHeadBlock.POWER);
		float target = power * DEGREES_PER_POWER;
		if (!synced) {
			synced = true;
			lastPower = power;
			keyAngle = target;
			prevKeyAngle = target;
			return;
		}
		if (power > lastPower) playTick(power);
		lastPower = power;
		prevKeyAngle = keyAngle;
		keyAngle = target;
	}

	private void playTick(int power) {
		if (level == null) return;
		float pitch = 0.7F + 0.8F * power / 15.0F;
		level.playLocalSound(
				worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
				RNSoundEvents.HEAD_TICK.get(), SoundSource.BLOCKS, TICK_VOLUME, pitch, false
		);
	}

	public float getKeyAngle(float partialTick) {
		return Mth.lerp(partialTick, prevKeyAngle, keyAngle);
	}
}
