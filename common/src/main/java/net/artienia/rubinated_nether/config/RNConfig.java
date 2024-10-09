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

    @ConfigEntry(
        id = "netherite_template_recipe",
        type = EntryType.BOOLEAN,
        translation = "config.rubinated_nether.custom_smithing_recipe"
    )
    @Comment("Enable custom netherite smithing template recipe")
    public static boolean customSmithingRecipe = true;

    @ConfigSeparator(translation = "config.rubinated_nether.chandelier")
    @ConfigEntry(
        id = "chandelier_damage_multiplier",
        type = EntryType.FLOAT,
        translation = "config.rubinated_nether.chandelier_damage_multiplier"
    )
    @Comment("Chandelier damage multiplier")
    public static float chandelierMultiplier = 0.22f;

    @ConfigEntry(
        id = "chandelier_max_damage",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.chandelier_max_damage"
    )
    @Comment("Chandelier max damage")
    public static int chandelierMaxDamage = 500;

    @ConfigSeparator(translation = "block.rubinated_nether.ruby_brazier")
    @ConfigEntry(
        id = "brazier_effect_range",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.brazier_effect_range"
    )
    @Comment("Brazier effect range in blocks")
    @IntRange(min = 0, max = 32)
    public static int brazierRange = 5;

    @ConfigEntry(
        id = "brazier_effect_duration",
        type = EntryType.FLOAT,
        translation = "config.rubinated_nether.brazier_effect_duration"
    )
    @Comment("Brazier effect duration in seconds")
    public static float brazierDuration = 15.0f;

    @ConfigEntry(
        id = "brazier_effect_particles",
        type = EntryType.BOOLEAN,
        translation = "config.rubinated_nether.brazier_effect_particles"
    )
    @Comment("""
    Enable beacon effect particles for brazier.
    You may need to wait for the effect to ware off for this to apply""")
    public static boolean brazierEffectParticles = false;

    @ConfigSeparator(translation = "block.rubinated_nether.nether_ruby_ore")
    @ConfigEntry(
        id = "ruby_ore_min_shards",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.nether_ruby_ore.min_shards"
    )
    @SuppressWarnings("unused")
    public static int minRubyOreShards = 2;

    @ConfigEntry(
        id = "ruby_ore_max_shards",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.nether_ruby_ore.max_shards"
    )
    @SuppressWarnings("unused")
    public static int maxRubyOreShards = 7;

    @ConfigSeparator(translation = "block.rubinated_nether.molten_ruby_ore")
    @ConfigEntry(
            id = "ruby_ore_min_molten",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.min_molten"
    )
    @SuppressWarnings("unused")
    public static int minRubyOreMolten = 1;

    @ConfigEntry(
            id = "ruby_ore_max_molten",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.max_molten"
    )
    @SuppressWarnings("unused")
    public static int maxRubyOreMolten = 2;
    @ConfigEntry(
            id = "ruby_ore_min_nuggets",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.min_nuggets"
    )
    @SuppressWarnings("unused")
    public static int minRubyOreNuggets = 2;

    @ConfigEntry(
            id = "ruby_ore_max_nuggets",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.max_nuggets"
    )
    @SuppressWarnings("unused")
    public static int maxRubyOreNuggets = 4;

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
    }
}
