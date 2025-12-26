package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.data.registries.RNBiomeModifiers;
import corundum.rubinated_nether.data.registries.RNConfiguredFeatures;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import corundum.rubinated_nether.data.registries.RNPlacedFeatures;
import corundum.rubinated_nether.data.sub_providers.RNBlockLoot;
import corundum.rubinated_nether.data.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Datagen {
	public static void datagen(final GatherDataEvent event) {
		DataGenerator datagen = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		PackOutput output = datagen.getPackOutput();

		datagen.addProvider(event.includeClient(), new RNBlockStates(output, fileHelper));
		datagen.addProvider(event.includeClient(), new RNItemModels(output, fileHelper));

		datagen.addProvider(event.includeServer(), new RNRecipeProvider(output, lookupProvider));
		datagen.addProvider(event.includeServer(), new RNAdvancements(output, lookupProvider, fileHelper));


		// Tags
		var blockTags = new RNBlockTags(output, lookupProvider, fileHelper);

		datagen.addProvider(event.includeClient(), blockTags);
		
		datagen.addProvider(
			event.includeClient(), 
			new RNItemTags(
				output, 
				lookupProvider, 
				blockTags.contentsGetter(), 
				fileHelper
			)
		);

		datagen.addProvider(event.includeClient(), new RNFluidTags(output, lookupProvider, fileHelper));
		datagen.addProvider(event.includeClient(), new RNEntityTags(output, lookupProvider, fileHelper));
		datagen.addProvider(event.includeClient(), new RNEnchantmentTags(output, lookupProvider, fileHelper));
		datagen.addProvider(event.includeClient(), new RNLanguage(output));

		// Worldgen
		datagen.addProvider(
			event.includeClient(), 
			new DatapackBuiltinEntriesProvider(
				output, 
				lookupProvider, 
				new RegistrySetBuilder()
					.add(Registries.CONFIGURED_FEATURE, RNConfiguredFeatures::bootstap)
					.add(Registries.PLACED_FEATURE, RNPlacedFeatures::bootstap)
					.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RNBiomeModifiers::bootstap)
					.add(Registries.JUKEBOX_SONG, RNJukeboxSongs::bootstap),
				Collections.singleton(RubinatedNether.MODID)
			)
		);

		// Loot
		datagen.addProvider(
			event.includeServer(),
			new LootTableProvider(
				output, 
				Set.of(), 
				List.of(
					new SubProviderEntry(
						RNBlockLoot::new,
						LootContextParamSets.BLOCK
					)
				), 
				event.getLookupProvider()
			)
		);
	}
}
