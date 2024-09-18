package net.artienia.rubinated_nether.worldgen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> NETHER_RUBY_ORE_PLACED_KEY = registerKey("nether_ruby_ore_placed");
    public static final ResourceKey<PlacedFeature> MOLTEN_RUBY_ORE_PLACED_KEY = registerKey("molten_ruby_ore_placed");
    public static final ResourceKey<PlacedFeature> RUBINATED_BLACKSTONE_PLACED_KEY = registerKey("rubinated_blackstone_placed");


    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, NETHER_RUBY_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_RUBY_ORE_KEY),
                ModOrePlacement.commonOrePlacement(1,
                        HeightRangePlacement.triangle(VerticalAnchor.belowTop(30), VerticalAnchor.belowTop(0))));
        register(context, MOLTEN_RUBY_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOLTEN_RUBY_ORE_KEY),
                ModOrePlacement.commonOrePlacement(100,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(40))));
        register(context, RUBINATED_BLACKSTONE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBINATED_BLACKSTONE_KEY),
                ModOrePlacement.commonOrePlacement(250,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(32), VerticalAnchor.absolute(200))));
    }


    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(RubinatedNether.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
