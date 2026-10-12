package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.trim.VaseEngraving;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class VaseItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final VaseItemRenderer INSTANCE = new VaseItemRenderer();

    private static final float EPS = 0.002f;
    private static final String WAXED_PREFIX = "waxed_";

    private VaseItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        // Waxed vase items (e.g. "waxed_bronze_vase") share the same base cube model
        // as their non-waxed counterpart ("bronze_vase"), so strip the prefix before
        // looking up the "_base" model — only one _base model is generated per tarnish stage.
        String path = itemId.getPath();
        if (path.startsWith(WAXED_PREFIX)) {
            path = path.substring(WAXED_PREFIX.length());
        }

        ModelResourceLocation baseModelLoc =
                ModelResourceLocation.standalone(RubinatedNether.id("item/" + path + "_base"));

        BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(baseModelLoc);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5); // cancel the extra -0.5 that render() re-applies internally
        Minecraft.getInstance().getItemRenderer()
                .render(stack, displayContext, false, poseStack, bufferSource, packedLight, packedOverlay, baseModel);
        poseStack.popPose();

        VaseEngraving engraving = stack.get(RNDataComponents.VASE_ENGRAVING.get());
        if (engraving != null) {
            TextureAtlasSprite sprite = VaseEngravingAtlas.INSTANCE.getSprite(engraving.pattern(), engraving.material());
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(VaseEngravingAtlas.ATLAS_LOCATION));

            for (Direction face : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                drawFace(poseStack, consumer, sprite, face, packedLight, packedOverlay);
            }
        }
    }

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