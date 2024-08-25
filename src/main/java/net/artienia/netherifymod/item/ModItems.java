package net.artienia.netherifymod.item;

import net.artienia.netherifymod.NetherifyMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NetherifyMod.MOD_ID);

    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOLTEN_RUBY = ITEMS.register("molten_ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RUBY_SHARD = ITEMS.register("ruby_shard",
            () -> new RubyCurrency(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
