package corundum.rubinated_nether.content.world.cinder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

final class CinderGrid {
	static final int NONE = Integer.MIN_VALUE;

	private static final int[][] NEIGHBORS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	final int length;
	final int half;
	private final int[][] height;
	private final int[][] extra;

	CinderGrid(int length, int half) {
		this.length = length;
		this.half = half;
		this.height = new int[length][half * 2 + 1];
		this.extra = new int[length][half * 2 + 1];
		for (int[] row : height) {
			java.util.Arrays.fill(row, NONE);
		}
	}

	void set(int t, int u, int h, int below) {
		if (t < 0 || t >= length || Math.abs(u) > half) {
			return;
		}
		height[t][u + half] = h;
		extra[t][u + half] = below;
	}

	boolean has(int t, int u) {
		return t >= 0 && t < length && Math.abs(u) <= half && height[t][u + half] != NONE;
	}

	int height(int t, int u) {
		return height[t][u + half];
	}

	List<BlockPos> blocks(BlockPos base, Direction along, Direction across) {
		List<BlockPos> result = new ArrayList<>();
		for (int t = 0; t < length; t++) {
			for (int u = -half; u <= half; u++) {
				if (!has(t, u)) {
					continue;
				}
				int h = height(t, u);
				int bottom = h - extra[t][u + half];
				int top = h;
				for (int[] n : NEIGHBORS) {
					int nt = t + n[0];
					int nu = u + n[1];
					if (has(nt, nu)) {
						int nh = height(nt, nu);
						if (nh > h) {
							top = Math.max(top, Math.min(nh, h + 1));
						}
					}
				}
				int x = base.getX() + along.getStepX() * t + across.getStepX() * u;
				int z = base.getZ() + along.getStepZ() * t + across.getStepZ() * u;
				for (int y = bottom; y <= top; y++) {
					result.add(new BlockPos(x, y, z));
				}
			}
		}
		return result;
	}
}
