package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Draws the name above the hotbar in the Rubinated style (replaces the former GuiMixin).
 * <p>
 * Only takes over the layer when the item needs the restyle; everything else is left to vanilla. The drawing code
 * mirrors NeoForge's patched {@code Gui#renderSelectedItemName(GuiGraphics, int)}, with the restyle applied to the
 * name component. Needs the {@code toolHighlightTimer}/{@code lastToolHighlight} entries in accesstransformer.cfg.
 */
@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public class SelectedItemNameHandler {

    @SubscribeEvent
    public static void onSelectedItemNameLayer(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.SELECTED_ITEM_NAME.equals(event.getName())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Gui gui = mc.gui;
        ItemStack stack = gui.lastToolHighlight;

        if (gui.toolHighlightTimer <= 0 || stack.isEmpty()) return;
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
        if (!RubinatedTextRenderer.useRubinatedLang()) return;
        if (!RubinatedTextRenderer.isRubinated(stack)) return;
        if (stack.has(DataComponents.CUSTOM_NAME)) return;

        event.setCanceled(true);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        mc.getProfiler().push("selectedItemName");

        MutableComponent name = Component.empty()
                .append(stack.getHoverName())
                .withStyle(stack.getRarity().getStyleModifier());
        Component tip = stack.getHighlightTip(RubinatedTextRenderer.restyleWithEnglishName(name, stack));

        int width = mc.font.width(tip);
        int x = (guiGraphics.guiWidth() - width) / 2;
        int y = guiGraphics.guiHeight() - Math.max(Math.max(gui.leftHeight, gui.rightHeight), 59);
        if (!mc.gameMode.canHurtPlayer()) {
            y += 14;
        }

        int alpha = (int) ((float) gui.toolHighlightTimer * 256.0F / 10.0F);
        if (alpha > 255) {
            alpha = 255;
        }

        if (alpha > 0) {
            Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);
            if (font == null) {
                guiGraphics.drawStringWithBackdrop(mc.font, tip, x, y, width, FastColor.ARGB32.color(alpha, -1));
            } else {
                x = (guiGraphics.guiWidth() - font.width(tip)) / 2;
                guiGraphics.drawStringWithBackdrop(font, tip, x, y, width, FastColor.ARGB32.color(alpha, -1));
            }
        }

        mc.getProfiler().pop();
    }
}
