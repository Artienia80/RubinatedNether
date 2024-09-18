package net.artienia.rubinated_nether.screen;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(RubinatedNether.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<FreezerMenu>> FREEZER_MENU =
            register("freezer_menu", FreezerMenu::new);


    private static<T extends AbstractContainerMenu> RegistrySupplier<MenuType<T>> register(String name, MenuType.MenuSupplier<T> menu) {
        return MENUS.register(name, () -> new MenuType<>(menu, FeatureFlags.VANILLA_SET));
    }

    public static void register() {
        MENUS.register();
    }
}