package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.content.entity.living.BronzeEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class BronzeModel<T extends BronzeEntity> extends HierarchicalModel<T> {

    private final ModelPart root;

    public BronzeModel(ModelPart root) {
        this.root = root.getChild("bronze");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bronze = partdefinition.addOrReplaceChild("bronze", CubeListBuilder.create(), PartPose.offset(4.0F, 24.0F, -4.0F));

        PartDefinition head = bronze.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 12).addBox(-9.0F, -10.0F, -1.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition windup = head.addOrReplaceChild("windup", CubeListBuilder.create().texOffs(39, 14).mirror().addBox(-9.0F, -16.0F, 4.0F, 10.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(49, 24).addBox(-5.0F, -15.0F, 3.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rod1 = bronze.addOrReplaceChild("rod1", CubeListBuilder.create(), PartPose.offset(-4.0F, -1.0F, 4.0F));

        PartDefinition cube_r1 = rod1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod2 = bronze.addOrReplaceChild("rod2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -1.0F, 4.0F, 0.0F, -2.0944F, 0.0F));

        PartDefinition cube_r2 = rod2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod3 = bronze.addOrReplaceChild("rod3", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -1.0F, 4.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r3 = rod3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod4 = bronze.addOrReplaceChild("rod4", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -1.0F, 4.0F, 0.0F, 1.0472F, 0.0F));

        PartDefinition cube_r4 = rod4.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod5 = bronze.addOrReplaceChild("rod5", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -1.0F, 4.0F, 0.0F, -1.0472F, 0.0F));

        PartDefinition cube_r5 = rod5.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod6 = bronze.addOrReplaceChild("rod6", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -1.0F, 4.0F, 0.0F, 2.0944F, 0.0F));

        PartDefinition cube_r6 = rod6.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(BronzeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw,headPitch);


        //this.animateWalk(MosquitoAnimations.ANIM_VAMPIRE_WALK, limbSwing, limbSwingAmount, 4f, 54);
        //this.animate(entity.idleAnimationState,MosquitoAnimations.ANIM_VAMPIRE_IDLE,ageInTicks, 1f);
        //this.animate(entity.attackAnimationState,MosquitoAnimations.ANIM_VAMPIRE_ATTACK,ageInTicks, 1f);
        //this.animate(entity.dieAnimationState,MosquitoAnimations.ANIM_VAMPIRE_PERISH,ageInTicks, 1f);

    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}