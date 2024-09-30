package net.artienia.rubinated_nether.forge.client.curios;

import net.artienia.rubinated_nether.content.RNItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderers {

    public static void register() {
        CuriosRendererRegistry.register(RNItems.RUBY_LENS.get(), RubyLensCurioRenderer::new);
    }
}
