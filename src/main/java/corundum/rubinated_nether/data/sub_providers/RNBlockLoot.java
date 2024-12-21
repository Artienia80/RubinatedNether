package corundum.rubinated_nether.data.sub_providers;

import java.util.Set;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class RNBlockLoot extends BlockLootSubProvider {
	public RNBlockLoot(HolderLookup.Provider lookupProvider) {
		super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return RNBlocks.BLOCKS.getEntries()
			.stream()
			.map(block -> (Block) block.value())
			.toList();
	}

	@Override
	protected void generate() {
		this.dropSelf(RNBlocks.RUBY_BLOCK.get());
		this.dropSelf(RNBlocks.MOLTEN_RUBY_BLOCK.get());
		this.dropSelf(RNBlocks.BLEEDING_OBSIDIAN.get());

		this.dropSelf(RNBlocks.RUBY_LANTERN.get());
		this.dropSelf(RNBlocks.CHANDELIER.get());
		this.dropSelf(RNBlocks.LAVA_LAMP.get());
		this.dropSelf(RNBlocks.SOAKSTONE.get());

		this.dropSelf(RNBlocks.ALTAR_STONE.get());

		this.dropSelf(RNBlocks.POLISHED_ALTAR_STONE.get());
		this.dropSelf(RNBlocks.POLISHED_ALTAR_STONE_STAIRS.get());
		this.dropSelf(RNBlocks.POLISHED_ALTAR_STONE_SLAB.get());
		this.dropSelf(RNBlocks.POLISHED_ALTAR_STONE_WALL.get());


		this.dropSelf(RNBlocks.ALTAR_STONE_TILES.get());
		this.dropSelf(RNBlocks.ALTAR_STONE_TILES_SLAB.get());
		this.dropSelf(RNBlocks.ALTAR_STONE_TILES_STAIRS.get());
		this.dropSelf(RNBlocks.ALTAR_STONE_TILES_WALL.get());

		this.dropSelf(RNBlocks.ALTAR_STONE_PILLAR.get());

		this.dropSelf(RNBlocks.ALTAR_STONE_BRICKS.get());
		this.dropSelf(RNBlocks.ALTAR_STONE_BRICKS_SLAB.get());
		this.dropSelf(RNBlocks.ALTAR_STONE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.ALTAR_STONE_BRICKS_WALL.get());

		this.dropSelf(RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
		this.dropSelf(RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS.get());
		this.dropSelf(RNBlocks.RUNESTONE.get());

		this.dropWhenSilkTouch(RNBlocks.RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.RUBY_GLASS_PANE.get());
		this.dropWhenSilkTouch(RNBlocks.ORNATE_RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.ORNATE_RUBY_GLASS_PANE.get());
		this.dropWhenSilkTouch(RNBlocks.MOLTEN_RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get());

		this.dropWhenSilkTouch(RNBlocks.DRY_ICE.get());

		this.dropSelf(RNBlocks.FREEZER.get());
		
		this.dropOther(
			RNBlocks.MOLTEN_RUBY_ORE.get(),
			RNItems.MOLTEN_RUBY_ITEM.get()
		);
		this.dropOther(
			RNBlocks.NETHER_RUBY_ORE.get(),
			RNItems.RUBY_ITEM.get()
		);
		this.dropOther(
			RNBlocks.RUBINATED_BLACKSTONE.get(),
			RNItems.RUBY_SHARD_ITEM.get()
		);
	}
}
