package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.BronzeHeadBlock;
import corundum.rubinated_nether.content.blocks.entities.BronzeHeadBlockEntity;
import corundum.rubinated_nether.content.RNModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;

public class BronzeHeadItemRenderer extends BlockEntityWithoutLevelRenderer {
	public static final BronzeHeadItemRenderer INSTANCE = new BronzeHeadItemRenderer();

	private static final float STEP_DISTANCE = 0.4F;
	private static final int STEPS = 16;

	private static LivingEntity wearer;

	private BronzeHeadModel model;

	private BronzeHeadItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	public static void setWearer(LivingEntity entity) {
		wearer = entity;
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

		BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);
		boolean keyless = properties != null && "false".equals(properties.properties().get(BronzeHeadBlock.KEYED.getName()));

		model.setKeyVisible(!keyless);
		float keyDegrees = 0.0F;
		if (displayContext == ItemDisplayContext.HEAD && wearer != null) {
			int step = (int) Math.floor(wearer.walkDist / STEP_DISTANCE);
			keyDegrees = Math.floorMod(step, STEPS) * BronzeHeadBlockEntity.DEGREES_PER_POWER;
		}
		model.setKeyAngle(keyDegrees * Mth.DEG_TO_RAD);

		poseStack.pushPose();
		poseStack.translate(0.5, 0.75, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture)), packedLight, packedOverlay);
		poseStack.popPose();
	}
}
