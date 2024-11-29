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
    @Comment(
            value = "Enable the mod's creative tab. The items can always be found in vanilla tabs\n§eRequires game restart",
            translation = "config.rubinated_nether.creative_tab.desc"
    )
    public static boolean enableCreativeTab = true;

    @ConfigEntry(
            id = "enable_compat_tab",
            type = EntryType.BOOLEAN,
            translation = "config.rubinated_nether.compat_tab"
    )
    @Comment(
            value = "Enable the mod's creative compatibility tab. The items may be found in other mod's tabs\n§eRequires game restart",
            translation = "config.rubinated_nether.compat_tab.desc"
    )
    public static boolean enableCompatTab = true;

//    @ConfigEntry(
//            id = "netherite_smithing_template_recipe",
//            type = EntryType.BOOLEAN,
//            translation = "config.rubinated_nether.netherite_smithing_template_recipe"
//    )
//    @Comment(
//            value = "Enable a new Netherite Smithing Template Recipe\n§eRequires game restart",
//            translation = "config.rubinated_nether.netherite_smithing_template_recipe.desc"
//    )
    public static boolean netherite_smithing_template_recipe = true;

    @ConfigSeparator(translation = "block.rubinated_nether.ruby_chandelier")
    @ConfigEntry(
        id = "chandelier_damage_multiplier",
        type = EntryType.FLOAT,
        translation = "config.rubinated_nether.chandelier_damage_multiplier"
    )
    @Comment(
            value = "Specify how rapidly the Chandelier's damage increases",
            translation = "config.rubinated_nether.chandelier_damage_multiplier.desc"
    )
    public static float chandelierMultiplier = 0.22f;

    @ConfigEntry(
        id = "chandelier_max_damage",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.chandelier_max_damage"
    )
    @Comment(
            value = "Specify the Chandelier's maximum damage limit",
            translation = "config.rubinated_nether.chandelier_max_damage.desc"
    )
    public static int chandelierMaxDamage = 500;

    @ConfigSeparator(translation = "block.rubinated_nether.ruby_brazier")
    @ConfigEntry(
        id = "brazier_effect_range",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.brazier_effect_range"
    )
    @Comment(
            value = "Specify the Brazier's effect range in blocks",
            translation = "config.rubinated_nether.brazier_effect_range.desc"
    )
    @IntRange(min = 0, max = 32)
    public static int brazierRange = 16;

    @ConfigEntry(
        id = "brazier_effect_duration",
        type = EntryType.FLOAT,
        translation = "config.rubinated_nether.brazier_effect_duration"
    )
    @Comment(
            value = "Specify the Brazier's effect duration in seconds",
            translation = "config.rubinated_nether.brazier_effect_duration.desc"
    )
    public static float brazierDuration = 15.0f;

    @ConfigEntry(
        id = "brazier_effect_particles",
        type = EntryType.BOOLEAN,
        translation = "config.rubinated_nether.brazier_effect_particles"
    )
    @Comment(
            value = "Enable beacon effect particles for brazier.\nYou may need to wait for the effect to ware off for this to apply",
            translation = "config.rubinated_nether.brazier_effect_particles.desc"
    )
    public static boolean brazierEffectParticles = false;

    @ConfigSeparator(translation = "block.rubinated_nether.nether_ruby_ore")
    @ConfigEntry(
        id = "ruby_ore_min_shards",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.nether_ruby_ore.min_shards"
    )
    @Comment(
            value = "Specify the minimum amount of Ruby Shards dropped per ore",
            translation = "config.rubinated_nether.nether_ruby_ore.min_shards.desc"
    )
    @SuppressWarnings("unused")
    @IntRange(min = 0, max = 9)
    public static int minRubyOreShards = 2;

    @ConfigEntry(
        id = "ruby_ore_max_shards",
        type = EntryType.INTEGER,
        translation = "config.rubinated_nether.nether_ruby_ore.max_shards"
    )
    @Comment(
            value = "Specify the maximum amount of Ruby Shards dropped per ore",
            translation = "config.rubinated_nether.nether_ruby_ore.max_shards.desc"
    )
    @SuppressWarnings("unused")
    @IntRange(min = 0, max = 9)
    public static int maxRubyOreShards = 7;

    @ConfigSeparator(translation = "block.rubinated_nether.molten_ruby_ore")
    @ConfigEntry(
            id = "ruby_ore_min_molten",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.min_molten"
    )
    @Comment(
            value = "Specify the minimum amount of Molten Ruby dropped per ore",
            translation = "config.rubinated_nether.molten_ruby_ore.min_molten.desc"
    )
    @SuppressWarnings("unused")
    @IntRange(min = 1, max = 3)
    public static int minRubyOreMolten = 1;

    @ConfigEntry(
            id = "ruby_ore_max_molten",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.max_molten"
    )
    @Comment(
            value = "Specify the maximum amount of Molten Ruby dropped per ore",
            translation = "config.rubinated_nether.molten_ruby_ore.max_molten.desc"
    )
    @SuppressWarnings("unused")
    @IntRange(min = 1, max = 3)
    public static int maxRubyOreMolten = 2;

    @ConfigEntry(
            id = "ruby_ore_min_nuggets",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.min_nuggets"
    )
    @Comment(
            value = "Specify the minimum amount of Molten Ruby Nuggets dropped per ore",
            translation = "config.rubinated_nether.molten_ruby_ore.min_nuggets.desc"
    )
    @SuppressWarnings("unused")
    @IntRange(min = 0, max = 9)
    public static int minRubyOreNuggets = 2;

    @ConfigEntry(
            id = "ruby_ore_max_nuggets",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.molten_ruby_ore.max_nuggets"
    )
    @Comment(
            value = "Specify the maximum amount of Molten Ruby Nuggets dropped per ore",
            translation = "config.rubinated_nether.molten_ruby_ore.max_nuggets.desc"
    )
    @SuppressWarnings("unused")
    @IntRange(min = 0, max = 9)
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
        @Comment(
                value = "Set the Ruby Lens opacity. 1 is maximum and default.",
                translation = "config.rubinated_nether.client.ruby_lens_opacity.desc"
                )
        public static float rubyLensOpacity = 1f;

        @ConfigEntry(
            id = "brazier_particles",
            type = EntryType.INTEGER,
            translation = "config.rubinated_nether.client.brazier_particles"
        )
        @IntRange(min = 1, max = 6)
        public static int brazierParticleCount = 2;
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
        @Comment(
                value = "Enable this mod's Worldgen",
                translation = "config.rubinated_nether.worldgen.enabled.desc"
        )
        public static boolean enabled = true;

        @ConfigSeparator(translation = "config.rubinated_nether.worldgen.features")

        @ConfigEntry(
            id = "nether_ruby_ore",
            type = EntryType.BOOLEAN,
            translation = "block.rubinated_nether.nether_ruby_ore"
        )
        @Comment(
                value = "Enable Nether Ruby Ore spawning near the Nether roof",
                translation = "config.rubinated_nether.spawn_nether_ruby_ore.desc"
        )
        public static boolean netherRubyOre = true;

        @ConfigEntry(
            id = "molten_ruby_ore",
            type = EntryType.BOOLEAN,
            translation = "block.rubinated_nether.molten_ruby_ore"
        )
        @Comment(
                value = "Enable Molten Ruby Ore spawning inside Magma veins",
                translation =  "config.rubinated_nether.spawn_molten_ruby_ore.desc"
        )
        public static boolean moltenRubyOre = true;

        @ConfigEntry(
            id = "rubinated_blackstone",
            type = EntryType.BOOLEAN,
            translation = "block.rubinated_nether.rubinated_blackstone"
        )
        @Comment(
                value = "Enable Rubinated Blackstone spawning inside Bastion Remnants",
                translation =  "config.rubinated_nether.spawn_rubinated_blackstone.desc"
        )
        public static boolean rubinatedBlackstone = true;
    }
}
