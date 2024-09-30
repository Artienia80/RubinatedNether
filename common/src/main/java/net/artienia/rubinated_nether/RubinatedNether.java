package net.artienia.rubinated_nether;

import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import net.artienia.rubinated_nether.config.RNConfig;
import net.artienia.rubinated_nether.content.*;
import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.artienia.rubinated_nether.worldgen.RNPlacementFilters;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwu.serenity.critter.RegistryManager;
import uwu.serenity.critter.platform.PlatformUtils;

public final class RubinatedNether {

    public static final String MOD_ID = "rubinated_nether";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RegistryManager REGISTRIES = RegistryManager.create(MOD_ID);
    public static final Configurator CONFIGURATOR = new Configurator();

    public static void init() {
        LOGGER.info("Rubinating your nether");

        // Register the config to the configurator
        CONFIGURATOR.registerConfig(RNConfig.class);

        // Intialize the registries
        RNSounds.register();
        RNBlocks.register();
        RNItems.register();
        RNBlockEntities.register();
        RNTabs.init();
        RNMenuTypes.register();
        RNRecipes.register();
        RNPlacementFilters.register();
    }

    public static void setup() {
        FreezerBlockEntity.addItemFreezingTime(Items.SNOWBALL, 50);
        FreezerBlockEntity.addItemFreezingTime(Blocks.SNOW_BLOCK, 400);
        FreezerBlockEntity.addItemFreezingTime(Blocks.FROSTED_ICE, 800);
        FreezerBlockEntity.addItemFreezingTime(Blocks.ICE, 1600);
        FreezerBlockEntity.addItemFreezingTime(Blocks.BLUE_ICE, 3200);
        FreezerBlockEntity.addItemFreezingTime(Blocks.PACKED_ICE, 6400);

        if(PlatformUtils.modLoaded("aether")){
            // Scuffing this a little to avoid loading more dependencies
            Block icestone = BuiltInRegistries.BLOCK.get(new ResourceLocation("aether", "icestone"));
            FreezerBlockEntity.addItemFreezingTime(icestone, 1200);
        }
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
}
