package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.CrossbowRodProjectileEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

public class CrossbowRodProjectileModel extends EntityModel<CrossbowRodProjectileEntity> {
    public static final ModelLayerLocation BRONZE_LAYER = layer("crossbow_bronze_rod");
    public static final ModelLayerLocation BREEZE_LAYER = layer("crossbow_breeze_rod");
    public static final ModelLayerLocation BLAZE_LAYER = layer("crossbow_blaze_rod");

    private final ModelPart rod;

    public CrossbowRodProjectileModel(ModelPart root) {
        this.rod = root.getChild("rod");
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, name), "main");
    }

    private static LayerDefinition createLayer(int u, int v, float length, int textureWidth, int textureHeight) {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild(
                "rod",
                CubeListBuilder.create()
                        .texOffs(u, v)
                        .addBox(-1.0F, -length / 2.0F, -1.0F, 2.0F, length, 2.0F, new CubeDeformation(0.0F)),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, textureWidth, textureHeight);
    }

    public static LayerDefinition createBronzeLayer() {
        return createLayer(12, 38, 11.0F, 64, 64);
    }

    public static LayerDefinition createBreezeLayer() {
        return createLayer(0, 17, 8.0F, 32, 32);
    }

    public static LayerDefinition createBlazeLayer() {
        return createLayer(0, 16, 8.0F, 64, 32);
    }

    @Override
    public void setupAnim(CrossbowRodProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.rod.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
