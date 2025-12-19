package corundum.rubinated_nether;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.client.RubinatedNetherClient;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.commands.RubinateCommand;
import corundum.rubinated_nether.content.RNCapabilities;
import corundum.rubinated_nether.content.enchantment.RNEnchantmentEffects;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.recipe.RNRecipeCategories;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import corundum.rubinated_nether.data.Datagen;
import corundum.rubinated_nether.event.RNAnvilRepairHandler;
import corundum.rubinated_nether.event.RNBronzeDiseasedHeartHandler;
import corundum.rubinated_nether.misc.DatapackRegistry;
import corundum.rubinated_nether.misc.RNAttachments;
import corundum.rubinated_nether.utils.RNConfig;
import corundum.rubinated_nether.utils.RNEntityDataSerializers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(RubinatedNether.MODID)
public class RubinatedNether {

	public static final String MODID = "rubinated_nether";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static final ImmutableList<DeferredRegister<?>> REGISTRIES = ImmutableList.of(
			RNBlocks.BLOCKS,
			RNItems.ITEMS,
			RNAttachments.ATTACHMENT_TYPES,
			RNArmorMaterials.ARMOR_MATERIALS,
			RNEntityCreator.ENTITY_TYPES,
			RNParticleTypes.PARTICLES,
			RNSoundEvents.SOUNDS,
			RNCreativeTabs.CREATIVE_MODE_TABS,
			RNRecipes.RECIPE_TYPES,
			RNRecipeSerializers.RECIPE_SERIALIZERS,
			RNBlockEntities.BLOCK_ENTITY_TYPES,
			RNMenuTypes.MENUS,
			RNEntityDataSerializers.SERIALIZERS
	);

	public RubinatedNether(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
		LOGGER.info("Rubinating all over your Nether...");

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::addPackFinders); // Add this line

		modEventBus.addListener(Datagen::datagen);
		modEventBus.addListener(DatapackRegistry::datapackRegistry);
		RNEnchantmentEffects.register(modEventBus);
		RNEffects.register(modEventBus);
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		RNAnvilRepairHandler.register();
		RNCapabilities.register(modEventBus);

		for (var registry : REGISTRIES)
			registry.register(modEventBus);

		MidnightConfig.init(MODID, RNConfig.class);

		if (dist == Dist.CLIENT) {
			RubinatedNetherClient.client(modEventBus);
			modEventBus.addListener(RNRecipeCategories::registerRecipeCategories);
			NeoForge.EVENT_BUS.addListener(RNBronzeDiseasedHeartHandler::onPlayerHeartType);

		}

	}

	public void addPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
			var resourcePath = ModList.get()
					.getModFileById(MODID)
					.getFile()
					.findResource("resourcepacks/simple_freezer");

			event.addRepositorySource((consumer) -> {
				var pack = Pack.readMetaAndCreate(
						new PackLocationInfo(
								"builtin/simple_freezer",
								Component.literal("Simplified Freezer Model"),
								PackSource.BUILT_IN,
								Optional.empty()
						),
						new Pack.ResourcesSupplier() {
							@Override
							public PackResources openPrimary(PackLocationInfo location) {
								return new PathPackResources(location, resourcePath);
							}

							@Override
							public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
								return new PathPackResources(location, resourcePath);
							}
						},
						PackType.CLIENT_RESOURCES,
						new PackSelectionConfig(false, Pack.Position.TOP, false)
				);

				if (pack != null) {
					consumer.accept(pack);
				}
			});
		}
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}

	public void registerCommands(RegisterCommandsEvent event) {
		RubinateCommand.register(event.getDispatcher());
	}

	public static ResourceLocation id(String s) {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, s);
	}
}