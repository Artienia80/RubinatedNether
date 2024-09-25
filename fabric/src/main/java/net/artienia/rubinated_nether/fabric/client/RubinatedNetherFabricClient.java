package net.artienia.rubinated_nether.fabric.client;

import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.fabric.client.trinkets.TrinketsRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import uwu.serenity.critter.platform.PlatformUtils;

public final class RubinatedNetherFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        RubinatedNetherClient.clientSetup();
        RubinatedNetherClient.registeModelLayes((location, definition) -> EntityModelLayerRegistry.registerModelLayer(location, definition::get));

        if(PlatformUtils.modLoaded("trinkets")) TrinketsRenderers.register();
    }
}
