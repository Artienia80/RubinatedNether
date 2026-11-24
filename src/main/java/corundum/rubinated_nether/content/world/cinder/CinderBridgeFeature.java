package corundum.rubinated_nether.content.world.cinder;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CinderBridgeFeature extends Feature<NoneFeatureConfiguration> {
	private static final int MIN_GAP = 8;
	private static final int MAX_GAP = 25;
	private static final int MAX_HEIGHT_DIFFERENCE = 1;
	private static final int MAX_HALF_WIDTH = 6;

	public CinderBridgeFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource random = context.random();
		BlockPos origin = context.origin();
		if (!CinderFormations.isAir(level, origin)) {
			return false;
		}
		Direction along = Direction.Plane.HORIZONTAL.getRandomDirection(random);
		Direction back = along.getOpposite();
		Direction across = along.getClockWise();

		int a = CinderFormations.findTerrain(level, origin, back, CinderFormations.MAX_REACH - 1);
		if (a < 0) {
			return false;
		}
		int b = CinderFormations.findTerrain(level, origin, along, CinderFormations.MAX_REACH - 1);
		if (b < 0) {
			return false;
		}
		int gap = a + b - 1;
		if (gap < MIN_GAP || gap > MAX_GAP) {
			return false;
		}
		BlockPos wallA = origin.relative(back, a);
		BlockPos wallB = origin.relative(along, b);
		List<Integer> lipsA = lips(level, wallA, along, origin.getY());
		List<Integer> lipsB = lips(level, wallB, back, origin.getY());
		int bestDifference = Integer.MAX_VALUE;
		List<int[]> pairs = new ArrayList<>();
		for (int candidateA : lipsA) {
			for (int candidateB : lipsB) {
				int difference = Math.abs(candidateA - candidateB);
				if (difference > MAX_HEIGHT_DIFFERENCE || difference > bestDifference) {
					continue;
				}
				if (difference < bestDifference) {
					bestDifference = difference;
					pairs.clear();
				}
				pairs.add(new int[]{candidateA, candidateB});
			}
		}
		if (pairs.isEmpty()) {
			return false;
		}
		int[] chosen = pairs.get(random.nextInt(pairs.size()));
		int yA = chosen[0];
		int yB = chosen[1];

		int[] heights = buildHeights(random, gap, yA, yB);
		if (heights == null) {
			return false;
		}

		double endHalf = 2.4 + random.nextDouble() * 1.0;
		double pinch = Math.max(0.3, (4.0 / gap) * (0.9 + random.nextDouble() * 0.3));
		double amplitude = random.nextDouble() * 1.4;
		double phase = random.nextDouble() * Math.PI * 2;
		double frequency = 1.5 + random.nextDouble() * 2.0;

		BlockPos base = new BlockPos(wallA.getX(), yA, wallA.getZ()).relative(along);
		CinderGrid grid = new CinderGrid(gap, MAX_HALF_WIDTH);
		int[] centers = new int[gap];
		int[] halves = new int[gap];
		for (int t = 0; t < gap; t++) {
			double progress = (double) t / (gap - 1);
			centers[t] = (int) Math.round(amplitude * Math.sin(Math.PI * progress) * Math.sin(phase + progress * frequency));
			double field = Math.exp(-pinch * t) + Math.exp(-pinch * (gap - 1 - t));
			halves[t] = Math.max(0, (int) Math.floor(endHalf * field + 0.3 + (random.nextDouble() - 0.5) * 0.5));
			halves[t] = Math.min(halves[t], 4);
		}
		halves[0] = Math.max(halves[0], 2);
		halves[gap - 1] = Math.max(halves[gap - 1], 2);

		for (int t = 0; t < gap; t++) {
			int previousCenter = t == 0 ? centers[t] : centers[t - 1];
			int minU = Math.min(previousCenter, centers[t]) - halves[t];
			int maxU = Math.max(previousCenter, centers[t]) + halves[t];
			int fromEnd = Math.min(t, gap - 1 - t);
			for (int u = minU; u <= maxU; u++) {
				boolean edge = (u == minU || u == maxU) && halves[t] >= 2;
				if (edge && fromEnd > 0 && random.nextFloat() < 0.3F) {
					continue;
				}
				grid.set(t, u, heights[t], 0);
			}
		}

		List<BlockPos> raw = grid.blocks(base, along, across);
		Set<BlockPos> candidates = CinderFormations.airOnly(level, raw);
		for (int t = 0; t < gap; t++) {
			BlockPos spine = offset(base, along, across, t, centers[t], heights[t]);
			if (!candidates.contains(spine)) {
				return false;
			}
			if (t > 0 && t < gap - 1
					&& (!CinderFormations.isAir(level, spine.above()) || !CinderFormations.isAir(level, spine.above(2)))) {
				return false;
			}
		}

		List<BlockPos> startContacts = new ArrayList<>();
		List<BlockPos> endContacts = new ArrayList<>();
		for (BlockPos pos : candidates) {
			int t = (pos.getX() - base.getX()) * along.getStepX() + (pos.getZ() - base.getZ()) * along.getStepZ();
			if (t == 0 && CinderFormations.isTerrain(level, pos.relative(back))) {
				startContacts.add(pos);
			} else if (t == gap - 1 && CinderFormations.isTerrain(level, pos.relative(along))) {
				endContacts.add(pos);
			}
		}
		if (startContacts.isEmpty() || endContacts.isEmpty()) {
			return false;
		}
		Set<BlockPos> connected = CinderFormations.reach(candidates, startContacts);
		boolean reachesEnd = false;
		for (BlockPos contact : endContacts) {
			if (connected.contains(contact)) {
				reachesEnd = true;
				break;
			}
		}
		if (!reachesEnd) {
			return false;
		}
		CinderFormations.place(level, connected, random);
		return true;
	}

	private static BlockPos offset(BlockPos base, Direction along, Direction across, int t, int u, int y) {
		return new BlockPos(
				base.getX() + along.getStepX() * t + across.getStepX() * u,
				y,
				base.getZ() + along.getStepZ() * t + across.getStepZ() * u
		);
	}

	private static List<Integer> lips(WorldGenLevel level, BlockPos wall, Direction outward, int centerY) {
		List<Integer> lips = new ArrayList<>();
		for (int dy = 4; dy >= -4; dy--) {
			BlockPos solid = new BlockPos(wall.getX(), centerY + dy, wall.getZ());
			BlockPos outside = solid.relative(outward);
			if (CinderFormations.isTerrain(level, solid)
					&& !CinderFormations.isTerrain(level, solid.above())
					&& CinderFormations.isAir(level, outside)
					&& CinderFormations.isAir(level, outside.above())
					&& CinderFormations.isAir(level, outside.above(2))) {
				lips.add(solid.getY());
			}
		}
		return lips;
	}

	private static int[] buildHeights(RandomSource random, int gap, int yA, int yB) {
		double sagMax = 0.0;
		int[] heights = new int[gap];
		for (int t = 0; t < gap; t++) {
			double progress = (double) t / (gap - 1);
			double sag = sagMax * Math.sin(Math.PI * progress);
			heights[t] = (int) Math.round(yA + (yB - yA) * progress - sag);
		}
		heights[0] = yA;
		heights[gap - 1] = yB;
		for (int t = 1; t < gap - 1; t++) {
			heights[t] = Math.max(heights[t - 1] - 1, Math.min(heights[t - 1] + 1, heights[t]));
		}
		for (int t = gap - 2; t >= 1; t--) {
			heights[t] = Math.max(heights[t + 1] - 1, Math.min(heights[t + 1] + 1, heights[t]));
		}
		for (int t = 1; t < gap; t++) {
			if (Math.abs(heights[t] - heights[t - 1]) > 1) {
				return null;
			}
		}
		return heights;
	}
}
