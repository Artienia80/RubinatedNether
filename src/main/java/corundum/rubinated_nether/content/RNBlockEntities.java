package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.RubyLaserBlockEntity;
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
		() -> BlockEntityType.Builder.of(FreezerBlockEntity::new, RNBlocks.FREEZER.get()).build(null)
	);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RubyLaserBlockEntity>> RUBY_LASER = BLOCK_ENTITY_TYPES.register(
		"ruby_laser",
		() -> BlockEntityType.Builder.of(RubyLaserBlockEntity::new, RNBlocks.RUBY_LASER.get()).build(null)
	);
}
