package corundum.rubinated_nether.data.tags;

import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

@SuppressWarnings("null")
public class RubinatedNetherBlockTags extends BlockTagsProvider {
	public RubinatedNetherBlockTags(
		PackOutput output, 
		CompletableFuture<HolderLookup.Provider> lookupProvider, 
		ExistingFileHelper existingFileHelper
	) {
		super(output, lookupProvider, RubinatedNether.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		// Mining tags 
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
			RubinatedNetherBlocks.MOLTEN_RUBY_ORE.get(),
			RubinatedNetherBlocks.CHANDELIER.get()
		);

		this.tag(BlockTags.NEEDS_STONE_TOOL).add(
			RubinatedNetherBlocks.CHANDELIER.get()
		);
	}
}
