package corundum.rubinated_nether.content.screen;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import corundum.rubinated_nether.content.menu.coffer.RubinatedCofferMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RubinatedCofferScreen extends AbstractContainerScreen<RubinatedCofferMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/gui/coffer_gui.png");
    private static final int GHOST_OVERLAY = 0x988B8B8B;
    private static final int ZERO_COLOR = 0xFFC52425;

    public RubinatedCofferScreen(RubinatedCofferMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 153;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component label = RubinatedTextRenderer.useRubinatedLang() ? RubinatedTextRenderer.applyRubin(this.title) : this.title;
        guiGraphics.drawString(this.font, label, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);
        ItemStack cached = this.cachedFor(slot);
        if (!cached.isEmpty()) {
            guiGraphics.renderFakeItem(cached, slot.x, slot.y);
            guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, GHOST_OVERLAY);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            guiGraphics.drawString(this.font, "0", slot.x + 19 - 2 - this.font.width("0"), slot.y + 6 + 3, ZERO_COLOR, true);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            ItemStack cached = this.cachedFor(this.hoveredSlot);
            if (!cached.isEmpty()) {
                List<Component> lines = List.of(
                        Component.translatable("tooltip.rubinated_nether.coffer_cached", cached.getHoverName()).withStyle(ChatFormatting.GRAY),
                        Component.translatable("tooltip.rubinated_nether.coffer_cached_clear").withStyle(ChatFormatting.DARK_GRAY)
                );
                guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
                return;
            }
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private ItemStack cachedFor(Slot slot) {
        if (slot.index < 0 || slot.index >= CofferBlockEntity.CONTAINER_SIZE || slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        return this.menu.getCachedStack(slot.index);
    }
}