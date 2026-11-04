package corundum.rubinated_nether.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.utils.RubyLensHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CuriosRubyLensRenderer extends RubyLensRenderer implements ICurioRenderer {
	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(
		ItemStack stack,
		SlotContext slotContext,
		PoseStack poseStack,
		RenderLayerParent<T, M> renderLayerParent,
		MultiBufferSource buffer,
		int light,
		float limbSwing,
		float limbSwingAmount,
		float partialTicks,
		float ageInTicks,
		float netHeadYaw,
		float headPitch
	) {
		if (RubyLensHelper.isWearingInHead(slotContext.entity())) return;
		renderInternal(renderLayerParent.getModel(), poseStack, buffer, light);
	}
}
