package corundum.rubinated_nether.content.world.chasm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PumiceChasmShape {
	static final double MAX_HALF_WIDTH = 2.5;
	static final double MIN_HALF_WIDTH = 0.9;
	static final double MARGIN_REACH = 1.4;
	static final double TAPER = 0.3;
	static final int MIN_THICKNESS = 1;
	static final int MAX_THICKNESS = 3;
	private static final double END_FADE = 5.0;
	private static final double END_WIDTH = 0.55;

	private final long seed;
	private final double phaseA;
	private final double phaseB;
	private final Map<Long, Double> reach = new HashMap<>();

	PumiceChasmShape(List<double[]> path, long seed, double baseHalfWidth) {
		this.seed = seed;
		this.phaseA = unit(0, 0, 1) * Math.PI * 2.0;
		this.phaseB = unit(0, 0, 2) * Math.PI * 2.0;

		int size = path.size();
		for (int i = 0; i < size; i++) {
			double[] point = path.get(i);
			int edge = Math.min(i, size - 1 - i);
			double fade = Math.min(1.0, edge / END_FADE);
			double ends = END_WIDTH + (1.0 - END_WIDTH) * fade;
			double vary = 1.0 + 0.15 * Math.sin(i * 0.35 + phaseA) + 0.07 * Math.sin(i * 0.9 + phaseB);
			double width = Math.max(MIN_HALF_WIDTH, Math.min(MAX_HALF_WIDTH, baseHalfWidth * ends * vary));
			int radius = (int) Math.ceil(width * MARGIN_REACH * 1.2) + 1;
			int cx = (int) Math.floor(point[0]);
			int cz = (int) Math.floor(point[1]);
			for (int x = cx - radius; x <= cx + radius; x++) {
				for (int z = cz - radius; z <= cz + radius; z++) {
					double dx = x + 0.5 - point[0];
					double dz = z + 0.5 - point[1];
					double ratio = Math.sqrt(dx * dx + dz * dz) / width;
					reach.merge(key(x, z), ratio, Math::min);
				}
			}
		}
		reach.replaceAll((key, ratio) -> ratio / jitter(x(key), z(key)));
		reach.values().removeIf(ratio -> ratio > MARGIN_REACH);
	}

	Map<Long, Double> reach() {
		return Collections.unmodifiableMap(reach);
	}

	int thickness(int x, int z) {
		double value = 0.5
				+ 0.32 * Math.sin(x * 0.55 + z * 0.35 + phaseA)
				+ 0.22 * Math.sin(z * 0.8 - x * 0.3 + phaseB)
				+ (unit(x, z, 4) - 0.5) * 0.3;
		value = Math.max(0.0, Math.min(0.999, value));
		return MIN_THICKNESS + (int) (value * (MAX_THICKNESS - MIN_THICKNESS + 1));
	}

	double hollowLimit(int y, int surface, int bottom) {
		double span = Math.max(1, surface - bottom);
		double depth = Math.max(0.0, Math.min(1.0, (surface - y) / span));
		double wobble = (unit(y, 0, 5) - 0.5) * 0.12;
		return Math.min(1.0, 1.0 - TAPER * depth + wobble);
	}

	private double jitter(int x, int z) {
		return 1.0 + 0.09 * Math.sin(x * 0.9 + z * 0.6 + phaseA) + 0.16 * (unit(x, z, 3) - 0.5);
	}

	private double unit(long a, long b, long salt) {
		long h = seed ^ (a * 0x9E3779B97F4A7C15L) ^ (b * 0xC2B2AE3D27D4EB4FL) ^ (salt * 0x165667B19E3779F9L);
		h ^= h >>> 30;
		h *= 0xBF58476D1CE4E5B9L;
		h ^= h >>> 27;
		h *= 0x94D049BB133111EBL;
		h ^= h >>> 31;
		return (h >>> 11) * 0x1.0p-53;
	}

	static long key(int x, int z) {
		return ((long) x << 32) | (z & 0xFFFFFFFFL);
	}

	static int x(long key) {
		return (int) (key >> 32);
	}

	static int z(long key) {
		return (int) key;
	}
}
