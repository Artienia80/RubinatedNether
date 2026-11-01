package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.content.blocks.entities.gearbox.GearboxBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GearboxRenderer implements BlockEntityRenderer<GearboxBlockEntity> {

    public static final Map<TarnishStage, Material> BODY_TEXTURES = Map.of(
            TarnishStage.UNAFFECTED,   new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/bronze_gearbox")),
            TarnishStage.DISCOLORED,   new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/discolored_bronze_gearbox")),
            TarnishStage.CORRODED,     new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/corroded_bronze_gearbox")),
            TarnishStage.TARNISHED,    new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/tarnished_bronze_gearbox")),
            TarnishStage.CRYSTALLIZED, new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/crystallized_bronze_gearbox"))
    );

    public static final Map<TarnishStage, Material> ANIM_BODY_TEXTURES = Map.of(
            TarnishStage.UNAFFECTED,   new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/bronze_gearbox_anim")),
            TarnishStage.DISCOLORED,   new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/discolored_bronze_gearbox_anim")),
            TarnishStage.CORRODED,     new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/corroded_bronze_gearbox_anim")),
            TarnishStage.TARNISHED,    new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/tarnished_bronze_gearbox_anim")),
            TarnishStage.CRYSTALLIZED, new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/bronze_gearbox/crystallized_bronze_gearbox_anim"))
    );

    public static final Map<TarnishStage, Material> KEY_TEXTURES = Map.of(
            TarnishStage.UNAFFECTED,   new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("entity/bronze/bronze")),
            TarnishStage.DISCOLORED,   new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("entity/bronze/bronze_discolored")),
            TarnishStage.CORRODED,     new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("entity/bronze/bronze_corroded")),
            TarnishStage.TARNISHED,    new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("entity/bronze/bronze_tarnished")),
            TarnishStage.CRYSTALLIZED, new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("entity/bronze/bronze_crystallized"))
    );

    private static final float KEY_TEXTURE_SIZE = 64.0F;
    private static final int[] KEY_STEM_NORTH = {51, 26, 53, 31};
    private static final int[] KEY_STEM_EAST = {49, 26, 51, 31};
    private static final int[] KEY_STEM_SOUTH = {55, 26, 57, 31};
    private static final int[] KEY_STEM_WEST = {53, 26, 55, 31};
    private static final int[] KEY_STEM_UP = {51, 24, 53, 26};
    private static final int[] KEY_HANDLE = {49, 14, 59, 18};

    private static final String BODY = "body";
    private final ModelPart body;

    public GearboxRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(RNModelLayers.BRONZE_GEARBOX);
        this.body = root.getChild(BODY);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 48).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 0).addBox(-8.0F, 6.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.0F, 8.0F, 8.0F, 3.14159F, .0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void render(GearboxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        boolean flag = level != null;

        BlockState blockstate = flag ? blockEntity.getBlockState() : RNBlocks.BRONZE_GEARBOX.get().defaultBlockState();
        Block block = blockstate.getBlock();
        if (block instanceof GearboxBlock gearboxBlock) {
            poseStack.pushPose();
            boolean lit = blockstate.getValue(GearboxBlock.LIT);
            float dir = blockstate.getValue(GearboxBlock.FACING).toYRot();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-dir));
            poseStack.translate(-0.5F, -0.5F, -0.5F);

            TarnishStage stage = gearboxBlock.getAge();
            Material tex = (lit ? ANIM_BODY_TEXTURES : BODY_TEXTURES).getOrDefault(stage, BODY_TEXTURES.get(TarnishStage.UNAFFECTED));
            VertexConsumer consumer = tex.buffer(multiBufferSource, RenderType::entityCutoutNoCull);

            float angle = lit ? (level.getGameTime() + partialTick) * 0.05f : this.calcAngle(blockstate);
            this.renderKey(poseStack, multiBufferSource, stage, angle, packedLight, packedOverlay);
            this.body.render(poseStack, consumer, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderKey(PoseStack poseStack, MultiBufferSource buffers, TarnishStage stage, float angle, int light, int overlay) {
        Material texture = KEY_TEXTURES.getOrDefault(stage, KEY_TEXTURES.get(TarnishStage.UNAFFECTED));
        VertexConsumer consumer = texture.buffer(buffers, RenderType::entityCutoutNoCull);

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotation(angle));
        PoseStack.Pose pose = poseStack.last();

        keyFace(consumer, pose, light, overlay, Direction.NORTH, 7, 16, 7, 9, 21, 9, KEY_STEM_NORTH);
        keyFace(consumer, pose, light, overlay, Direction.EAST, 7, 16, 7, 9, 21, 9, KEY_STEM_EAST);
        keyFace(consumer, pose, light, overlay, Direction.SOUTH, 7, 16, 7, 9, 21, 9, KEY_STEM_SOUTH);
        keyFace(consumer, pose, light, overlay, Direction.WEST, 7, 16, 7, 9, 21, 9, KEY_STEM_WEST);
        keyFace(consumer, pose, light, overlay, Direction.UP, 7, 16, 7, 9, 21, 9, KEY_STEM_UP);

        keyFace(consumer, pose, light, overlay, Direction.NORTH, 3, 18, 8, 13, 22, 8, KEY_HANDLE);
        keyFace(consumer, pose, light, overlay, Direction.SOUTH, 3, 18, 8, 13, 22, 8, KEY_HANDLE);

        poseStack.popPose();
    }

    private static void keyFace(VertexConsumer consumer, PoseStack.Pose pose, int light, int overlay, Direction face,
                                float x0, float y0, float z0, float x1, float y1, float z1, int[] uv) {
        float[][] corners = switch (face) {
            case NORTH -> new float[][]{{x1, y1, z0}, {x0, y1, z0}, {x0, y0, z0}, {x1, y0, z0}};
            case SOUTH -> new float[][]{{x0, y1, z1}, {x1, y1, z1}, {x1, y0, z1}, {x0, y0, z1}};
            case EAST -> new float[][]{{x1, y1, z1}, {x1, y1, z0}, {x1, y0, z0}, {x1, y0, z1}};
            case WEST -> new float[][]{{x0, y1, z0}, {x0, y1, z1}, {x0, y0, z1}, {x0, y0, z0}};
            default -> new float[][]{{x0, y1, z0}, {x1, y1, z0}, {x1, y1, z1}, {x0, y1, z1}};
        };
        float u0 = uv[0] / KEY_TEXTURE_SIZE;
        float v0 = uv[1] / KEY_TEXTURE_SIZE;
        float u1 = uv[2] / KEY_TEXTURE_SIZE;
        float v1 = uv[3] / KEY_TEXTURE_SIZE;
        float[][] uvs = {{u0, v0}, {u1, v0}, {u1, v1}, {u0, v1}};
        int[] order = {0, 3, 2, 1};
        for (int index : order) {
            float[] corner = corners[index];
            consumer.addVertex(pose, (corner[0] - 8.0F) / 16.0F, (corner[1] - 16.0F) / 16.0F, (corner[2] - 8.0F) / 16.0F)
                    .setColor(255, 255, 255, 255)
                    .setUv(uvs[index][0], uvs[index][1])
                    .setOverlay(overlay)
                    .setLight(light)
                    .setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
        }
    }

    private float calcAngle(BlockState state) {
        return (float) state.getValue(GearboxBlock.CRANK_LEVEL) / 3;
    }
}