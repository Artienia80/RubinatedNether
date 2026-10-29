package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.LaserMode;
import corundum.rubinated_nether.content.blocks.entities.AbstractLaserBlockEntity;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;

public abstract class AbstractLaserRenderer<T extends AbstractLaserBlockEntity> implements BlockEntityRenderer<T> {
	private static final int ULTRAVIOLET_COLOR = 0x6600AA;  // Lighter purple (was 0x330033)
	private static final int INFRARED_COLOR = 0x990033;     // Lighter crimson (was 0x330000)

	private final Quaternionf tempQuat = new Quaternionf();

	protected abstract float getSpinDirection();

	protected abstract int getSillyColor(float ticks);

	protected abstract float getFadeRange(T blockEntity);

	protected abstract RenderType getRenderType(boolean colored);

	@Override
	public void render(
			T blockEntity,
			float partialTick,
			PoseStack poseStack,
			MultiBufferSource buffer,
			int packedLight,
			int packedOverlay
	) {
		var player = Minecraft.getInstance().player;

		boolean hasLens = player.getItemBySlot(EquipmentSlot.HEAD).is(RNItems.RUBY_LENS.get());

		if (blockEntity.alwaysVisible() || hasLens) {

			poseStack.pushPose();
			poseStack.translate(0.5f, 0.5f, 0.5f);

			// laser facing
			Direction facing = blockEntity.getFacing();
			var xRot = (facing == Direction.UP) ? 0 : (Direction.Plane.VERTICAL.test(facing) ? 180f : 90f);
			var zRot = (Math.max(facing.get2DDataValue(), 0) & 3) * 90f;

			tempQuat.rotationXYZ(xRot * Mth.DEG_TO_RAD, 0, zRot * Mth.DEG_TO_RAD);

			// rotating animation with different speeds for UV/IR
			// Use a smooth client-side clock (in ticks) instead of level.getGameTime(): on a dedicated server
			// the client's game time is only resynced by the server in coarse steps, which made the spin choppy.
			// 7200 ticks is a whole multiple of every rotation / texture / palette period used below.
			float lerpedTime = (float) ((Util.getMillis() % 360000L) / 50.0);
			float angle;

			LaserMode mode = blockEntity.getMode();
			if (mode == LaserMode.ULTRAVIOLET) {
				// UV spins twice as fast
				angle = getSpinDirection() * (lerpedTime * 6) % 360f;
			} else if (mode == LaserMode.INFRARED) {
				// IR spins half as fast
				angle = getSpinDirection() * (lerpedTime * 1.5f) % 360f;
			} else {
				// Normal speed for spectrum and other modes
				angle = getSpinDirection() * (lerpedTime * 3) % 360f;
			}

			tempQuat.rotateY(angle * Mth.DEG_TO_RAD);

			poseStack.mulPose(tempQuat);
			poseStack.translate(-0.5f, -0.5f, -0.5f);

			var maxY = (float) (blockEntity.getRenderRange() + 1f);
			var startY = hasLens ? 1f : (float) blockEntity.getVisibleStart();

			List<AbstractLaserBlockEntity.BeamSegment> segments = blockEntity.getBeamSegments();
			var current = new AbstractLaserBlockEntity.BeamSegment(0d, Optional.empty(), false);
			int next = 0;
			while (next < segments.size() && (float) segments.get(next).start() <= startY) {
				current = segments.get(next++);
			}

			AbstractLaserBlockEntity.BeamSegment firstDrawn = null;
			AbstractLaserBlockEntity.BeamSegment lastDrawn = null;
			float firstY = startY;
			float lastY = startY;

			float cursor = startY;
			while (cursor < maxY) {
				float end = next < segments.size() ? Math.min(maxY, (float) segments.get(next).start()) : maxY;
				if (end > cursor + 0.001f) {
					renderSegment(poseStack, buffer, blockEntity, current, mode, cursor, end, lerpedTime);
					if (firstDrawn == null) {
						firstDrawn = current;
						firstY = cursor;
					}
					lastDrawn = current;
					lastY = end;
				}
				cursor = end;
				if (next < segments.size()) {
					current = segments.get(next++);
				}
			}
			if (firstDrawn != null) {
				float fadeRange = getFadeRange(blockEntity);
				if (firstY > 1.001f) {
					renderCap(poseStack, buffer, firstDrawn, mode, firstY, lerpedTime, fadeRange, false);
				}
				renderCap(poseStack, buffer, lastDrawn, mode, lastY, lerpedTime, fadeRange, true);
			}
			poseStack.popPose();
		}
	}

	private record Style(int color, boolean colored) {}

	private Style resolveStyle(AbstractLaserBlockEntity.BeamSegment segment, LaserMode mode, float lerpedTime) {
		int color;
		boolean useGrayscale = false;
		if (segment.color().isPresent()) {
			color = FastColor.ARGB32.color(255, segment.color().get());
		} else if (segment.silly()) {
			color = FastColor.ARGB32.color(255, getSillyColor(lerpedTime));
		} else {
			if (mode == LaserMode.SPECTRUM) {
				color = FastColor.ARGB32.color(255, 255, 255, 255); // White - no tinting
			} else if (mode == LaserMode.ULTRAVIOLET) {
				color = FastColor.ARGB32.color(255, ULTRAVIOLET_COLOR);
				useGrayscale = true;
			} else { // INFRARED
				color = FastColor.ARGB32.color(255, INFRARED_COLOR);
				useGrayscale = true;
			}
		}
		return new Style(color, segment.color().isPresent() || segment.silly() || useGrayscale);
	}

	private void renderCap(
			PoseStack poseStack,
			MultiBufferSource buffer,
			AbstractLaserBlockEntity.BeamSegment segment,
			LaserMode mode,
			float y,
			float lerpedTime,
			float maxRange,
			boolean facesUp
	) {
		Style style = resolveStyle(segment, mode, lerpedTime);
		var consumer = buffer.getBuffer(getRenderType(style.colored()));

		boolean isUVorIR = (mode == LaserMode.ULTRAVIOLET || mode == LaserMode.INFRARED);
		float fadeScale = isUVorIR ? 0.5f : 1f;
		float distance = Math.min(maxRange, Math.max(0f, y - 1f));
		int alpha = (int) (255 * Math.max(0f, fadeScale * (1f - (distance / maxRange))));
		int color = FastColor.ARGB32.color(
				alpha,
				FastColor.ARGB32.red(style.color()),
				FastColor.ARGB32.green(style.color()),
				FastColor.ARGB32.blue(style.color())
		);

		PoseStack.Pose pose = poseStack.last();
		float u0 = 1f / 6f;
		float u1 = 5f / 6f;
		float v = 6f / 192f;
		float ny = facesUp ? 1f : -1f;

		float[][] corners = facesUp
				? new float[][]{{.4f, .4f}, {.4f, .6f}, {.6f, .6f}, {.6f, .4f}}
				: new float[][]{{.4f, .4f}, {.6f, .4f}, {.6f, .6f}, {.4f, .6f}};
		float[] us = {u0, u0, u1, u1};

		for (int i = 0; i < 4; i++) {
			consumer.addVertex(pose.pose(), corners[i][0], y, corners[i][1])
					.setColor(color)
					.setUv(us[i], v)
					.setOverlay(OverlayTexture.NO_OVERLAY)
					.setUv2(LightTexture.FULL_BRIGHT, 1)
					.setNormal(pose, 0, ny, 0);
		}
	}

	private void renderSegment(
			PoseStack poseStack,
			MultiBufferSource buffer,
			T blockEntity,
			AbstractLaserBlockEntity.BeamSegment segment,
			LaserMode mode,
			float startY,
			float maxY,
			float lerpedTime
	) {
		Style style = resolveStyle(segment, mode, lerpedTime);
		int color = style.color();

		// Use fallback render type if shaders in use because beacon beam broken
		var consumer = buffer.getBuffer(getRenderType(style.colored()));

		float fadeRange = getFadeRange(blockEntity);
		renderFace(poseStack, consumer, .4f, startY, .6f, .6f, maxY, .6f, color, lerpedTime, Direction.NORTH, fadeRange, mode);
		renderFace(poseStack, consumer, .6f, startY, .4f, .4f, maxY, .4f, color, lerpedTime, Direction.SOUTH, fadeRange, mode);
		renderFace(poseStack, consumer, .4f, startY, .4f, .4f, maxY, .6f, color, lerpedTime, Direction.EAST, fadeRange, mode);
		renderFace(poseStack, consumer, .6f, startY, .6f, .6f, maxY, .4f, color, lerpedTime, Direction.WEST, fadeRange, mode);
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
			float maxRange,
			LaserMode mode
	) {
		PoseStack.Pose pose = matrices.last();
		var startV = (minY - 1f) / 15f;
		var maxV = (maxY - 1f) / 15f;

		var v0 = 1 - (ticks % 150f) / 150f + (startV * 0.4f);
		var v1 = 1 - (ticks % 150f) / 150f + (maxV * 0.4f);

		// Check if we're in UV or IR mode for special transparency handling
		boolean isUVorIR = (mode == LaserMode.ULTRAVIOLET || mode == LaserMode.INFRARED);

		// Calculate transparency
		float startDistance = Math.min(maxRange, minY - 1f);
		float endDistance = Math.min(maxRange, maxY - 1f);
		float fadeScale = isUVorIR ? 0.5f : 1f;
		float startMultiplier = Math.max(0f, fadeScale * (1f - (startDistance / maxRange)));
		float alphaMultiplier = Math.max(0f, fadeScale * (1f - (endDistance / maxRange)));

		var startAlpha = (int) (255 * startMultiplier);
		var endAlpha = (int) (255 * alphaMultiplier);

		var startColor = (isUVorIR || startDistance > 0f) ? FastColor.ARGB32.color(
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

	public AABB getRenderBoundingBox(T blockEntity) {
		var facing = blockEntity.getFacing();
		var end = facing.getNormal().multiply(blockEntity.getCurrentRange() + 1);
		return new AABB(blockEntity.getBlockPos()).expandTowards(end.getX(), end.getY(), end.getZ());
	}
}
