package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.content.blocks.entities.GearboxBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GearboxRenderer implements BlockEntityRenderer<GearboxBlockEntity> {
    public static final ResourceLocation BODY_TEX = RubinatedNether.id("textures/block/gearbox/gearbox.png");
    public static final ResourceLocation CRANK_TEX = RubinatedNether.id("textures/block/gearbox/crank.png");

    private static final String BODY = "body";
    private static final String CRANK = "crank";
    private final ModelPart crank;
    private final ModelPart body;

    public GearboxRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(RNModelLayers.GEARBOX);
        this.body = modelpart.getChild(BODY);
        this.crank = modelpart.getChild(CRANK);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(BODY,
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F),
                PartPose.ZERO);
        partdefinition.addOrReplaceChild(CRANK,
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, 15.0F, 8.0F, 16.0F, 16.0F, 0.0F),
                PartPose.offset(8.0F, 23.0F, 8.0F).rotation(0f, 45f, 0f));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(GearboxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        boolean flag = level != null;

        BlockState blockstate = flag ? blockEntity.getBlockState() : RNBlocks.GEARBOX.get().defaultBlockState();
        Block var12 = blockstate.getBlock();
        if (var12 instanceof GearboxBlock) {
            poseStack.pushPose();
            boolean f = blockstate.getValue(GearboxBlock.LIT);
            if(f) {
                this.render(poseStack, multiBufferSource, this.crank, this.body, 90, 0, packedOverlay);
            }
            poseStack.popPose();
        }
    }

    private void render(PoseStack poseStack, MultiBufferSource source, ModelPart crank, ModelPart body, float angle, int packedLight, int packedOverlay) {
        crank.y = -(angle * 3 * ((float) Math.PI / 2F));

        VertexConsumer crankConsumer = source.getBuffer(RenderType.entityCutout(CRANK_TEX));
        crank.render(poseStack, crankConsumer, packedLight, packedOverlay);

        VertexConsumer bodyConsumer = source.getBuffer(RenderType.entityCutout(BODY_TEX));
        body.render(poseStack, bodyConsumer, packedLight, packedOverlay);
    }
}
