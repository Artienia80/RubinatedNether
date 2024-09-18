package net.artienia.rubinated_nether.client.blockEntityRenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.artienia.rubinated_nether.block.entity.RubyLaserBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RubyLaserRenderer implements BlockEntityRenderer<RubyLaserBlockEntity> {

    public RubyLaserRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(RubyLaserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {


    }
}
