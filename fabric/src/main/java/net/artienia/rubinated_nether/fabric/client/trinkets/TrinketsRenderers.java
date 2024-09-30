package net.artienia.rubinated_nether.fabric.client.trinkets;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.artienia.rubinated_nether.content.RNItems;

public class TrinketsRenderers {

    public static void register() {
        TrinketRendererRegistry.registerRenderer(RNItems.RUBY_LENS.get(), new RubyLensTrinketRenderer());
    }

}
