package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBannerPatterns;
import corundum.rubinated_nether.content.trim.RNTrimMaterials;
import corundum.rubinated_nether.content.trim.RNTrimPatterns;
import corundum.rubinated_nether.data.registries.*;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;

public class RNDatapackProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, RNConfiguredFeatures::bootstap)
            .add(Registries.PLACED_FEATURE, RNPlacedFeatures::bootstap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RNBiomeModifiers::bootstap)
            .add(Registries.JUKEBOX_SONG, RNJukeboxSongs::bootstap)
            .add(Registries.TRIM_MATERIAL, RNTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, RNTrimPatterns::bootstrap)
            .add(Registries.BANNER_PATTERN, context -> {
                for (var entry : RNBannerPatterns.BANNER_PATTERN_ENTRIES) {
                    context.register(
                            entry.key(),
                            new BannerPattern(
                                    RubinatedNether.id(entry.key().location().getPath()),
                                    entry.translationKey()
                            )
                    );
                }
            });

    public RNDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(RubinatedNether.MODID));
        System.out.println("[RNDatapackProvider] Provider created!");

    }
}