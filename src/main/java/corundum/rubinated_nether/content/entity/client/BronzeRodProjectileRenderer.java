package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeRodProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BronzeRodProjectileRenderer extends EntityRenderer<BronzeRodProjectileEntity> {
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze.png");

	private final BronzeRodProjectileModel model;

	public BronzeRodProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new BronzeRodProjectileModel(context.bakeLayer(BronzeRodProjectileModel.LAYER_LOCATION));
	}

	@Override
	public void render(BronzeRodProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		// Same orientation as a vanilla arrow (pointing along +X)...
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
		// ...but the rod cube is built standing up (along Y), so lay it down along X.
		poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

		VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
				buffer, this.model.renderType(TEXTURE), false, false);
		this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(BronzeRodProjectileEntity entity) {
		return TEXTURE;
	}
}
