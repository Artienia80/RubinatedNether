package corundum.rubinated_nether.content.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class FeatureBiomes {
	private FeatureBiomes() {
	}

	public static boolean allowed(WorldGenLevel level, BlockPos pos) {
		Holder<Biome> biome = level.getBiome(pos);
		return biome.is(Biomes.NETHER_WASTES) || biome.is(Biomes.CRIMSON_FOREST) || biome.is(Biomes.WARPED_FOREST);
	}
}
