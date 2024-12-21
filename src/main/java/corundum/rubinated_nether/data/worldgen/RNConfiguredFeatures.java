package corundum.rubinated_nether.data.worldgen;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

public class RNConfiguredFeatures {
	public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_RUBY_ORE = createKey("nether_ruby_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MOLTEN_RUBY_ORE = createKey("molten_ruby_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBINATED_BLACKSTONE = createKey("rubinated_blackstone");

	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
		return ResourceKey.create(
			Registries.CONFIGURED_FEATURE, 
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, name)
		);
	}

	public static void bootstap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
		context.register(
			NETHER_RUBY_ORE,
			new ConfiguredFeature<>(
				Feature.ORE, 
				new OreConfiguration(
					new BlockMatchTest(Blocks.NETHERRACK),
					RNBlocks.NETHER_RUBY_ORE.get().defaultBlockState(),
					3
				)
			)
		);

		context.register(
			MOLTEN_RUBY_ORE,
			new ConfiguredFeature<>(
				Feature.ORE, 
				new OreConfiguration(
					new BlockMatchTest(Blocks.MAGMA_BLOCK),
					RNBlocks.MOLTEN_RUBY_ORE.get().defaultBlockState(),
					5
				)
			)
		);

		context.register(
			RUBINATED_BLACKSTONE,
			new ConfiguredFeature<>(
				Feature.ORE, 
				new OreConfiguration(
					new BlockMatchTest(Blocks.GILDED_BLACKSTONE),
					RNBlocks.RUBINATED_BLACKSTONE.get().defaultBlockState(),
					25
				)
			)
		);
	}
}
