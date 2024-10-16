package net.artienia.rubinated_nether.datagen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.datagen.models.RNModels;
import net.artienia.rubinated_nether.datagen.loot.RNBlockLootTables;
import net.artienia.rubinated_nether.datagen.worldgen.RNFeatureGen;
import net.artienia.rubinated_nether.worldgen.RNConfiguredFeatures;
import net.artienia.rubinated_nether.worldgen.RNPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class RNDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(RNModels::new);
        pack.addProvider(RNBlockLootTables::new);
        pack.addProvider(RNFeatureGen::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        RNFeatureGen.buildRegistries(registryBuilder);
    }

}
