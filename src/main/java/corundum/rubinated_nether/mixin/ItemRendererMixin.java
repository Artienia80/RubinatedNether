package corundum.rubinated_nether.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import corundum.rubinated_nether.client.render.RNRenderTypes;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private static final ThreadLocal<ItemStack> rubinated_nether$currentStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);

    @Inject(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At("HEAD")
    )
    private void rubinated_nether$captureStack(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        rubinated_nether$currentStack.set(itemStack);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At("RETURN")
    )
    private void rubinated_nether$clearStack(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        rubinated_nether$currentStack.set(ItemStack.EMPTY);
    }

    @Inject(
            method = "getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void rubinated_nether$redirectFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean isItem, boolean glint, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!glint) return;
        ItemStack stack = rubinated_nether$currentStack.get();
        if (stack.isEmpty()) return;
        if (!rubinated_nether$hasRubinatedCurse(stack)) return;
        cir.setReturnValue(VertexMultiConsumer.create(
                bufferSource.getBuffer(isItem ? RNRenderTypes.RUBINATED_GLINT : RNRenderTypes.RUBINATED_ENTITY_GLINT),
                bufferSource.getBuffer(renderType)
        ));
    }

    @Inject(
            method = "getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void rubinated_nether$redirectFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, boolean noEntity, boolean withGlint, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!withGlint) return;
        ItemStack stack = rubinated_nether$currentStack.get();
        if (stack.isEmpty()) return;
        if (!rubinated_nether$hasRubinatedCurse(stack)) return;
        cir.setReturnValue(VertexMultiConsumer.create(
                bufferSource.getBuffer(noEntity ? RNRenderTypes.RUBINATED_GLINT : RNRenderTypes.RUBINATED_ENTITY_GLINT),
                bufferSource.getBuffer(renderType)
        ));
    }

    @Unique
    private static boolean rubinated_nether$hasRubinatedCurse(ItemStack stack) {
        return EnchantmentHelper.hasTag(stack, RNTags.Enchantments.RUBINATED_CURSES);
    }
}