package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.living.AbstractBronzeEntity;
import corundum.rubinated_nether.content.entity.living.BronzeEntity;
import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import corundum.rubinated_nether.content.entity.client.BronzeModel;
import corundum.rubinated_nether.content.entity.client.BronzeRenderer;
import corundum.rubinated_nether.content.entity.layer.RNModelLayers;
import corundum.rubinated_nether.content.entity.living.CorrodedEntity;
import corundum.rubinated_nether.content.entity.living.CrystallizedEntity;
import corundum.rubinated_nether.content.entity.living.DiscoloredEntity;
import corundum.rubinated_nether.content.entity.living.TarnishedEntity;
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

	public static final Supplier<EntityType<BronzeEntity>> BRONZE_ENTITY =
			registerEntity(
			"bronze",
			EntityType.Builder.of(BronzeEntity::new, MobCategory.MONSTER)
					.sized(0.7F, 1.4F)
	);
	public static final Supplier<EntityType<DiscoloredEntity>> DISCOLORED_ENTITY =
			registerEntity(
			"discolored",
			EntityType.Builder.of(DiscoloredEntity::new, MobCategory.MONSTER)
					.sized(0.7F, 1.4F)
	);
	public static final Supplier<EntityType<CorrodedEntity>> CORRODED_ENTITY =
			registerEntity(
			"corroded",
			EntityType.Builder.of(CorrodedEntity::new, MobCategory.MONSTER)
					.sized(0.7F, 1.4F)
	);
	public static final Supplier<EntityType<TarnishedEntity>> TARNISHED_ENTITY =
			registerEntity(
			"tarnished",
			EntityType.Builder.of(TarnishedEntity::new, MobCategory.MONSTER)
					.sized(0.7F, 1.4F)
	);
	public static final Supplier<EntityType<CrystallizedEntity>> CRYSTALLIZED_ENTITY =
			registerEntity(
			"crystallized",
			EntityType.Builder.of(CrystallizedEntity::new, MobCategory.MONSTER)
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
		event.put(RNEntities.BRONZE_ENTITY.get(), BronzeEntity.createAttributes().build());
		event.put(RNEntities.DISCOLORED_ENTITY.get(), DiscoloredEntity.createAttributes().build());
		event.put(RNEntities.CORRODED_ENTITY.get(), CorrodedEntity.createAttributes().build());
		event.put(RNEntities.TARNISHED_ENTITY.get(), TarnishedEntity.createAttributes().build());
		event.put(RNEntities.CRYSTALLIZED_ENTITY.get(), CrystallizedEntity.createAttributes().build());
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			BronzeEntity.init();
			DiscoloredEntity.init();
			CorrodedEntity.init();
			TarnishedEntity.init();
			CrystallizedEntity.init();
		});
	}

	// RENDERERS

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(RNEntities.BRONZE_ENTITY.get(), BronzeRenderer::new);
		event.registerEntityRenderer(RNEntities.DISCOLORED_ENTITY.get(), BronzeRenderer::new);
		event.registerEntityRenderer(RNEntities.CORRODED_ENTITY.get(), BronzeRenderer::new);
		event.registerEntityRenderer(RNEntities.TARNISHED_ENTITY.get(), BronzeRenderer::new);
		event.registerEntityRenderer(RNEntities.CRYSTALLIZED_ENTITY.get(), BronzeRenderer::new);
	}

	// LAYERS

	@SubscribeEvent
	public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(RNModelLayers.BRONZE, BronzeModel::createBodyLayer);
	}
}
