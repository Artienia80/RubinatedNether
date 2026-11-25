package corundum.rubinated_nether.content.world.chasm;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.world.FeatureBiomes;
import corundum.rubinated_nether.content.world.FeatureLocations;
import corundum.rubinated_nether.content.world.MagmaBlob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PumiceChasmFeature extends Feature<NoneFeatureConfiguration> {
	private static final int NO_SURFACE = Integer.MIN_VALUE;
	private static final int MIN_SURFACE = 40;
	private static final int MAX_SURFACE = 112;
	private static final int SCAN_UP = 4;
	private static final int SCAN_DOWN = 5;
	private static final int MAX_STEP = 3;
	private static final int ARM_MIN = 12;
	private static final int ARM_MAX = 18;
	private static final int MIN_POINTS = 16;
	private static final double MAX_CURVE = 0.18;
	private static final double VALIDATE_RADIUS = 3.2;
	private static final int MIN_DROP = 18;
	private static final int MAX_DROP = 22;
	private static final int MIN_LAVA = 3;
	private static final int MAX_LAVA = 5;
	private static final int MIN_LAVA_Y = 8;
	private static final int MIN_AIR_GAP = 8;
	private static final int MIN_OPENING = 30;
	private static final int CLEAR_ABOVE = 3;
	private static final float MARGIN_CHANCE = 0.35F;
	private static final int MAGMA_BLOBS = 6;
	private static final float RUBY_CHANCE = 0.25F;
	private static final Direction[] LAVA_SEAL = {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	public PumiceChasmFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource random = context.random();
		BlockPos origin = context.origin();

		int refY = origin.getY() - 1;
		Map<Long, Integer> surfaces = new HashMap<>();
		int originSurface = surface(level, origin.getX(), origin.getZ(), refY, surfaces);
		if (originSurface == NO_SURFACE || originSurface < MIN_SURFACE || originSurface > MAX_SURFACE) {
			return false;
		}
		double startX = origin.getX() + 0.5;
		double startZ = origin.getZ() + 0.5;
		if (!footprintValid(level, startX, startZ, refY, surfaces)) {
			return false;
		}

		double heading = random.nextDouble() * Mth.TWO_PI;
		List<double[]> forward = walk(level, random, origin, heading, ARM_MIN + random.nextInt(ARM_MAX - ARM_MIN + 1), refY, surfaces);
		List<double[]> backward = walk(level, random, origin, heading + Math.PI, ARM_MIN + random.nextInt(ARM_MAX - ARM_MIN + 1), refY, surfaces);

		List<double[]> path = new ArrayList<>();
		for (int i = backward.size() - 1; i >= 0; i--) {
			path.add(backward.get(i));
		}
		path.add(new double[]{startX, startZ});
		path.addAll(forward);
		if (path.size() < MIN_POINTS) {
			return false;
		}

		PumiceChasmShape shape = new PumiceChasmShape(path, random.nextLong(), 1.5 + random.nextDouble() * 0.5);

		List<long[]> openingColumns = new ArrayList<>();
		List<Long> marginColumns = new ArrayList<>();
		int surfaceSum = 0;
		int minSurface = Integer.MAX_VALUE;
		for (Map.Entry<Long, Double> entry : shape.reach().entrySet()) {
			long key = entry.getKey();
			int x = PumiceChasmShape.x(key);
			int z = PumiceChasmShape.z(key);
			int surface = surface(level, x, z, refY, surfaces);
			if (entry.getValue() <= 1.0) {
				if (surface == NO_SURFACE) {
					return false;
				}
				openingColumns.add(new long[]{key, surface});
				surfaceSum += surface;
				minSurface = Math.min(minSurface, surface);
			} else if (surface != NO_SURFACE) {
				marginColumns.add(key);
			}
		}
		if (openingColumns.size() < MIN_OPENING) {
			return false;
		}

		int meanSurface = Math.round((float) surfaceSum / openingColumns.size());
		int drop = MIN_DROP + random.nextInt(MAX_DROP - MIN_DROP + 1);
		int lavaDepth = MIN_LAVA + random.nextInt(MAX_LAVA - MIN_LAVA + 1);
		int lavaTop = meanSurface - drop;
		int lavaBottom = lavaTop - lavaDepth + 1;
		if (meanSurface < MIN_SURFACE || meanSurface > MAX_SURFACE || lavaBottom < MIN_LAVA_Y) {
			return false;
		}
		if (minSurface - PumiceChasmShape.MAX_THICKNESS - lavaTop < MIN_AIR_GAP) {
			return false;
		}

		List<BlockPos> capCells = new ArrayList<>();
		List<BlockPos> hollowCells = new ArrayList<>();
		Set<Long> hollow = new HashSet<>();
		for (long[] column : openingColumns) {
			int x = PumiceChasmShape.x(column[0]);
			int z = PumiceChasmShape.z(column[0]);
			double reach = shape.reach().get(column[0]);
			int surface = (int) column[1];
			int thickness = shape.thickness(x, z);
			for (int y = surface - thickness + 1; y <= surface; y++) {
				capCells.add(new BlockPos(x, y, z));
			}
			for (int y = lavaBottom; y <= surface - thickness; y++) {
				if (reach <= shape.hollowLimit(y, meanSurface, lavaBottom)) {
					BlockPos pos = new BlockPos(x, y, z);
					hollowCells.add(pos);
					hollow.add(pos.asLong());
				}
			}
		}
		for (BlockPos pos : capCells) {
			if (!canCarve(level.getBlockState(pos))) {
				return false;
			}
		}
		for (BlockPos pos : hollowCells) {
			if (!canCarve(level.getBlockState(pos))) {
				return false;
			}
		}

		for (BlockPos pos : capCells) {
			if (!FeatureBiomes.allowed(level, pos)) {
				return false;
			}
		}
		for (BlockPos pos : hollowCells) {
			if (!FeatureBiomes.allowed(level, pos)) {
				return false;
			}
		}

		BlockState pumice = RNBlocks.PUMICE.get().defaultBlockState();
		for (BlockPos pos : capCells) {
			level.setBlock(pos, pumice, 2);
		}
		for (long[] column : openingColumns) {
			clearAbove(level, PumiceChasmShape.x(column[0]), (int) column[1], PumiceChasmShape.z(column[0]));
		}
		for (long key : marginColumns) {
			if (random.nextFloat() < MARGIN_CHANCE) {
				int x = PumiceChasmShape.x(key);
				int z = PumiceChasmShape.z(key);
				int surface = surface(level, x, z, refY, surfaces);
				level.setBlock(new BlockPos(x, surface, z), pumice, 2);
				clearAbove(level, x, surface, z);
			}
		}

		BlockState air = Blocks.AIR.defaultBlockState();
		BlockState lava = Blocks.LAVA.defaultBlockState();
		List<BlockPos> lavaCells = new ArrayList<>();
		for (BlockPos pos : hollowCells) {
			if (pos.getY() <= lavaTop) {
				level.setBlock(pos, lava, 2);
				lavaCells.add(pos);
			} else {
				level.setBlock(pos, air, 2);
			}
		}

		BlockState seal = Blocks.NETHERRACK.defaultBlockState();
		for (BlockPos pos : lavaCells) {
			for (Direction direction : LAVA_SEAL) {
				BlockPos neighbor = pos.relative(direction);
				if (hollow.contains(neighbor.asLong())) {
					continue;
				}
				BlockState state = level.getBlockState(neighbor);
				if (isOpen(state) || (!state.getFluidState().isEmpty() && !state.getFluidState().is(FluidTags.LAVA))) {
					level.setBlock(neighbor, seal, 2);
				}
			}
		}

		Set<Long> cap = new HashSet<>();
		for (BlockPos pos : capCells) {
			cap.add(pos.asLong());
		}
		List<BlockPos> wallSeeds = new ArrayList<>();
		for (BlockPos pos : hollowCells) {
			if (pos.getY() > lavaTop) {
				wallSeeds.add(pos);
			}
		}
		int blobCeiling = minSurface - PumiceChasmShape.MAX_THICKNESS;
		if (!wallSeeds.isEmpty()) {
			for (int i = 0; i < MAGMA_BLOBS; i++) {
				BlockPos cell = wallSeeds.get(random.nextInt(wallSeeds.size()));
				Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
				BlockPos.MutableBlockPos cursor = cell.mutable();
				int steps = 0;
				while (hollow.contains(cursor.asLong()) && steps++ < 8) {
					cursor.move(direction);
				}
				MagmaBlob.place(level, random, cursor.immutable(), MagmaBlob.rollRadius(random), RUBY_CHANCE,
						pos -> pos.getY() < blobCeiling && !hollow.contains(pos.asLong()) && !cap.contains(pos.asLong()) && canHoldMagma(level.getBlockState(pos)));
			}
		}
		FeatureLocations.record(level, "pumice_chasm", new BlockPos(origin.getX(), originSurface, origin.getZ()));
		return true;
	}

	private static boolean canHoldMagma(BlockState state) {
		return isTerrain(state) || state.is(Blocks.MAGMA_BLOCK) || state.is(RNBlocks.MOLTEN_RUBY_ORE.get());
	}

	private static List<double[]> walk(WorldGenLevel level, RandomSource random, BlockPos origin, double heading, int length, int refY, Map<Long, Integer> surfaces) {
		List<double[]> points = new ArrayList<>();
		double x = origin.getX() + 0.5;
		double z = origin.getZ() + 0.5;
		double curve = (random.nextDouble() - 0.5) * 0.2;
		int previous = refY;
		for (int i = 0; i < length; i++) {
			curve = Mth.clamp(curve + (random.nextDouble() - 0.5) * 0.14, -MAX_CURVE, MAX_CURVE);
			heading += curve;
			x += Math.cos(heading);
			z += Math.sin(heading);
			if (!inRegion(x, z, origin)) {
				break;
			}
			int center = surface(level, Mth.floor(x), Mth.floor(z), refY, surfaces);
			if (center == NO_SURFACE || Math.abs(center - previous) > MAX_STEP) {
				break;
			}
			if (!footprintValid(level, x, z, refY, surfaces)) {
				break;
			}
			points.add(new double[]{x, z});
			previous = center;
		}
		return points;
	}

	private static boolean footprintValid(WorldGenLevel level, double x, double z, int refY, Map<Long, Integer> surfaces) {
		int radius = (int) Math.ceil(VALIDATE_RADIUS);
		int cx = Mth.floor(x);
		int cz = Mth.floor(z);
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				double offsetX = cx + dx + 0.5 - x;
				double offsetZ = cz + dz + 0.5 - z;
				if (offsetX * offsetX + offsetZ * offsetZ > VALIDATE_RADIUS * VALIDATE_RADIUS) {
					continue;
				}
				if (surface(level, cx + dx, cz + dz, refY, surfaces) == NO_SURFACE) {
					return false;
				}
			}
		}
		return true;
	}

	private static int surface(WorldGenLevel level, int x, int z, int refY, Map<Long, Integer> surfaces) {
		long key = PumiceChasmShape.key(x, z);
		Integer cached = surfaces.get(key);
		if (cached != null) {
			return cached;
		}
		int result = NO_SURFACE;
		int top = refY + SCAN_UP;
		int bottom = refY - SCAN_DOWN;
		if (top < level.getMaxBuildHeight() && bottom > level.getMinBuildHeight()) {
			BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, top, z);
			if (isOpen(level.getBlockState(cursor)) && isOpen(level.getBlockState(cursor.setY(top - 1)))) {
				for (int y = top - 2; y >= bottom; y--) {
					BlockState state = level.getBlockState(cursor.setY(y));
					if (isOpen(state)) {
						continue;
					}
					if (isTerrain(state)) {
						result = y;
					}
					break;
				}
			}
		}
		surfaces.put(key, result);
		return result;
	}

	private static void clearAbove(WorldGenLevel level, int x, int surface, int z) {
		BlockState air = Blocks.AIR.defaultBlockState();
		for (int i = 1; i <= CLEAR_ABOVE; i++) {
			BlockPos pos = new BlockPos(x, surface + i, z);
			BlockState state = level.getBlockState(pos);
			if (!state.isAir() && isOpen(state)) {
				level.setBlock(pos, air, 2);
			}
		}
	}

	private static boolean inRegion(double x, double z, BlockPos origin) {
		int minX = (origin.getX() >> 4 << 4) - 10;
		int minZ = (origin.getZ() >> 4 << 4) - 10;
		return x >= minX && x <= minX + 35 && z >= minZ && z <= minZ + 35;
	}

	private static boolean isOpen(BlockState state) {
		return state.isAir() || (state.canBeReplaced() && state.getFluidState().isEmpty());
	}

	private static boolean isTerrain(BlockState state) {
		return state.is(BlockTags.BASE_STONE_NETHER)
				|| state.is(BlockTags.SOUL_SPEED_BLOCKS)
				|| state.is(BlockTags.NYLIUM)
				|| state.is(Blocks.GRAVEL)
				|| state.is(RNBlocks.CINDER.get());
	}

	private static boolean canCarve(BlockState state) {
		return isOpen(state)
				|| state.getFluidState().is(FluidTags.LAVA)
				|| isTerrain(state)
				|| state.is(Blocks.MAGMA_BLOCK)
				|| state.is(Blocks.GLOWSTONE)
				|| state.is(Blocks.NETHER_QUARTZ_ORE)
				|| state.is(Blocks.NETHER_GOLD_ORE)
				|| state.is(RNBlocks.NETHER_RUBY_ORE.get())
				|| state.is(RNBlocks.MOLTEN_RUBY_ORE.get());
	}
}
