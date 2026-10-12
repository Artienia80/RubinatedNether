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
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static final RenderType LASER_BEAM = createRenderType(
			"laser_beam",
			BronzeLaserRenderer.LASER_TEXTURE
	);

	public static final RenderType LASER_BEAM_GRAYSCALE = createRenderType(
			"laser_beam_grayscale",
			BronzeLaserRenderer.LASER_TEXTURE_GREYSCALE
	);

	public static final RenderType COPPER_LASER_BEAM = createRenderType(
			"copper_laser_beam",
			CopperLaserRenderer.LASER_TEXTURE
	);

	public static final RenderType COPPER_LASER_BEAM_GRAYSCALE = createRenderType(
			"copper_laser_beam_grayscale",
			CopperLaserRenderer.LASER_TEXTURE_GREYSCALE
	);

	private static final ResourceLocation RUBINATED_GLINT_ITEM_TEXTURE =
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/misc/rubinated_glint_item.png");

	private static final ResourceLocation RUBINATED_GLINT_ENTITY_TEXTURE =
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/misc/rubinated_glint_entity.png");

	public static final RenderType RUBINATED_GLINT = RenderType.create(
			"rubinated_nether:rubinated_glint",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RENDERTYPE_GLINT_SHADER)
					.setTextureState(new TextureStateShard(RUBINATED_GLINT_ITEM_TEXTURE, true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(EQUAL_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(GLINT_TEXTURING)
					.setLayeringState(NO_LAYERING)
					.createCompositeState(false)
	);

	public static final RenderType RUBINATED_ENTITY_GLINT = RenderType.create(
			"rubinated_nether:rubinated_entity_glint",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
					.setTextureState(new TextureStateShard(RUBINATED_GLINT_ENTITY_TEXTURE, true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(EQUAL_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(ENTITY_GLINT_TEXTURING)
					.setLayeringState(VIEW_OFFSET_Z_LAYERING)
					.createCompositeState(false)
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