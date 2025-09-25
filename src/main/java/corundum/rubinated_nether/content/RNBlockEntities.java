package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.entities.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
		).build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrazierBlockEntity>> BRAZIER = BLOCK_ENTITY_TYPES.register(
		"brazier", 
		() -> BlockEntityType.Builder.of(
			BrazierBlockEntity::new, 
			RNBlocks.BRAZIER.get()
		).build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BronzeLaserBlockEntity>> BRONZE_LASER = BLOCK_ENTITY_TYPES.register(
		"bronze_laser",
		() -> BlockEntityType.Builder.of(
			BronzeLaserBlockEntity::new, 
			RNBlocks.BRONZE_LASER.get()
		).build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunestoneBlockEntity>> RUNESTONE = BLOCK_ENTITY_TYPES.register(
			"runestone",
			() -> BlockEntityType.Builder.of(
					RunestoneBlockEntity::new,
					RNBlocks.RUNESTONE.get()
			).build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RubinationAltarBlockEntity>> RUBINATION_ALTAR = BLOCK_ENTITY_TYPES.register(
			"rubination_altar",
			() -> BlockEntityType.Builder.of(
					RubinationAltarBlockEntity::new,
					RNBlocks.RUBINATION_ALTAR.get()
			).build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CofferBlockEntity>> COFFER = BLOCK_ENTITY_TYPES.register(
			"coffer",
			() -> BlockEntityType.Builder.of(
					CofferBlockEntity::new,
					RNBlocks.SHRINE_STONE_COFFER.get()
			).build(null)
	);
}
