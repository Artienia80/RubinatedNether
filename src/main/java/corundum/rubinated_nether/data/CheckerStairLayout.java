package corundum.rubinated_nether.data;

import java.util.ArrayList;
import java.util.List;

final class CheckerStairLayout {
	record Face(String direction, int u1, int v1, int u2, int v2, boolean cull, boolean flipped) {
	}

	record Part(int[] from, int[] to, List<Face> faces) {
	}

	private static final String[] DIRECTIONS = {"down", "up", "north", "south", "west", "east"};

	private static final int[][] NORMALS = {
			{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}
	};

	private static final int[][][] VERTICES = {
			{{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}},
			{{0, 1, 0}, {0, 1, 1}, {1, 1, 1}, {1, 1, 0}},
			{{1, 1, 0}, {1, 0, 0}, {0, 0, 0}, {0, 1, 0}},
			{{0, 1, 1}, {0, 0, 1}, {1, 0, 1}, {1, 1, 1}},
			{{0, 1, 0}, {0, 0, 0}, {0, 0, 1}, {0, 1, 1}},
			{{1, 1, 1}, {1, 0, 1}, {1, 0, 0}, {1, 1, 0}}
	};

	private static final int[][] BASE = {
			{0, 0, 0}, {16, 8, 16},
			{0, 0, 0, 16, 16, 1}, {1, 0, 0, 16, 16, 0}, {2, 0, 8, 16, 16, 1},
			{3, 0, 8, 16, 16, 1}, {4, 0, 8, 16, 16, 1}, {5, 0, 8, 16, 16, 1}
	};

	private static final int[][] STEP = {
			{8, 8, 0}, {16, 16, 16},
			{1, 8, 0, 16, 16, 1}, {2, 0, 0, 8, 8, 1}, {3, 8, 0, 16, 8, 1},
			{4, 0, 0, 16, 8, 0}, {5, 0, 0, 16, 8, 1}
	};

	private static final int[][] INNER_CORNER = {
			{0, 8, 8}, {8, 16, 16},
			{1, 0, 8, 8, 16, 1}, {2, 8, 0, 16, 8, 0}, {3, 0, 0, 8, 8, 1}, {4, 8, 0, 16, 8, 1}
	};

	private static final int[][] OUTER_CORNER = {
			{8, 8, 8}, {16, 16, 16},
			{1, 8, 8, 16, 16, 1}, {2, 0, 0, 8, 8, 0}, {3, 8, 0, 16, 8, 1},
			{4, 8, 0, 16, 8, 0}, {5, 0, 0, 8, 8, 1}
	};

	private CheckerStairLayout() {
	}

	static List<Part> parts(String shape, int xRot, int yRot, boolean coarse, boolean blockFlipped) {
		int[][][] templates = switch (shape) {
			case "inner" -> new int[][][]{BASE, STEP, INNER_CORNER};
			case "outer" -> new int[][][]{BASE, OUTER_CORNER};
			default -> new int[][][]{BASE, STEP};
		};
		int cells = coarse ? 2 : 4;
		List<Part> parts = new ArrayList<>();
		for (int[][] template : templates) {
			List<Face> faces = new ArrayList<>();
			for (int i = 2; i < template.length; i++) {
				int[] face = template[i];
				boolean swapped = swapped(template[0], template[1], face, xRot, yRot, cells);
				faces.add(new Face(DIRECTIONS[face[0]], face[1], face[2], face[3], face[4], face[5] == 1, swapped ^ blockFlipped));
			}
			parts.add(new Part(template[0], template[1], faces));
		}
		return parts;
	}

	private static boolean swapped(int[] from, int[] to, int[] face, int xRot, int yRot, int cells) {
		int direction = face[0];
		double[] origin = corner(from, to, VERTICES[direction][0]);
		double[] down = corner(from, to, VERTICES[direction][1]);
		double[] across = corner(from, to, VERTICES[direction][3]);
		double s = 0.5 / Math.abs(face[3] - face[1]);
		double t = 0.5 / Math.abs(face[4] - face[2]);
		double[] point = new double[3];
		for (int i = 0; i < 3; i++) {
			point[i] = origin[i] + s * (across[i] - origin[i]) + t * (down[i] - origin[i]);
		}
		double[] world = rotate(point, xRot, yRot, 8);
		double[] normal = rotate(new double[]{NORMALS[direction][0], NORMALS[direction][1], NORMALS[direction][2]}, xRot, yRot, 0);
		int parity = 0;
		for (int i = 0; i < 3; i++) {
			parity += (int) Math.floor(world[i] / 16.0 * cells - 0.001 * normal[i]);
		}
		boolean expectedWhite = Math.floorMod(parity, 2) == 1;
		double texelU = face[1] + (face[3] > face[1] ? 0.5 : -0.5);
		double texelV = face[2] + (face[4] > face[2] ? 0.5 : -0.5);
		int texel = (int) Math.floor(texelU / 16.0 * cells) + (int) Math.floor(texelV / 16.0 * cells);
		boolean textureWhite = Math.floorMod(texel, 2) == 0;
		return textureWhite != expectedWhite;
	}

	private static double[] corner(int[] from, int[] to, int[] vertex) {
		return new double[]{
				vertex[0] == 0 ? from[0] : to[0],
				vertex[1] == 0 ? from[1] : to[1],
				vertex[2] == 0 ? from[2] : to[2]
		};
	}

	private static double[] rotate(double[] vector, int xRot, int yRot, int centre) {
		double x = vector[0] - centre;
		double y = vector[1] - centre;
		double z = vector[2] - centre;
		if (xRot == 180) {
			y = -y;
			z = -z;
		}
		for (int i = 0; i < yRot / 90; i++) {
			double nx = -z;
			z = x;
			x = nx;
		}
		return new double[]{x + centre, y + centre, z + centre};
	}
}
