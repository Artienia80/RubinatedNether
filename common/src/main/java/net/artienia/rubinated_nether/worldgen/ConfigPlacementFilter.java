package net.artienia.rubinated_nether.worldgen;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import net.artienia.rubinated_nether.RubinatedNether;
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
        return RNConfig.Worldgen.enabled
            && RubinatedNether.CONFIGURATOR.getConfig(RNConfig.class)
            .getSubConfig("worldgen")
            .flatMap(worldgen -> worldgen.getEntry(configName))
            .filter(entry -> entry.type() == EntryType.BOOLEAN)
            .map(entry -> {
                try {
                    return entry.field().getBoolean(null);
                } catch (Exception e) {
                    RubinatedNether.LOGGER.error("Failed to read config field: {}", configName);
                    return true;
                }
            })
            .orElseGet(() -> {
                RubinatedNether.LOGGER.warn("No Config field with name: {}", configName);
                return true;
            });
    }

    @Override
    public PlacementModifierType<ConfigPlacementFilter> type() {
        return RNPlacementFilters.CONFIG_FILTER.get();
    }
}
