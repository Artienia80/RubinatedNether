package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.render.BronzeLaserRenderer;
import corundum.rubinated_nether.client.render.CofferRenderer;
import corundum.rubinated_nether.client.render.CopperLaserRenderer;
import corundum.rubinated_nether.content.blocks.ChandelierBlock;
import corundum.rubinated_nether.content.blocks.entities.BrazierBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.BronzeLaserBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.CopperLaserBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.GearboxBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.RubinationAltarBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.RunestoneBlockEntity;
import corundum.rubinated_nether.content.entity.client.BronzeModel;
import corundum.rubinated_nether.content.entity.client.BronzeRenderer;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMaterialAtlasesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			RubinatedNether.MODID
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FreezerBlockEntity>> FREEZER = BLOCK_ENTITY_TYPES.register(
			"freezer",
			() -> BlockEntityType.Builder.of(
					FreezerBlockEntity::new,
					RNBlocks.FREEZER.get()
			).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "freezer")
            )
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrazierBlockEntity>> BRAZIER = BLOCK_ENTITY_TYPES.register(
			"brazier",
			() -> BlockEntityType.Builder.of(
					BrazierBlockEntity::new,
					RNBlocks.BRAZIER.get()
			).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "brazier")
            )
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BronzeLaserBlockEntity>> BRONZE_LASER =
			BLOCK_ENTITY_TYPES.register(
					"bronze_laser",
					() -> BlockEntityType.Builder.of(
							BronzeLaserBlockEntity::new,
							RNBlocks.BRONZE_LASER.get(),
							RNBlocks.DISCOLORED_BRONZE_LASER.get(),
							RNBlocks.CORRODED_BRONZE_LASER.get(),
							RNBlocks.TARNISHED_BRONZE_LASER.get(),
							RNBlocks.CRYSTALLIZED_BRONZE_LASER.get()
					).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "bronze_laser")
            )
			);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperLaserBlockEntity>> COPPER_LASER =
			BLOCK_ENTITY_TYPES.register(
					"copper_laser",
					() -> BlockEntityType.Builder.of(
							CopperLaserBlockEntity::new,
							RNBlocks.COPPER_LASER.get(),
							RNBlocks.EXPOSED_COPPER_LASER.get(),
							RNBlocks.WEATHERED_COPPER_LASER.get(),
							RNBlocks.OXIDIZED_COPPER_LASER.get(),
							RNBlocks.WAXED_COPPER_LASER.get(),
							RNBlocks.WAXED_EXPOSED_COPPER_LASER.get(),
							RNBlocks.WAXED_WEATHERED_COPPER_LASER.get(),
							RNBlocks.WAXED_OXIDIZED_COPPER_LASER.get()
					).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "copper_laser")
            )
			);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunestoneBlockEntity>> RUNESTONE = BLOCK_ENTITY_TYPES.register(
			"runestone",
			() -> BlockEntityType.Builder.of(
					RunestoneBlockEntity::new,
					RNBlocks.RUNESTONE.get()
			).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "runestone")
            )
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RubinationAltarBlockEntity>> RUBINATION_ALTAR = BLOCK_ENTITY_TYPES.register(
			"rubination_altar",
			() -> BlockEntityType.Builder.of(
					RubinationAltarBlockEntity::new,
					RNBlocks.RUBINATION_ALTAR.get()
			).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "rubination_altar")
            )
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CofferBlockEntity>> COFFER = BLOCK_ENTITY_TYPES.register(
			"coffer",
			() -> BlockEntityType.Builder.of(
					CofferBlockEntity::new,
					RNBlocks.SHRINE_STONE_COFFER.get()
			).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "coffer")
            )
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChandelierBlock.ChandelierBlockEntity>> CHANDELIER = BLOCK_ENTITY_TYPES.register(
			"chandelier",
			() -> BlockEntityType.Builder.of(
					ChandelierBlock.ChandelierBlockEntity::new,
					RNBlocks.BRONZE_CHANDELIER.get(),
					RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get(),
					RNBlocks.CORRODED_BRONZE_CHANDELIER.get(),
					RNBlocks.TARNISHED_BRONZE_CHANDELIER.get(),
					RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get()
			).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "chandelier")
            )
	);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GearboxBlockEntity>> GEARBOX = BLOCK_ENTITY_TYPES.register(
            "gearbox",
            () -> BlockEntityType.Builder.of(
                    GearboxBlockEntity::new,
                    RNBlocks.GEARBOX.get()
            ).build(
                    Util.fetchChoiceType(References.BLOCK_ENTITY, "gearbox")
            )
    );
}