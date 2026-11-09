package corundum.rubinated_nether.utils;

import java.util.Arrays;

public final class HeadCone {
	public static final int RANGE = 15;
	public static final int SIZE = RANGE * 2 + 1;

	private static final double CELL_MARGIN = 0.5D;
	private static final double EPSILON = 1.0E-9D;

	public interface Probe {
		boolean blocks(int dx, int dz);
	}

	private final boolean[] visible = new boolean[SIZE * SIZE];
	private final byte[] probed = new byte[SIZE * SIZE];

	public void cast(double forwardX, double forwardZ, double halfAngle, boolean pierce, Probe probe) {
		Arrays.fill(visible, false);
		Arrays.fill(probed, (byte) 0);

		if (halfAngle <= 0.0D) {
			castBeam(forwardX, forwardZ, pierce, probe);
			return;
		}

		double length = Math.hypot(forwardX, forwardZ);
		if (length < EPSILON) return;
		double fx = forwardX / length;
		double fz = forwardZ / length;

		for (int cx = -RANGE; cx <= RANGE; cx++) {
			for (int cz = -RANGE; cz <= RANGE; cz++) {
				if (cx == 0 && cz == 0) continue;
				double dist = Math.hypot(cx, cz);
				double cos = Math.max(-1.0D, Math.min(1.0D, (cx * fx + cz * fz) / dist));
				if (Math.acos(cos) > halfAngle + CELL_MARGIN / dist) continue;
				if (!pierce && !clear(cx, cz, probe)) continue;
				visible[(cx + RANGE) * SIZE + (cz + RANGE)] = true;
			}
		}
	}

	private boolean clear(int targetX, int targetZ, Probe probe) {
		int stepX = Integer.signum(targetX);
		int stepZ = Integer.signum(targetZ);
		int ax = Math.abs(targetX);
		int az = Math.abs(targetZ);
		int ix = 0;
		int iz = 0;
		int cx = 0;
		int cz = 0;

		while (ix < ax || iz < az) {
			int side;
			if (ix >= ax) side = 1;
			else if (iz >= az) side = -1;
			else side = (2 * ix + 1) * az - (2 * iz + 1) * ax;

			if (side == 0) {
				if (!(cx + stepX == targetX && cz == targetZ) && blockedAt(cx + stepX, cz, probe)) return false;
				if (!(cx == targetX && cz + stepZ == targetZ) && blockedAt(cx, cz + stepZ, probe)) return false;
				cx += stepX;
				cz += stepZ;
				ix++;
				iz++;
			} else if (side < 0) {
				cx += stepX;
				ix++;
			} else {
				cz += stepZ;
				iz++;
			}

			if ((cx != targetX || cz != targetZ) && blockedAt(cx, cz, probe)) return false;
		}
		return true;
	}

	private boolean blockedAt(int cx, int cz, Probe probe) {
		if (Math.abs(cx) > RANGE || Math.abs(cz) > RANGE) return true;
		int index = (cx + RANGE) * SIZE + (cz + RANGE);
		if (probed[index] == 0) {
			probed[index] = (byte) (probe.blocks(cx, cz) ? 2 : 1);
		}
		return probed[index] == 2;
	}

	private void castBeam(double dirX, double dirZ, boolean pierce, Probe probe) {
		double scale = Math.max(Math.abs(dirX), Math.abs(dirZ));
		if (scale < EPSILON) return;

		double dx = dirX / scale;
		double dz = dirZ / scale;
		int stepX = dx > 0.0D ? 1 : -1;
		int stepZ = dz > 0.0D ? 1 : -1;
		double deltaX = Math.abs(dx) < EPSILON ? Double.POSITIVE_INFINITY : 1.0D / Math.abs(dx);
		double deltaZ = Math.abs(dz) < EPSILON ? Double.POSITIVE_INFINITY : 1.0D / Math.abs(dz);
		double nextX = deltaX * 0.5D;
		double nextZ = deltaZ * 0.5D;
		double limit = RANGE + 0.5D - EPSILON;

		int cx = 0;
		int cz = 0;
		while (true) {
			if (Math.abs(nextX - nextZ) < EPSILON) {
				if (nextX >= limit) return;
				cx += stepX;
				cz += stepZ;
				nextX += deltaX;
				nextZ += deltaZ;
			} else if (nextX < nextZ) {
				if (nextX >= limit) return;
				cx += stepX;
				nextX += deltaX;
			} else {
				if (nextZ >= limit) return;
				cz += stepZ;
				nextZ += deltaZ;
			}

			if (Math.abs(cx) > RANGE || Math.abs(cz) > RANGE) return;

			int index = (cx + RANGE) * SIZE + (cz + RANGE);
			visible[index] = true;
			if (pierce) continue;

			if (probed[index] == 0) {
				probed[index] = (byte) (probe.blocks(cx, cz) ? 2 : 1);
			}
			if (probed[index] == 2) return;
		}
	}

	public boolean isVisible(int dx, int dz) {
		if (Math.abs(dx) > RANGE || Math.abs(dz) > RANGE) return false;
		return visible[(dx + RANGE) * SIZE + (dz + RANGE)];
	}

	public boolean sees(int minDx, int maxDx, int minDz, int maxDz) {
		int fromX = Math.max(minDx, -RANGE);
		int toX = Math.min(maxDx, RANGE);
		int fromZ = Math.max(minDz, -RANGE);
		int toZ = Math.min(maxDz, RANGE);
		for (int x = fromX; x <= toX; x++) {
			for (int z = fromZ; z <= toZ; z++) {
				if (visible[(x + RANGE) * SIZE + (z + RANGE)]) return true;
			}
		}
		return false;
	}

	public int visibleCount() {
		int count = 0;
		for (boolean cell : visible) {
			if (cell) count++;
		}
		return count;
	}
}
