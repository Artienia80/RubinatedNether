package net.artienia.rubinated_nether.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import net.artienia.rubinated_nether.RubinatedNether;

@Config(RubinatedNether.MOD_ID)
public final class RNConfig {

    @Category(
        id = "client",
        translation = "config.rubinated_nether.client"
    )
    public static final class Client {

    }

    @Category(
        id = "worldgen",
        translation = "config.rubinated_nether.worldgen"
    )
    @SuppressWarnings("unused")
    public static final class Worldgen {

        @ConfigEntry(
            id = "enabled",
            type = EntryType.BOOLEAN,
            translation = "config.rubinated_nether.worldgen.enabled"
        )
        @Comment("Enable the mod's worldgen")
        public static boolean enabled = true;

        @ConfigSeparator(translation = "config.rubinated_nether.worldgen.features")

        @ConfigEntry(
            id = "nether_ruby_ore",
            type = EntryType.BOOLEAN,
            translation = "config.rubinated_nether.worldgen.nether_ores"
        )
        public static boolean netherRubyOre = true;

        @ConfigEntry(
            id = "molten_ruby_ore",
            type = EntryType.BOOLEAN,
            translation = "config.rubinated_nether.worldgen.molten_ruby_ore"
        )
        public static boolean moltenRubyOre = true;

        @ConfigEntry(
            id = "rubinated_blackstone",
            type = EntryType.BOOLEAN,
            translation = "config.rubinated_nether.worldgen.rubinated_blackstone"
        )
        public static boolean rubinatedBlackstone = true;

        public static boolean shouldPlaceFeature(String fieldName) {
            // TODO: A better way of doing this
            return enabled && RubinatedNether.CONFIGURATOR.getConfig(RNConfig.class)
                .getSubConfig("worldgen")
                .flatMap(worldgen -> worldgen.getEntry(fieldName))
                .filter(entry -> entry.type() == EntryType.BOOLEAN)
                .map(entry -> {
                    try {
                        return entry.field().getBoolean(null);
                    } catch (Exception e) {
                        RubinatedNether.LOGGER.warn("No config field with name: {}", fieldName);
                        return true;
                    }
                })
                .orElse(true);
        }
    }
}
