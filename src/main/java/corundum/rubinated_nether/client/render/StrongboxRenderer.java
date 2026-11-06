package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.content.blocks.entities.BronzeStrongboxBlockEntity;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.Util;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class StrongboxRenderer implements BlockEntityRenderer<BronzeStrongboxBlockEntity> {
    private static final int NATURAL_CYCLE_TICKS = 20;
    private static final int GRID_COLUMNS = 3;
    private static final int GRID_SLOTS = BronzeStrongboxBlockEntity.CONTAINER_SIZE;
    private static final float GRID_STEP = 0.22F;
    private static final float GRID_SCALE = 0.625F;
    private static final float CENTER = 0.5F;
    private static final float NATURAL_SCALE = 1.25F;
    private static final float ITEM_CENTER_Y = 0.5F;
    private static final float BLOCK_CENTER_Y = 0.55F;
    private static final float SPIN_DEGREES_PER_TICK = 2.0F;
    private static final long SPIN_PERIOD_MILLIS = 360000L;

    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public StrongboxRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BronzeStrongboxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!RNConfig.strongboxItemDisplay) {
            return;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(BronzeStrongboxBlock.KEY) == 0) {
            return;
        }
        List<ItemStack> stacks = blockEntity.getDisplayStacks();
        if (stacks.isEmpty()) {
            return;
        }
        int light = surroundingLight(level, blockEntity.getBlockPos(), packedLight);
        float spin = (float) ((Util.getMillis() % SPIN_PERIOD_MILLIS) / 50.0) * SPIN_DEGREES_PER_TICK;
        if (state.getValue(BronzeStrongboxBlock.NATURAL)) {
            long cycle = Util.getMillis() / (NATURAL_CYCLE_TICKS * 50L);
            int hash = Mth.murmurHash3Mixer((int) (cycle ^ (cycle >>> 32)) ^ blockEntity.getBlockPos().hashCode());
            this.renderItem(level, poseStack, bufferSource, light, stacks.get(Math.floorMod(hash, stacks.size())), CENTER, CENTER, NATURAL_SCALE, spin);
            return;
        }
        int count = Math.min(stacks.size(), GRID_SLOTS);
        for (int i = 0; i < count; i++) {
            float x = CENTER + (i % GRID_COLUMNS - 1) * GRID_STEP;
            float z = CENTER + (i / GRID_COLUMNS - 1) * GRID_STEP;
            this.renderItem(level, poseStack, bufferSource, light, stacks.get(i), x, z, GRID_SCALE, spin);
        }
    }

    private void renderItem(Level level, PoseStack poseStack, MultiBufferSource bufferSource, int light, ItemStack stack, float x, float z, float scale, float spin) {
        BakedModel model = this.itemRenderer.getModel(stack, level, null, 0);
        ItemTransform ground = model.getTransforms().ground;
        float centerY = model.isGui3d() ? BLOCK_CENTER_Y : ITEM_CENTER_Y;
        poseStack.pushPose();
        poseStack.translate(x, centerY - ground.translation.y() * scale, z);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        this.random.setSeed(ItemEntityRenderer.getSeedForItemStack(stack));
        ItemEntityRenderer.renderMultipleFromCount(this.itemRenderer, poseStack, bufferSource, light, stack, this.random, level);
        poseStack.popPose();
    }

    private static int surroundingLight(Level level, BlockPos pos, int fallback) {
        int block = LightTexture.block(fallback);
        int sky = LightTexture.sky(fallback);
        for (Direction direction : Direction.values()) {
            int neighbour = LevelRenderer.getLightColor(level, pos.relative(direction));
            block = Math.max(block, LightTexture.block(neighbour));
            sky = Math.max(sky, LightTexture.sky(neighbour));
        }
        return LightTexture.pack(block, sky);
    }
}