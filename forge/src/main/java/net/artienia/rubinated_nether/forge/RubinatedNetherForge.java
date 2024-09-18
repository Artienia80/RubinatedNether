package net.artienia.rubinated_nether.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.artienia.rubinated_nether.RubinatedNether;

@Mod(RubinatedNether.MOD_ID)
public final class RubinatedNetherForge {
    public RubinatedNetherForge() {
        // Event bus stuff
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(RubinatedNether.MOD_ID, modBus);
        modBus.addListener(this::onSetup);

        // Run our common setup.
        RubinatedNether.init();
    }

    public void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RubinatedNether::setup);
    }
}
