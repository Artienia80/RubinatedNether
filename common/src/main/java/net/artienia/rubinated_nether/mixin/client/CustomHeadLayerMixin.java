package net.artienia.rubinated_nether.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin {

    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/CustomHeadLayer;translateToHead(Lcom/mojang/blaze3d/vertex/PoseStack;Z)V"
        ),
        cancellable = true
    )
    public void cancelIfRubyLens(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if(livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get())) {
            poseStack.popPose();
            ci.cancel();
        }
    }
}
