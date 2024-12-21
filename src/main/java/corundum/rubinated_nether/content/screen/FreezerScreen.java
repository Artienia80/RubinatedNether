package corundum.rubinated_nether.content.screen;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.menu.FreezerMenu;
import corundum.rubinated_nether.content.recipe.FreezerRecipeBookComponent;
import corundum.rubinated_nether.content.recipe.FreezingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class FreezerScreen extends AbstractRecipeBookScreen<SingleRecipeInput, FreezingRecipe, FreezerMenu, FreezerRecipeBookComponent> {
    public static final ResourceLocation FREEZER_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/gui/freezer_gui.png");

    public FreezerScreen(FreezerMenu menu, Inventory inventory, Component title) {
        super(menu, new FreezerRecipeBookComponent(), inventory, title);
    }

    @Override
    public void init() {
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