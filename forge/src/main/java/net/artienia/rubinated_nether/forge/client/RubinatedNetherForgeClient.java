package net.artienia.rubinated_nether.forge.client;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RubinatedNether.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RubinatedNetherForgeClient {

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        event.enqueueWork(RubinatedNetherClient::clientSetup);
    }

}
