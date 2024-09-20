package net.artienia.rubinated_nether.forge.client;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.client.render.RubyLensOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RubinatedNether.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RubinatedNetherForgeClient {

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        event.enqueueWork(RubinatedNetherClient::clientSetup);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.VIGNETTE.id(), "ruby_lens_overlay",
            (forgeGui, arg, f, i, j) -> RubyLensOverlay.renderHud(forgeGui, arg));
    }

}
