package net.artienia.rubinated_nether.screen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.recipe.AbstractRecipeBookScreen;
import net.artienia.rubinated_nether.recipe.FreezerRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FreezerScreen extends AbstractRecipeBookScreen<FreezerMenu, FreezerRecipeBookComponent> {
    private static final ResourceLocation FREEZER_GUI_TEXTURES = new ResourceLocation(RubinatedNether.MOD_ID, "textures/gui/freezer_gui.png");

    public FreezerScreen(FreezerMenu menu, Inventory inventory, Component title) {
        super(menu, new FreezerRecipeBookComponent(), inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.initScreen(20);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.blit(FREEZER_GUI_TEXTURES, left, top, 0, 0, this.imageWidth, this.imageHeight);
        if (this.getMenu().isLit()) {
            int litProgress = this.getMenu().getLitProgress();
            guiGraphics.blit(FREEZER_GUI_TEXTURES, left + 56, top + 36 + 12 - litProgress, 176, 12 - litProgress, 14, litProgress + 1);
        }
        int burnProgress = this.getMenu().getBurnProgress();
        guiGraphics.blit(FREEZER_GUI_TEXTURES, left + 79, top + 34, 176, 14, burnProgress + 1, 16);
    }
}
