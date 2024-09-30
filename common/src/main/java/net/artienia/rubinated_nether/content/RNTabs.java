package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.world.item.CreativeModeTab;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.stdlib.creativeTabs.CreativeTabRegistrar;

public final class RNTabs {
    public static final CreativeTabRegistrar TABS = CreativeTabRegistrar.create(RubinatedNether.REGISTRIES);
    
    public static final RegistryEntry<CreativeModeTab> MAIN = TABS.entry("main")
        .icon(RNItems.RUBY::asStack)
        .displayItems((itemDisplayParameters, output) -> {
            output.accept(RNItems.RUBY.get());
            output.accept(RNItems.MOLTEN_RUBY.get());
            output.accept(RNItems.RUBY_SHARD.get());
            output.accept(RNItems.MOLTEN_RUBY_NUGGET.get());

            output.accept(RNItems.SHIMMER_DISC.get());

            output.accept(RNBlocks.NETHER_RUBY_ORE.get());
            output.accept(RNBlocks.MOLTEN_RUBY_ORE.get());
            output.accept(RNBlocks.RUBINATED_BLACKSTONE.get());

            output.accept(RNBlocks.RUBY_BLOCK.get());
            output.accept(RNBlocks.MOLTEN_RUBY_BLOCK.get());

            output.accept(RNBlocks.BLEEDING_OBSIDIAN.get());

            output.accept(RNBlocks.RUBY_GLASS.get());
            output.accept(RNBlocks.RUBY_GLASS_PANE.get());
            output.accept(RNBlocks.MOLTEN_RUBY_GLASS.get());
            output.accept(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get());


            output.accept(RNBlocks.RUBY_LANTERN.get());
            output.accept(RNBlocks.RUBY_CHANDELIER.get());
            output.accept(RNBlocks.RUBY_LAVA_LAMP.get());

            output.accept(RNBlocks.FREEZER.get());
            output.accept(RNItems.FROSTED_ICE.get());

            output.accept(RNItems.RUBY_LENS.get());
            output.accept(RNBlocks.RUBY_LASER.get());
        })
        .register();

    public static void init() {
        if(RNConfig.enableCreativeTab) TABS.register();
    }
}
