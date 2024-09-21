package net.artienia.rubinated_nether.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.artienia.rubinated_nether.client.render.blockEntity.RubyLaserRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class ModRenderTypes extends RenderType {

    public static final RenderType LASER_BEAM = RenderType.create(
        "rubinated_nether:laser_beam",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        512,
        false,
        true,
        CompositeState.builder()
            .setShaderState(new ShaderStateShard(GameRenderer::getRendertypeBeaconBeamShader))
            .setTextureState(new TextureStateShard(RubyLaserRenderer.LASER_TEXTURE, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setCullState(RenderStateShard.CULL)
            .setLightmapState(RenderStateShard.NO_LIGHTMAP)
            .setOverlayState(RenderStateShard.NO_OVERLAY)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );

    public static final RenderType LASER_BEAM_GREYSCALE = RenderType.create(
        "rubinated_nether:laser_beam_greyscale",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        512,
        false,
        true,
        CompositeState.builder()
            .setShaderState(new ShaderStateShard(GameRenderer::getRendertypeBeaconBeamShader))
            .setTextureState(new TextureStateShard(RubyLaserRenderer.LASER_TEXTURE_GREYSCALE, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setCullState(RenderStateShard.CULL)
            .setLightmapState(RenderStateShard.NO_LIGHTMAP)
            .setOverlayState(RenderStateShard.NO_OVERLAY)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );

    private ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}
