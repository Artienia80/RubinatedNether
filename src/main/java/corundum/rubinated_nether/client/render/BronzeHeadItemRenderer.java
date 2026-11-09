package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.BronzeHeadBlock;
import corundum.rubinated_nether.content.RNModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BronzeHeadItemRenderer extends BlockEntityWithoutLevelRenderer {
	public static final BronzeHeadItemRenderer INSTANCE = new BronzeHeadItemRenderer();

	private BronzeHeadModel model;

	private BronzeHeadItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		this.model = null;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (model == null) {
			model = new BronzeHeadModel(Minecraft.getInstance().getEntityModels().bakeLayer(RNModelLayers.BRONZE_HEAD));
		}
		TarnishStage stage = stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BronzeHeadBlock head
				? head.getStage()
				: TarnishStage.UNAFFECTED;
		ResourceLocation texture = BronzeHeadRenderer.textureFor(stage);

		model.setKeyVisible(true);
		model.setKeyAngle(0.0F);

		poseStack.pushPose();
		poseStack.translate(0.5, 0.75, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture)), packedLight, packedOverlay);
		poseStack.popPose();
	}
}
