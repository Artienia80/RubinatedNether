package net.artienia.rubinated_nether.forge;


import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.nio.file.Path;

@Mod(RubinatedNether.MOD_ID)
public final class RubinatedNetherForge {
    public RubinatedNetherForge() {
        // Event bus stuff
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(RubinatedNether.REGISTRIES);
        modBus.addListener(this::onSetup);
        modBus.addListener(this::addResourcePack);

        // Run our common setup.
        RubinatedNether.init();
    }

    public void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RubinatedNether::setup);
    }


    public void addResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Path resourcePath = ModList.get().getModFileById(RubinatedNether.MOD_ID).getFile().findResource("resourcepacks/better_netherite_template");

            Pack pack = Pack.readMetaAndCreate("rubinated_nether/better_netherite_template", Component.literal("Rubinated Netherite Template"), false,
                path -> new PathPackResources(path, resourcePath, true), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.DEFAULT);
            Pack pack2 = Pack.readMetaAndCreate("rubinated_nether/better_netherite_template", Component.literal("Rubinated Netherite Template"), false,
                path -> new PathPackResources(path, resourcePath, true), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.DEFAULT);

            if(!FMLEnvironment.production) return;
            event.addRepositorySource(packConsumer -> {
                packConsumer.accept(pack);
                packConsumer.accept(pack2);
            });
        }
    }
}
