package net.artienia.rubinated_nether.forge;


import net.artienia.rubinated_nether.client.config.RNConfigScreen;
import net.artienia.rubinated_nether.forge.conditions.RecipeConfigCondition;
import net.artienia.rubinated_nether.forge.conditions.ModLoadedLootTable;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraftforge.fml.loading.FMLEnvironment;
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

        // Config screen
        if(FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> RNConfigScreen.create(screen)));
        }
    }

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RubinatedNether.setup();
            CraftingHelper.register(RecipeConfigCondition.Serializer.INSTANCE);
        });
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

        if(event.getPackType() == PackType.SERVER_DATA) {
            if(ModList.get().isLoaded("spelunkery"))
                createPack(event, modFile, "compat_spelunkery", "Rubinated Nether Spelunkery Compat", true);
        }
    }

    private static void createPack(AddPackFindersEvent event, IModFile modFile, String id, String name, boolean required) {
        Path resourcePath = modFile.findResource("resourcepacks/" + id);

        Pack pack = Pack.readMetaAndCreate(
                "builtin/" + id,
                Component.literal(name), required,
                path -> new PathPackResources(path, resourcePath, true),
                event.getPackType(), Pack.Position.TOP, PackSource.BUILT_IN
        );

        if(pack != null) event.addRepositorySource(consumer -> consumer.accept(pack));
    }
}
