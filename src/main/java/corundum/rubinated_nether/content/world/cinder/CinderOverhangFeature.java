package corundum.rubinated_nether.content.world.cinder;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CinderOverhangFeature extends Feature<NoneFeatureConfiguration> {
	private static final int MIN_BLOCKS = 60;
	private static final int MIN_CONTACTS = 8;
	private static final int MAX_CLIMB = 10;
	private static final int LAVA_SCAN = 40;
	private static final float LAVA_FRACTION = 0.3F;
	private static final float DRY_ACCEPT = 0.3F;

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

		int distance = CinderFormations.findTerrain(level, origin, back, 8);
		if (distance < 0) {
			return false;
		}
		BlockPos wall = origin.relative(back, distance);
		int climbed = 0;
		BlockPos top = wall;
		while (CinderFormations.isTerrain(level, top.above())) {
			top = top.above();
			if (++climbed > MAX_CLIMB) {
				return false;
			}
		}
		if (!CinderFormations.isAir(level, top.above())
				|| !CinderFormations.isAir(level, top.relative(along))
				|| !CinderFormations.isAir(level, top.relative(along).above())) {
			return false;
		}
		BlockPos base = top.relative(along);

		int anchorHalf = 5 + random.nextInt(3);
		int length = 12 + random.nextInt(11);
		double roundness = 0.8 + random.nextDouble() * 0.7;

		int lanes = anchorHalf * 2 + 1;
		int[] laneY = new int[lanes];
		boolean any = false;
		for (int i = 0; i < lanes; i++) {
			laneY[i] = groundTop(level, base.relative(across, i - anchorHalf).relative(back), base.getY(), along);
			any |= laneY[i] != CinderGrid.NONE;
		}
		if (!any) {
			return false;
		}
		for (int i = 0; i < lanes; i++) {
			if (laneY[i] == CinderGrid.NONE) {
				laneY[i] = nearest(laneY, i);
			}
		}

		int drift = 0;
		Set<BlockPos> shape = new HashSet<>();
		for (int t = 0; t < length; t++) {
			double progress = (double) t / length;
			double span = Math.pow(1.0 - Math.pow(progress, 2.0), roundness);
			int half = (int) Math.round(anchorHalf * span);
			if (t > 1 && random.nextFloat() < 0.25F) {
				drift = Math.max(-2, Math.min(2, drift + (random.nextBoolean() ? 1 : -1)));
			}
			int shift = t == 0 ? 0 : drift;
			for (int u = -half; u <= half; u++) {
				boolean edge = u == -half || u == half;
				if (edge && t > 0 && half >= 2 && random.nextFloat() < 0.3F) {
					continue;
				}
				int lane = Math.max(-anchorHalf, Math.min(anchorHalf, u + shift));
				BlockPos pos = base.relative(along, t).relative(across, u + shift);
				BlockPos placed = new BlockPos(pos.getX(), laneY[lane + anchorHalf], pos.getZ());
				if (!inRegion(placed, origin)) {
					return false;
				}
				shape.add(placed);
			}
		}

		Set<BlockPos> candidates = CinderFormations.airOnly(level, shape);
		List<BlockPos> contacts = new ArrayList<>();
		for (BlockPos pos : candidates) {
			if (isFirstRow(pos, base, along) && CinderFormations.isTerrain(level, pos.relative(back))) {
				contacts.add(pos);
			}
		}
		if (contacts.size() < MIN_CONTACTS) {
			return false;
		}
		Set<BlockPos> connected = CinderFormations.reach(candidates, contacts);
		if (connected.size() < MIN_BLOCKS || connected.size() < shape.size() * 0.5) {
			return false;
		}

		int sampled = 0;
		int wet = 0;
		int index = 0;
		for (BlockPos pos : connected) {
			if (index++ % 3 != 0) {
				continue;
			}
			sampled++;
			if (overLava(level, pos)) {
				wet++;
			}
		}
		if (sampled > 0 && (float) wet / sampled < LAVA_FRACTION && random.nextFloat() > DRY_ACCEPT) {
			return false;
		}

		CinderFormations.place(level, connected);
		return true;
	}

	private static int groundTop(WorldGenLevel level, BlockPos column, int refY, Direction along) {
		BlockPos pos = new BlockPos(column.getX(), refY, column.getZ());
		int y = Integer.MIN_VALUE;
		if (CinderFormations.isTerrain(level, pos)) {
			y = refY;
			for (int i = 0; i < 3; i++) {
				if (CinderFormations.isTerrain(level, new BlockPos(pos.getX(), y + 1, pos.getZ()))) {
					y++;
				}
			}
			if (CinderFormations.isTerrain(level, new BlockPos(pos.getX(), y + 1, pos.getZ()))) {
				return CinderGrid.NONE;
			}
		} else {
			for (int d = 1; d <= 3; d++) {
				if (CinderFormations.isTerrain(level, new BlockPos(pos.getX(), refY - d, pos.getZ()))) {
					y = refY - d;
					break;
				}
			}
			if (y == Integer.MIN_VALUE) {
				return CinderGrid.NONE;
			}
		}
		BlockPos outside = new BlockPos(pos.getX() + along.getStepX(), y, pos.getZ() + along.getStepZ());
		if (!CinderFormations.isAir(level, outside) || !CinderFormations.isAir(level, outside.above())) {
			return CinderGrid.NONE;
		}
		return y;
	}

	private static int nearest(int[] lanes, int index) {
		for (int d = 1; d < lanes.length; d++) {
			if (index - d >= 0 && lanes[index - d] != CinderGrid.NONE) {
				return lanes[index - d];
			}
			if (index + d < lanes.length && lanes[index + d] != CinderGrid.NONE) {
				return lanes[index + d];
			}
		}
		return lanes[index];
	}

	private static boolean overLava(WorldGenLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos cursor = pos.mutable();
		for (int i = 0; i < LAVA_SCAN; i++) {
			cursor.move(Direction.DOWN);
			if (cursor.getY() < level.getMinBuildHeight()) {
				return false;
			}
			if (level.getFluidState(cursor).is(FluidTags.LAVA)) {
				return true;
			}
			if (!level.getBlockState(cursor).isAir()) {
				return false;
			}
		}
		return false;
	}

	private static boolean inRegion(BlockPos pos, BlockPos origin) {
		int minX = (origin.getX() >> 4 << 4) - 15;
		int minZ = (origin.getZ() >> 4 << 4) - 15;
		return pos.getX() >= minX && pos.getX() <= minX + 45 && pos.getZ() >= minZ && pos.getZ() <= minZ + 45;
	}

	private static boolean isFirstRow(BlockPos pos, BlockPos base, Direction along) {
		int offset = (pos.getX() - base.getX()) * along.getStepX() + (pos.getZ() - base.getZ()) * along.getStepZ();
		return offset == 0;
	}
}
