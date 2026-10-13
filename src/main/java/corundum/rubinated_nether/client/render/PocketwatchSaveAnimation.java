package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;
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
    private static final int FULL_FRAME = 0;
    private static final int FRAME_COUNT = 16;
    private static final int REWIND_LOOPS = 6;
    private static final int REWIND_STEPS = FRAME_COUNT * REWIND_LOOPS;

    private static final ParticleOptions GLYPH_PARTICLE = ParticleTypes.ENCHANT;

    private static boolean loggedSpawn = false;

    private static int age = -1;
    private static ItemStack stack = ItemStack.EMPTY;
    private static int shownFrame = -1;

    public static void start() {
        stack = new ItemStack(RNItems.POCKETWATCH.get());
        shownFrame = -1;
        applyFrame(FULL_FRAME);
        age = 0;
        loggedSpawn = false;
        debug("animation started, particle setting: " + Minecraft.getInstance().options.particles().get());
    }

    public static void debug(String message) {
        RubinatedNether.LOGGER.info("[PW debug] {}", message);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.literal("[PW debug] " + message), false);
        }
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

        if (age >= FLOAT_IN_END && age < BREAK_TICK) {
            spawnGlyphs(age >= (REWIND_START + BREAK_TICK) / 2 ? 3 : 2, false);
        }

        if (age == BREAK_TICK) {
            debug("break tick reached");
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GLASS_BREAK, 0.9F, 1.0F));
            spawnGlyphs(36, true);
        }
        if (age > DURATION) {
            age = -1;
            stack = ItemStack.EMPTY;
        }
    }

    private static void spawnGlyphs(int count, boolean burst) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null) {
            return;
        }

        Vec3 look = player.getLookAngle();
        Vec3 right = look.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() < 1.0E-4) {
            right = new Vec3(1.0, 0.0, 0.0);
        }
        right = right.normalize();
        Vec3 up = right.cross(look).normalize();
        Vec3 center = player.getEyePosition().add(look.scale(2.5));

        if (!loggedSpawn) {
            loggedSpawn = true;
            debug("spawning glyphs, eye=" + player.getEyePosition() + " center=" + center);
        }

        for (int i = 0; i < count; i++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
            double distance = burst
                    ? 1.3 + level.getRandom().nextDouble() * 1.1
                    : 1.1 + level.getRandom().nextDouble() * 0.6;
            Vec3 offset = right.scale(Math.cos(angle) * distance).add(up.scale(Math.sin(angle) * distance));

            if (burst) {
                Vec3 end = center.add(offset);
                level.addParticle(GLYPH_PARTICLE, end.x, end.y, end.z, -offset.x, -offset.y, -offset.z);
            } else {
                level.addParticle(GLYPH_PARTICLE, center.x, center.y, center.z, offset.x, offset.y, offset.z);
            }
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