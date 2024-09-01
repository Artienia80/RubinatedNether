package net.artienia.rubinated_nether.item;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.BlockItem;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RubinatedNether.MOD_ID);

    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOLTEN_RUBY = ITEMS.register("molten_ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RUBY_SHARD = ITEMS.register("ruby_shard",
            () -> new RubyCurrency(new Item.Properties()));

    public static final RegistryObject<Item> FROSTED_ICE = ITEMS.register("frosted_ice",
            () -> new BlockItem(Blocks.FROSTED_ICE, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
