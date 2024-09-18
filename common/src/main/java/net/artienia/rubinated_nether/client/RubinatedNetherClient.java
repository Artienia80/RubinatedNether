package net.artienia.rubinated_nether.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.artienia.rubinated_nether.block.entity.ModBlockEntityTypes;
import net.artienia.rubinated_nether.client.blockEntityRenderers.RubyLaserRenderer;
import net.artienia.rubinated_nether.screen.FreezerScreen;
import net.artienia.rubinated_nether.screen.ModMenuTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RubinatedNetherClient {

    public static void clientSetup() {
        // Register screen provider
        MenuRegistry.registerScreenFactory(ModMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
        // Register ruby laser renderer
        BlockEntityRendererRegistry.register(ModBlockEntityTypes.RUBY_LASER.get(), RubyLaserRenderer::new);
    }
}
