package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HeadConeCache {
	private static final long FULL_RESCAN_INTERVAL_TICKS = 20;
	private static final VoxelShape BEAM_BAND = Shapes.box(0.0D, 0.4D, 0.0D, 1.0D, 0.6D, 1.0D);

	private final HeadCone cone = new HeadCone();
	private final BlockState[] states = new BlockState[HeadCone.SIZE * HeadCone.SIZE];
	private final int[] cells = new int[HeadCone.SIZE * HeadCone.SIZE];
	private int count = -1;
	private int key;
	private long scannedAt;

	public void refresh(Level level, BlockPos origin, double forwardX, double forwardZ, double halfAngle, boolean pierce, int key) {
		if (isValid(level, origin, key)) return;
		rescan(level, origin, forwardX, forwardZ, halfAngle, pierce, key);
	}

	public boolean sees(AABB box, BlockPos origin) {
		int minX = Mth.floor(box.minX) - origin.getX();
		int maxX = Math.max(minX, Mth.floor(box.maxX - 1.0E-6D) - origin.getX());
		int minZ = Mth.floor(box.minZ) - origin.getZ();
		int maxZ = Math.max(minZ, Mth.floor(box.maxZ - 1.0E-6D) - origin.getZ());
		return cone.sees(minX, maxX, minZ, maxZ);
	}

	private boolean isValid(Level level, BlockPos origin, int key) {
		if (count < 0 || this.key != key) return false;

		long now = level.getGameTime();
		if (now < scannedAt || now - scannedAt >= FULL_RESCAN_INTERVAL_TICKS) return false;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < count; i++) {
			int cell = cells[i];
			pos.set(origin.getX() + cell / HeadCone.SIZE - HeadCone.RANGE, origin.getY(), origin.getZ() + cell % HeadCone.SIZE - HeadCone.RANGE);
			if (!level.hasChunkAt(pos)) {
				if (states[i] != null) return false;
				continue;
			}
			if (level.getBlockState(pos) != states[i]) return false;
		}
		return true;
	}

	private void rescan(Level level, BlockPos origin, double forwardX, double forwardZ, double halfAngle, boolean pierce, int key) {
		this.key = key;
		this.scannedAt = level.getGameTime();
		this.count = 0;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		cone.cast(forwardX, forwardZ, halfAngle, pierce, (dx, dz) -> {
			pos.set(origin.getX() + dx, origin.getY(), origin.getZ() + dz);
			cells[count] = (dx + HeadCone.RANGE) * HeadCone.SIZE + (dz + HeadCone.RANGE);
			if (!level.hasChunkAt(pos)) {
				states[count++] = null;
				return true;
			}
			BlockState state = level.getBlockState(pos);
			states[count++] = state;
			return blocks(level, pos, state);
		});
	}

	private static boolean blocks(Level level, BlockPos pos, BlockState state) {
		if (state.isAir()) return false;
		if (state.is(RNTags.Blocks.LASER_TRANSPARENT) && !state.is(RNTags.Blocks.LASER_NO_SIGNAL)) return false;
		return Shapes.joinIsNotEmpty(state.getCollisionShape(level, pos), BEAM_BAND, BooleanOp.AND);
	}
}
