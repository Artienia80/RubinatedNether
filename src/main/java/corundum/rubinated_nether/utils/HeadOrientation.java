package corundum.rubinated_nether.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class HeadOrientation {
	public static final int STEPS = 8;

	private static final double R = Math.sqrt(0.5D);
	private static final double[] COS = {1.0D, R, 0.0D, -R, -1.0D, -R, 0.0D, R};
	private static final double[] SIN = {0.0D, R, 1.0D, R, 0.0D, -R, -1.0D, -R};
	private static final double MIN_PROJECTION_SQR = 0.01D;

	private static final Vec3[][] LOOKS = new Vec3[6][STEPS];
	private static final Direction[][][] OUTPUTS = new Direction[6][STEPS][];

	static {
		for (Direction normal : Direction.values()) {
			Vec3 a = axisA(normal);
			Vec3 b = axisB(normal);
			for (int step = 0; step < STEPS; step++) {
				Vec3 look = new Vec3(
						a.x * COS[step] + b.x * SIN[step],
						a.y * COS[step] + b.y * SIN[step],
						a.z * COS[step] + b.z * SIN[step]
				);
				LOOKS[normal.get3DDataValue()][step] = look;
				OUTPUTS[normal.get3DDataValue()][step] = directionsOf(look);
			}
		}
	}

	private HeadOrientation() {
	}

	public static Vec3 axisA(Direction normal) {
		return normal.getAxis().isVertical() ? new Vec3(0.0D, 0.0D, 1.0D) : new Vec3(0.0D, 1.0D, 0.0D);
	}

	public static Vec3 axisB(Direction normal) {
		if (normal.getAxis().isVertical()) return new Vec3(-1.0D, 0.0D, 0.0D);
		Vec3 a = axisA(normal);
		double nx = -normal.getStepX();
		double ny = -normal.getStepY();
		double nz = -normal.getStepZ();
		return new Vec3(ny * a.z - nz * a.y, nz * a.x - nx * a.z, nx * a.y - ny * a.x);
	}

	public static Vec3 look(Direction normal, int step) {
		return LOOKS[normal.get3DDataValue()][step & (STEPS - 1)];
	}

	public static Direction[] outputs(Direction normal, int step) {
		return OUTPUTS[normal.get3DDataValue()][step & (STEPS - 1)];
	}

	public static int stepFor(Direction normal, double x, double y, double z) {
		Vec3 a = axisA(normal);
		Vec3 b = axisB(normal);
		double wa = x * a.x + y * a.y + z * a.z;
		double wb = x * b.x + y * b.y + z * b.z;
		if (wa * wa + wb * wb < MIN_PROJECTION_SQR) return 0;
		double angle = Math.atan2(wb, wa);
		return Math.floorMod((int) Math.floor(angle / (Math.PI / 4.0D) + 0.5D), STEPS);
	}

	public static Direction.Axis uAxis(Direction.Axis normalAxis) {
		return normalAxis == Direction.Axis.Z ? Direction.Axis.X : normalAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
	}

	public static Direction.Axis vAxis(Direction.Axis normalAxis) {
		return normalAxis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
	}

	private static Direction[] directionsOf(Vec3 look) {
		Direction[] found = new Direction[3];
		int count = 0;
		if (look.x > 0.1D) found[count++] = Direction.EAST;
		else if (look.x < -0.1D) found[count++] = Direction.WEST;
		if (look.y > 0.1D) found[count++] = Direction.UP;
		else if (look.y < -0.1D) found[count++] = Direction.DOWN;
		if (look.z > 0.1D) found[count++] = Direction.SOUTH;
		else if (look.z < -0.1D) found[count++] = Direction.NORTH;
		Direction[] result = new Direction[count];
		System.arraycopy(found, 0, result, 0, count);
		return result;
	}
}
