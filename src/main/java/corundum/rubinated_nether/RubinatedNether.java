package corundum.rubinated_nether;

import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.recipe.RNRecipeCategories;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import corundum.rubinated_nether.data.Datagen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(RubinatedNether.MODID)
public class RubinatedNether {
	public static final String MODID = "rubinated_nether";
	public static final Logger LOGGER = LogUtils.getLogger();

	public RubinatedNether(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
		LOGGER.info("Rubinating all over your Nether...");
		modEventBus.addListener(RubinatedNether::onSetup);

		RNBlocks.BLOCKS.register(modEventBus);
		RNItems.ITEMS.register(modEventBus);
		RNCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
		RNRecipes.RECIPE_TYPES.register(modEventBus);
		RNRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		RNBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
		RNMenuTypes.MENUS.register(modEventBus);

		modEventBus.addListener(Datagen::datagen);
		if (dist == Dist.CLIENT) {
			modEventBus.addListener(RNRecipeCategories::registerRecipeCategories);
		}
	}

	public static void onSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(RubinatedNether::setup);
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
	}
}
