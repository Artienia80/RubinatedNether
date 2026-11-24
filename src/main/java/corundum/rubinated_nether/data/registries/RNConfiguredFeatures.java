package corundum.rubinated_nether.data.registries;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.world.cinder.RNFeatures;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
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
	public static final ResourceKey<ConfiguredFeature<?, ?>> MOLTEN_RUBY_ORE = createKey("molten_ruby_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CINDER_OVERHANG = createKey("cinder_overhang");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CINDER_BRIDGE = createKey("cinder_bridge");

	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
		return ResourceKey.create(
				Registries.CONFIGURED_FEATURE,
				ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, name)
		);
	}

	public static void bootstap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
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

		context.register(CINDER_OVERHANG, new ConfiguredFeature<>(RNFeatures.CINDER_OVERHANG.get(), NoneFeatureConfiguration.INSTANCE));
		context.register(CINDER_BRIDGE, new ConfiguredFeature<>(RNFeatures.CINDER_BRIDGE.get(), NoneFeatureConfiguration.INSTANCE));
	}
}