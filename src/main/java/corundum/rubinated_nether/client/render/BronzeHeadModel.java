package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BronzeHeadModel {
	private final ModelPart root;
	private final ModelPart windup;

	public BronzeHeadModel(ModelPart part) {
		this.root = part.getChild("root");
		this.windup = this.root.getChild("head").getChild("windup");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		head.addOrReplaceChild("windup",
				CubeListBuilder.create()
						.texOffs(39, 14).mirror().addBox(-5.0F, -6.0F, 0.0F, 10.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(49, 24).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -10.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	public void setKeyVisible(boolean visible) {
		this.windup.visible = visible;
	}

	public void setKeyAngle(float radians) {
		this.windup.yRot = radians;
	}

	public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
		this.root.render(poseStack, consumer, packedLight, packedOverlay, -1);
	}
}
