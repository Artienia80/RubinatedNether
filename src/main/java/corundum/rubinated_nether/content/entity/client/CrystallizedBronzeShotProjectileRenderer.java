package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.CrystallizedBronzeShotProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CrystallizedBronzeShotProjectileRenderer extends EntityRenderer<CrystallizedBronzeShotProjectileEntity> {
	private CrystallizedBronzeShotProjectileModel model;

	public CrystallizedBronzeShotProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new CrystallizedBronzeShotProjectileModel(context.bakeLayer(CrystallizedBronzeShotProjectileModel.LAYER_LOCATION));
	}

	@Override
	public void render(CrystallizedBronzeShotProjectileEntity pEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		if (!pEntity.isGrounded()) {
			poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, pEntity.yRotO, pEntity.getYRot())));
			poseStack.mulPose(Axis.XP.rotationDegrees(pEntity.getRenderingRotation() * 5f));
			poseStack.translate(0, -1.0f, 0);
		} else {
			poseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot()));
			poseStack.mulPose(Axis.XP.rotationDegrees(pEntity.getXRot()));
			poseStack.translate(0, -1.0f, 0);
		}

		VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(
				buffer, this.model.renderType(this.getTextureLocation(pEntity)), false, false);
		this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(pEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(CrystallizedBronzeShotProjectileEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze_shot/crystallized_bronze_shot.png");
	}
}