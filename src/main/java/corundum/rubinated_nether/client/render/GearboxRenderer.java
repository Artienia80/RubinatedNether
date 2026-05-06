package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.content.blocks.entities.GearboxBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GearboxRenderer implements BlockEntityRenderer<GearboxBlockEntity> {
    public static final Material BODY_TEX = new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/gearbox/gearbox"));
    public static final Material ANIM_BODY_TEX = new Material(InventoryMenu.BLOCK_ATLAS, RubinatedNether.id("block/gearbox/gearbox_anim"));

    private static final String BODY = "body";
    private static final String CRANK = "crank";
    private final ModelPart crank;
    private final ModelPart body;

    public GearboxRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(RNModelLayers.GEARBOX);
        this.body = root.getChild(BODY);
        this.crank = root.getChild(CRANK);
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

        partdefinition.addOrReplaceChild("crank", CubeListBuilder.create().texOffs(48, 48).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.0F, 23.0F, 8.0F, 3.14159F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void render(GearboxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        boolean flag = level != null;

        BlockState blockstate = flag ? blockEntity.getBlockState() : RNBlocks.GEARBOX.get().defaultBlockState();
        Block block = blockstate.getBlock();
        if (block instanceof GearboxBlock) {
            poseStack.pushPose();
            boolean f = blockstate.getValue(GearboxBlock.LIT);
            float dir = blockstate.getValue(GearboxBlock.FACING).toYRot();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-dir));
            poseStack.translate(-0.5F, -0.5F, -0.5F);


            this.render(level, partialTick, poseStack, multiBufferSource, this.crank, this.body, packedLight, packedOverlay, f);
            poseStack.popPose();
        }
    }

    private void render(Level level, float partialTick, PoseStack poseStack, MultiBufferSource source, ModelPart crank, ModelPart body, int packedLight, int packedOverlay, boolean f) {
        VertexConsumer consumer = (f ? ANIM_BODY_TEX : BODY_TEX).buffer(source, RenderType::entityCutoutNoCull);

        crank.yRot = f ? (level.getGameTime() + partialTick) * 0.05f : this.crank.yRot;
        crank.render(poseStack, consumer, packedLight, packedOverlay);
        body.render(poseStack, consumer, packedLight, packedOverlay);
    }
}
