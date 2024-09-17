package net.artienia.rubinated_nether.item;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RubinatedNether.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () ->
            CreativeModeTab.builder().title(Component.literal("Rubinated Nether"))
                    .icon(() -> ModItems.RUBY.get().getDefaultInstance())
                    .displayItems(((itemDisplayParameters, event) -> {
                        event.accept(ModItems.RUBY.get());
                        event.accept(ModItems.MOLTEN_RUBY.get());
                        event.accept(ModItems.RUBY_SHARD.get());
                        event.accept(ModBlocks.RUBY_BLOCK.get());
                        event.accept(ModBlocks.MOLTEN_RUBY_BLOCK.get());
                        event.accept(ModBlocks.MOLTEN_RUBY_ORE.get());
                        event.accept(ModBlocks.NETHER_RUBY_ORE.get());
                        event.accept(ModBlocks.RUBINATED_BLACKSTONE.get());
                        event.accept(ModBlocks.BLEEDING_OBSIDIAN.get());
                        event.accept(ModItems.FROSTED_ICE.get());
                        event.accept(ModBlocks.RUBY_GLASS.get());
                        event.accept(ModBlocks.RUBY_GLASS_PANE.get());
                        event.accept(ModBlocks.RUBY_LAVA_LAMP.get());
                        event.accept(ModBlocks.RUBY_CHANDELIER.get());
                        event.accept(ModBlocks.RUBY_LANTERN.get());
                        event.accept(ModBlocks.FREEZER.get());
                        event.accept(ModBlocks.RUBY_LASER.get());
                            })).build());

    public static void init(IEventBus bus) {
        TABS.register(bus);
    }
}
