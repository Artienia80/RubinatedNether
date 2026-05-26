package corundum.rubinated_nether.data;

import corundum.rubinated_nether.content.RNBannerPatterns;
import corundum.rubinated_nether.data.sub_providers.RNBlockLoot;
import corundum.rubinated_nether.data.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RNData {
	public static void datagen(final GatherDataEvent event) {
		DataGenerator datagen = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		PackOutput output = datagen.getPackOutput();

		datagen.addProvider(event.includeClient(), new RNBlockStates(output, fileHelper));
		datagen.addProvider(event.includeClient(), new RNItemModels(output, fileHelper));

		datagen.addProvider(event.includeServer(), new RNRecipeProvider(output, lookupProvider));

		// Create datapack provider first so advancements can use its patched lookup,
		// which includes custom banner patterns, trim patterns, etc.
		var datapackProvider = new RNDatapackProvider(output, lookupProvider);
		datagen.addProvider(event.includeServer(), datapackProvider);

		datagen.addProvider(event.includeServer(), new RNAdvancements(output, datapackProvider.getRegistryProvider(), fileHelper));

		datagen.addProvider(event.includeServer(), new RNBannerPatterns.Provider(output));

		var blockTags = new RNBlockTags(output, lookupProvider, fileHelper);

		datagen.addProvider(event.includeClient(), blockTags);

		datagen.addProvider(event.includeClient(), new RNItemTags(output, lookupProvider, blockTags.contentsGetter(), fileHelper));

		datagen.addProvider(event.includeClient(), new RNFluidTags(output, lookupProvider, fileHelper));
		datagen.addProvider(event.includeClient(), new RNEntityTags(output, lookupProvider, fileHelper));
		datagen.addProvider(event.includeClient(), new RNEnchantmentTags(output, lookupProvider, fileHelper));
		datagen.addProvider(event.includeClient(), new RNLanguageProvider(output));

		datagen.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
				List.of(new SubProviderEntry(RNBlockLoot::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));
	}
}