package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.CrossbowRodType;
import corundum.rubinated_nether.content.entity.CrossbowRodProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CrossbowRodProjectileRenderer extends EntityRenderer<CrossbowRodProjectileEntity> {
    private static final ResourceLocation BRONZE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze.png");
    private static final ResourceLocation BREEZE_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze.png");
    private static final ResourceLocation BLAZE_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/blaze.png");

    private final CrossbowRodProjectileModel bronzeModel;
    private final CrossbowRodProjectileModel breezeModel;
    private final CrossbowRodProjectileModel blazeModel;

    public CrossbowRodProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.bronzeModel = new CrossbowRodProjectileModel(context.bakeLayer(CrossbowRodProjectileModel.BRONZE_LAYER));
        this.breezeModel = new CrossbowRodProjectileModel(context.bakeLayer(CrossbowRodProjectileModel.BREEZE_LAYER));
        this.blazeModel = new CrossbowRodProjectileModel(context.bakeLayer(CrossbowRodProjectileModel.BLAZE_LAYER));
    }

    @Override
    public void render(CrossbowRodProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        CrossbowRodType type = entity.getRodType();
        CrossbowRodProjectileModel model = this.modelFor(type);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, model.renderType(this.textureFor(type)), false, false);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private CrossbowRodProjectileModel modelFor(CrossbowRodType type) {
        return switch (type) {
            case BRONZE -> this.bronzeModel;
            case BREEZE -> this.breezeModel;
            case BLAZE -> this.blazeModel;
        };
    }

    private ResourceLocation textureFor(CrossbowRodType type) {
        return switch (type) {
            case BRONZE -> BRONZE_TEXTURE;
            case BREEZE -> BREEZE_TEXTURE;
            case BLAZE -> BLAZE_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getTextureLocation(CrossbowRodProjectileEntity entity) {
        return this.textureFor(entity.getRodType());
    }
}
