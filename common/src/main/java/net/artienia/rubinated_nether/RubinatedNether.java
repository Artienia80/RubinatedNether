package net.artienia.rubinated_nether;

import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import net.artienia.rubinated_nether.config.RNConfig;
import net.artienia.rubinated_nether.content.*;
import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.artienia.rubinated_nether.integrations.RNModCompat;
import net.artienia.rubinated_nether.worldgen.RNPlacementFilters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwu.serenity.critter.RegistryManager;

public final class RubinatedNether {

    public static final String MOD_ID = "rubinated_nether";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RegistryManager REGISTRIES = RegistryManager.create(MOD_ID);
    public static final Configurator CONFIGURATOR = new Configurator();

    public static void init() {
        // *Rubinates your nether*
        LOGGER.info("Rubinating all over your nether");

        // Register the config to the configurator
        CONFIGURATOR.registerConfig(RNConfig.class);

        // Intialize the registries
        RNSounds.register();
        RNParticleTypes.register();
        RNBlocks.register();
        RNItems.register();
        RNBlockEntities.register();
        RNTabs.register();
        RNMenuTypes.register();
        RNRecipes.register();
        RNPlacementFilters.register();
        RNLootNumberProviders.register();
        RNLootConditions.register();

        // Initialize mod compat
        RNModCompat.init();
    }

    public static void setup() {
        // Register freezing times
        FreezerBlockEntity.addItemFreezingTime(Items.SNOWBALL, 25);
        FreezerBlockEntity.addItemFreezingTime(Blocks.SNOW_BLOCK, 150);
        FreezerBlockEntity.addItemFreezingTime(Blocks.FROSTED_ICE, 300);
        FreezerBlockEntity.addItemFreezingTime(Blocks.ICE, 600);
        FreezerBlockEntity.addItemFreezingTime(Blocks.PACKED_ICE, 1200);
        FreezerBlockEntity.addItemFreezingTime(Blocks.BLUE_ICE, 2400);
        FreezerBlockEntity.addItemFreezingTime(RNBlocks.DRY_ICE, 4800);

        // Mod compat setup
        RNModCompat.setup();
    }


    public static ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
}
