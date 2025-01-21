package corundum.rubinated_nether;

import corundum.rubinated_nether.client.RubinatedNetherClient;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.entity.client.BronzeChargeProjectileRenderer;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.recipe.RNRecipeCategories;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;

import corundum.rubinated_nether.data.Datagen;
import corundum.rubinated_nether.misc.DatapackRegistry;
import corundum.rubinated_nether.utils.RNConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(RubinatedNether.MODID)
public class RubinatedNether {
	public static final String MODID = "rubinated_nether";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static final ImmutableList<DeferredRegister<?>> REGISTRIES = ImmutableList.of(
		RNBlocks.BLOCKS,
		RNItems.ITEMS,
		RNEntities.ENTITY_TYPES,
		RNParticleTypes.PARTICLES,
		RNSoundEvents.SOUNDS,
		RNCreativeTabs.CREATIVE_MODE_TABS,
		RNRecipes.RECIPE_TYPES,
		RNRecipeSerializers.RECIPE_SERIALIZERS,
		RNBlockEntities.BLOCK_ENTITY_TYPES,
		RNMenuTypes.MENUS
	);

	public RubinatedNether(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
		LOGGER.info("Rubinating all over your Nether...");
		MidnightConfig.init(MODID, RNConfig.class);

		for (var registry : REGISTRIES) 
			registry.register(modEventBus);

		modEventBus.addListener(Datagen::datagen);
		modEventBus.addListener(DatapackRegistry::datapackRegistry);

		if (dist == Dist.CLIENT) {
			RubinatedNetherClient.client(modEventBus);
			modEventBus.addListener(RNRecipeCategories::registerRecipeCategories);
		}
	}

	@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			EntityRenderers.register(
				RNEntities.BRONZE_CHARGE.get(), 
				BronzeChargeProjectileRenderer::new
			);
		}
	}

	@EventBusSubscriber(modid = MODID)
	public static class ServerModEvents {
		@SubscribeEvent
		public static void freezerFuel(ServerAboutToStartEvent event) {
			var entries = event.getServer().registryAccess().registryOrThrow(DatapackRegistry.FREEZER_FUELS).entrySet();
			LOGGER.info("Registered Freezer Fuels: " + entries.size());

			for (var entry : entries) {
				var x = entry.getValue();
				var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(x.item()));

				LOGGER.info(x.toString());
				FreezerBlockEntity.addItemFreezingTime(item, x.freezeTime());
			}
		}
	}

	public static ResourceLocation id(String s) {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, s);
	}
}
