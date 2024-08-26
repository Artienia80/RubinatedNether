package net.artienia.rubinated_nether.screen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RubinatedNether.MOD_ID);

    public static final RegistryObject<MenuType<FreezerMenu>> FREEZER_MENU =
            register("freezer_menu", FreezerMenu::new);


    private static<T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> menu) {
        return MENUS.register(name, () -> new MenuType<>(menu, FeatureFlags.VANILLA_SET));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}