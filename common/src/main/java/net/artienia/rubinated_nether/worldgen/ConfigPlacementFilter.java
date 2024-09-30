package net.artienia.rubinated_nether.worldgen;

import com.mojang.serialization.Codec;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.*;

public class ConfigPlacementFilter extends PlacementFilter {

    public static final Codec<ConfigPlacementFilter> CODEC = Codec.STRING
        .xmap(ConfigPlacementFilter::new, filter -> filter.configName);

    public final String configName;

    public ConfigPlacementFilter(String feature) {
        this.configName = feature;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        return RNConfig.Worldgen.shouldPlaceFeature(configName);
    }

    @Override
    public PlacementModifierType<ConfigPlacementFilter> type() {
        return RNPlacementFilters.CONFIG_FILTER.get();
    }
}
