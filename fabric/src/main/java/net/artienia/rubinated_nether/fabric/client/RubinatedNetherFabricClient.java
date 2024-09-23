package net.artienia.rubinated_nether.fabric.client;

import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.fabric.client.trinkets.TrinketsRenderers;
import net.fabricmc.api.ClientModInitializer;
import uwu.serenity.critter.platform.PlatformUtils;

public final class RubinatedNetherFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        RubinatedNetherClient.clientSetup();

        if(PlatformUtils.modLoaded("trinkets")) TrinketsRenderers.register();
    }
}
