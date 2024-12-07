package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.screen.FreezerMenu;
import net.artienia.rubinated_nether.content.screen.FreezerScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import uwu.serenity.critter.api.generic.Registrar;
import uwu.serenity.critter.stdlib.extras.ExtraBuilders;
import uwu.serenity.critter.stdlib.extras.menu.MenuEntry;

public final class RNMenuTypes {
	public static final Registrar<MenuType<?>> MENUS =
			RubinatedNether.REGISTRIES.getRegistrar(Registries.MENU);

	public static final MenuEntry<FreezerMenu> FREEZER_MENU =
		ExtraBuilders.menu(MENUS, "freezer_menu", FreezerMenu::new)
			.screen(() -> FreezerScreen::new)
			.register();


	public static void register() {
		MENUS.register();
	}
}