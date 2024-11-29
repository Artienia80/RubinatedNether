package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.item.RubyCurrency;
import net.artienia.rubinated_nether.content.item.RubyLens;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import uwu.serenity.critter.api.entry.flags.EntryFlag;
import uwu.serenity.critter.creative.TabPlacement;
import uwu.serenity.critter.stdlib.items.ItemEntry;
import uwu.serenity.critter.stdlib.items.ItemRegistrar;


public final class RNItems {

    public static final ItemRegistrar ITEMS = ItemRegistrar.create(RubinatedNether.REGISTRIES);

    public static final EntryFlag GENERATE_FLAT_MODEL = EntryFlag.of(RubinatedNether.id("flat_model"));

    public static final ItemEntry<Item> RUBY = ITEMS.entry("ruby", Item::new)
        .creativeTab(CreativeModeTabs.INGREDIENTS, TabPlacement.after(Items.DIAMOND))
        .flags(GENERATE_FLAT_MODEL)
        .register();

    public static final ItemEntry<Item> MOLTEN_RUBY = ITEMS.entry("molten_ruby", Item::new)
        .creativeTab(CreativeModeTabs.INGREDIENTS, TabPlacement.after(RUBY))
        .flags(GENERATE_FLAT_MODEL)
        .register();

    public static final ItemEntry<RubyCurrency> RUBY_SHARD = ITEMS.entry("ruby_shard", RubyCurrency::new)
        .creativeTab(CreativeModeTabs.INGREDIENTS, TabPlacement.after(Items.AMETHYST_SHARD))
        .flags(GENERATE_FLAT_MODEL)
        .register();

    public static final ItemEntry<Item> MOLTEN_RUBY_NUGGET = ITEMS.entry("molten_ruby_nugget", Item::new)
            .creativeTab(CreativeModeTabs.INGREDIENTS, TabPlacement.after(RUBY_SHARD))
            .flags(GENERATE_FLAT_MODEL)
            .register();

    public static final ItemEntry<BlockItem> FROSTED_ICE = ITEMS.entry("frosted_ice", p -> new BlockItem(Blocks.FROSTED_ICE, p))
        .creativeTab(CreativeModeTabs.NATURAL_BLOCKS, TabPlacement.before(Blocks.ICE))
        .register();

    public static final ItemEntry<RubyLens> RUBY_LENS = ITEMS.entry("ruby_lens", RubyLens::new)
        .creativeTab(CreativeModeTabs.TOOLS_AND_UTILITIES, TabPlacement.after(Items.SPYGLASS))
        .flags(GENERATE_FLAT_MODEL)
        .register();

    public static final ItemEntry<Item> RUBY_ICON = ITEMS.entry("ruby_icon", Item::new)
            .flags(GENERATE_FLAT_MODEL)
            .register();

    public static final ItemEntry<RecordItem> SHIMMER_DISC = ITEMS.entry("shimmer_disc", p -> new RecordItem(15, RNSounds.SHIMMER.get(), p, 226))
        .properties(p -> p.stacksTo(1)
            .rarity(Rarity.RARE))
        .creativeTab(CreativeModeTabs.TOOLS_AND_UTILITIES, TabPlacement.after(Items.MUSIC_DISC_PIGSTEP))
        .flags(GENERATE_FLAT_MODEL)
        .register();

    public static void register() {
        ITEMS.register();
    }
}
