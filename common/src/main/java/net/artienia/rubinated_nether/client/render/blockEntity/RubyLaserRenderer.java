package net.artienia.rubinated_nether.client.render.blockEntity;

import com.mojang.blaze3d.vertex.*;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.block.ruby_laser.RubyLaserBlock;
import net.artienia.rubinated_nether.content.block.ruby_laser.RubyLaserBlockEntity;
import net.artienia.rubinated_nether.client.render.RNRenderTypes;
import net.artienia.rubinated_nether.client.render.ShaderHelper;
import net.artienia.rubinated_nether.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class RubyLaserRenderer implements BlockEntityRenderer<RubyLaserBlockEntity> {

    public static final ResourceLocation LASER_TEXTURE = RubinatedNether.id("textures/misc/ruby_laser_beam.png");
    public static final ResourceLocation LASER_TEXTURE_GREYSCALE = RubinatedNether.id("textures/misc/ruby_laser_beam_greyscale.png");

    private static final float[] BASE_COLOR = new float[]{1f, 1f, 1f};
    private static final float[] TINTED_COLOR = new float[]{1f, 0, .5f};

    private final Quaternionf tempQuat = new Quaternionf();

    public RubyLaserRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(RubyLaserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if(player == null || level == null) return;

        if(blockEntity.alwaysVisible() || Platform.rubyLensEquipped(player)) {

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            // laser facing
            Direction facing = blockEntity.getBlockState().getValue(RubyLaserBlock.FACING);
            float xRot = (facing == Direction.UP) ? 0 : (Direction.Plane.VERTICAL.test(facing) ? 180f : 90f);
            float zRot = (Math.max(facing.get2DDataValue(), 0) & 3) * 90f;

            tempQuat.rotationXYZ(xRot * Mth.DEG_TO_RAD, 0, zRot * Mth.DEG_TO_RAD);

            // rotating animation
            float lerpedTime = Mth.lerp(partialTick, level.getGameTime(), level.getGameTime() + 1);
            float angle = (lerpedTime * 3) % 360f;
            tempQuat.rotateY(angle * Mth.DEG_TO_RAD);

            poseStack.mulPose(tempQuat);
            poseStack.translate(-0.5f, -0.5f, -0.5f);

            float maxY = (float) (blockEntity.getRenderRange() + 1f);
            float[] color;

            if(blockEntity.isColored()) {
                color = blockEntity.getColor();
            } else if(blockEntity.isSilly()) {
                float hue = lerpedTime % 50f / 50f;
                int col = Mth.hsvToRgb(hue, .8f, 1f);
                color = new float[]{FastColor.ARGB32.red(col), FastColor.ARGB32.green(col) , FastColor.ARGB32.blue(col)};
            } else {
                color = blockEntity.getBlockState().getValue(RubyLaserBlock.TINTED) ? TINTED_COLOR : BASE_COLOR;
            }

            // Use fallback render type if shaders in use because beacon beam broken
            VertexConsumer consumer = buffer.getBuffer(getRenderType(blockEntity.isColored() || blockEntity.isSilly()));

            renderFace(poseStack, consumer, .4f, 1, .6f, .6f, maxY, .6f, color, lerpedTime, Direction.NORTH);
            renderFace(poseStack, consumer, .6f, 1, .4f, .4f, maxY, .4f, color, lerpedTime, Direction.SOUTH);
            renderFace(poseStack, consumer, .4f, 1, .4f, .4f, maxY, .6f, color, lerpedTime, Direction.EAST);
            renderFace(poseStack, consumer, .6f, 1, .6f, .6f, maxY, .4f, color, lerpedTime, Direction.WEST);
            poseStack.popPose();
        }

    }

    private void renderFace(PoseStack matrices, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float[] color, float ticks, Direction face) {
        PoseStack.Pose pose = matrices.last();
        float maxV = (maxY - 1f) / 15f;
        float endAlpha = Mth.clamp(1f - maxV, 0, 1);

        float v0 = 1 - (ticks % 150f) / 150f;
        float v1 = v0 + (maxV * 0.4f);
        float r = color[0], g = color[1], b = color[2];

        buffer.vertex(pose.pose(), minX, minY, minZ).color(r, g, b, 1f).uv(0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
        buffer.vertex(pose.pose(), maxX, minY, maxZ).color(r, g, b, 1f).uv(1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
        buffer.vertex(pose.pose(), maxX, maxY, maxZ).color(r, g, b, endAlpha).uv(1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
        buffer.vertex(pose.pose(), minX, maxY, minZ).color(r, g, b, endAlpha).uv(0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
    }

    protected RenderType getRenderType(boolean colored) {
        if(ShaderHelper.isShaderPackInUse()) {
            return RenderType.entityTranslucentEmissive(colored ? LASER_TEXTURE_GREYSCALE : LASER_TEXTURE);
        } else {
            return colored ? RNRenderTypes.LASER_BEAM_GREYSCALE : RNRenderTypes.LASER_BEAM;
        }
    }
}
