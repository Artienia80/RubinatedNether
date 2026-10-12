package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.entities.VaseBlockEntity;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.trim.VaseEngraving;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class VaseBlockEntityRenderer implements BlockEntityRenderer<VaseBlockEntity> {

    public VaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(VaseBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        DoubleBlockHalf half = blockEntity.getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if (half != DoubleBlockHalf.LOWER) return;

        VaseEngraving engraving = blockEntity.getEngraving();
        if (engraving == null) return;

        TextureAtlasSprite sprite = VaseEngravingAtlas.INSTANCE.getSprite(engraving.pattern(), engraving.material());
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(VaseEngravingAtlas.ATLAS_LOCATION));

        poseStack.pushPose();
        for (Direction face : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            drawFace(poseStack, consumer, sprite, face, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    private static final float EPS = 0.002f;

    private void drawFace(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite,
                          Direction face, int light, int overlay) {
        var pose = poseStack.last();
        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();


        switch (face) {

            case SOUTH -> {
                float z = 1f + EPS;
                v(consumer, pose, 0, 0, z, u0, v1, light, overlay, 0, 0, 1);
                v(consumer, pose, 1, 0, z, u1, v1, light, overlay, 0, 0, 1);
                v(consumer, pose, 1, 1, z, u1, v0, light, overlay, 0, 0, 1);
                v(consumer, pose, 0, 1, z, u0, v0, light, overlay, 0, 0, 1);
            }
            case NORTH -> {
                float z = -EPS;
                v(consumer, pose, 1, 0, z, u0, v1, light, overlay, 0, 0, -1);
                v(consumer, pose, 0, 0, z, u1, v1, light, overlay, 0, 0, -1);
                v(consumer, pose, 0, 1, z, u1, v0, light, overlay, 0, 0, -1);
                v(consumer, pose, 1, 1, z, u0, v0, light, overlay, 0, 0, -1);
            }
            case EAST -> {
                float x = 1f + EPS;
                v(consumer, pose, x, 0, 1, u0, v1, light, overlay, 1, 0, 0);
                v(consumer, pose, x, 0, 0, u1, v1, light, overlay, 1, 0, 0);
                v(consumer, pose, x, 1, 0, u1, v0, light, overlay, 1, 0, 0);
                v(consumer, pose, x, 1, 1, u0, v0, light, overlay, 1, 0, 0);
            }
            case WEST -> {
                float x = -EPS;
                v(consumer, pose, x, 0, 0, u0, v1, light, overlay, -1, 0, 0);
                v(consumer, pose, x, 0, 1, u1, v1, light, overlay, -1, 0, 0);
                v(consumer, pose, x, 1, 1, u1, v0, light, overlay, -1, 0, 0);
                v(consumer, pose, x, 1, 0, u0, v0, light, overlay, -1, 0, 0);
            }
            default -> {}
        }
    }

    private void v(VertexConsumer c, PoseStack.Pose pose, float x, float y, float z,
                   float u, float vv, int light, int overlay, float nx, float ny, float nz) {
        c.addVertex(pose, x, y, z).setColor(255, 255, 255, 255).setUv(u, vv)
                .setLight(light).setOverlay(overlay).setNormal(pose, nx, ny, nz);
    }
}