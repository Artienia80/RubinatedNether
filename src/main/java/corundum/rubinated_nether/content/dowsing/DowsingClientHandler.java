package corundum.rubinated_nether.content.dowsing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public class DowsingClientHandler {
    private static final float HAND_X = 0.32F;
    private static final float HAND_Y = -0.4F;
    private static final float HAND_Z = -0.55F;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            DowsingClient.reset();
            return;
        }
        DowsingClient.tickAll(minecraft.level.players());
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        DowsingClient.reset();
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !DowsingClient.isActive(player) || !DowsingRods.isRod(event.getItemStack())) {
            return;
        }

        float partialTick = event.getPartialTick();
        boolean mainHand = event.getHand() == InteractionHand.MAIN_HAND;
        boolean rightSide = mainHand == (player.getMainArm() == HumanoidArm.RIGHT);
        float side = rightSide ? 1.0F : -1.0F;
        float angle = DowsingClient.getAngle(player, partialTick);

        float swing = player.getAttackAnim(partialTick);
        float sqrtSwing = Mth.sqrt(swing);
        float swingX = -0.4F * Mth.sin(sqrtSwing * Mth.PI);
        float swingY = 0.2F * Mth.sin(sqrtSwing * Mth.TWO_PI);
        float swingZ = -0.2F * Mth.sin(swing * Mth.PI);
        float swingYaw = Mth.sin(swing * swing * Mth.PI);
        float swingRoll = Mth.sin(sqrtSwing * Mth.PI);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(side * swingX, swingY, swingZ);
        poseStack.translate(side * HAND_X, HAND_Y - 0.6F * event.getEquipProgress() + (mainHand ? DowsingRods.ROD_PIXEL : 0.0F), HAND_Z);
        poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + swingYaw * -20.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(side * swingRoll * -20.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(swingRoll * -80.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(side * angle));
        poseStack.translate(0.0F, 0.0F, -DowsingRods.ROD_HALF_LENGTH);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        poseStack.scale(DowsingRods.ROD_SCALE, DowsingRods.ROD_SCALE, DowsingRods.ROD_SCALE);

        boolean arrived = DowsingClient.isArrived(player);
        minecraft.getItemRenderer().renderStatic(
                player,
                arrived ? DowsingRods.withGlint(event.getItemStack()) : event.getItemStack(),
                ItemDisplayContext.NONE,
                !rightSide,
                poseStack,
                event.getMultiBufferSource(),
                minecraft.level,
                event.getPackedLight(),
                OverlayTexture.NO_OVERLAY,
                player.getId()
        );

        poseStack.popPose();
        event.setCanceled(true);
    }
}
