package corundum.rubinated_nether.content.world.sinkhole;

public final class SinkholeShape {
	public static final double EDGE_JITTER = 0.08;

	private final double centerX;
	private final double centerZ;
	private final double openRadius;
	private final double bottomRadius;
	private final int depth;
	private final double phaseA;
	private final double phaseB;
	private final double phaseC;

	public SinkholeShape(double centerX, double centerZ, double openRadius, double bottomRatio, int depth, double phaseA, double phaseB, double phaseC) {
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.openRadius = openRadius;
		this.bottomRadius = openRadius * bottomRatio;
		this.depth = depth;
		this.phaseA = phaseA;
		this.phaseB = phaseB;
		this.phaseC = phaseC;
	}

	public int depth() {
		return depth;
	}

	public double centerX() {
		return centerX;
	}

	public double centerZ() {
		return centerZ;
	}

	public int reach() {
		return (int) Math.ceil(Math.max(openRadius, bottomRadius) * 1.13) + 1;
	}

	public double radius(int layer, double dx, double dz) {
		double t = depth <= 1 ? 1.0 : (double) layer / (depth - 1);
		double ease = 1.0 - (1.0 - t) * (1.0 - t);
		double base = openRadius + (bottomRadius - openRadius) * ease;
		double angle = Math.atan2(dz, dx);
		double edge = 0.05 * Math.sin(2.0 * angle + phaseA) + 0.03 * Math.sin(3.0 * angle + phaseB);
		double wobble = 0.04 * Math.sin(layer * 1.7 + phaseC) * Math.min(layer, 3) / 3.0;
		double radius = base * (1.0 + edge + wobble);
		if (depth > 1 && layer == depth - 1) {
			radius *= 0.93;
		}
		return radius;
	}

	public boolean contains(int layer, int blockX, int blockZ) {
		double dx = blockX + 0.5 - centerX;
		double dz = blockZ + 0.5 - centerZ;
		double radius = radius(layer, dx, dz);
		return dx * dx + dz * dz <= radius * radius;
	}

	public int[] extent(int layer) {
		int reach = reach();
		int baseX = (int) Math.floor(centerX);
		int baseZ = (int) Math.floor(centerZ);
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxZ = Integer.MIN_VALUE;
		for (int x = baseX - reach; x <= baseX + reach; x++) {
			for (int z = baseZ - reach; z <= baseZ + reach; z++) {
				if (contains(layer, x, z)) {
					minX = Math.min(minX, x);
					maxX = Math.max(maxX, x);
					minZ = Math.min(minZ, z);
					maxZ = Math.max(maxZ, z);
				}
			}
		}
		if (minX == Integer.MAX_VALUE) {
			return new int[]{0, 0};
		}
		return new int[]{maxX - minX + 1, maxZ - minZ + 1};
	}
}
