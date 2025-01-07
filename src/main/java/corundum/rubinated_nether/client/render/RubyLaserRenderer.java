package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.RubyLaserBlock;
import corundum.rubinated_nether.content.blocks.entities.RubyLaserBlockEntity;
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
import net.minecraft.world.entity.EquipmentSlot;
import org.joml.Quaternionf;

public class RubyLaserRenderer implements BlockEntityRenderer<RubyLaserBlockEntity> {
	public static final ResourceLocation LASER_TEXTURE = RubinatedNether.id("textures/misc/ruby_laser_beam.png");
	public static final ResourceLocation LASER_TEXTURE_GREYSCALE = RubinatedNether.id("textures/misc/ruby_laser_beam_greyscale.png");

	private static final int BASE_COLOR = 0xFF0000;
	private static final int TINTED_COLOR = 0x990000;

	private final Quaternionf tempQuat = new Quaternionf();

	public RubyLaserRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(RubyLaserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		LocalPlayer player = Minecraft.getInstance().player;
		ClientLevel level = Minecraft.getInstance().level;

		if(blockEntity.alwaysVisible() || player.getItemBySlot(EquipmentSlot.HEAD).is(RNItems.RUBY_LENS.get())) {

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
			int color;

			if(blockEntity.isColored()) {
				color = blockEntity.getColor().orElse(1);
			} else if(blockEntity.isSilly()) {
				float hue = lerpedTime % 50f / 50f;
				int col = Mth.hsvToRgb(hue, .8f, 1f);
				color = FastColor.ARGB32.color(255, col);
			} else {
				color = FastColor.ARGB32.color(255, blockEntity.getBlockState().getValue(RubyLaserBlock.TINTED) ? TINTED_COLOR : BASE_COLOR);
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

	private void renderFace(PoseStack matrices, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color, float ticks, Direction face) {
		PoseStack.Pose pose = matrices.last();
		float maxV = (maxY - 1f) / 15f;

		float v0 = 1 - (ticks % 150f) / 150f;
		float v1 = v0 + (maxV * 0.4f);

		buffer.addVertex(pose.pose(), minX, minY, minZ).setColor(color).setUv(0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 1)
			.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
		buffer.addVertex(pose.pose(), maxX, minY, maxZ).setColor(color).setUv(1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 1)
			.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
		buffer.addVertex(pose.pose(), maxX, maxY, maxZ).setColor(color).setUv(1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 1)
			.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
		buffer.addVertex(pose.pose(), minX, maxY, minZ).setColor(color).setUv(0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 1)
			.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
	}

	protected RenderType getRenderType(boolean colored) {
		if(ShaderHelper.isShaderPackInUse()) {
			return RenderType.entityTranslucentEmissive(colored ? LASER_TEXTURE_GREYSCALE : LASER_TEXTURE);
		} else {
			return colored ? RNRenderTypes.LASER_BEAM_GREYSCALE : RNRenderTypes.LASER_BEAM;
		}
	}
}