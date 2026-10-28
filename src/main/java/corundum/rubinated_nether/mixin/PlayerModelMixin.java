package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.dowsing.DowsingClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
    @Shadow
    @Final
    public ModelPart leftSleeve;

    @Shadow
    @Final
    public ModelPart rightSleeve;

    protected PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("TAIL")
    )
    private void rubinated_nether$dowsingPose(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !DowsingClient.isActive(player)) {
            return;
        }

        DowsingClient.setRenderAngle(player, DowsingClient.getAngle(player, ageInTicks - entity.tickCount));
        float pitch = -Mth.HALF_PI + this.head.xRot;
        float yaw = this.head.yRot + this.body.yRot;
        float swing = 0.0F;

        if (this.attackTime > 0.0F) {
            float progress = 1.0F - this.attackTime;
            progress *= progress;
            progress *= progress;
            progress = 1.0F - progress;
            swing = Mth.sin(progress * Mth.PI) * 1.2F + Mth.sin(this.attackTime * Mth.PI) * -(this.head.xRot - 0.7F) * 0.75F;
        }

        this.rightArm.xRot = pitch - swing;
        this.leftArm.xRot = pitch - swing;
        this.rightArm.yRot = yaw;
        this.leftArm.yRot = yaw;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;

        this.rightSleeve.copyFrom(this.rightArm);
        this.leftSleeve.copyFrom(this.leftArm);
    }
}
