package net.artienia.rubinated_nether.datagen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.datagen.blockstates.ModModels;
import net.artienia.rubinated_nether.datagen.loot.ModBlockLootTables;
import net.artienia.rubinated_nether.worldgen.ModConfiguredFeatures;
import net.artienia.rubinated_nether.worldgen.ModPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ModModels::new);
        pack.addProvider(ModBlockLootTables::new);
        pack.addProvider(ModRegistries::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
    }

    private static class ModRegistries extends FabricDynamicRegistryProvider {

        public ModRegistries(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            registries.lookup(Registries.CONFIGURED_FEATURE).ifPresent(entries::addAll);
            registries.lookup(Registries.PLACED_FEATURE).ifPresent(entries::addAll);
        }

        @Override
        public String getName() {
            return RubinatedNether.MOD_ID;
        }
    }

}
