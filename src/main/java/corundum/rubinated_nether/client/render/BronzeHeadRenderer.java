package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.blocks.BronzeHeadBlock;
import corundum.rubinated_nether.content.blocks.entities.BronzeHeadBlockEntity;
import corundum.rubinated_nether.utils.HeadOrientation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
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
	}
}
