package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import javax.annotation.Nullable;

/**
 * The anvil's name field for strongboxes: it stays greyed out until a fitting key is in the second slot, and a
 * strongbox with a password shows its name as scrambled Rubin letters (never the real name) until then.
 */
@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public final class StrongboxAnvilClient {
    private static final ResourceLocation TEXT_FIELD_DISABLED = ResourceLocation.withDefaultNamespace("container/anvil/text_field_disabled");
    private static final int DISABLED_TEXT_COLOR = 0x707070;

    @Nullable
    private static AnvilScreen trackedScreen;
    private static StrongboxLock.AnvilState lastState = StrongboxLock.AnvilState.FREE;

    private StrongboxAnvilClient() {}

    private static StrongboxLock.AnvilState stateOf(AnvilScreen screen) {
        ItemStack left = screen.getMenu().getSlot(0).getItem();
        ItemStack right = screen.getMenu().getSlot(1).getItem();
        return StrongboxLock.anvilState(left, right);
    }

    // Runs right before the screen is drawn, so the real name is never on screen, not even for a frame.
    @SubscribeEvent
    public static void onRenderPre(ScreenEvent.Render.Pre event) {
        if (!(event.getScreen() instanceof AnvilScreen screen)) {
            return;
        }
        if (trackedScreen != screen) {
            trackedScreen = screen;
            lastState = StrongboxLock.AnvilState.FREE;
        }

        StrongboxLock.AnvilState state = stateOf(screen);
        EditBox box = screen.name;
        ItemStack left = screen.getMenu().getSlot(0).getItem();

        switch (state) {
            case LOCKED -> {
                if (!box.getValue().isEmpty()) {
                    box.setValue("");
                }
                box.setEditable(false);
            }
            case NEEDS_KEY -> box.setEditable(false);
            case FREE -> {
                if (lastState != StrongboxLock.AnvilState.FREE) {
                    box.setValue(left.isEmpty() ? "" : left.getHoverName().getString());
                    box.setEditable(!left.isEmpty());
                }
            }
        }
        lastState = state;
    }

    // Covers the field with the greyed-out look and draws the scrambled (or plain) name in it.
    @SubscribeEvent
    public static void onRenderPost(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AnvilScreen screen)) {
            return;
        }
        StrongboxLock.AnvilState state = stateOf(screen);
        if (state == StrongboxLock.AnvilState.FREE) {
            return;
        }

        ItemStack left = screen.getMenu().getSlot(0).getItem();
        Component text = state == StrongboxLock.AnvilState.LOCKED
                ? StrongboxPasswordText.obscuredName(left)
                : left.getHoverName();

        GuiGraphics graphics = event.getGuiGraphics();
        int x = screen.getGuiLeft();
        int y = screen.getGuiTop();
        graphics.blitSprite(TEXT_FIELD_DISABLED, x + 59, y + 20, 110, 16);
        graphics.drawString(Minecraft.getInstance().font, text, x + 62, y + 24, DISABLED_TEXT_COLOR);
    }
}
