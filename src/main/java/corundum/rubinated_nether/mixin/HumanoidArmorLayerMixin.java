package corundum.rubinated_nether.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.client.render.RNRenderTypes;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @Inject(
            method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;)V"
            ),
            cancellable = true
    )
    private void rubinated_nether$replaceArmorGlint(
            PoseStack poseStack, MultiBufferSource bufferSource,
            LivingEntity livingEntity, EquipmentSlot slot, int packedLight,
            net.minecraft.client.model.HumanoidModel<?> armorModel,
            float a, float b, float c, float d, float e, float f,
            CallbackInfo ci
    ) {
        ItemStack stack = livingEntity.getItemBySlot(slot);
        if (!rubinated_nether$hasRubinatedCurse(stack)) return;

        ci.cancel();

        Model resolved = net.neoforged.neoforge.client.ClientHooks.getArmorModel(livingEntity, stack, slot, armorModel);
        resolved.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RNRenderTypes.RUBINATED_ENTITY_GLINT),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }

    @Unique
    private static boolean rubinated_nether$hasRubinatedCurse(ItemStack stack) {
        return EnchantmentHelper.hasTag(stack, RNTags.Enchantments.RUBINATED_CURSES);
    }
}