package corundum.rubinated_nether.content.screen;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.menu.CofferMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CofferScreen extends AbstractContainerScreen<CofferMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/gui/coffer_gui.png");

    public CofferScreen(CofferMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 149;
        // Adjust inventory label position for the shorter GUI
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}