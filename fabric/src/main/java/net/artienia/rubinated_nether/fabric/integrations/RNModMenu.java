package net.artienia.rubinated_nether.fabric.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.artienia.rubinated_nether.client.RubinatedNetherClient;

public class RNModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RubinatedNetherClient::getConfigScreen;
    }
}
