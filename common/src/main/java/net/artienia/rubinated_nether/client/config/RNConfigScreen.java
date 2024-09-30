package net.artienia.rubinated_nether.client.config;

import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfig;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RNConfigScreen extends ConfigScreen {

    private static final ResourceLocation BACKROUND = RubinatedNether.id("textures/block/ruby_block.png");

    public RNConfigScreen(@Nullable Screen lastScreen, @Nullable ConfigScreen screen, ResourcefulConfig config) {
        super(lastScreen, screen, config);
    }

    public static RNConfigScreen create(@Nullable Screen parent) {
        ResourcefulConfig config = RubinatedNether.CONFIGURATOR.getConfig(RNConfig.class);
        if(config == null) return null;
        return new RNConfigScreen(parent, null, config);
    }

    @Override
    public void renderDirtBackground(GuiGraphics guiGraphics) {
        guiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        guiGraphics.blit(BACKROUND, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
