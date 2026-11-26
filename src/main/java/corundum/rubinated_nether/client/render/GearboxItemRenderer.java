package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class GearboxItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final GearboxItemRenderer INSTANCE = new GearboxItemRenderer();

    private GearboxRenderer renderer;

    private GearboxItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        if (!(block instanceof GearboxBlock gearbox)) {
            return;
        }
        if (this.renderer == null) {
            this.renderer = new GearboxRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(RNModelLayers.BRONZE_GEARBOX));
        }
        TarnishStage stage = gearbox.getAge();
        Minecraft minecraft = Minecraft.getInstance();
        long gameTime = minecraft.level != null ? minecraft.level.getGameTime() : Util.getMillis() / 50L;
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        float angle = GearboxRenderer.litAngle(gameTime, partialTick);
        this.renderer.renderBox(poseStack, bufferSource, stage, true, angle, false, packedLight, packedOverlay);
    }
}
