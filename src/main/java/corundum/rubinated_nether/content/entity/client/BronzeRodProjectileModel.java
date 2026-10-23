package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeRodProjectileEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * One of the Bronze mob's rods: the same 2x11x2 cube with the same UVs, but straight and centred.
 * It uses the Bronze mob texture (64x64), so the UVs line up exactly.
 */
public class BronzeRodProjectileModel extends EntityModel<BronzeRodProjectileEntity> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "bronze_rod"), "main");
	private final ModelPart rod;

	public BronzeRodProjectileModel(ModelPart root) {
		this.rod = root.getChild("rod");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild(
				"rod",
				CubeListBuilder.create()
						.texOffs(12, 38)
						.addBox(-1.0F, -5.5F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.ZERO
		);

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(BronzeRodProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		rod.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
