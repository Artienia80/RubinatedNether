package corundum.rubinated_nether.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.content.entity.animation.BronzeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class BronzeModel<T extends BronzeEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart bronze;
    private final ModelPart head;
    private final ModelPart windup;

    public BronzeModel(ModelPart root) {
        this.root = root.getChild("root");
        this.bronze = this.root.getChild("bronze");
        this.head = this.bronze.getChild("head");
        this.windup = head.getChild("windup");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bronze = root.addOrReplaceChild("bronze", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = bronze.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition windup = head.addOrReplaceChild("windup", CubeListBuilder.create().texOffs(39, 14).mirror().addBox(-5.0F, -6.0F, 0.0F, 10.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(49, 24).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition rods = bronze.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition rod1 = rods.addOrReplaceChild("rod1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = rod1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod2 = rods.addOrReplaceChild("rod2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        PartDefinition rod_2 = rod2.addOrReplaceChild("rod_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = rod_2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod3 = rods.addOrReplaceChild("rod3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r3 = rod3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod4 = rods.addOrReplaceChild("rod4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        PartDefinition rod_4 = rod4.addOrReplaceChild("rod_4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r4 = rod_4.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod5 = rods.addOrReplaceChild("rod5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        PartDefinition rod_5 = rod5.addOrReplaceChild("rod_5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r5 = rod_5.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition rod6 = rods.addOrReplaceChild("rod6", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        PartDefinition rod_6 = rod6.addOrReplaceChild("rod_6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r6 = rod_6.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 38).addBox(1.7189F, -12.2678F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(BronzeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if(!entity.isBurrowed()) {
            this.applyHeadRotation(netHeadYaw, headPitch);
            this.head.visible = true;
        } else {
            this.applyHeadRotation(0, 0);
            this.head.visible = false;
        }


        this.animateWalk(BronzeAnimations.MOVE, limbSwing, limbSwingAmount, 4f, 54);
        this.animate(entity.idleAnimationState,BronzeAnimations.IDLE,ageInTicks, 1f);
        this.animate(entity.unaffectedAttackAnimationState,BronzeAnimations.BASH,ageInTicks, 1f);
        this.animate(entity.defendAnimationState,BronzeAnimations.DEFEND, ageInTicks, 1f);
        this.animate(entity.stunAnimationState,BronzeAnimations.STUNNED, ageInTicks, 1f);
        this.animate(entity.drillAnimationState,BronzeAnimations.DRILL_DOWN, ageInTicks, 1f);
        this.animate(entity.undergroundWalkAnimationState,BronzeAnimations.MOVE_UNDERGROUND, ageInTicks, 1f);
        this.animate(entity.ambushAnimationState,BronzeAnimations.DRILL_UP,ageInTicks, 1f);
        this.animate(entity.shockwaveAnimationState,BronzeAnimations.SHOCKWAVE,ageInTicks, 1f);
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        this.head.xRot = headPitch * 0.017453292F;
        this.head.yRot = headYaw * 0.017453292F;
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