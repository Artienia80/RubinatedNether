package net.artienia.rubinated_nether.screen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.recipe.FreezerRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FreezerScreen extends AbstractFreezerScreen<FreezerMenu> {
    private static final ResourceLocation FREEZER_GUI_TEXTURES = new ResourceLocation(RubinatedNether.MOD_ID, "textures/gui/freezer_gui.png");

    public FreezerScreen(FreezerMenu menu, Inventory inventory, Component title) {
        super(menu, new FreezerRecipeBookComponent(), inventory, title, FREEZER_GUI_TEXTURES);
    }
}
