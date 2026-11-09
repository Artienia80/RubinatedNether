package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.blocks.BronzeHeadBlock;
import corundum.rubinated_nether.content.blocks.entities.BronzeHeadBlockEntity;
import corundum.rubinated_nether.utils.HeadCone;
import corundum.rubinated_nether.utils.HeadConeCache;
import corundum.rubinated_nether.utils.HeadOrientation;
import corundum.rubinated_nether.utils.RubyLensHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BronzeHeadRenderer implements BlockEntityRenderer<BronzeHeadBlockEntity> {
	public static final ResourceLocation TEXTURE = RubinatedNether.id("textures/entity/bronze/bronze.png");
	public static final ResourceLocation DISCOLORED_TEXTURE = RubinatedNether.id("textures/entity/bronze/bronze_discolored.png");
	public static final ResourceLocation CORRODED_TEXTURE = RubinatedNether.id("textures/entity/bronze/bronze_corroded.png");
	public static final ResourceLocation TARNISHED_TEXTURE = RubinatedNether.id("textures/entity/bronze/bronze_tarnished.png");
	public static final ResourceLocation CRYSTALLIZED_TEXTURE = RubinatedNether.id("textures/entity/bronze/bronze_crystallized.png");

	public static ResourceLocation textureFor(TarnishStage stage) {
		return switch (stage) {
			case UNAFFECTED -> TEXTURE;
			case DISCOLORED -> DISCOLORED_TEXTURE;
			case CORRODED -> CORRODED_TEXTURE;
			case TARNISHED -> TARNISHED_TEXTURE;
			case CRYSTALLIZED -> CRYSTALLIZED_TEXTURE;
		};
	}

	private static final float RANGE_INSET = 0.04F;
	private static final int RANGE_ALPHA = 60;

	private final BronzeHeadModel model;

	public BronzeHeadRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new BronzeHeadModel(context.bakeLayer(RNModelLayers.BRONZE_HEAD));
	}

	@Override
	public void render(BronzeHeadBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState state = blockEntity.getBlockState();
		ResourceLocation texture = textureFor(((BronzeHeadBlock) state.getBlock()).getStage());

		model.setKeyVisible(state.getValue(BronzeHeadBlock.KEYED));
		model.setKeyAngle(blockEntity.getKeyAngle(partialTick) * Mth.DEG_TO_RAD);

		Direction attach = state.getValue(BronzeHeadBlock.ATTACH);

		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		if (attach.getAxis().isHorizontal()) {
			Vec3 a = HeadOrientation.axisA(attach);
			Vec3 b = HeadOrientation.axisB(attach);
			Matrix3f basis = new Matrix3f().set(
					new Vector3f((float) -b.x, (float) -b.y, (float) -b.z),
					new Vector3f(attach.getStepX(), attach.getStepY(), attach.getStepZ()),
					new Vector3f((float) a.x, (float) a.y, (float) a.z)
			);
			poseStack.mulPose(new Quaternionf().setFromNormalized(basis));
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - BronzeHeadBlock.getStep(state) * 45.0F));
		if (attach == Direction.DOWN) poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		poseStack.translate(0.0, 0.25, 0.0);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture)), packedLight, packedOverlay);
		poseStack.popPose();

		if (state.getValue(BronzeHeadBlock.KEYED) && RubyLensHelper.isWearing(Minecraft.getInstance().player)) {
			renderRange(blockEntity.getCone(), poseStack, bufferSource);
		}
	}

	private static void renderRange(HeadConeCache cone, PoseStack poseStack, MultiBufferSource bufferSource) {
		Direction.Axis plane = cone.getPlane();
		int planeIndex = plane.ordinal();
		int uIndex = HeadOrientation.uAxis(plane).ordinal();
		int vIndex = HeadOrientation.vAxis(plane).ordinal();
		VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());
		Matrix4f pose = poseStack.last().pose();
		float[] a = new float[3];
		float[] b = new float[3];
		float[] c = new float[3];
		float[] d = new float[3];

		for (int du = -HeadCone.RANGE; du <= HeadCone.RANGE; du++) {
			for (int dv = -HeadCone.RANGE; dv <= HeadCone.RANGE; dv++) {
				if (!cone.isVisible(du, dv)) continue;

				a[planeIndex] = b[planeIndex] = c[planeIndex] = d[planeIndex] = 0.5F;
				a[uIndex] = d[uIndex] = du + RANGE_INSET;
				b[uIndex] = c[uIndex] = du + 1.0F - RANGE_INSET;
				a[vIndex] = b[vIndex] = dv + RANGE_INSET;
				c[vIndex] = d[vIndex] = dv + 1.0F - RANGE_INSET;

				quad(buffer, pose, a, b, c, d);
				quad(buffer, pose, d, c, b, a);
			}
		}
	}

	private static void quad(VertexConsumer buffer, Matrix4f pose, float[] a, float[] b, float[] c, float[] d) {
		vertex(buffer, pose, a);
		vertex(buffer, pose, b);
		vertex(buffer, pose, c);
		vertex(buffer, pose, d);
	}

	private static void vertex(VertexConsumer buffer, Matrix4f pose, float[] p) {
		buffer.addVertex(pose, p[0], p[1], p[2]).setColor(255, 70, 70, RANGE_ALPHA);
	}

	public AABB getRenderBoundingBox(BronzeHeadBlockEntity blockEntity) {
		return new AABB(blockEntity.getBlockPos()).inflate(HeadCone.RANGE);
	}
}
