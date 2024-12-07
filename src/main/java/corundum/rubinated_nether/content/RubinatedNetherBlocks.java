package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RubinatedNetherBlocks {
	// Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RubinatedNether.MODID);

	// Creates a new Block with the id "examplemod:example_block", combining the namespace and path
	public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

	// Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
	public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = RubinatedNetherItems.ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
}
