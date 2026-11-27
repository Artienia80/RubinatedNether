package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class StrongboxItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final StrongboxItemRenderer INSTANCE = new StrongboxItemRenderer();

    private static final int ITEM_CYCLE_TICKS = 20;
    private static final float ITEM_SCALE = 1.25F;
    private static final float ITEM_CENTER_Y = 0.5F;
    private static final float BLOCK_CENTER_Y = 0.55F;
    private static final float SPIN_DEGREES_PER_TICK = 2.0F;
    private static final long SPIN_PERIOD_MILLIS = 360000L;

    private final RandomSource random = RandomSource.create();
    private List<ItemStack> displayStacks;

    private StrongboxItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        if (!(block instanceof BronzeStrongboxBlock)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        long gameTime = minecraft.level != null ? minecraft.level.getGameTime() : Util.getMillis() / 50L;
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        BakedModel open = minecraft.getModelManager().getModel(ModelResourceLocation.standalone(RubinatedNether.id("block/" + name + "_open")));
        BakedModel key = minecraft.getModelManager().getModel(ModelResourceLocation.standalone(RubinatedNether.id("block/" + name + "_key_0")));
        VertexConsumer consumer = bufferSource.getBuffer(Sheets.cutoutBlockSheet());
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        itemRenderer.renderModelLists(open, ItemStack.EMPTY, packedLight, packedOverlay, poseStack, consumer);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotation(GearboxRenderer.itemAngle()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        itemRenderer.renderModelLists(key, ItemStack.EMPTY, packedLight, packedOverlay, poseStack, consumer);
        poseStack.popPose();
        if (RNConfig.strongboxItemDisplay) {
            this.renderContents(minecraft, gameTime, poseStack, bufferSource, packedLight);
        }
    }

    private void renderContents(Minecraft minecraft, long gameTime, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        List<ItemStack> stacks = this.stacks();
        ItemStack shown = stacks.get((int) (gameTime / ITEM_CYCLE_TICKS % stacks.size()));
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BakedModel model = itemRenderer.getModel(shown, minecraft.level, null, 0);
        ItemTransform ground = model.getTransforms().ground;
        float centerY = model.isGui3d() ? BLOCK_CENTER_Y : ITEM_CENTER_Y;
        float spin = (float) ((Util.getMillis() % SPIN_PERIOD_MILLIS) / 50.0) * SPIN_DEGREES_PER_TICK;
        poseStack.pushPose();
        poseStack.translate(0.5F, centerY - ground.translation.y() * ITEM_SCALE, 0.5F);
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        this.random.setSeed(ItemEntityRenderer.getSeedForItemStack(shown));
        ItemEntityRenderer.renderMultipleFromCount(itemRenderer, poseStack, bufferSource, light, shown, this.random, minecraft.level);
        poseStack.popPose();
    }

    private List<ItemStack> stacks() {
        if (this.displayStacks == null) {
            this.displayStacks = List.of(
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(RNItems.RUBY.get()),
                    new ItemStack(RNItems.BRONZE_SHOT.get()),
                    new ItemStack(Items.QUARTZ),
                    new ItemStack(RNItems.POMEGRANATE.get()),
                    new ItemStack(RNItems.MOLTEN_RUBY.get()),
                    new ItemStack(Items.ARROW),
                    new ItemStack(RNItems.BRONZE_SCRAP.get()),
                    new ItemStack(Items.OBSIDIAN),
                    new ItemStack(RNBlocks.PUMICE.get())
            );
        }
        return this.displayStacks;
    }
}
