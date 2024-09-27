package net.artienia.rubinated_nether.forge.client;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.artienia.rubinated_nether.client.render.hud.RubyLensOverlay;
import net.artienia.rubinated_nether.forge.client.curios.CuriosRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.nio.file.Path;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RubinatedNether.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RubinatedNetherForgeClient {

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        event.enqueueWork(RubinatedNetherClient::clientSetup);
        if(ModList.get().isLoaded("curios")) CuriosRenderers.register();
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.VIGNETTE.id(), "ruby_lens_overlay",
            (forgeGui, arg, f, i, j) -> RubyLensOverlay.renderHud(forgeGui, arg));
    }

    @SubscribeEvent
    public static void registerEntityLayes(EntityRenderersEvent.AddLayers event) {
        RubinatedNetherClient.registerEntityLayers(event.getContext().getEntityRenderDispatcher(), event.getEntityModels(), event::getSkin);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        RubinatedNetherClient.registeModelLayes(event::registerLayerDefinition);
    }

}
