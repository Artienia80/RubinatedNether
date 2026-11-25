package corundum.rubinated_nether.data.registries;

import com.google.common.collect.ImmutableList;
import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class RNPlacedFeatures {
	public static final ResourceKey<PlacedFeature> MOLTEN_RUBY_ORE = createKey("molten_ruby_ore");
	public static final ResourceKey<PlacedFeature> CINDER_OVERHANG = createKey("cinder_overhang");
	public static final ResourceKey<PlacedFeature> CINDER_BRIDGE = createKey("cinder_bridge");
	public static final ResourceKey<PlacedFeature> PUMICE_CHASM = createKey("pumice_chasm");
	public static final ResourceKey<PlacedFeature> PUMICE_SINKHOLE = createKey("pumice_sinkhole");

	public static void bootstap(BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

		context.register(
				MOLTEN_RUBY_ORE,
				new PlacedFeature(
						configuredFeatures.getOrThrow(RNConfiguredFeatures.MOLTEN_RUBY_ORE),
						orePlacement(
								50,
								HeightRangePlacement.triangle(
										VerticalAnchor.absolute(0),
										VerticalAnchor.absolute(40)
								)
						)
				)
		);

		context.register(
				CINDER_OVERHANG,
				new PlacedFeature(
						configuredFeatures.getOrThrow(RNConfiguredFeatures.CINDER_OVERHANG),
						List.of(
								CountPlacement.of(64),
								InSquarePlacement.spread(),
								HeightRangePlacement.uniform(VerticalAnchor.absolute(33), VerticalAnchor.absolute(118)),
								BiomeFilter.biome()
						)
				)
		);

		context.register(
				CINDER_BRIDGE,
				new PlacedFeature(
						configuredFeatures.getOrThrow(RNConfiguredFeatures.CINDER_BRIDGE),
						List.of(
								RarityFilter.onAverageOnceEvery(3),
								CountPlacement.of(16),
								InSquarePlacement.spread(),
								HeightRangePlacement.uniform(VerticalAnchor.absolute(33), VerticalAnchor.absolute(118)),
								BiomeFilter.biome()
						)
				)
		);

		context.register(
				PUMICE_CHASM,
				new PlacedFeature(
						configuredFeatures.getOrThrow(RNConfiguredFeatures.PUMICE_CHASM),
						List.of(
								RarityFilter.onAverageOnceEvery(24),
								CountOnEveryLayerPlacement.of(1),
								BiomeFilter.biome()
						)
				)
		);

		context.register(
				PUMICE_SINKHOLE,
				new PlacedFeature(
						configuredFeatures.getOrThrow(RNConfiguredFeatures.PUMICE_SINKHOLE),
						List.of(
								RarityFilter.onAverageOnceEvery(2),
								CountPlacement.of(3),
								InSquarePlacement.spread(),
								HeightRangePlacement.uniform(VerticalAnchor.absolute(35), VerticalAnchor.absolute(110)),
								BiomeFilter.biome()
						)
				)
		);
	}

	public static ResourceKey<PlacedFeature> createKey(String name) {
		return ResourceKey.create(
				Registries.PLACED_FEATURE,
				ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, name)
		);
	}

	public static List<PlacementModifier> orePlacement(int count, PlacementModifier heightRange) {
		return ImmutableList.<PlacementModifier>builder()
				.add(heightRange)
				.add(CountPlacement.of(count))
				.add(InSquarePlacement.spread())
				.add(BiomeFilter.biome())
				.build();
	}
}