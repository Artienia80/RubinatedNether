package corundum.rubinated_nether.data.sub_providers;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Set;

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

		this.add(
				RNBlocks.MOLTEN_RUBY_CAULDRON.get(),
				(block) -> {
					return LootTable.lootTable()
							.withPool(
									LootPool.lootPool()
											.add(LootItem.lootTableItem(net.minecraft.world.level.block.Blocks.CAULDRON))
							);
				}
		);

		this.waxableDrop(RNBlocks.BRONZE_LANTERN);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_LANTERN);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_LANTERN);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_LANTERN);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_LANTERN);

		this.waxableDrop(RNBlocks.BRONZE_CHAIN);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_CHAIN);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_CHAIN);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_CHAIN);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_CHAIN);

		this.waxableDrop(RNBlocks.BRONZE_SPRING);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_SPRING);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_SPRING);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_SPRING);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_SPRING);

		this.dropSelf(RNBlocks.BRONZE_CHANDELIER.get());
        this.dropSelf(RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get());
        this.dropSelf(RNBlocks.CORRODED_BRONZE_CHANDELIER.get());
        this.dropSelf(RNBlocks.TARNISHED_BRONZE_CHANDELIER.get());
        this.dropSelf(RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get());

		this.dropSelf(RNBlocks.SOAKSTONE.get());

		this.add(
				RNBlocks.SHRINE_STONE.get(),
				(block) -> LootTable.lootTable()
						.withPool(
								LootPool.lootPool()
										.when(this.hasSilkTouch())
										.add(LootItem.lootTableItem(RNBlocks.SHRINE_STONE))
						)
						.withPool(
								applyExplosionCondition(
										RNBlocks.SHRINE_STONE,
										LootPool.lootPool()
												.when(this.hasSilkTouch().invert())
												.add(LootItem.lootTableItem(RNBlocks.COBBLED_SHRINE_STONE))
								)
						)
		);
		this.dropSelf(RNBlocks.SHRINE_STONE_STAIRS.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_SLAB.get());
		this.dropSelf(RNBlocks.SHRINE_STONE_WALL.get());

		this.dropSelf(RNBlocks.COBBLED_SHRINE_STONE.get());
		this.dropSelf(RNBlocks.COBBLED_SHRINE_STONE_STAIRS.get());
		this.dropSelf(RNBlocks.COBBLED_SHRINE_STONE_SLAB.get());
		this.dropSelf(RNBlocks.COBBLED_SHRINE_STONE_WALL.get());

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
		this.dropSelf(RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get());
		this.dropSelf(RNBlocks.RUBINATED_SHRINE_STONE_TILES.get());

		this.add(RNBlocks.RUNESTONE.get(), this.createDoorTable(RNBlocks.RUNESTONE.get()));
		this.dropSelf(RNBlocks.SHRINE_STONE_COFFER.get());

		this.dropWhenSilkTouch(RNBlocks.RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.RUBY_GLASS_PANE.get());
		this.dropWhenSilkTouch(RNBlocks.ORNATE_RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.ORNATE_RUBY_GLASS_PANE.get());
		this.dropWhenSilkTouch(RNBlocks.MOLTEN_RUBY_GLASS.get());
		this.dropWhenSilkTouch(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get());

		this.dropWhenSilkTouch(RNBlocks.DRY_ICE.get());

		this.dropSelf(RNBlocks.FREEZER.get());
		this.dropSelf(RNBlocks.BRAZIER.get());
		this.dropSelf(RNBlocks.RUBINATION_ALTAR.get());

		this.dropSelf(RNBlocks.SHRINE_STONE_COFFER.get());

		this.waxableDrop(RNBlocks.BRONZE_BLOCK);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_BLOCK);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_BLOCK);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_BLOCK);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK);

		this.waxableDrop(RNBlocks.CHISELED_BRONZE);
		this.waxableDrop(RNBlocks.DISCOLORED_CHISELED_BRONZE);
		this.waxableDrop(RNBlocks.CORRODED_CHISELED_BRONZE);
		this.waxableDrop(RNBlocks.TARNISHED_CHISELED_BRONZE);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_CHISELED_BRONZE);

		this.waxableDrop(RNBlocks.BRONZE_BULB);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_BULB);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_BULB);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_BULB);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_BULB);

		this.waxableDrop(RNBlocks.CUT_BRONZE_PILLAR);
		this.waxableDrop(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR);
		this.waxableDrop(RNBlocks.CORRODED_CUT_BRONZE_PILLAR);
		this.waxableDrop(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR);

		this.waxableDrop(RNBlocks.CUT_BRONZE_BRICKS);
		this.waxableDrop(RNBlocks.CUT_BRONZE_BRICKS_STAIRS);
		this.waxableDrop(RNBlocks.CUT_BRONZE_BRICKS_SLAB);
		this.waxableDrop(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS);
		this.waxableDrop(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS);
		this.waxableDrop(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB);
		this.waxableDrop(RNBlocks.CORRODED_CUT_BRONZE_BRICKS);
		this.waxableDrop(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS);
		this.waxableDrop(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB);
		this.waxableDrop(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS);
		this.waxableDrop(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS);
		this.waxableDrop(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB);

		this.waxableDrop(RNBlocks.BRONZE_GRATE);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_GRATE);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_GRATE);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_GRATE);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_GRATE);

		this.waxableDrop(RNBlocks.BRONZE_LAMP);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_LAMP);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_LAMP);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_LAMP);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_LAMP);

		this.waxableDrop(RNBlocks.BRONZE_LASER);
		this.waxableDrop(RNBlocks.DISCOLORED_BRONZE_LASER);
		this.waxableDrop(RNBlocks.CORRODED_BRONZE_LASER);
		this.waxableDrop(RNBlocks.TARNISHED_BRONZE_LASER);
		this.waxableDrop(RNBlocks.CRYSTALLIZED_BRONZE_LASER);

		this.dropSelf(RNBlocks.COPPER_LASER.get());
		this.dropSelf(RNBlocks.EXPOSED_COPPER_LASER.get());
		this.dropSelf(RNBlocks.WEATHERED_COPPER_LASER.get());
		this.dropSelf(RNBlocks.OXIDIZED_COPPER_LASER.get());
		this.dropSelf(RNBlocks.WAXED_COPPER_LASER.get());
		this.dropSelf(RNBlocks.WAXED_EXPOSED_COPPER_LASER.get());
		this.dropSelf(RNBlocks.WAXED_WEATHERED_COPPER_LASER.get());
		this.dropSelf(RNBlocks.WAXED_OXIDIZED_COPPER_LASER.get());

		this.dropSelf(RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.get());

		this.add(
				RNBlocks.CRYSTALLIZED_BRONZE_CLUSTER.get(),
				(block) -> {
					return LootTable.lootTable()
							.withPool(
									applyExplosionCondition(
											RNBlocks.CRYSTALLIZED_BRONZE_CLUSTER,
											LootPool.lootPool()
													.add(
															LootItem.lootTableItem(RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL)
																	.apply(
																			SetItemCountFunction.setCount(
																					UniformGenerator.between(1, 2)
																			)
																	)
																	.apply(
																			ApplyBonusCount.addOreBonusCount(
																					registries
																							.lookupOrThrow(Registries.ENCHANTMENT)
																							.getOrThrow(Enchantments.FORTUNE)
																			)
																	)
													)
									)
							);
				}
		);

		this.add(
				RNBlocks.MOLTEN_RUBY_ORE.get(),
				(block) -> {
					return LootTable.lootTable()
							.withPool(
									LootPool.lootPool()
											.when(this.hasSilkTouch())
											.add(LootItem.lootTableItem(RNBlocks.MOLTEN_RUBY_ORE))
							)
							.withPool(
									applyExplosionCondition(
											RNBlocks.MOLTEN_RUBY_ORE,
											LootPool.lootPool()
													.when(this.hasSilkTouch().invert())
													.add(
															LootItem.lootTableItem(RNItems.MOLTEN_RUBY_NUGGET)
																	.apply(
																			SetItemCountFunction.setCount(UniformGenerator.between(3, 9))
																	)
																	.apply(
																			ApplyBonusCount.addOreBonusCount(
																					registries
																							.lookupOrThrow(Registries.ENCHANTMENT)
																							.getOrThrow(Enchantments.FORTUNE)
																			)
																	)
													)
									)
							);
				}
		);
		this.dropOther(
			RNBlocks.NETHER_RUBY_ORE.get(),
			RNItems.RUBY.get()
		);
		this.dropOther(
			RNBlocks.RUBINATED_BLACKSTONE.get(),
			RNItems.RUBY_SHARD.get()
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
					LootItem.lootTableItem(RNItems.RUBY_SHARD)
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

	private void waxableDrop(DeferredBlock<?> block) {
		var waxedCondition = LootItemBlockStatePropertyCondition
				.hasBlockStateProperties(block.get())
				.setProperties(
						StatePropertiesPredicate.Builder
								.properties()
								.hasProperty(TarnishingBronze.WAXED, true)
				);

		var waxedItem = BuiltInRegistries.ITEM.get(
				RubinatedNether.id(WaxableBlockItem.getWaxableItem(block))
		);

		var fn = LootTable.lootTable()
				.withPool(
						LootPool.lootPool()
								.when(waxedCondition)
								.add(LootItem.lootTableItem(waxedItem))
				)
				.withPool(
						LootPool.lootPool()
								.when(waxedCondition.invert())
								.add(LootItem.lootTableItem(block))
				);

		add(
				block.get(),
				fn
		);
	}
}
