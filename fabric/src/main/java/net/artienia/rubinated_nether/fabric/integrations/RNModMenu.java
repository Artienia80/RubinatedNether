package net.artienia.rubinated_nether.fabric.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.client.config.RNConfigScreen;

public class RNModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RNConfigScreen::create;
    }
}
