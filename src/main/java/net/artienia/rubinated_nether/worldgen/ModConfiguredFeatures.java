package net.artienia.rubinated_nether.worldgen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_RUBY_ORE_KEY = registerKey("nether_ruby_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOLTEN_RUBY_ORE_KEY = registerKey("molten_ruby_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBINATED_BLACKSTONE_KEY = registerKey("rubinated_blackstone");


    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest netherrackReplacables = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest magmaReplacables = new BlockMatchTest(Blocks.MAGMA_BLOCK);
        RuleTest gildedReplacables = new BlockMatchTest(Blocks.GILDED_BLACKSTONE);



        register(context, NETHER_RUBY_ORE_KEY, Feature.ORE, new OreConfiguration(netherrackReplacables,
                ModBlocks.NETHER_RUBY_ORE.get().defaultBlockState(), 3));
        register(context, MOLTEN_RUBY_ORE_KEY, Feature.ORE, new OreConfiguration(magmaReplacables,
                ModBlocks.MOLTEN_RUBY_ORE.get().defaultBlockState(), 5));
        register(context, RUBINATED_BLACKSTONE_KEY, Feature.ORE, new OreConfiguration(gildedReplacables,
                ModBlocks.RUBINATED_BLACKSTONE.get().defaultBlockState(), 25));

    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(RubinatedNether.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}