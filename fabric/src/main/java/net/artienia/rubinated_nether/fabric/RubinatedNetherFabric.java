package net.artienia.rubinated_nether.fabric;

import net.artienia.rubinated_nether.RNEvents;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.fabric.conditions.RNResourceConditionTypes;
import net.artienia.rubinated_nether.worldgen.RNPlacedFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class RubinatedNetherFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        // Run our common setup.
        RubinatedNether.init();
        RubinatedNether.setup();
        RNResourceConditionTypes.init();

        ModContainer mod = FabricLoader.getInstance()
            .getModContainer(RubinatedNether.MOD_ID)
            .orElseThrow();

        // Register ruby template RP
        ResourceManagerHelper.registerBuiltinResourcePack(
            RubinatedNether.id("better_netherite_template"), mod,
            Component.literal("Rubinated Netherite Template"),
            ResourcePackActivationType.DEFAULT_ENABLED
        );

        // Spelunkery Compat
        if(FabricLoader.getInstance().isModLoaded("spelunkery")) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                RubinatedNether.id("compat_spelunkery"), mod,
                Component.literal("Rubinated Nether Mod Compat"),
                ResourcePackActivationType.DEFAULT_ENABLED
            );
        }

        registerBiomeModifications();
        registerEvents();
    }

    private void registerBiomeModifications() {
        BiomeModifications.create(RubinatedNether.id("rubies"))
            .add(ModificationPhase.ADDITIONS, BiomeSelectors.tag(BiomeTags.IS_NETHER), (selection, context) -> {
                BiomeModificationContext.GenerationSettingsContext generation = context.getGenerationSettings();
                generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RNPlacedFeatures.NETHER_RUBY_ORE_PLACED_KEY);
                generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RNPlacedFeatures.MOLTEN_RUBY_ORE_PLACED_KEY);
                generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RNPlacedFeatures.RUBINATED_BLACKSTONE_PLACED_KEY);
            });
    }

    private void registerEvents() {
        UseBlockCallback.EVENT.register(RNEvents::interactBlock);
    }
}
