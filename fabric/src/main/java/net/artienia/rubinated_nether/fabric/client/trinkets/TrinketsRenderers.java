package net.artienia.rubinated_nether.fabric.client.trinkets;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.artienia.rubinated_nether.item.ModItems;

public class TrinketsRenderers {

    public static void register() {
        TrinketRendererRegistry.registerRenderer(ModItems.RUBY_LENS.get(), new RubyLensTrinketRenderer());
    }

}
