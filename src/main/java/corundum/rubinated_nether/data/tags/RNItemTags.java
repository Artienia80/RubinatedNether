package corundum.rubinated_nether.data.tags;

import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RNItemTags extends ItemTagsProvider {
	public RNItemTags(
		PackOutput output, 
		CompletableFuture<HolderLookup.Provider> registries, 
		CompletableFuture<TagLookup<Block>> blockTags, 
		ExistingFileHelper helper
	) {
        super(output, registries, blockTags, RubinatedNether.MODID, helper);
    }

	@Override
	public void addTags(Provider provider) {
		this.tag(RNTags.Items.RUNES).add(
				RNItems.GREED_RUNE.asItem(),
				RNItems.WRATH_RUNE.asItem(),
				RNItems.SLOTH_RUNE.asItem(),
				RNItems.GLUTTONY_RUNE.asItem(),
				RNItems.ENVY_RUNE.asItem(),
				RNItems.VAINGLORY_RUNE.asItem(),
				RNItems.PRIDE_RUNE.asItem(),
				RNItems.ACEDIA_RUNE.asItem(),
				RNItems.LUXURIA_RUNE.asItem(),
				RNItems.INSIDIAE_RUNE.asItem(),
				RNItems.SUPERBIA_RUNE.asItem(),
				RNItems.TRISTIA_RUNE.asItem(),
				RNItems.STUDIOSE_RUNE.asItem(),
				RNItems.ARDENTER_RUNE.asItem(),
				RNItems.NIMIS_RUNE.asItem()
		);
	}
}
