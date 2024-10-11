package net.artienia.rubinated_nether.forge;


import net.artienia.rubinated_nether.client.config.RNConfigScreen;
import net.artienia.rubinated_nether.forge.conditions.ConfigCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.registries.ForgeRegistries;
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
                () -> new ConfigScreenHandler.ConfigScreenFactory(RNConfigScreen::create));
        }
    }

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RubinatedNether::setup);
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
            $ -> CraftingHelper.register(ConfigCondition.SERIALIZER));
    }

    @SubscribeEvent
    public static void addResourcePack(AddPackFindersEvent event) {
        // Broke in dev
        if(!FMLEnvironment.production) return;

        Path resourcePath = ModList.get()
            .getModFileById(RubinatedNether.MOD_ID)
            .getFile()
            .findResource("resourcepacks/better_netherite_template");

        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Pack pack = Pack.readMetaAndCreate(
                "rubinated_nether/better_netherite_template_assets",
                Component.literal("Rubinated Netherite Template"), false,
                path -> new PathPackResources(path, resourcePath, true),
                PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN
            );

            event.addRepositorySource(consumer -> consumer.accept(pack));
        } else if(event.getPackType() == PackType.SERVER_DATA) {
            Pack pack2 = Pack.readMetaAndCreate(
                "rubinated_nether/better_netherite_template_data",
                Component.literal("Rubinated Netherite Template"), true,
                path -> new PathPackResources(path, resourcePath, true),
                PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN
            );

            event.addRepositorySource(consumer -> consumer.accept(pack2));
        }
    }
}
