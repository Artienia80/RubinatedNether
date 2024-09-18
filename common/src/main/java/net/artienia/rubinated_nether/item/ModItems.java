package net.artienia.rubinated_nether.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(RubinatedNether.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> MOLTEN_RUBY = ITEMS.register("molten_ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> RUBY_SHARD = ITEMS.register("ruby_shard",
            () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> FROSTED_ICE = ITEMS.register("frosted_ice",
            () -> new BlockItem(Blocks.FROSTED_ICE, new Item.Properties()));

    public static void register() {
        ITEMS.register();
    }
}
