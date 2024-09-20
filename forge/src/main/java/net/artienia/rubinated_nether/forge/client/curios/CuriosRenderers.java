package net.artienia.rubinated_nether.forge.client.curios;

import net.artienia.rubinated_nether.item.ModItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderers {

    public static void register() {
        CuriosRendererRegistry.register(ModItems.RUBY_LENS.get(), RubyLensCurioRenderer::new);
    }
}
