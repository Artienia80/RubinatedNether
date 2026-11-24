package corundum.rubinated_nether.content.world.cinder;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class CinderFormations {
	static final int MAX_REACH = 14;
	private static final float SUSPICIOUS_CHANCE = 1F;

	private CinderFormations() {
	}

	static boolean isTerrain(BlockState state) {
		return state.is(BlockTags.BASE_STONE_NETHER)
				|| state.is(BlockTags.SOUL_SPEED_BLOCKS)
				|| state.is(BlockTags.NYLIUM)
				|| state.is(RNBlocks.CINDER.get());
	}

	static boolean isAir(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).isAir();
	}

	static boolean isTerrain(BlockGetter level, BlockPos pos) {
		return isTerrain(level.getBlockState(pos));
	}

	static int findTerrain(BlockGetter level, BlockPos origin, Direction direction, int maxSteps) {
		BlockPos.MutableBlockPos cursor = origin.mutable();
		for (int i = 1; i <= maxSteps; i++) {
			cursor.move(direction);
			if (cursor.getY() < level.getMinBuildHeight() || cursor.getY() >= level.getMaxBuildHeight()) {
				return -1;
			}
			BlockState state = level.getBlockState(cursor);
			if (state.isAir()) {
				continue;
			}
			return isTerrain(state) ? i : -1;
		}
		return -1;
	}

	static Set<BlockPos> airOnly(WorldGenLevel level, Collection<BlockPos> blocks) {
		Set<BlockPos> result = new HashSet<>();
		for (BlockPos pos : blocks) {
			if (pos.getY() > level.getMinBuildHeight() + 1
					&& pos.getY() < level.getMaxBuildHeight() - 1
					&& isAir(level, pos)) {
				result.add(pos);
			}
		}
		return result;
	}

	static Set<BlockPos> reach(Set<BlockPos> candidates, Collection<BlockPos> seeds) {
		Set<BlockPos> seen = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		for (BlockPos seed : seeds) {
			if (candidates.contains(seed) && seen.add(seed)) {
				queue.add(seed);
			}
		}
		while (!queue.isEmpty()) {
			BlockPos current = queue.poll();
			for (Direction direction : Direction.values()) {
				BlockPos next = current.relative(direction);
				if (candidates.contains(next) && seen.add(next)) {
					queue.add(next);
				}
			}
		}
		return seen;
	}

	static void place(WorldGenLevel level, Collection<BlockPos> blocks, RandomSource random) {
		place(level, blocks);
		if (blocks.isEmpty() || random.nextFloat() >= SUSPICIOUS_CHANCE) {
			return;
		}
		List<BlockPos> exposed = new ArrayList<>();
		for (BlockPos pos : blocks) {
			if (isAir(level, pos.above())) {
				exposed.add(pos);
			}
		}
		if (exposed.isEmpty()) {
			return;
		}
		BlockPos target = exposed.get(random.nextInt(exposed.size()));
		level.setBlock(target, RNBlocks.SUSPICIOUS_CINDER.get().defaultBlockState(), 2);
		level.getBlockEntity(target, BlockEntityType.BRUSHABLE_BLOCK)
				.ifPresent(entity -> entity.setLootTable(RNBlocks.SUSPICIOUS_CINDER_LOOT, target.asLong()));
	}

	static void place(WorldGenLevel level, Collection<BlockPos> blocks) {
		BlockState cinder = RNBlocks.CINDER.get().defaultBlockState();
		for (BlockPos pos : blocks) {
			level.setBlock(pos, cinder, 2);
		}
	}
}
