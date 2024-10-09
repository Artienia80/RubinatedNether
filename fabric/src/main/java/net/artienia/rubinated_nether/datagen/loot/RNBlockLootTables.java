package net.artienia.rubinated_nether.datagen.loot;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.content.RNItems;
import net.artienia.rubinated_nether.content.loot.ConfigUniform;
import net.artienia.rubinated_nether.content.loot.ItemTierLootCondition;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import uwu.serenity.critter.api.entry.RegistryEntry;

public class RNBlockLootTables extends FabricBlockLootTableProvider {

    public RNBlockLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    public void generate() {

        // Self-dropping blocks (automated)
        RubinatedNether.REGISTRIES.getAllEntries(Registries.BLOCK, RNBlocks.DROP_SELF)
            .forEach(entry -> dropSelf(entry.get()));

        this.add(RNBlocks.NETHER_RUBY_ORE.get(), block -> createToolDependantDrops(block, RNItems.RUBY_SHARD, RNItems.RUBY));
        this.add(RNBlocks.MOLTEN_RUBY_ORE.get(), block -> createToolDependantMoltenDrops(block, RNItems.MOLTEN_RUBY_NUGGET, RNItems.MOLTEN_RUBY));
        this.add(RNBlocks.RUBINATED_BLACKSTONE.get(),
            block -> createCopperLikeOreDrops(RNBlocks.RUBINATED_BLACKSTONE.get(), RNItems.RUBY_SHARD.get()));

    }

    protected LootTable.Builder createToolDependantDrops(Block block, ItemLike ironTier, ItemLike diamondTier) {
        return createSilkTouchDispatchTable(block, AlternativesEntry.alternatives(
            applyExplosionDecay(block, LootItem.lootTableItem(diamondTier)
                .when(ItemTierLootCondition.builder()
                    .minTier(Tiers.DIAMOND))
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
            ),
            applyExplosionDecay(block, LootItem.lootTableItem(ironTier)
                .when(ItemTierLootCondition.builder()
                    .minTier(Tiers.IRON)
                    .maxTier(Tiers.IRON))
                .apply(SetItemCountFunction.setCount(ConfigUniform.of("ruby_ore_min_shards", "ruby_ore_max_shards")))
            )
        ));
    }

    protected LootTable.Builder createToolDependantMoltenDrops(Block block, ItemLike lowTier, ItemLike netheriteTier) {
        return createSilkTouchDispatchTable(block, AlternativesEntry.alternatives(
                applyExplosionDecay(block, LootItem.lootTableItem(netheriteTier)
                        .when(ItemTierLootCondition.builder()
                                .minTier(Tiers.NETHERITE))
                        .apply(SetItemCountFunction.setCount(ConfigUniform.of("ruby_ore_min_molten", "ruby_ore_max_molten")))
                ),
                applyExplosionDecay(block, LootItem.lootTableItem(lowTier)
                        .when(ItemTierLootCondition.builder()
                                .minTier(Tiers.IRON)
                                .maxTier(Tiers.DIAMOND))
                        .apply(SetItemCountFunction.setCount(ConfigUniform.of("ruby_ore_min_nuggets", "ruby_ore_max_nuggets")))
                )
        ));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 7.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    protected LootTable.Builder createEmeraldLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }


}
