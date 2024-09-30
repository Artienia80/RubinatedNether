package net.artienia.rubinated_nether.datagen.loot;

import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.content.RNItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModBlockLootTables extends FabricBlockLootTableProvider {

    public ModBlockLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    public void generate() {
        this.dropSelf(RNBlocks.RUBY_BLOCK.get());
        this.dropSelf(RNBlocks.MOLTEN_RUBY_BLOCK.get());
        this.dropSelf(RNBlocks.BLEEDING_OBSIDIAN.get());
        this.dropSelf(RNBlocks.RUBY_GLASS.get());
        this.dropSelf(RNBlocks.MOLTEN_RUBY_GLASS.get());
        this.dropSelf(RNBlocks.RUBY_GLASS_PANE.get());
        this.dropSelf(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get());
        this.dropSelf(RNBlocks.RUBY_CHANDELIER.get());
        this.dropSelf(RNBlocks.RUBY_LANTERN.get());
        this.dropSelf(RNBlocks.RUBY_LAVA_LAMP.get());

        this.add(RNBlocks.NETHER_RUBY_ORE.get(),
            block -> createEmeraldLikeOreDrops(RNBlocks.NETHER_RUBY_ORE.get(), RNItems.RUBY.get()));
        this.add(RNBlocks.MOLTEN_RUBY_ORE.get(),
            block -> createCopperLikeOreDrops(RNBlocks.MOLTEN_RUBY_ORE.get(), RNItems.MOLTEN_RUBY_NUGGET.get()));
        this.add(RNBlocks.RUBINATED_BLACKSTONE.get(),
            block -> createCopperLikeOreDrops(RNBlocks.RUBINATED_BLACKSTONE.get(), RNItems.RUBY_SHARD.get()));

        this.dropSelf(RNBlocks.FREEZER.get());
        this.dropSelf(RNBlocks.RUBY_LASER.get());
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
