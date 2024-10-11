package net.artienia.rubinated_nether.integrations.spelunkery;

import com.ordana.spelunkery.reg.ModBlocks;
import com.ordana.spelunkery.reg.ModItems;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.content.RNItems;
import net.artienia.rubinated_nether.content.RNTabs;
import net.artienia.rubinated_nether.integrations.CompatHandler;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import uwu.serenity.critter.creative.TabPlacement;
import uwu.serenity.critter.stdlib.blocks.BlockEntry;
import uwu.serenity.critter.stdlib.blocks.BlockRegistrar;
import uwu.serenity.critter.stdlib.items.ItemEntry;
import uwu.serenity.critter.stdlib.items.ItemRegistrar;

public class SpelunkeryCompat implements CompatHandler {

    public static final BlockRegistrar SPELUNKERY_BLOCKS = BlockRegistrar.create(RubinatedNether.REGISTRIES);
    public static final ItemRegistrar SPELUNKERY_ITEMS = ItemRegistrar.create(RubinatedNether.REGISTRIES);

    public static final BlockEntry<Block> ROUGH_RUBY_BLOCK = SPELUNKERY_BLOCKS.entry("rough_ruby_block", Block::new)
        .copyProperties(() -> Blocks.RAW_COPPER_BLOCK)
        .properties(p -> p.requiresCorrectToolForDrops()
            .strength(5.0f, 6.0f)
            .sound(SoundType.CALCITE))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.NATURAL_BLOCKS, TabPlacement.after(() -> ModBlocks.ROUGH_EMERALD_BLOCK.get().asItem()))
        .transform(RNTabs.modTabIfEnabled(TabPlacement.after(RNBlocks.MOLTEN_RUBY_BLOCK)))
        .build()
        .register();

    public static final ItemEntry<Item> ROUGH_RUBY = SPELUNKERY_ITEMS.entry("rough_ruby", Item::new)
        .creativeTab(CreativeModeTabs.INGREDIENTS, TabPlacement.after(ModItems.ROUGH_EMERALD::get))
        .transform(RNTabs.modTabIfEnabled(TabPlacement.after(RNItems.MOLTEN_RUBY)))
        .register();

    public static final ItemEntry<Item> ROUGH_RUBY_SHARD = SPELUNKERY_ITEMS.entry("rough_ruby_shard", Item::new)
            .creativeTab(CreativeModeTabs.INGREDIENTS, TabPlacement.after(ModItems.ROUGH_EMERALD_SHARD::get))
            .transform(RNTabs.modTabIfEnabled(TabPlacement.after(RNItems.MOLTEN_RUBY_NUGGET)))
            .register();


    @Override
    public void init() {
        SPELUNKERY_BLOCKS.register();
        SPELUNKERY_ITEMS.register();
    }
}
