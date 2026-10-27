package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.entities.BronzeLaserBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BronzeLaserRenderer extends AbstractLaserRenderer<BronzeLaserBlockEntity> {
	public static final ResourceLocation LASER_TEXTURE = RubinatedNether.id("textures/misc/bronze_laser_beam.png");
	public static final ResourceLocation LASER_TEXTURE_GREYSCALE = RubinatedNether.id("textures/misc/laser_beam_greyscale.png");

	public BronzeLaserRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected float getSpinDirection() {
		return 1f;
	}

	@Override
	protected int getSillyColor(float ticks) {
		float hue = ticks % 50f / 50f;
		return Mth.hsvToRgb(hue, .8f, 1f);
	}

	@Override
	protected float getFadeRange(BronzeLaserBlockEntity blockEntity) {
		// Get the maximum range for this laser type to calculate proper fade
		return (float) blockEntity.getCurrentRange();
	}

	@Override
	protected RenderType getRenderType(boolean colored) {
		if (ShaderHelper.isShaderPackInUse())
			return RenderType.entityTranslucentEmissive(colored ? LASER_TEXTURE_GREYSCALE : LASER_TEXTURE);
		else
			return colored ? RNRenderTypes.LASER_BEAM_GRAYSCALE : RNRenderTypes.LASER_BEAM;
	}
}
