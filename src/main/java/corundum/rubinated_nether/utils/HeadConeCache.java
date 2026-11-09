package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HeadConeCache {
	private static final long FULL_RESCAN_INTERVAL_TICKS = 20;
	private static final VoxelShape BEAM_BAND_Y = Shapes.box(0.0D, 0.4D, 0.0D, 1.0D, 0.6D, 1.0D);
	private static final VoxelShape BEAM_BAND_X = Shapes.box(0.4D, 0.0D, 0.0D, 0.6D, 1.0D, 1.0D);
	private static final VoxelShape BEAM_BAND_Z = Shapes.box(0.0D, 0.0D, 0.4D, 1.0D, 1.0D, 0.6D);

	private final HeadCone cone = new HeadCone();
	private final BlockState[] states = new BlockState[HeadCone.SIZE * HeadCone.SIZE];
	private final int[] cells = new int[HeadCone.SIZE * HeadCone.SIZE];
	private int count = -1;
	private int key;
	private long scannedAt;
	private Direction.Axis plane = Direction.Axis.Y;

	public void refresh(Level level, BlockPos origin, double forwardU, double forwardV, double halfAngle, boolean pierce, int key, Direction.Axis plane) {
		if (isValid(level, origin, key)) return;
		rescan(level, origin, forwardU, forwardV, halfAngle, pierce, key, plane);
	}

	public boolean sees(AABB box, BlockPos origin) {
		Direction.Axis uAxis = HeadOrientation.uAxis(plane);
		Direction.Axis vAxis = HeadOrientation.vAxis(plane);
		int minU = Mth.floor(box.min(uAxis)) - origin.get(uAxis);
		int maxU = Math.max(minU, Mth.floor(box.max(uAxis) - 1.0E-6D) - origin.get(uAxis));
		int minV = Mth.floor(box.min(vAxis)) - origin.get(vAxis);
		int maxV = Math.max(minV, Mth.floor(box.max(vAxis) - 1.0E-6D) - origin.get(vAxis));
		return cone.sees(minU, maxU, minV, maxV);
	}

	private static void place(BlockPos.MutableBlockPos pos, BlockPos origin, Direction.Axis plane, int du, int dv) {
		Direction.Axis uAxis = HeadOrientation.uAxis(plane);
		Direction.Axis vAxis = HeadOrientation.vAxis(plane);
		int x = origin.getX();
		int y = origin.getY();
		int z = origin.getZ();
		if (uAxis == Direction.Axis.X) x += du;
		else if (uAxis == Direction.Axis.Y) y += du;
		else z += du;
		if (vAxis == Direction.Axis.X) x += dv;
		else if (vAxis == Direction.Axis.Y) y += dv;
		else z += dv;
		pos.set(x, y, z);
	}

	private boolean isValid(Level level, BlockPos origin, int key) {
		if (count < 0 || this.key != key) return false;

		long now = level.getGameTime();
		if (now < scannedAt || now - scannedAt >= FULL_RESCAN_INTERVAL_TICKS) return false;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < count; i++) {
			int cell = cells[i];
			place(pos, origin, plane, cell / HeadCone.SIZE - HeadCone.RANGE, cell % HeadCone.SIZE - HeadCone.RANGE);
			if (!level.hasChunkAt(pos)) {
				if (states[i] != null) return false;
				continue;
			}
			if (level.getBlockState(pos) != states[i]) return false;
		}
		return true;
	}

	private void rescan(Level level, BlockPos origin, double forwardU, double forwardV, double halfAngle, boolean pierce, int key, Direction.Axis plane) {
		this.key = key;
		this.plane = plane;
		this.scannedAt = level.getGameTime();
		this.count = 0;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		cone.cast(forwardU, forwardV, halfAngle, pierce, (du, dv) -> {
			place(pos, origin, plane, du, dv);
			cells[count] = (du + HeadCone.RANGE) * HeadCone.SIZE + (dv + HeadCone.RANGE);
			if (!level.hasChunkAt(pos)) {
				states[count++] = null;
				return true;
			}
			BlockState state = level.getBlockState(pos);
			states[count++] = state;
			return blocks(level, pos, state, plane);
		});
	}

	private static boolean blocks(Level level, BlockPos pos, BlockState state, Direction.Axis plane) {
		if (state.isAir()) return false;
		if (state.is(RNTags.Blocks.LASER_TRANSPARENT) && !state.is(RNTags.Blocks.LASER_NO_SIGNAL)) return false;
		VoxelShape band = plane == Direction.Axis.Y ? BEAM_BAND_Y : plane == Direction.Axis.X ? BEAM_BAND_X : BEAM_BAND_Z;
		return Shapes.joinIsNotEmpty(state.getCollisionShape(level, pos), band, BooleanOp.AND);
	}
}
