package net.artienia.rubinated_nether.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for trinkets and curios
 */
public abstract class RubyLensRenderer {

    private final Map<HeadedModel, RubyLensModel<?>> models = new HashMap<>();

    protected void renderInternal(EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        if(entityModel instanceof HeadedModel headed) {
            RubyLensModel<?> model = models.computeIfAbsent(headed, $ -> new RubyLensModel<>(RubyLensModel.createBodyLayer().bakeRoot()));
            model.copyHeadData(headed.getHead());
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(RubyLensRenderLayer.RUBY_LENS_TEXTURE)), light, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
