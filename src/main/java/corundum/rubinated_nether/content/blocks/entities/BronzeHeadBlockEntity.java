package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.BronzeHeadBlock;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BronzeHeadBlockEntity extends BlockEntity implements TickableBlockEntity {
	public static final float DEGREES_PER_POWER = 22.5F;
	private static final float TURN_SPEED = 2.0F;

	/** How far every head can see, in blocks. */
	public static final int MAX_RANGE = 15;

	private float keyAngle;
	private float prevKeyAngle;
	private boolean synced;

	/** Server only: power coming from what the head currently sees. */
	private int visionPower;
	private boolean initialized;

	public BronzeHeadBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRONZE_HEAD.get(), pos, blockState);
	}

	// ------------------------------------------------------------------ server

	@Override
	public void tick() {
		if (level == null) return;

		// first tick after placing / loading: pick up any redstone signal that is already there
		if (!initialized) {
			initialized = true;
			refreshPower();
		}

		BlockState state = getBlockState();
		if (!state.getValue(BronzeHeadBlock.KEYED)) return; // sheared: purely decorative

		// scan every other tick, offset per head so they don't all scan on the same tick
		if ((level.getGameTime() + worldPosition.hashCode()) % 2 != 0) return;

		int seen = scanVision(state);
		if (seen != visionPower) {
			visionPower = seen;
			refreshPower();
		}
	}

	/**
	 * Recomputes the head's output: the stronger of what it sees and the redstone signal it receives.
	 * A sheared head does neither and stays at 0.
	 */
	public void refreshPower() {
		if (level == null || level.isClientSide) return;
		BlockState state = getBlockState();
		if (!(state.getBlock() instanceof BronzeHeadBlock)) return;

		int target = 0;
		if (state.getValue(BronzeHeadBlock.KEYED)) {
			target = Math.max(visionPower, redstoneInput(state));
		} else {
			visionPower = 0;
		}

		if (state.getValue(BronzeHeadBlock.POWER) != target) {
			level.setBlock(worldPosition, state.setValue(BronzeHeadBlock.POWER, target), Block.UPDATE_ALL);
			BronzeHeadBlock.updateBehind(level, worldPosition, state);
		}
	}

	/** Redstone input from every side except the back, where the head outputs its own signal. */
	private int redstoneInput(BlockState state) {
		Direction back = state.getValue(BronzeHeadBlock.FACING).getOpposite();
		int best = 0;
		for (Direction dir : Direction.values()) {
			if (dir == back) continue;
			best = Math.max(best, level.getSignal(worldPosition.relative(dir), dir));
		}
		return best;
	}

	private TarnishStage getStage(BlockState state) {
		return state.getBlock() instanceof BronzeHeadBlock head ? head.getStage() : TarnishStage.UNAFFECTED;
	}

	/**
	 * How wide the cone is on each side of the centre line, at a given distance in front of the head.
	 * Straight line (Bronze, Crystallized): 0. Discolored 1,2: one block wider every two blocks.
	 * Corroded 1,1: one block wider every block. Tarnished 2,1: two blocks wider every block.
	 */
	private static int halfWidth(TarnishStage stage, int row) {
		return switch (stage) {
			case UNAFFECTED, CRYSTALLIZED -> 0;
			case DISCOLORED -> row / 2;
			case CORRODED -> row;
			case TARNISHED -> row * 2;
		};
	}

	private int scanVision(BlockState state) {
		TarnishStage stage = getStage(state);
		boolean crystallized = stage == TarnishStage.CRYSTALLIZED;

		Direction facing = state.getValue(BronzeHeadBlock.FACING);
		Direction.Axis facingAxis = facing.getAxis();
		Vec3 origin = Vec3.atCenterOf(worldPosition);
		Vec3 forwardVec = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

		double lateralReach = Math.min(MAX_RANGE + 1, halfWidth(stage, MAX_RANGE) + 2);
		AABB area = new AABB(origin, origin.add(forwardVec.scale(MAX_RANGE + 1))).inflate(
				facingAxis == Direction.Axis.X ? 0 : lateralReach,
				lateralReach,
				facingAxis == Direction.Axis.Z ? 0 : lateralReach
		);

		int nearestRow = Integer.MAX_VALUE;
		for (Entity entity : level.getEntities((Entity) null, area,
				candidate -> !candidate.isSpectator() && (!crystallized || candidate instanceof Player))) {
			AABB box = entity.getBoundingBox();
			Vec3 relative = box.getCenter().subtract(origin);

			double forward = relative.dot(forwardVec);
			int row = (int) Math.floor(forward + 0.5);
			if (row < 1 || row > MAX_RANGE || row >= nearestRow) continue;

			// sideways distance of the entity from the centre line, measured to the edge of its hitbox
			// so a big mob clipping the edge of the cone still counts
			double lateralSq = 0;
			double shrunkSq = 0;
			for (Direction.Axis axis : Direction.Axis.values()) {
				if (axis == facingAxis) continue;
				double offset = Math.abs(axis.choose(relative.x, relative.y, relative.z));
				double half = axis.choose(box.getXsize(), box.getYsize(), box.getZsize()) / 2;
				lateralSq += offset * offset;
				double shrunk = Math.max(0, offset - half);
				shrunkSq += shrunk * shrunk;
			}

			if (Math.sqrt(forward * forward + lateralSq) > MAX_RANGE + 0.5) continue;
			if (Math.sqrt(shrunkSq) > halfWidth(stage, row) + 0.5) continue;

			// Crystallized heads see players straight through walls; every other head needs a clear line
			if (!crystallized && !canSee(entity, origin.add(forwardVec.scale(0.51)))) continue;

			nearestRow = row;
			grantLaserDetectionAdvancement(entity);
		}

		return nearestRow == Integer.MAX_VALUE ? 0 : Mth.clamp(MAX_RANGE + 1 - nearestRow, 0, 15);
	}

	// Rays go to the entity's centre or its eyes. Solid blocks stop them; glass, panes, bars and grates
	// (the laser_transparent tag) don't. Tinted glass (laser_no_signal) blocks them.
	private boolean canSee(Entity entity, Vec3 from) {
		return hasClearPath(entity, from, entity.getBoundingBox().getCenter())
				|| hasClearPath(entity, from, entity.getEyePosition());
	}

	private boolean hasClearPath(Entity entity, Vec3 from, Vec3 to) {
		Vec3 direction = to.subtract(from);
		if (direction.lengthSqr() < 1.0E-6) return true;
		Vec3 step = direction.normalize().scale(0.01);
		Vec3 cursor = from;
		for (int i = 0; i < 32; i++) {
			BlockHitResult hit = level.clip(new ClipContext(cursor, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
			if (hit.getType() == HitResult.Type.MISS) return true;
			BlockState hitState = level.getBlockState(hit.getBlockPos());
			if (!hitState.is(RNTags.Blocks.LASER_TRANSPARENT) || hitState.is(RNTags.Blocks.LASER_NO_SIGNAL)) return false;
			cursor = hit.getLocation().add(step);
		}
		return false;
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

	// ------------------------------------------------------------------ client

	@Override
	public void clientTick() {
		BlockState state = getBlockState();
		float target = state.getValue(BronzeHeadBlock.POWER) * DEGREES_PER_POWER;
		if (!synced) {
			synced = true;
			keyAngle = target;
			prevKeyAngle = target;
			return;
		}
		prevKeyAngle = keyAngle;
		float diff = target - keyAngle;
		if (Math.abs(diff) <= TURN_SPEED) {
			keyAngle = target;
		} else {
			keyAngle += Math.signum(diff) * TURN_SPEED;
		}
	}

	public float getKeyAngle(float partialTick) {
		return Mth.lerp(partialTick, prevKeyAngle, keyAngle);
	}
}
