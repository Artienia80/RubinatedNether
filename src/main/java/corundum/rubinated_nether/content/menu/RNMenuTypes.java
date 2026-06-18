package corundum.rubinated_nether.content.menu;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.menu.coffer.CofferMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENUS =DeferredRegister.create(
		BuiltInRegistries.MENU, 
		RubinatedNether.MODID
	);

	public static final DeferredHolder<MenuType<?>, MenuType<FreezerMenu>> FREEZER_MENU = MENUS.register(
			"freezer_menu",
			() -> new MenuType<>(
					FreezerMenu::new,
					FeatureFlags.VANILLA_SET
			)
	);

	public static final DeferredHolder<MenuType<?>, MenuType<RubinationMenu>> RUBINATION_MENU = MENUS.register(
			"rubination_menu",
			() -> new MenuType<>(
					RubinationMenu::new,
					FeatureFlags.VANILLA_SET
			)
	);

	public static final DeferredHolder<MenuType<?>, MenuType<CofferMenu>> COFFER_MENU = MENUS.register(
			"coffer_menu",
			() -> new MenuType<>(
					CofferMenu::new,
					FeatureFlags.VANILLA_SET
			)
	);

    public static final DeferredHolder<MenuType<?>, MenuType<VaseMenu>> VASE_MENU = MENUS.register(
            "vase_menu",
            () -> new MenuType<>(
                    VaseMenu::new,
                    FeatureFlags.VANILLA_SET
            )
    );
}
