package net.artienia.rubinated_nether.forge.datagen.loot;

import dev.architectury.registry.registries.RegistrySupplier;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.stream.StreamSupport;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.RUBY_BLOCK.get());
        this.dropSelf(ModBlocks.MOLTEN_RUBY_BLOCK.get());
        this.dropSelf(ModBlocks.BLEEDING_OBSIDIAN.get());
        this.dropSelf(ModBlocks.RUBY_GLASS.get());
        this.dropSelf(ModBlocks.MOLTEN_RUBY_GLASS.get());
        this.dropSelf(ModBlocks.RUBY_GLASS_PANE.get());
        this.dropSelf(ModBlocks.MOLTEN_RUBY_GLASS_PANE.get());
        this.dropSelf(ModBlocks.RUBY_CHANDELIER.get());
        this.dropSelf(ModBlocks.RUBY_LANTERN.get());


        this.add(ModBlocks.NETHER_RUBY_ORE.get(),
                block -> createEmeraldLikeOreDrops(ModBlocks.NETHER_RUBY_ORE.get(), ModItems.RUBY.get()));
        this.add(ModBlocks.MOLTEN_RUBY_ORE.get(),
                block -> createCopperLikeOreDrops(ModBlocks.MOLTEN_RUBY_ORE.get(), ModItems.MOLTEN_RUBY.get()));
        this.add(ModBlocks.RUBINATED_BLACKSTONE.get(),
                block -> createCopperLikeOreDrops(ModBlocks.RUBINATED_BLACKSTONE.get(), ModItems.RUBY_SHARD.get()));

        this.dropSelf(ModBlocks.FREEZER.get());

    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    protected LootTable.Builder createEmeraldLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return StreamSupport.stream(ModBlocks.BLOCKS.spliterator(), false).map(RegistrySupplier::get)::iterator;
    }
}