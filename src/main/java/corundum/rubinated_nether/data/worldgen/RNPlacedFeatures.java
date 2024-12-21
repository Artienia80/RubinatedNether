package corundum.rubinated_nether.data.worldgen;

import java.util.List;

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
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class RNPlacedFeatures {
	public static final ResourceKey<PlacedFeature> NETHER_RUBY_ORE = createKey("nether_ruby_ore");
	public static final ResourceKey<PlacedFeature> MOLTEN_RUBY_ORE = createKey("molten_ruby_ore");
	public static final ResourceKey<PlacedFeature> RUBINATED_BLACKSTONE = createKey("rubinated_blackstone");

	public static void bootstap(BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

		context.register(
			NETHER_RUBY_ORE, 
			new PlacedFeature(
				configuredFeatures.getOrThrow(RNConfiguredFeatures.NETHER_RUBY_ORE),
				orePlacement(
					1,
					HeightRangePlacement.triangle(
						VerticalAnchor.belowTop(30), 
						VerticalAnchor.belowTop(0)
					)
				)
			)
		);

		context.register(
			MOLTEN_RUBY_ORE, 
			new PlacedFeature(
				configuredFeatures.getOrThrow(RNConfiguredFeatures.MOLTEN_RUBY_ORE),
				orePlacement(
					100,
					HeightRangePlacement.triangle(
						VerticalAnchor.absolute(0), 
						VerticalAnchor.absolute(40)
					)
				)
			)
		);

		context.register(
			RUBINATED_BLACKSTONE, 
			new PlacedFeature(
				configuredFeatures.getOrThrow(RNConfiguredFeatures.RUBINATED_BLACKSTONE),
				orePlacement(
					250,
					HeightRangePlacement.triangle(
						VerticalAnchor.absolute(32), 
						VerticalAnchor.absolute(200)
					)
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
