package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import corundum.rubinated_nether.content.entity.client.BronzeModel;
import corundum.rubinated_nether.content.entity.client.BronzeRenderer;
import corundum.rubinated_nether.content.entity.layer.RNModelLayers;
import corundum.rubinated_nether.content.entity.living.BronzeTypeEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RNEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
		BuiltInRegistries.ENTITY_TYPE, 
		RubinatedNether.MODID
	);

	public static final Supplier<EntityType<BronzeShotProjectileEntity>> BRONZE_SHOT = registerEntity(
			"bronze_shot",
			EntityType.Builder.<BronzeShotProjectileEntity>of(BronzeShotProjectileEntity::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
	);

	public static final Supplier<EntityType<BronzeTypeEntity>> BRONZE_ENTITY =
			registerEntity(
			"bronze",
			EntityType.Builder.of(BronzeTypeEntity::new, MobCategory.MONSTER)
					.sized(0.7F, 1.4F)
	);

	private static <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
		return ENTITY_TYPES.register(
				name,
				() -> builder.build(name)
		);
	}

	// ATTRIBUTES

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(RNEntities.BRONZE_ENTITY.get(), BronzeTypeEntity.createMonsterAttributes().build());
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(BronzeTypeEntity::init);
	}

	// RENDERERS

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(RNEntities.BRONZE_ENTITY.get(), BronzeRenderer::new);
	}

	// LAYERS

	@SubscribeEvent
	public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(RNModelLayers.BRONZE, BronzeModel::createBodyLayer);
	}
}
