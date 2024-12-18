package corundum.rubinated_nether.data.sub_providers;

import java.util.Set;

import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import corundum.rubinated_nether.content.RubinatedNetherItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class RubinatedNetherBlockLoot extends BlockLootSubProvider {
	public RubinatedNetherBlockLoot(HolderLookup.Provider lookupProvider) {
		super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return RubinatedNetherBlocks.BLOCKS.getEntries()
			.stream()
			.map(block -> (Block) block.value())
			.toList();
	}

	@Override
	protected void generate() {
		this.dropSelf(RubinatedNetherBlocks.CHANDELIER.get());

		this.dropSelf(RubinatedNetherBlocks.ALTAR_STONE.get());
		this.dropSelf(RubinatedNetherBlocks.ALTAR_STONE_TILES.get());
		this.dropSelf(RubinatedNetherBlocks.ALTAR_STONE_PILLAR.get());
		this.dropSelf(RubinatedNetherBlocks.ALTAR_STONE_BRICKS.get());
		this.dropSelf(RubinatedNetherBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
		this.dropSelf(RubinatedNetherBlocks.BLEEDING_CHISELED_ALTAR_STONE_BRICKS.get());
		this.dropSelf(RubinatedNetherBlocks.RUNESTONE.get());

		this.dropWhenSilkTouch(RubinatedNetherBlocks.DRY_ICE.get());
		
		this.dropOther(
			RubinatedNetherBlocks.MOLTEN_RUBY_ORE.get(),
			RubinatedNetherItems.MOLTEN_RUBY_ITEM.get()
		);
	}
}
