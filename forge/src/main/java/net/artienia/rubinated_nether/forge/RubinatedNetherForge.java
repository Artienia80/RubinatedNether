package net.artienia.rubinated_nether.forge;


import net.artienia.rubinated_nether.forge.conditions.ModLoadedLootTable;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.RegisterEvent;

import java.nio.file.Path;

@Mod(RubinatedNether.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RubinatedNetherForge {
    public RubinatedNetherForge() {
        // Register the registry manager
        FMLJavaModLoadingContext.get()
            .getModEventBus()
            .register(RubinatedNether.REGISTRIES);

        // Run our common setup.
        RubinatedNether.init();
    }

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RubinatedNether::setup);
    }

    @SubscribeEvent
    public void registerLootData(RegisterEvent event)
    {
        if (!event.getRegistryKey().equals(Registries.LOOT_CONDITION_TYPE))
            return;

        event.register(Registries.LOOT_CONDITION_TYPE, new ResourceLocation(RubinatedNether.MOD_ID, "is_mod_loaded"), () -> ModLoadedLootTable.TYPE);
    }

    @SubscribeEvent
    public static void addResourcePack(AddPackFindersEvent event) {
        IModFile modFile = ModList.get()
            .getModFileById(RubinatedNether.MOD_ID)
            .getFile();

        createPack(event, modFile, "better_netherite_template", "Better Netherite Template", false);



        if(ModList.get().isLoaded("spelunkery"))
            createPack(event, modFile, "compat_spelunkery", "Rubinated Nether Spelunkery Compat", true);

    }

    private static void createPack(AddPackFindersEvent event, IModFile modFile, String id, String name, boolean required) {
        Path resourcePath = modFile.findResource("resourcepacks/" + id);

        Pack pack = Pack.readMetaAndCreate(
                "rubinated_nether/" + id,
                Component.literal(name), required,
                path -> new PathPackResources(path, resourcePath, true),
                event.getPackType(), Pack.Position.TOP, PackSource.BUILT_IN
        );

        if(pack != null) event.addRepositorySource(consumer -> consumer.accept(pack));
    }
}
