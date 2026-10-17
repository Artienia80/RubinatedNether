package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
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

    private static final int GLYPH_COUNT = 300;
    private static final double GLYPH_MIN_RADIUS = 3.0;
    private static final double GLYPH_MAX_RADIUS = 5.0;
    private static final double SPIRIT_RADIUS = 4.0;
    private static final int SPIRITS_PER_TICK = 3;
    private static final double SPIRIT_RISE_SPEED = 0.03;

    private static int age = -1;
    private static ItemStack stack = ItemStack.EMPTY;
    private static int shownFrame = -1;

    public static void start() {
        stack = new ItemStack(RNItems.POCKETWATCH.get());
        shownFrame = -1;
        applyFrame(FULL_FRAME);
        age = 0;
        debug("animation started");
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

        if (age == 1) {
            spawnGlyphSphere();
        }

        if (age >= 1 && age < BREAK_TICK) {
            spawnSpirits(SPIRITS_PER_TICK);
        }

        if (age == BREAK_TICK) {
            debug("break tick reached");
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.GLASS_BREAK, 0.9F, 1.0F));
        }
        if (age > DURATION) {
            age = -1;
            stack = ItemStack.EMPTY;
        }
    }

    /** Rubin script glyphs: start at the player, shoot out to random points on a sphere of radius 3-5. */
    private static void spawnGlyphSphere() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null) {
            return;
        }

        Vec3 origin = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);

        for (int i = 0; i < GLYPH_COUNT; i++) {
            double theta = level.getRandom().nextDouble() * Math.PI * 2.0;
            double cosPhi = level.getRandom().nextDouble() * 2.0 - 1.0;
            double sinPhi = Math.sqrt(1.0 - cosPhi * cosPhi);
            double radius = GLYPH_MIN_RADIUS + level.getRandom().nextDouble() * (GLYPH_MAX_RADIUS - GLYPH_MIN_RADIUS);

            double ox = sinPhi * Math.cos(theta) * radius;
            double oy = cosPhi * radius;
            double oz = sinPhi * Math.sin(theta) * radius;

            // velocity = target offset from the origin (see RubinGlyphParticle)
            spawnParticle(RNParticleTypes.RUBIN_GLYPH.get(), origin.x, origin.y, origin.z, ox, oy, oz);
        }
    }

    /** Ruby Spirit particles: appear on the ground around the player and rise slowly. */
    private static void spawnSpirits(int count) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null) {
            return;
        }

        for (int i = 0; i < count; i++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
            double distance = SPIRIT_RADIUS * Math.sqrt(level.getRandom().nextDouble());
            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;
            double y = groundY(level, x, player.getY(), z);
            if (Double.isNaN(y)) {
                continue;
            }
            spawnParticle(RNParticleTypes.RUBY_SPIRIT.get(), x, y + 0.05, z, 0.0, SPIRIT_RISE_SPEED, 0.0);
        }
    }

    // Scans down from just above the player's feet for the top of the first collidable block.
    private static double groundY(ClientLevel level, double x, double startY, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(x), Mth.floor(startY) + 1, Mth.floor(z));
        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
            return Double.NaN;
        }
        for (int i = 0; i < 12; i++) {
            pos.move(Direction.DOWN);
            VoxelShape shape = level.getBlockState(pos).getCollisionShape(level, pos);
            if (!shape.isEmpty()) {
                return pos.getY() + shape.max(Direction.Axis.Y);
            }
        }
        return Double.NaN;
    }

    // Same call the Brazier uses for Ruby Spirit; goes straight to the particle engine.
    private static void spawnParticle(ParticleOptions type, double x, double y, double z, double vx, double vy, double vz) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.particleEngine != null) {
            mc.particleEngine.createParticle(type, x, y, z, vx, vy, vz);
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