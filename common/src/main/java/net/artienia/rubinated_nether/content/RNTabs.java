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
            output.accept(RNItems.RUBY);
            output.accept(RNItems.MOLTEN_RUBY);
            output.accept(RNItems.RUBY_SHARD);
            output.accept(RNItems.MOLTEN_RUBY_NUGGET);

            output.accept(RNItems.SHIMMER_DISC);

            output.accept(RNBlocks.NETHER_RUBY_ORE);
            output.accept(RNBlocks.MOLTEN_RUBY_ORE);
            output.accept(RNBlocks.RUBINATED_BLACKSTONE);

            output.accept(RNBlocks.RUBY_BLOCK);
            output.accept(RNBlocks.MOLTEN_RUBY_BLOCK);

            output.accept(RNBlocks.BLEEDING_OBSIDIAN);

            output.accept(RNBlocks.RUBY_GLASS);
            output.accept(RNBlocks.RUBY_GLASS_PANE);
            output.accept(RNBlocks.MOLTEN_RUBY_GLASS);
            output.accept(RNBlocks.MOLTEN_RUBY_GLASS_PANE);


            output.accept(RNBlocks.RUBY_LANTERN);
            output.accept(RNBlocks.RUBY_CHANDELIER);
            output.accept(RNBlocks.RUBY_LAVA_LAMP);

            output.accept(RNBlocks.FREEZER);
            output.accept(RNItems.FROSTED_ICE);

            output.accept(RNItems.RUBY_LENS);
            output.accept(RNBlocks.RUBY_LASER);
        })
        .register();

    public static void init() {
        if(RNConfig.enableCreativeTab) TABS.register();
    }
}
