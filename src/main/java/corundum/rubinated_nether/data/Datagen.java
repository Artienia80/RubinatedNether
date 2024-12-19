package corundum.rubinated_nether.data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.data.sub_providers.RubinatedNetherBlockLoot;
import corundum.rubinated_nether.data.tags.RubinatedNetherBlockTags;
import corundum.rubinated_nether.data.tags.RubinatedNetherFluidTags;
import corundum.rubinated_nether.data.tags.RubinatedNetherItemTags;
import corundum.rubinated_nether.data.worldgen.RubinatedNetherBiomeModifiers;
import corundum.rubinated_nether.data.worldgen.RubinatedNetherConfiguredFeatures;
import corundum.rubinated_nether.data.worldgen.RubinatedNetherPlacedFeatures;
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

		datagen.addProvider(event.includeClient(), new RubinatedNetherBlockStates(output, fileHelper));
		datagen.addProvider(event.includeClient(), new RubinatednetherItemModels(output, fileHelper));
		datagen.addProvider(event.includeClient(), new RubinatedNetherLanguage(output));

		datagen.addProvider(event.includeServer(), new RubinatedNetherRecipes(output, lookupProvider));

		// Tags
		var blockTags = new RubinatedNetherBlockTags(output, lookupProvider, fileHelper);

		datagen.addProvider(event.includeClient(), blockTags);
		
		datagen.addProvider(
			event.includeClient(), 
			new RubinatedNetherItemTags(
				output, 
				lookupProvider, 
				blockTags.contentsGetter(), 
				fileHelper
			)
		);

		datagen.addProvider(event.includeClient(), new RubinatedNetherFluidTags(output, lookupProvider, fileHelper));

		// Worldgen
		datagen.addProvider(
			event.includeClient(), 
			new DatapackBuiltinEntriesProvider(
				output, 
				lookupProvider, 
				new RegistrySetBuilder()
					.add(Registries.CONFIGURED_FEATURE, RubinatedNetherConfiguredFeatures::bootstap)
					.add(Registries.PLACED_FEATURE, RubinatedNetherPlacedFeatures::bootstap)
					.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RubinatedNetherBiomeModifiers::bootstap),
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
						RubinatedNetherBlockLoot::new,
						LootContextParamSets.BLOCK
					)
				), 
				event.getLookupProvider()
			)
		);
	}
}
