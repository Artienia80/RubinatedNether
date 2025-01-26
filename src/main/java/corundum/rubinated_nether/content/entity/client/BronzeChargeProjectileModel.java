package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class BronzeChargeProjectileModel extends EntityModel<BronzeShotProjectileEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "bronze_shot"), "main");
    private final ModelPart bronze_shot;

    public BronzeChargeProjectileModel(ModelPart root) {
        this.bronze_shot = root.getChild("bronze_shot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild(
			"bronze_shot", 
			CubeListBuilder.create()
				.texOffs(4, 10)
				.addBox(-11.0F, -11.0F, 5.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), 
				
			PartPose.offset(8.0F, 24.0F, -8.0F)
		);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(BronzeShotProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bronze_shot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

}