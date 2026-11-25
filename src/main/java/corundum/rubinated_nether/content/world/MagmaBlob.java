package corundum.rubinated_nether.content.world;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public final class MagmaBlob {
	public static final double MIN_RADIUS = 2.5;
	public static final double MAX_RADIUS = 4.0;
	private static final float RUBY_SCALE = 0.1F;

	private MagmaBlob() {
	}

	public static double rollRadius(RandomSource random) {
		return MIN_RADIUS + random.nextDouble() * (MAX_RADIUS - MIN_RADIUS);
	}

	public static void place(WorldGenLevel level, RandomSource random, BlockPos center, double radius, float rubyChance, Predicate<BlockPos> allowed) {
		double rx = radius * (0.8 + random.nextDouble() * 0.4);
		double ry = radius * (0.7 + random.nextDouble() * 0.4);
		double rz = radius * (0.8 + random.nextDouble() * 0.4);
		int ex = Mth.ceil(rx * 1.2);
		int ey = Mth.ceil(ry * 1.2);
		int ez = Mth.ceil(rz * 1.2);
		BlockState magma = Blocks.MAGMA_BLOCK.defaultBlockState();
		BlockState ruby = RNBlocks.MOLTEN_RUBY_ORE.get().defaultBlockState();
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int dx = -ex; dx <= ex; dx++) {
			for (int dy = -ey; dy <= ey; dy++) {
				for (int dz = -ez; dz <= ez; dz++) {
					double nx = dx / rx;
					double ny = dy / ry;
					double nz = dz / rz;
					double distance = nx * nx + ny * ny + nz * nz;
					if (distance > 1.0 + (random.nextDouble() - 0.5) * 0.4) {
						continue;
					}
					cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					if (!allowed.test(cursor)) {
						continue;
					}
					level.setBlock(cursor, random.nextFloat() < rubyChance * RUBY_SCALE ? ruby : magma, 2);
				}
			}
		}
	}
}
