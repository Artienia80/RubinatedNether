package net.artienia.rubinated_nether.fabric.client.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.artienia.rubinated_nether.client.render.entity.RubyLensRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RubyLensTrinketRenderer extends RubyLensRenderer implements TrinketRenderer {

	@Override
	public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, LivingEntity livingEntity, float v, float v1, float v2, float v3, float v4, float v5) {
		this.renderInternal(entityModel, poseStack, multiBufferSource, i);
	}
}
