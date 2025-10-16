package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.CopperLaserBlock;
import corundum.rubinated_nether.content.blocks.entities.CopperLaserBlockEntity;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;

public class CopperLaserRenderer implements BlockEntityRenderer<CopperLaserBlockEntity> {
	public static final ResourceLocation LASER_TEXTURE = RubinatedNether.id("textures/misc/copper_laser_beam.png");
	public static final ResourceLocation LASER_TEXTURE_GREYSCALE = RubinatedNether.id("textures/misc/laser_beam_greyscale.png");

	private static final int BASE_COLOR = 0x00FF00;
	private static final int ULTRAVIOLET_COLOR = 0x6600AA;  // Lighter purple (was 0x330033)
	private static final int INFRARED_COLOR = 0x990033;     // Lighter crimson (was 0x330000)

	private final Quaternionf tempQuat = new Quaternionf();

	public CopperLaserRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(
			CopperLaserBlockEntity blockEntity,
			float partialTick,
			PoseStack poseStack,
			MultiBufferSource buffer,
			int packedLight,
			int packedOverlay
	) {
		var player = Minecraft.getInstance().player;
		var level = Minecraft.getInstance().level;

		if (blockEntity.alwaysVisible() || player.getItemBySlot(EquipmentSlot.HEAD).is(RNItems.RUBY_LENS.get())) {

			poseStack.pushPose();
			poseStack.translate(0.5f, 0.5f, 0.5f);

			// laser facing
			Direction facing = blockEntity.getBlockState().getValue(CopperLaserBlock.FACING);
			var xRot = (facing == Direction.UP) ? 0 : (Direction.Plane.VERTICAL.test(facing) ? 180f : 90f);
			var zRot = (Math.max(facing.get2DDataValue(), 0) & 3) * 90f;

			tempQuat.rotationXYZ(xRot * Mth.DEG_TO_RAD, 0, zRot * Mth.DEG_TO_RAD);

			// rotating animation with different speeds for UV/IR
			var lerpedTime = Mth.lerp(partialTick, level.getGameTime(), level.getGameTime() + 1);
			float angle;

			CopperLaserBlock.LaserMode mode = blockEntity.getBlockState().getValue(CopperLaserBlock.MODE);
			if (mode == CopperLaserBlock.LaserMode.ULTRAVIOLET) {
				// UV spins twice as fast
				angle = -(lerpedTime * 6) % 360f;
			} else if (mode == CopperLaserBlock.LaserMode.INFRARED) {
				// IR spins half as fast
				angle = -(lerpedTime * 1.5f) % 360f;
			} else {
				// Normal speed for spectrum and other modes
				angle = -(lerpedTime * 3) % 360f;
			}

			tempQuat.rotateY(angle * Mth.DEG_TO_RAD);

			poseStack.mulPose(tempQuat);
			poseStack.translate(-0.5f, -0.5f, -0.5f);

			var maxY = (float) (blockEntity.getRenderRange() + 1f);

			int color;
			boolean useGrayscale = false;
			if (blockEntity.isColored()) {
				color = blockEntity.getColor().orElse(BASE_COLOR);
			} else if(blockEntity.isSilly()) {
				float hue = lerpedTime % 50f / 50f;
				int col = Mth.hsvToRgb(hue, .8f, 1f);
				color = FastColor.ARGB32.color(255, col);
			} else {
				if (mode == CopperLaserBlock.LaserMode.SPECTRUM) {
					color = FastColor.ARGB32.color(255, 255, 255, 255); // White - no tinting
				} else if (mode == CopperLaserBlock.LaserMode.ULTRAVIOLET) {
					color = FastColor.ARGB32.color(255, ULTRAVIOLET_COLOR);
					useGrayscale = true;
				} else { // INFRARED
					color = FastColor.ARGB32.color(255, INFRARED_COLOR);
					useGrayscale = true;
				}
			}

			// Use fallback render type if shaders in use because beacon beam broken
			var consumer = buffer.getBuffer(getRenderType(blockEntity.isColored() || blockEntity.isSilly() || useGrayscale));

			renderFace(poseStack, consumer, .4f, 1, .6f, .6f, maxY, .6f, color, lerpedTime, Direction.NORTH, blockEntity);
			renderFace(poseStack, consumer, .6f, 1, .4f, .4f, maxY, .4f, color, lerpedTime, Direction.SOUTH, blockEntity);
			renderFace(poseStack, consumer, .4f, 1, .4f, .4f, maxY, .6f, color, lerpedTime, Direction.EAST, blockEntity);
			renderFace(poseStack, consumer, .6f, 1, .6f, .6f, maxY, .4f, color, lerpedTime, Direction.WEST, blockEntity);
			poseStack.popPose();
		}
	}

	private void renderFace(
			PoseStack matrices,
			VertexConsumer buffer,
			float minX,
			float minY,
			float minZ,
			float maxX,
			float maxY,
			float maxZ,
			int color,
			float ticks,
			Direction face,
			CopperLaserBlockEntity blockEntity
	) {
		PoseStack.Pose pose = matrices.last();
		var maxV = (maxY - 1f) / 15f;

		var v0 = 1 - (ticks % 150f) / 150f;
		var v1 = v0 + (maxV * 0.4f);

		// Copper laser always has 15 block range
		var maxRange = 15f;

		// Check if we're in UV or IR mode for special transparency handling
		CopperLaserBlock.LaserMode mode = blockEntity.getBlockState().getValue(CopperLaserBlock.MODE);
		boolean isUVorIR = (mode == CopperLaserBlock.LaserMode.ULTRAVIOLET || mode == CopperLaserBlock.LaserMode.INFRARED);

		// Calculate transparency
		var distance = Math.min(maxRange, maxY - 1f); // Clamp to 0-maxRange
		float alphaMultiplier;

		if (isUVorIR) {
			// For UV/IR: Start from 50% (0.5) and end at 0%
			alphaMultiplier = Math.max(0f, 0.5f * (1f - (distance / maxRange)));
		} else {
			// For other modes: Start from 100% and end at 0%
			alphaMultiplier = Math.max(0f, 1f - (distance / maxRange));
		}

		var startAlpha = isUVorIR ? (int)(255 * 0.5f) : 255; // 50% for UV/IR, 100% for others
		var endAlpha = (int)(255 * alphaMultiplier);

		// Apply alpha to start color for UV/IR modes
		var startColor = isUVorIR ? FastColor.ARGB32.color(
				startAlpha,
				FastColor.ARGB32.red(color),
				FastColor.ARGB32.green(color),
				FastColor.ARGB32.blue(color)
		) : color;

		var endColor = FastColor.ARGB32.color(
				endAlpha,
				FastColor.ARGB32.red(color),
				FastColor.ARGB32.green(color),
				FastColor.ARGB32.blue(color)
		);

		buffer.addVertex(pose.pose(), minX, minY, minZ)
				.setColor(startColor)
				.setUv(0, v0)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setUv2(LightTexture.FULL_BRIGHT,1)
				.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());

		buffer.addVertex(pose.pose(), maxX, minY, maxZ)
				.setColor(startColor)
				.setUv(1, v0)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setUv2(LightTexture.FULL_BRIGHT,1)
				.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());

		buffer.addVertex(pose.pose(), maxX, maxY, maxZ)
				.setColor(endColor)
				.setUv(1, v1)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setUv2(LightTexture.FULL_BRIGHT,200)
				.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());

		buffer.addVertex(pose.pose(), minX, maxY, minZ)
				.setColor(endColor)
				.setUv(0, v1)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setUv2(LightTexture.FULL_BRIGHT, 200)
				.setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
	}

	public AABB getRenderBoundingBox(CopperLaserBlockEntity blockEntity) {
		var facing = blockEntity.getBlockState().getValue(CopperLaserBlock.FACING);
		var end = facing.getNormal().multiply(blockEntity.getCurrentRange() + 1);
		return new AABB(blockEntity.getBlockPos()).expandTowards(end.getX(), end.getY(), end.getZ());
	}

	protected RenderType getRenderType(boolean colored) {
		if (ShaderHelper.isShaderPackInUse())
			return RenderType.entityTranslucentEmissive(colored ? LASER_TEXTURE_GREYSCALE : LASER_TEXTURE);
		else
			// FIX: Use copper-specific render types instead of shared ones
			return colored ? RNRenderTypes.COPPER_LASER_BEAM_GRAYSCALE : RNRenderTypes.COPPER_LASER_BEAM;
	}
}