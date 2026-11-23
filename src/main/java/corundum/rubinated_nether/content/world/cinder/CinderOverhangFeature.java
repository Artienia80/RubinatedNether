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

public class CinderOverhangFeature extends Feature<NoneFeatureConfiguration> {
	private static final int MIN_BLOCKS = 16;
	private static final int MAX_HALF_WIDTH = 6;

	public CinderOverhangFeature(Codec<NoneFeatureConfiguration> codec) {
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

		int distance = CinderFormations.findTerrain(level, origin, back, 6);
		if (distance < 0) {
			return false;
		}
		BlockPos wall = origin.relative(back, distance);
		List<Integer> lips = new ArrayList<>();
		for (int dy = 3; dy >= -3; dy--) {
			BlockPos solid = wall.above(dy);
			BlockPos outside = solid.relative(along);
			if (CinderFormations.isTerrain(level, solid)
					&& CinderFormations.isAir(level, outside)
					&& CinderFormations.isAir(level, outside.above())) {
				lips.add(solid.getY());
			}
		}
		if (lips.isEmpty()) {
			return false;
		}
		int lipY = lips.get(random.nextInt(lips.size()));
		BlockPos base = new BlockPos(wall.getX(), lipY, wall.getZ()).relative(along);

		int length = 6 + random.nextInt(8);
		int startHalf = random.nextInt(2);
		int endHalf = 2 + random.nextInt(3);
		double widen = 0.7 + random.nextDouble() * 0.9;
		int droop = random.nextInt(4);
		double droopCurve = 1.0 + random.nextDouble() * 0.6;

		CinderGrid grid = new CinderGrid(length, MAX_HALF_WIDTH);
		int center = 0;
		int previousCenter = 0;
		int previousHeight = lipY;
		for (int t = 0; t < length; t++) {
			double progress = length == 1 ? 0 : (double) t / (length - 1);
			int half = (int) Math.round(startHalf + (endHalf - startHalf) * Math.pow(progress, widen));
			if (t > 2 && random.nextFloat() < 0.3F) {
				center = Math.max(-2, Math.min(2, center + (random.nextBoolean() ? 1 : -1)));
			}
			int rawHeight = lipY - (int) Math.round(droop * Math.pow(progress, droopCurve));
			int rowHeight = Math.max(previousHeight - 1, Math.min(previousHeight + 1, rawHeight));
			previousHeight = rowHeight;
			int minU = Math.min(previousCenter, center) - half;
			int maxU = Math.max(previousCenter, center) + half;
			for (int u = minU; u <= maxU; u++) {
				boolean edge = u == minU || u == maxU;
				if (edge && half >= 2 && t > 0 && random.nextFloat() < 0.3F) {
					continue;
				}
				int h = rowHeight;
				if (edge && half >= 2 && random.nextFloat() < 0.4F) {
					h--;
				}
				int below = t <= 2 && !edge ? 1 : 0;
				grid.set(t, u, h, below);
			}
			previousCenter = center;
		}

		List<BlockPos> raw = grid.blocks(base, along, across);
		Set<BlockPos> candidates = CinderFormations.airOnly(level, raw);
		List<BlockPos> contacts = new ArrayList<>();
		for (BlockPos pos : candidates) {
			if (CinderFormations.isTerrain(level, pos.relative(back))
					&& isFirstRow(pos, base, along)) {
				contacts.add(pos);
			}
		}
		if (contacts.isEmpty()) {
			return false;
		}
		Set<BlockPos> connected = CinderFormations.reach(candidates, contacts);
		if (connected.size() < MIN_BLOCKS || connected.size() < raw.size() * 0.7) {
			return false;
		}
		CinderFormations.place(level, connected);
		return true;
	}

	private static boolean isFirstRow(BlockPos pos, BlockPos base, Direction along) {
		int offset = (pos.getX() - base.getX()) * along.getStepX() + (pos.getZ() - base.getZ()) * along.getStepZ();
		return offset == 0;
	}
}
