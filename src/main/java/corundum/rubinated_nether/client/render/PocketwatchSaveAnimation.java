package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public class PocketwatchSaveAnimation {

    private static final ResourceLocation LAYER_ID = RubinatedNether.id("pocketwatch_save");

    private static final int DURATION = 50;
    private static final int FLOAT_IN_END = 7;
    private static final int REWIND_START = 10;
    private static final int BREAK_TICK = 35;
    private static final int FADE_START = 46;
    private static final int FULL_FRAME = 15;
    private static final int FRAME_COUNT = 16;
    private static final int REWIND_LOOPS = 6;
    private static final int REWIND_STEPS = FULL_FRAME + FRAME_COUNT * (REWIND_LOOPS - 1);

    private static int age = -1;
    private static ItemStack stack = ItemStack.EMPTY;
    private static int shownFrame = -1;

    public static void start() {
        stack = new ItemStack(RNItems.POCKETWATCH.get());
        shownFrame = -1;
        applyFrame(FULL_FRAME);
        age = 0;
    }

    private static void applyFrame(int frame) {
        if (frame != shownFrame) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(frame));
            shownFrame = frame;
        }
    }

    @SubscribeEvent
    public static void onRegisterLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, PocketwatchSaveAnimation::render);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (age < 0) {
            return;
        }
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        age++;
        if (age == BREAK_TICK) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GLASS_BREAK, 0.9F, 1.0F));
        }
        if (age > DURATION) {
            age = -1;
            stack = ItemStack.EMPTY;
        }
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (age < 0 || stack.isEmpty()) {
            return;
        }

        float t = Math.min(DURATION, age + deltaTracker.getGameTimeDeltaPartialTick(true));
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        float maxScale = height * 0.5F / 16.0F;
        float centerX = width / 2.0F;
        float centerY = height / 2.0F;

        float x = centerX;
        float y = centerY;
        float scale = maxScale;

        if (t < FLOAT_IN_END) {
            float u = t / FLOAT_IN_END;
            float eased = 1.0F - (1.0F - u) * (1.0F - u) * (1.0F - u);
            y = Mth.lerp(eased, height * 0.8F, centerY);
            scale = maxScale * (0.3F + 0.7F * eased);
        } else {
            y = centerY + Mth.sin(t * 0.6F) * height * 0.006F;
        }

        int frame = FULL_FRAME;
        if (t >= BREAK_TICK) {
            frame = PocketwatchItem.BROKEN_MODEL_VALUE;
        } else if (t > REWIND_START) {
            float u = Mth.clamp((t - REWIND_START) / (BREAK_TICK - REWIND_START), 0.0F, 1.0F);
            float curve = (float) Math.pow(u, 2.4D);
            frame = Math.floorMod(FULL_FRAME - (int) (curve * REWIND_STEPS), FRAME_COUNT);
            float shake = curve * height * 0.012F;
            x += Mth.sin(t * 3.1F) * shake;
            y += Mth.cos(t * 4.3F) * shake;
        }

        if (t >= BREAK_TICK) {
            float since = t - BREAK_TICK;
            float jolt = Math.max(0.0F, 1.0F - since / 4.0F);
            scale *= 1.0F + 0.18F * jolt;
            x += Mth.sin(since * 5.0F) * jolt * height * 0.015F;

            float flash = Math.max(0.0F, 1.0F - since / 2.5F);
            if (flash > 0.0F) {
                int alpha = (int) (flash * 140.0F);
                graphics.fill(0, 0, width, height, (alpha << 24) | 0xFFFFFF);
            }
        }

        if (t > FADE_START) {
            float fade = Mth.clamp((t - FADE_START) / (DURATION - FADE_START), 0.0F, 1.0F);
            scale *= 1.0F - fade * fade;
            y += fade * height * 0.05F;
        }

        applyFrame(frame);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0.0F);
        pose.scale(scale, scale, scale);
        pose.translate(-8.0F, -8.0F, 0.0F);
        graphics.renderItem(stack, 0, 0);
        pose.popPose();
    }
}