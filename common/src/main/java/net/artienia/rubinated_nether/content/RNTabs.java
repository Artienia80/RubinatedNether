package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.creative.TabPlacement;
import uwu.serenity.critter.platform.PlatformUtils;
import uwu.serenity.critter.stdlib.creativeTabs.CreativeTabRegistrar;
import uwu.serenity.critter.stdlib.items.ItemBuilder;

import java.util.function.UnaryOperator;

public final class RNTabs {
    public static final CreativeTabRegistrar TABS = CreativeTabRegistrar.create(RubinatedNether.REGISTRIES);
    
    public static final RegistryEntry<CreativeModeTab> MAIN = TABS.entry("main")
        .icon(RNItems.RUBY::asStack)
        .displayItems((itemDisplayParameters, output) -> {
            output.accept(RNItems.RUBY);
            output.accept(RNItems.MOLTEN_RUBY);
            output.accept(RNItems.RUBY_SHARD);
            output.accept(RNItems.MOLTEN_RUBY_NUGGET);

            output.accept(RNBlocks.NETHER_RUBY_ORE);
            output.accept(RNBlocks.MOLTEN_RUBY_ORE);
            output.accept(RNBlocks.RUBINATED_BLACKSTONE);

            output.accept(RNBlocks.RUBY_BLOCK);
            output.accept(RNBlocks.MOLTEN_RUBY_BLOCK);

            output.accept(RNBlocks.BLEEDING_OBSIDIAN);
			output.accept(RNBlocks.ALTAR_STONE);
			output.accept(RNBlocks.ALTAR_STONE_BRICKS);
			output.accept(RNBlocks.ALTAR_STONE_TILES);
			output.accept(RNBlocks.ALTAR_STONE_PILLAR);
			output.accept(RNBlocks.CHISELED_ALTAR_STONE_BRICKS);
            output.accept(RNBlocks.SOAKSTONE);

            output.accept(RNBlocks.RUBY_GLASS);
            output.accept(RNBlocks.RUBY_GLASS_PANE);
            output.accept(RNBlocks.MOLTEN_RUBY_GLASS);
            output.accept(RNBlocks.MOLTEN_RUBY_GLASS_PANE);
            output.accept(RNBlocks.ORNATE_RUBY_GLASS);
            output.accept(RNBlocks.ORNATE_RUBY_GLASS_PANE);




            output.accept(RNBlocks.RUBY_LANTERN);
            output.accept(RNBlocks.RUBY_CHANDELIER);
            output.accept(RNBlocks.RUBY_LAVA_LAMP);
            output.accept(RNBlocks.RUBY_BRAZIER);

            output.accept(RNBlocks.FREEZER);
            output.accept(RNItems.FROSTED_ICE);
            output.accept(RNBlocks.DRY_ICE);

            output.accept(RNItems.RUBY_LENS);
            output.accept(RNBlocks.RUBY_LASER);

            output.accept(RNBlocks.RUBINATION_ATLAR);
			output.accept(RNBlocks.RUNESTONE);

            output.accept(RNItems.SHIMMER_DISC);


            //output.accept(RNBlocks.RUBY_RAIL);
        })
        .register();

    public static final RegistryEntry<CreativeModeTab> COMPAT = TABS.entry("compat_tab")
            .icon(RNItems.RUBY_ICON::asStack)
            .displayItems((itemDisplayParameters, output) -> {
                if(PlatformUtils.modLoaded("netherexp")){
                     output.accept(BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "soul_ruby_ore")));
                }
                    })
            .register();

    public static void register() {
        if(RNConfig.enableCreativeTab) TABS.register();
        if(RNConfig.enableCompatTab) TABS.register();

    }


    public static <I extends Item, P> UnaryOperator<ItemBuilder<I, P>> compatTabIfEnabled(TabPlacement placement) {
        if(RNConfig.enableCompatTab) {
            return b -> b.creativeTab(COMPAT, placement);
        }
        return UnaryOperator.identity();
    }

    public static <I extends Item, P> UnaryOperator<ItemBuilder<I, P>> modTabIfEnabled(TabPlacement placement) {
        if(RNConfig.enableCreativeTab) {
            return b -> b.creativeTab(MAIN, placement);
        }
        return UnaryOperator.identity();
    }
}
