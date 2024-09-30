package net.artienia.rubinated_nether.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import net.artienia.rubinated_nether.RubinatedNether;

@Config(RubinatedNether.MOD_ID)
public final class RNConfig {

    @ConfigEntry(
        id = "enable_creative_tab",
        type = EntryType.BOOLEAN,
        translation = "config.rubinated_nether.creative_tab"
    )
    @Comment("""
    Enable the mod's creative tab. The items can always be found in vanilla tabs
    
    §eRequires game restart""")
    public static boolean enableCreativeTab = true;

    @ConfigSeparator(translation = "config.rubinated_nether.chandelier")
    @ConfigEntry(
        id = "chandelier_multiplier",
        type = EntryType.FLOAT,
        translation = "config.rubinated_nether.chandelier_damage_multiplier"
    )
    @Comment("Chandelier damage multiplier")
    public static float chandelierMultiplier = 0.22f;

    @ConfigEntry(
        id = "chandelier_damage_multiplier",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.chandelier_max_damage"
    )
    @Comment("Chandelier max damage")
    public static int chandelierMaxDamage = 500;

    @Category(
        id = "client",
        translation = "config.rubinated_nether.client"
    )
    public static final class Client {

        @ConfigEntry(
            id = "ruby_lens_opacity",
            type = EntryType.FLOAT,
            translation = "config.rubinated_nether.client.ruby_lens_opacity"
        )
        @FloatRange(min = 0f, max = 1f)
        @Comment("Set the ruby lens opacity. 1 is maximum and default.")
        public static float rubyLensOpacity = 1f;
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
            translation = "block.rubinated_nether.nether_ruby_ore"
        )
        public static boolean netherRubyOre = true;

        @ConfigEntry(
            id = "molten_ruby_ore",
            type = EntryType.BOOLEAN,
            translation = "block.rubinated_nether.molten_ruby_ore"
        )
        public static boolean moltenRubyOre = true;

        @ConfigEntry(
            id = "rubinated_blackstone",
            type = EntryType.BOOLEAN,
            translation = "block.rubinated_nether.rubinated_blackstone"
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
