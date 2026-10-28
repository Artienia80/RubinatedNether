package corundum.rubinated_nether.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import corundum.rubinated_nether.content.dowsing.DowsingClient;
import corundum.rubinated_nether.content.dowsing.DowsingRods;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
    @Shadow
    @Final
    private ItemInHandRenderer itemInHandRenderer;

    protected ItemInHandLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void rubinated_nether$renderDowsingRod(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !DowsingClient.isActive(player) || !DowsingRods.isRod(stack)) {
            return;
        }

        boolean left = arm == HumanoidArm.LEFT;
        boolean mainHand = player.getMainArm() == arm;
        float lift = mainHand ? DowsingRods.ROD_PIXEL : 0.0F;

        poseStack.pushPose();
        this.getParentModel().translateToHand(arm, poseStack);
        poseStack.translate(left ? 0.0625F : -0.0625F, 0.625F, -0.125F - lift);
        poseStack.mulPose(Axis.ZP.rotationDegrees(left ? DowsingClient.getRenderAngle(player) : -DowsingClient.getRenderAngle(player)));
        poseStack.translate(0.0F, DowsingRods.ROD_HALF_LENGTH, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        poseStack.scale(DowsingRods.ROD_SCALE, DowsingRods.ROD_SCALE, DowsingRods.ROD_SCALE);
        boolean arrived = DowsingClient.isArrived(player);
        DowsingRods.setArrivalRender(arrived);
        this.itemInHandRenderer.renderItem(entity, arrived ? DowsingRods.withGlint(stack) : stack, ItemDisplayContext.NONE, left, poseStack, buffer, light);
        DowsingRods.setArrivalRender(false);
        poseStack.popPose();

        ci.cancel();
    }
}
