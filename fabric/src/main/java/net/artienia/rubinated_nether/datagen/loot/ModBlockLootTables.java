package net.artienia.rubinated_nether.datagen.loot;

import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.item.ModItems;
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
        this.dropSelf(ModBlocks.RUBY_BLOCK.get());
        this.dropSelf(ModBlocks.MOLTEN_RUBY_BLOCK.get());
        this.dropSelf(ModBlocks.BLEEDING_OBSIDIAN.get());
        this.dropSelf(ModBlocks.RUBY_GLASS.get());
        this.dropSelf(ModBlocks.MOLTEN_RUBY_GLASS.get());
        this.dropSelf(ModBlocks.RUBY_GLASS_PANE.get());
        this.dropSelf(ModBlocks.MOLTEN_RUBY_GLASS_PANE.get());
        this.dropSelf(ModBlocks.RUBY_CHANDELIER.get());
        this.dropSelf(ModBlocks.RUBY_LANTERN.get());
        this.dropSelf(ModBlocks.RUBY_LAVA_LAMP.get());

        this.add(ModBlocks.NETHER_RUBY_ORE.get(),
            block -> createEmeraldLikeOreDrops(ModBlocks.NETHER_RUBY_ORE.get(), ModItems.RUBY.get()));
        this.add(ModBlocks.MOLTEN_RUBY_ORE.get(),
            block -> createCopperLikeOreDrops(ModBlocks.MOLTEN_RUBY_ORE.get(), ModItems.MOLTEN_RUBY_NUGGET.get()));
        this.add(ModBlocks.RUBINATED_BLACKSTONE.get(),
            block -> createCopperLikeOreDrops(ModBlocks.RUBINATED_BLACKSTONE.get(), ModItems.RUBY_SHARD.get()));

        this.dropSelf(ModBlocks.FREEZER.get());
        this.dropSelf(ModBlocks.RUBY_LASER.get());
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F)))
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
