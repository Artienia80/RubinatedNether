package corundum.rubinated_nether.content.screen;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.menu.VaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VaseScreen extends AbstractContainerScreen<VaseMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = RubinatedNether.id("textures/gui/vase_gui.png");

    public VaseScreen(VaseMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        ++this.imageHeight;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}

