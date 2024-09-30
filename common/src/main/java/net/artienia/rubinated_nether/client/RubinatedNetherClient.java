package net.artienia.rubinated_nether.client;

import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfig;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.client.render.entity.RubyLensModel;
import net.artienia.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import net.artienia.rubinated_nether.config.RNConfig;
import net.artienia.rubinated_nether.content.screen.FreezerScreen;
import net.artienia.rubinated_nether.content.RNMenuTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RubinatedNetherClient {
    public static void clientSetup() {
        // Register screen provider
        MenuScreens.register(RNMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
    }

    public static Screen getConfigScreen(Screen parent) {
        ResourcefulConfig config = RubinatedNether.CONFIGURATOR.getConfig(RNConfig.class);
        if(config == null) return null;
        return new ConfigScreen(parent, null, config);
    }

    public static void registeModelLayes(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> layers) {
        layers.accept(RubyLensModel.LAYER_LOCATION, RubyLensModel::createBodyLayer);
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
