package net.artienia.rubinated_nether.client.config;

import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfig;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RNConfigScreen extends ConfigScreen {

	private static final ResourceLocation ORIGINAL = new ResourceLocation("textures/gui/options_background.png");
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
	public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
		BACKGROUND_LOCATION = BACKROUND;
		super.render(graphics, i, j, f);
		BACKGROUND_LOCATION = ORIGINAL;
	}
}
