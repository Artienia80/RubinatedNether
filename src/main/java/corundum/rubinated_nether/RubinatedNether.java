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
import corundum.rubinated_nether.misc.DatapackRegistry;
import corundum.rubinated_nether.misc.RNAttachments;
import corundum.rubinated_nether.utils.RNConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * A couple of guidelines for this codebase: <br>
 *     <b>-</b> The number of parameters in a method should never be more than <b>7</b>. <br>
 *     <b>-</b> In normal circumstances, methods should not be longer than <b>10</b> lines of code.
 *     Having more than that likely means some behaviour can be detached. And yes, lines with just
 *     a bracket or single line breakdowns don't count.   <br>
 *     <b>-</b> It is recommended that a line doesn't go over <b>100</b> columns. If it does it is either
 *     by a small count, or they should be cut down into more lines. <br>
 */
@Mod(RubinatedNether.MODID)
public class RubinatedNether {

	//TODO: Code Cleanup (including mandating the use of "var")
	//TODO: Add more comments

	//TODO: Figure out why the f**k accesstransformers are not working

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
			RNMenuTypes.MENUS
	);

	public RubinatedNether(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
		LOGGER.info("Rubinating all over your Nether...");

		modEventBus.addListener(this::commonSetup);

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
		}
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
//        LimitlessInvWrapper.registerLimitlessBlockEntityContainer(
//              CofferBlockEntity::getContainer,
//              Holder.direct(RNBlockEntities.COFFER.get())
//        );
		});
	}

	public void registerCommands(RegisterCommandsEvent event) {
		RubinateCommand.register(event.getDispatcher());
	}

	public static ResourceLocation id(String s) {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, s);
	}
}