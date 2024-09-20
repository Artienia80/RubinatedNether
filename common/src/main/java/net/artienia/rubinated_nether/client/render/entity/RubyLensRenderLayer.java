package net.artienia.rubinated_nether.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class RubyLensRenderLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    public static final ResourceLocation RUBY_LENS_TEXTURE = new ResourceLocation("textures/item/ruby_lens.png");

    private final M baseModel;
    private final RubyLensModel<T> lensModel;

    public RubyLensRenderLayer(RenderLayerParent<T, M> renderer, EntityModelSet models, M baseModel) {
        super(renderer);
        this.baseModel = baseModel;
        lensModel = new RubyLensModel<>(models.bakeLayer(RubyLensModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if(livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.RUBY_LENS.get())) {
            lensModel.copyHeadData(baseModel.getHead());
            lensModel.renderToBuffer(poseStack, buffer.getBuffer(lensModel.renderType(RUBY_LENS_TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
