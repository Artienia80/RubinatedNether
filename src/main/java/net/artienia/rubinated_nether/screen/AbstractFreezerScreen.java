package net.artienia.rubinated_nether.screen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.recipe.AbstractRecipeBookScreen;
import net.artienia.rubinated_nether.recipe.FreezerRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbstractFreezerScreen<T extends FreezerMenu> extends AbstractRecipeBookScreen<T, AbstractFurnaceRecipeBookComponent> {
    private final ResourceLocation texture;

    public AbstractFreezerScreen(T menu, AbstractFurnaceRecipeBookComponent recipeBook, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(menu, recipeBook, playerInventory, title);
        this.texture = texture;
    }

    @Override
    public void init() {
        super.init();
        this.initScreen(20);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        guiGraphics.blit(this.texture, left, top, 0, 0, this.getXSize(), this.getYSize());
        if (this.getMenu().isLit()) {
            int litProgress = this.getMenu().getLitProgress();
            guiGraphics.blit(this.texture, left + 56, top + 36 + 12 - litProgress, 176, 12 - litProgress, 14, litProgress + 1);
        }
        int burnProgress = this.getMenu().getBurnProgress();
        guiGraphics.blit(this.texture, left + 79, top + 34, 176, 14, burnProgress + 1, 16);
    }
}
