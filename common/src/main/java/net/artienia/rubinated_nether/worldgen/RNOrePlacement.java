package net.artienia.rubinated_nether.worldgen;

import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class RNOrePlacement {
    public static List<PlacementModifier> orePlacement(String configName, PlacementModifier first, PlacementModifier second) {
        return List.of(first, InSquarePlacement.spread(), second, BiomeFilter.biome(), new ConfigPlacementFilter(configName));
    }

    public static List<PlacementModifier> commonOrePlacement(String configName, int pCount, PlacementModifier pHeightRange) {
        return orePlacement(configName, CountPlacement.of(pCount), pHeightRange);
    }

    public static List<PlacementModifier> rareOrePlacement(String configName, int pChance, PlacementModifier pHeightRange) {
        return orePlacement(configName, RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
    }
}