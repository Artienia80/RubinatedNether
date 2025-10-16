package corundum.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RNRenderTypes extends RenderType {
	public RNRenderTypes(
			String name,
			VertexFormat format,
			VertexFormat.Mode mode,
			int bufferSize,
			boolean affectsCrumbling,
			boolean sortOnUpload,
			Runnable setupState,
			Runnable clearState
	) {
		super(
				name,
				format,
				mode,
				bufferSize,
				affectsCrumbling,
				sortOnUpload,
				setupState,
				clearState
		);
	}

	// Bronze laser render types (existing)
	public static final RenderType LASER_BEAM = createRenderType(
			"laser_beam",
			BronzeLaserRenderer.LASER_TEXTURE
	);

	public static final RenderType LASER_BEAM_GRAYSCALE = createRenderType(
			"laser_beam_grayscale",
			BronzeLaserRenderer.LASER_TEXTURE_GREYSCALE
	);

	// Copper laser render types (new)
	public static final RenderType COPPER_LASER_BEAM = createRenderType(
			"copper_laser_beam",
			CopperLaserRenderer.LASER_TEXTURE
	);

	public static final RenderType COPPER_LASER_BEAM_GRAYSCALE = createRenderType(
			"copper_laser_beam_grayscale",
			CopperLaserRenderer.LASER_TEXTURE_GREYSCALE
	);

	private static RenderType createRenderType(String name, ResourceLocation texture) {
		return RenderType.create(
				RubinatedNether.MODID + ":" + name,
				DefaultVertexFormat.BLOCK,
				VertexFormat.Mode.QUADS,
				512,
				false,
				true,
				CompositeState.builder()
						.setShaderState(new ShaderStateShard(GameRenderer::getRendertypeBeaconBeamShader))
						.setTextureState(new TextureStateShard(texture, false, false))
						.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)

						.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
						.setCullState(RenderStateShard.CULL)
						.setLightmapState(RenderStateShard.NO_LIGHTMAP)
						.setOverlayState(RenderStateShard.NO_OVERLAY)
						.setOutputState(RenderStateShard.MAIN_TARGET)
						.createCompositeState(false)
		);
	}
}