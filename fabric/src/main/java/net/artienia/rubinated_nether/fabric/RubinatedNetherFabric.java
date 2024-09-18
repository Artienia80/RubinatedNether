package net.artienia.rubinated_nether.fabric;

import net.artienia.rubinated_nether.worldgen.ModPlacedFeatures;
import net.fabricmc.api.ModInitializer;

import net.artienia.rubinated_nether.RubinatedNether;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class RubinatedNetherFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        // Run our common setup.
        RubinatedNether.init();
        RubinatedNether.setup();

        registerBiomeModifications();
    }

    public static void registerBiomeModifications() {
        BiomeModifications.create(RubinatedNether.id("rubies"))
            .add(ModificationPhase.ADDITIONS, BiomeSelectors.tag(BiomeTags.IS_NETHER), (selection, context) -> {
                BiomeModificationContext.GenerationSettingsContext generation = context.getGenerationSettings();
                generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.NETHER_RUBY_ORE_PLACED_KEY);
                generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MOLTEN_RUBY_ORE_PLACED_KEY);
                generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.RUBINATED_BLACKSTONE_PLACED_KEY);
            });
    }
}
