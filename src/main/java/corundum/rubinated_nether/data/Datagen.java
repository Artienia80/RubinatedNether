package corundum.rubinated_nether.data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.data.sub_providers.RNBlockLoot;
import corundum.rubinated_nether.data.tags.RNBlockTags;
import corundum.rubinated_nether.data.tags.RNFluidTags;
import corundum.rubinated_nether.data.tags.RNItemTags;
import corundum.rubinated_nether.data.worldgen.RNBiomeModifiers;
import corundum.rubinated_nether.data.worldgen.RNConfiguredFeatures;
import corundum.rubinated_nether.data.worldgen.RNPlacedFeatures;
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

public class Datagen {
	public static void datagen(final GatherDataEvent event) {
		DataGenerator datagen = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		PackOutput output = datagen.getPackOutput();

		datagen.addProvider(event.includeClient(), new RNBlockStates(output, fileHelper));
		datagen.addProvider(event.includeClient(), new RNItemModels(output, fileHelper));
		datagen.addProvider(event.includeClient(), new RNLanguage(output));

		datagen.addProvider(event.includeServer(), new RNRecipeProvider(output, lookupProvider));

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

		// Worldgen
		datagen.addProvider(
			event.includeClient(), 
			new DatapackBuiltinEntriesProvider(
				output, 
				lookupProvider, 
				new RegistrySetBuilder()
					.add(Registries.CONFIGURED_FEATURE, RNConfiguredFeatures::bootstap)
					.add(Registries.PLACED_FEATURE, RNPlacedFeatures::bootstap)
					.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RNBiomeModifiers::bootstap),
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
