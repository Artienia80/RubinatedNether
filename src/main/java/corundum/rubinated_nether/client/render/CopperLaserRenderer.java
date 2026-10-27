package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.entities.CopperLaserBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CopperLaserRenderer extends AbstractLaserRenderer<CopperLaserBlockEntity> {
	public static final ResourceLocation LASER_TEXTURE = RubinatedNether.id("textures/misc/copper_laser_beam.png");
	public static final ResourceLocation LASER_TEXTURE_GREYSCALE = RubinatedNether.id("textures/misc/laser_beam_greyscale.png");

	private static final int[] SILLY_PALETTE = {0x5BCEFA, 0xF5A9B8, 0xFFFFFF, 0xF5A9B8};
	private static final float SILLY_PERIOD = 80f;

	public CopperLaserRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected float getSpinDirection() {
		return -1f;
	}

	@Override
	protected int getSillyColor(float ticks) {
		float position = (ticks % SILLY_PERIOD) / SILLY_PERIOD * SILLY_PALETTE.length;
		int index = (int) position;
		float delta = position - index;
		int from = SILLY_PALETTE[index % SILLY_PALETTE.length];
		int to = SILLY_PALETTE[(index + 1) % SILLY_PALETTE.length];

		int r = Math.round(Mth.lerp(delta, (from >> 16) & 0xFF, (to >> 16) & 0xFF));
		int g = Math.round(Mth.lerp(delta, (from >> 8) & 0xFF, (to >> 8) & 0xFF));
		int b = Math.round(Mth.lerp(delta, from & 0xFF, to & 0xFF));
		return (r << 16) | (g << 8) | b;
	}

	@Override
	protected float getFadeRange(CopperLaserBlockEntity blockEntity) {
		// Copper laser always has 15 block range
		return 15f;
	}

	@Override
	protected RenderType getRenderType(boolean colored) {
		if (ShaderHelper.isShaderPackInUse())
			return RenderType.entityTranslucentEmissive(colored ? LASER_TEXTURE_GREYSCALE : LASER_TEXTURE);
		else
			// FIX: Use copper-specific render types instead of shared ones
			return colored ? RNRenderTypes.COPPER_LASER_BEAM_GRAYSCALE : RNRenderTypes.COPPER_LASER_BEAM;
	}
}
