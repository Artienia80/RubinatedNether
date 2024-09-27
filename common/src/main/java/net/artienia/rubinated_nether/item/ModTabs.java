package net.artienia.rubinated_nether.item;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.stdlib.creativeTabs.CreativeTabRegistrar;

public class ModTabs {
    public static final CreativeTabRegistrar TABS = CreativeTabRegistrar.create(RubinatedNether.REGISTRIES);
    
    public static final RegistryEntry<CreativeModeTab> MAIN = TABS.entry("main")
        .icon(ModItems.RUBY::asStack)
        .displayItems((itemDisplayParameters, output) -> {
            output.accept(ModItems.RUBY.get());
            output.accept(ModItems.MOLTEN_RUBY.get());
            output.accept(ModItems.RUBY_SHARD.get());
            output.accept(ModItems.MOLTEN_RUBY_NUGGET.get());

            output.accept(ModBlocks.NETHER_RUBY_ORE.get());
            output.accept(ModBlocks.MOLTEN_RUBY_ORE.get());
            output.accept(ModBlocks.RUBINATED_BLACKSTONE.get());

            output.accept(ModBlocks.RUBY_BLOCK.get());
            output.accept(ModBlocks.MOLTEN_RUBY_BLOCK.get());

            output.accept(ModBlocks.BLEEDING_OBSIDIAN.get());

            output.accept(ModBlocks.RUBY_GLASS.get());
            output.accept(ModBlocks.RUBY_GLASS_PANE.get());
            output.accept(ModBlocks.MOLTEN_RUBY_GLASS.get());
            output.accept(ModBlocks.MOLTEN_RUBY_GLASS_PANE.get());


            output.accept(ModBlocks.RUBY_LANTERN.get());
            output.accept(ModBlocks.RUBY_CHANDELIER.get());
            output.accept(ModBlocks.RUBY_LAVA_LAMP.get());

            output.accept(ModBlocks.FREEZER.get());
            output.accept(ModItems.FROSTED_ICE.get());

            output.accept(ModItems.RUBY_LENS.get());
            output.accept(ModBlocks.RUBY_LASER.get());
        })
        .register();

    public static void init() {
        TABS.register();
    }
}
