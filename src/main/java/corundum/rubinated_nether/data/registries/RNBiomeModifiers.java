package corundum.rubinated_nether.data.registries;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RNBiomeModifiers {

	public static final ResourceKey<BiomeModifier> MOLTEN_RUBY_ORE = createKey("molten_ruby_ore");

	private static ResourceKey<BiomeModifier> createKey(String key) {
		return ResourceKey.create(
				NeoForgeRegistries.Keys.BIOME_MODIFIERS,
				ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, key)
		);
	}

	public static void bootstap(BootstrapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

		context.register(
				MOLTEN_RUBY_ORE,
				new AddFeaturesBiomeModifier(
						biomes.getOrThrow(BiomeTags.IS_NETHER),
						HolderSet.direct(placedFeatures.getOrThrow(RNPlacedFeatures.MOLTEN_RUBY_ORE)),
						GenerationStep.Decoration.VEGETAL_DECORATION
				)
		);

	}
}