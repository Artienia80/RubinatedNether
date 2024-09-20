package net.artienia.rubinated_nether.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.artienia.rubinated_nether.block.entity.ModBlockEntityTypes;
import net.artienia.rubinated_nether.client.render.blockEntity.RubyLaserRenderer;
import net.artienia.rubinated_nether.client.render.entity.RubyLensModel;
import net.artienia.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import net.artienia.rubinated_nether.screen.FreezerScreen;
import net.artienia.rubinated_nether.screen.ModMenuTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class RubinatedNetherClient {
    public static void clientSetup() {
        // Register screen provider
        MenuRegistry.registerScreenFactory(ModMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
        // Register ruby laser renderer
        BlockEntityRendererRegistry.register(ModBlockEntityTypes.RUBY_LASER.get(), RubyLaserRenderer::new);
        // Register ruby lens model
        EntityModelLayerRegistry.register(RubyLensModel.LAYER_LOCATION, RubyLensModel::createBodyLayer);
    }

    @SuppressWarnings("unchecked")
    public static void registerEntityLayers(EntityRenderDispatcher dispatcher, EntityModelSet models, Function<String, PlayerRenderer> skinGetter) {
        dispatcher.renderers.forEach((type, renderer) -> {
            if(renderer instanceof LivingEntityRenderer<?,?> livingRenderer && livingRenderer.getModel() instanceof HeadedModel) {
                livingRenderer.addLayer(new RubyLensRenderLayer(livingRenderer, models, livingRenderer.getModel()));
            }
        });

        PlayerRenderer defaultSkin = skinGetter.apply("default");
        PlayerRenderer slimSkin = skinGetter.apply("slim");
        defaultSkin.addLayer(new RubyLensRenderLayer<>(defaultSkin, models, defaultSkin.getModel()));
        slimSkin.addLayer(new RubyLensRenderLayer<>(slimSkin, models, slimSkin.getModel()));
    }
}
