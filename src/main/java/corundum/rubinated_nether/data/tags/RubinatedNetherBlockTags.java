package corundum.rubinated_nether.data.tags;

import java.util.concurrent.CompletableFuture;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import corundum.rubinated_nether.content.RubinatedNetherTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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
			RubinatedNetherBlocks.CHANDELIER.get(),
			RubinatedNetherBlocks.DRY_ICE.get(),
			
			RubinatedNetherBlocks.ALTAR_STONE.get(),
			RubinatedNetherBlocks.ALTAR_STONE_BRICKS.get(),
			RubinatedNetherBlocks.CHISELED_ALTAR_STONE_BRICKS.get(),
			RubinatedNetherBlocks.ALTAR_STONE_TILES.get(),
			RubinatedNetherBlocks.ALTAR_STONE_PILLAR.get(),
			RubinatedNetherBlocks.RUNESTONE.get()
		);

		this.tag(BlockTags.NEEDS_STONE_TOOL).add(
			RubinatedNetherBlocks.CHANDELIER.get()
		);

		this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
			RubinatedNetherBlocks.ALTAR_STONE.get(),
			RubinatedNetherBlocks.ALTAR_STONE_BRICKS.get(),
			RubinatedNetherBlocks.CHISELED_ALTAR_STONE_BRICKS.get(),
			RubinatedNetherBlocks.ALTAR_STONE_TILES.get(),
			RubinatedNetherBlocks.ALTAR_STONE_PILLAR.get(),
			RubinatedNetherBlocks.RUNESTONE.get()
		);

		this.tag(RubinatedNetherTags.Blocks.COLDEST_ICE).add(
			Blocks.BLUE_ICE	
		);
	}
}
