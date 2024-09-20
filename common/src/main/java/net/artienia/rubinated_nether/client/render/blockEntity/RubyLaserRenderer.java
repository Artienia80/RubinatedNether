package net.artienia.rubinated_nether.client.render.blockEntity;

import com.mojang.blaze3d.vertex.*;
import dev.architectury.platform.Platform;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.RubyLaserBlock;
import net.artienia.rubinated_nether.block.entity.RubyLaserBlockEntity;
import net.artienia.rubinated_nether.client.render.ShaderHelper;
import net.artienia.rubinated_nether.item.ModItems;
import net.artienia.rubinated_nether.platform.PlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RubyLaserRenderer implements BlockEntityRenderer<RubyLaserBlockEntity> {

    private static final ResourceLocation LASER_TEXTURE = RubinatedNether.id("textures/misc/ruby_laser_beam.png");

    public RubyLaserRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(RubyLaserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if(player == null || level == null) return;

        if(blockEntity.alwaysVisible() || PlatformUtils.rubyLensEquipped(player)) {

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            // laser facing
            Direction facing = blockEntity.getBlockState().getValue(RubyLaserBlock.FACING);
            Vector3f rot = new Vector3f(
                facing == Direction.UP ? 0 : (Direction.Plane.VERTICAL.test(facing) ? 180f : 90f),
                0,
                (Math.max(facing.get2DDataValue(), 0) & 3) * 90f
            );
            rot.mul(Mth.DEG_TO_RAD);
            poseStack.mulPose(new Quaternionf().rotationXYZ(rot.x, rot.y, rot.z));

            // rotating animation
            float lerpedTime = Mth.lerp(partialTick, level.getGameTime(), level.getGameTime() + 1);
            float angle = (lerpedTime * 3) % 360f;
            poseStack.mulPose(new Quaternionf().rotationY(angle * Mth.DEG_TO_RAD));

            poseStack.translate(-0.5f, -0.5f, -0.5f);

            int i = blockEntity.getBlockRange() + 2;

            // Use fallback render type if shaders in use because beacon beam broken
            RenderType renderType = ShaderHelper.isShaderPackInUse() ? RenderType.entityTranslucentCull(LASER_TEXTURE) : RenderType.beaconBeam(LASER_TEXTURE, true);
            VertexConsumer consumer = buffer.getBuffer(renderType);

            renderFace(poseStack, consumer, .4f, 1, .6f, .6f, i, .6f, 1f, 1f, 1f, lerpedTime, Direction.NORTH);
            renderFace(poseStack, consumer, .6f, 1, .4f, .4f, i, .4f, 1f, 1f, 1f, lerpedTime, Direction.SOUTH);
            renderFace(poseStack, consumer, .4f, 1, .4f, .4f, i, .6f, 1f, 1f, 1f, lerpedTime, Direction.EAST);
            renderFace(poseStack, consumer, .6f, 1, .6f, .6f, i, .4f, 1f, 1f, 1f, lerpedTime, Direction.WEST);
            poseStack.popPose();
        }

    }

    private void renderFace(PoseStack matrices, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float ticks, Direction face) {
        PoseStack.Pose pose = matrices.last();
        float maxV = maxY / 15f;
        float endAlpha = Mth.clamp(1f - maxV, 0, 1);

        float v0 = 1 - (ticks % 150f) / 150;
        float v1 = v0 + (maxV * 0.4f);

        buffer.vertex(pose.pose(), minX, minY, minZ).color(r, g, b, 1f).uv(0, v0).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
        buffer.vertex(pose.pose(), maxX, minY, maxZ).color(r, g, b, 1f).uv(1, v0).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
        buffer.vertex(pose.pose(), maxX, maxY, maxZ).color(r, g, b, endAlpha).uv(1, v1).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
        buffer.vertex(pose.pose(), minX, maxY, minZ).color(r, g, b, endAlpha).uv(0, v1).uv2(LightTexture.FULL_BRIGHT)
            .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
    }
}
