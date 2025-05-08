package corundum.rubinated_nether;

import corundum.rubinated_nether.client.RubinatedNetherClient;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.enchantments.RNEnchantmentEffects;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.recipe.RNRecipeCategories;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
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
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * A couple of guidelines for this codebase: <br>
 * 		<b>-</b> The number of parameters in a method should never be more than <b>7</b>. <br>
 * 		<b>-</b> In normal circumstances, methods should not be longer than <b>10</b> lines of code.
 * 		Having more than that likely means some behaviour can be detached. And yes, lines with just
 * 		a bracket or single line breakdowns don't count.	<br>
 * 		<b>-</b> It is recommended that a line doesn't go over <b>100</b> columns. If it does it is either
 * 		by a small count, or they should be cut down into more lines. <br>
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
		RNEnchantmentEffects.ENTITY_ENCHANTMENT_EFFECTS,
		RNArmorMaterials.ARMOR_MATERIALS,
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

		modEventBus.addListener(Datagen::datagen);
		modEventBus.addListener(DatapackRegistry::datapackRegistry);

		for (var registry : REGISTRIES)
			registry.register(modEventBus);

		MidnightConfig.init(MODID, RNConfig.class);

		if (dist == Dist.CLIENT) {
			RubinatedNetherClient.client(modEventBus);
			modEventBus.addListener(RNRecipeCategories::registerRecipeCategories);
		}

		// Register RNLootInjector to the NeoForge event bus
		RNLootInjector.init();
	}


	public static ResourceLocation id(String s) {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, s);
	}
}
