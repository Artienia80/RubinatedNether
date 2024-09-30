package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.screen.FreezerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public final class RNMenuTypes {
    public static final Registrar<MenuType<?>> MENUS =
            RubinatedNether.REGISTRIES.getRegistrar(Registries.MENU);

    public static final RegistryEntry<MenuType<FreezerMenu>> FREEZER_MENU =
            register("freezer_menu", FreezerMenu::new);


    private static<T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> register(String name, MenuType.MenuSupplier<T> menu) {
        return MENUS.entry(name, () -> new MenuType<>(menu, FeatureFlags.VANILLA_SET)).register();
    }

    public static void register() {
        MENUS.register();
    }
}