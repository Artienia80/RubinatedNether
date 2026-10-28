package corundum.rubinated_nether.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import corundum.rubinated_nether.client.render.RNRenderTypes;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.dowsing.DowsingRods;
import corundum.rubinated_nether.content.items.MechanicalFuel;
import corundum.rubinated_nether.content.items.MechanicalTool;
import corundum.rubinated_nether.content.items.MusicBoxItem;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import corundum.rubinated_nether.content.items.RitualOfferingItem;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @WrapOperation(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer rubinated_nether$wrapFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean isItem, boolean glint, Operation<VertexConsumer> original, @Local(argsOnly = true) ItemStack stack) {
        if (glint && !stack.isEmpty() && rubinated_nether$usesRubinatedGlint(stack)) {
            return VertexMultiConsumer.create(
                    bufferSource.getBuffer(isItem ? RNRenderTypes.RUBINATED_GLINT : RNRenderTypes.RUBINATED_ENTITY_GLINT),
                    bufferSource.getBuffer(renderType)
            );
        }
        return original.call(bufferSource, renderType, isItem, glint);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer rubinated_nether$wrapFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, boolean noEntity, boolean withGlint, Operation<VertexConsumer> original, @Local(argsOnly = true) ItemStack stack) {
        if (withGlint && !stack.isEmpty() && rubinated_nether$usesRubinatedGlint(stack)) {
            return VertexMultiConsumer.create(
                    bufferSource.getBuffer(noEntity ? RNRenderTypes.RUBINATED_GLINT : RNRenderTypes.RUBINATED_ENTITY_GLINT),
                    bufferSource.getBuffer(renderType)
            );
        }
        return original.call(bufferSource, renderType, noEntity, withGlint);
    }

    @Unique
    private static boolean rubinated_nether$usesRubinatedGlint(ItemStack stack) {
        return DowsingRods.isArrivalRender()
                || rubinated_nether$hasRubinatedCurse(stack)
                || rubinated_nether$isRune(stack)
                || rubinated_nether$isRitualOffering(stack)
                || rubinated_nether$isChargedPocketwatch(stack)
                || rubinated_nether$isSinnerMusicBox(stack)
                || rubinated_nether$isFuelledMechanicalTool(stack);
    }

    @Unique
    private static boolean rubinated_nether$isRune(ItemStack stack) {
        return stack.getItem() instanceof RuneItem;
    }

    @Unique
    private static boolean rubinated_nether$isRitualOffering(ItemStack stack) {
        return stack.getItem() instanceof RitualOfferingItem;
    }

    @Unique
    private static boolean rubinated_nether$hasRubinatedCurse(ItemStack stack) {
        return EnchantmentHelper.hasTag(stack, RNTags.Enchantments.RUBINATED_CURSES);
    }

    @Unique
    private static boolean rubinated_nether$isChargedPocketwatch(ItemStack stack) {
        return stack.getItem() instanceof PocketwatchItem
                && stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON).equals(RNRarity.RUBINATED_NETHER_RUBY.get());
    }

    @Unique
    private static boolean rubinated_nether$isFuelledMechanicalTool(ItemStack stack) {
        return stack.getItem() instanceof MechanicalTool && MechanicalFuel.hasFuel(stack);
    }

    @Unique
    private static boolean rubinated_nether$isSinnerMusicBox(ItemStack stack) {
        return stack.getItem() instanceof MusicBoxItem && MusicBoxItem.isSinnerPlaying(stack);
    }
}