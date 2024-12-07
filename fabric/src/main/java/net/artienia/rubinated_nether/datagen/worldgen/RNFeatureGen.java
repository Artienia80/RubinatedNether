package net.artienia.rubinated_nether.datagen.worldgen;

import net.artienia.rubinated_nether.worldgen.RNConfiguredFeatures;
import net.artienia.rubinated_nether.worldgen.RNPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class RNFeatureGen extends FabricDynamicRegistryProvider {
	public RNFeatureGen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	public static void buildRegistries(RegistrySetBuilder builder) {
		builder.add(Registries.PLACED_FEATURE, RNPlacedFeatures::bootstrap)
			.add(Registries.CONFIGURED_FEATURE, RNConfiguredFeatures::bootstrap);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, Entries entries) {
		registries.lookup(Registries.PLACED_FEATURE).ifPresent(entries::addAll);
		registries.lookup(Registries.CONFIGURED_FEATURE).ifPresent(entries::addAll);
	}

	@Override
	public String getName() {
		return "RNFeatures";
	}
}
