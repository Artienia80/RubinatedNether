package corundum.rubinated_nether.data.sub_providers;

import java.util.Set;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

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

		this.dropSelf(RNBlocks.SHRINE_STONE.get());

		this.dropSelf(RNBlocks.POLISHED_SHRINE_STONE.get());
		this.dropSelf(RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get());
		this.dropSelf(RNBlocks.POLISHED_SHRINE_STONE_SLAB.get());
		this.dropSelf(RNBlocks.POLISHED_SHRINE_STONE_WALL.get());


		this.dropSelf(RNBlocks.SHRINE_STONE_TILES.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_TILES_SLAB.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_TILES_STAIRS.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_TILES_WALL.get());

		this.dropSelf(RNBlocks.SHRINE_STONE_PILLAR.get());

		this.dropSelf(RNBlocks.SHRINE_STONE_BRICKS.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_BRICKS_SLAB.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_BRICKS_WALL.get());

		this.dropSelf(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get());
		this.dropSelf(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get());
		this.dropSelf(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get());
		this.add(RNBlocks.RUNESTONE.get(), this.createDoorTable(RNBlocks.RUNESTONE.get()));

		this.dropWhenSilkTouch(RNBlocks.RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.RUBY_GLASS_PANE.get());
		this.dropWhenSilkTouch(RNBlocks.ORNATE_RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.ORNATE_RUBY_GLASS_PANE.get());
		this.dropWhenSilkTouch(RNBlocks.MOLTEN_RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get());

		this.dropWhenSilkTouch(RNBlocks.DRY_ICE.get());

		this.dropSelf(RNBlocks.FREEZER.get());
		this.dropSelf(RNBlocks.RUBY_LASER.get());
		this.dropSelf(RNBlocks.BRAZIER.get());

		this.dropSelf(RNBlocks.BRONZE_BLOCK.get());
		this.dropSelf(RNBlocks.DISCOLORED_BRONZE_BLOCK.get());
		this.dropSelf(RNBlocks.CORRODED_BRONZE_BLOCK.get());
		this.dropSelf(RNBlocks.TARNISHED_BRONZE_BLOCK.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());

		this.dropSelf(RNBlocks.CUT_BRONZE_PILLAR.get());
		this.dropSelf(RNBlocks.CUT_BRONZE_PILLAR_STAIRS.get());
		this.dropSelf(RNBlocks.CUT_BRONZE_PILLAR_SLAB.get());
		this.dropSelf(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get());
		this.dropSelf(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.dropSelf(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR_SLAB.get());
		this.dropSelf(RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get());
		this.dropSelf(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.dropSelf(RNBlocks.CORRODED_CUT_BRONZE_PILLAR_SLAB.get());
		this.dropSelf(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get());
		this.dropSelf(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.dropSelf(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR_SLAB.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_STAIRS.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR_SLAB.get());

		this.dropSelf(RNBlocks.CUT_BRONZE_BRICKS.get());
		this.dropSelf(RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.CUT_BRONZE_BRICKS_SLAB.get());
		this.dropSelf(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get());
		this.dropSelf(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get());
		this.dropSelf(RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get());
		this.dropSelf(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get());
		this.dropSelf(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get());
		this.dropSelf(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get());
		this.dropSelf(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get());


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

		this.add(
			RNBlocks.RUBINATED_BLACKSTONE.get(), 
			(block) -> {
				return LootTable.lootTable()
					.withPool(
						LootPool.lootPool()
							.when(this.hasSilkTouch())
							.add(LootItem.lootTableItem(RNBlocks.RUBINATED_BLACKSTONE))
					)
					.withPool(RubinatedBlackstonePool());
			}
		);
	}

	private LootPool.Builder RubinatedBlackstonePool() {
		// My internal "never-nester" is screaming right now
		// Forced to pick between going off the edge of the screen, or 20 layers of nesting :anguish:
		return applyExplosionCondition(
			RNBlocks.RUBINATED_BLACKSTONE,
			LootPool.lootPool()
				.when(this.hasSilkTouch().invert())
				.add(
					LootItem.lootTableItem(RNItems.RUBY_SHARD_ITEM)
						.apply(
							SetItemCountFunction.setCount(UniformGenerator.between(2, 5))
						)
						.apply(
							ApplyBonusCount.addOreBonusCount(
								registries
									.lookupOrThrow(Registries.ENCHANTMENT)
									.getOrThrow(Enchantments.FORTUNE)
							)
						)
				)
		);
	}
}
