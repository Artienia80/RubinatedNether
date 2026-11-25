package corundum.rubinated_nether.content.world.sinkhole;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.world.FeatureBiomes;
import corundum.rubinated_nether.content.world.FeatureLocations;
import corundum.rubinated_nether.content.world.MagmaBlob;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.List;

public class PumiceSinkholeFeature extends Feature<PumiceSinkholeConfiguration> {
	private static final int SURFACE_SCAN = 48;
	private static final int SHAPE_ATTEMPTS = 12;
	private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	public PumiceSinkholeFeature(Codec<PumiceSinkholeConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<PumiceSinkholeConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource random = context.random();
		PumiceSinkholeConfiguration config = context.config();
		BlockPos origin = context.origin();

		int surfaceY = findSurface(level, origin);
		if (surfaceY == Integer.MIN_VALUE) {
			return false;
		}

		int depth = Mth.randomBetweenInclusive(random, config.minDepth(), config.maxDepth());
		if (surfaceY - depth - 3 <= level.getMinBuildHeight()) {
			return false;
		}

		SinkholeShape shape = rollShape(random, config, origin, depth);
		if (shape == null) {
			return false;
		}

		int reach = shape.reach();
		int baseX = Mth.floor(shape.centerX());
		int baseZ = Mth.floor(shape.centerZ());

		LongOpenHashSet hollow = new LongOpenHashSet();
		LongOpenHashSet cap = new LongOpenHashSet();
		List<BlockPos> hollowCells = new ArrayList<>();
		List<BlockPos> bottomCells = new ArrayList<>();
		List<BlockPos> capCells = new ArrayList<>();

		for (int x = baseX - reach; x <= baseX + reach; x++) {
			for (int z = baseZ - reach; z <= baseZ + reach; z++) {
				if (shape.contains(0, x, z)) {
					BlockPos pos = new BlockPos(x, surfaceY, z);
					cap.add(pos.asLong());
					capCells.add(pos);
				}
				for (int layer = 0; layer < depth; layer++) {
					if (shape.contains(layer, x, z)) {
						BlockPos pos = new BlockPos(x, surfaceY - 1 - layer, z);
						hollow.add(pos.asLong());
						hollowCells.add(pos);
						if (layer == depth - 1) {
							bottomCells.add(pos);
						}
					}
				}
			}
		}
		if (capCells.isEmpty() || bottomCells.isEmpty()) {
			return false;
		}

		for (BlockPos pos : capCells) {
			if (!isCarvable(level.getBlockState(pos)) || !isOpenAbove(level.getBlockState(pos.above()))) {
				return false;
			}
		}
		for (BlockPos pos : hollowCells) {
			if (!isCarvable(level.getBlockState(pos))) {
				return false;
			}
		}
		if (!isEnclosed(level, capCells, hollowCells, hollow, cap)) {
			return false;
		}
		for (BlockPos pos : capCells) {
			if (!FeatureBiomes.allowed(level, pos)) {
				return false;
			}
		}
		for (BlockPos pos : bottomCells) {
			if (!FeatureBiomes.allowed(level, pos)) {
				return false;
			}
		}

		int lavaLayers = 1 + random.nextInt(2);
		int bottomY = surfaceY - depth;
		for (BlockPos pos : capCells) {
			level.setBlock(pos, RNBlocks.PUMICE.get().defaultBlockState(), 2);
		}
		for (BlockPos pos : hollowCells) {
			boolean lava = pos.getY() < bottomY + lavaLayers;
			level.setBlock(pos, lava ? Blocks.LAVA.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
		}

		int wallAttempts = Mth.ceil(config.magmaPockets() * 0.6F);
		int floorAttempts = config.magmaPockets() - wallAttempts;
		for (int i = 0; i < wallAttempts; i++) {
			BlockPos cell = hollowCells.get(random.nextInt(hollowCells.size()));
			Direction direction = HORIZONTAL[random.nextInt(HORIZONTAL.length)];
			BlockPos.MutableBlockPos cursor = cell.mutable();
			int steps = 0;
			while (hollow.contains(cursor.asLong()) && steps++ < reach * 2) {
				cursor.move(direction);
			}
			placeBlob(level, random, cursor.immutable(), hollow, cap, surfaceY, config.rubyChance());
		}
		for (int i = 0; i < floorAttempts; i++) {
			BlockPos cell = bottomCells.get(random.nextInt(bottomCells.size()));
			BlockPos seed = cell.below(1 + random.nextInt(2));
			placeBlob(level, random, seed, hollow, cap, surfaceY, config.rubyChance());
		}
		FeatureLocations.record(level, "pumice_sinkhole", new BlockPos(baseX, surfaceY, baseZ));
		return true;
	}

	private static SinkholeShape rollShape(RandomSource random, PumiceSinkholeConfiguration config, BlockPos origin, int depth) {
		double low = config.minOpeningWidth() / 2.0 / (1.0 - SinkholeShape.EDGE_JITTER);
		double high = config.maxOpeningWidth() / 2.0 / (1.0 + SinkholeShape.EDGE_JITTER);
		for (int attempt = 0; attempt < SHAPE_ATTEMPTS; attempt++) {
			double openRadius = Mth.lerp(random.nextDouble(), low, Math.max(low, high));
			double centerX = origin.getX() + 0.5 + (random.nextBoolean() ? 0.0 : 0.5);
			double centerZ = origin.getZ() + 0.5 + (random.nextBoolean() ? 0.0 : 0.5);
			SinkholeShape shape = new SinkholeShape(
					centerX, centerZ, openRadius, config.bottomWidthMultiplier(), depth,
					random.nextDouble() * Mth.TWO_PI, random.nextDouble() * Mth.TWO_PI, random.nextDouble() * Mth.TWO_PI
			);
			int[] extent = shape.extent(0);
			boolean fits = extent[0] >= config.minOpeningWidth() && extent[0] <= config.maxOpeningWidth()
					&& extent[1] >= config.minOpeningWidth() && extent[1] <= config.maxOpeningWidth();
			if (fits) {
				return shape;
			}
		}
		return null;
	}

	private static int findSurface(WorldGenLevel level, BlockPos origin) {
		BlockPos.MutableBlockPos cursor = origin.mutable();
		if (!level.getBlockState(cursor).isAir()) {
			return Integer.MIN_VALUE;
		}
		for (int i = 0; i < SURFACE_SCAN; i++) {
			cursor.move(Direction.DOWN);
			if (cursor.getY() <= level.getMinBuildHeight()) {
				return Integer.MIN_VALUE;
			}
			BlockState state = level.getBlockState(cursor);
			if (!state.isAir()) {
				return isCarvable(state) ? cursor.getY() : Integer.MIN_VALUE;
			}
		}
		return Integer.MIN_VALUE;
	}

	private static boolean isEnclosed(WorldGenLevel level, List<BlockPos> capCells, List<BlockPos> hollowCells, LongOpenHashSet hollow, LongOpenHashSet cap) {
		for (BlockPos pos : hollowCells) {
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				long key = neighbor.asLong();
				if (hollow.contains(key) || cap.contains(key)) {
					continue;
				}
				if (!isSolidShell(level.getBlockState(neighbor))) {
					return false;
				}
			}
		}
		for (BlockPos pos : capCells) {
			for (Direction direction : HORIZONTAL) {
				BlockPos neighbor = pos.relative(direction);
				if (cap.contains(neighbor.asLong())) {
					continue;
				}
				if (!isSolidShell(level.getBlockState(neighbor))) {
					return false;
				}
			}
		}
		return true;
	}

	private static void placeBlob(WorldGenLevel level, RandomSource random, BlockPos seed, LongOpenHashSet hollow, LongOpenHashSet cap, int surfaceY, float rubyChance) {
		MagmaBlob.place(level, random, seed, MagmaBlob.rollRadius(random), rubyChance, pos -> canHoldPocket(level, pos, hollow, cap, surfaceY));
	}

	private static boolean canHoldPocket(WorldGenLevel level, BlockPos pos, LongOpenHashSet hollow, LongOpenHashSet cap, int surfaceY) {
		if (pos.getY() >= surfaceY || hollow.contains(pos.asLong()) || cap.contains(pos.asLong())) {
			return false;
		}
		BlockState state = level.getBlockState(pos);
		return isCarvable(state) || state.is(Blocks.MAGMA_BLOCK) || state.is(RNBlocks.MOLTEN_RUBY_ORE.get());
	}

	private static boolean isCarvable(BlockState state) {
		return state.is(BlockTags.BASE_STONE_NETHER)
				|| state.is(BlockTags.SOUL_SPEED_BLOCKS)
				|| state.is(BlockTags.NYLIUM)
				|| state.is(Blocks.GRAVEL)
				|| state.is(Blocks.MAGMA_BLOCK)
				|| state.is(Blocks.NETHER_QUARTZ_ORE)
				|| state.is(Blocks.NETHER_GOLD_ORE)
				|| state.is(RNBlocks.NETHER_RUBY_ORE.get())
				|| state.is(RNBlocks.MOLTEN_RUBY_ORE.get());
	}

	private static boolean isSolidShell(BlockState state) {
		return !state.isAir() && state.getFluidState().isEmpty() && !state.canBeReplaced();
	}

	private static boolean isOpenAbove(BlockState state) {
		return (state.isAir() || state.canBeReplaced()) && state.getFluidState().isEmpty();
	}
}
