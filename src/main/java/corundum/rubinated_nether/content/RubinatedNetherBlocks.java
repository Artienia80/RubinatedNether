package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.*;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RubinatedNetherBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RubinatedNether.MODID);

	public static final DeferredBlock<MagmaExperienceBlock> MOLTEN_RUBY_ORE = BLOCKS.register(
		"molten_ruby_ore",
		() -> new MagmaExperienceBlock(
			Block.Properties
				.ofFullCopy(Blocks.MAGMA_BLOCK)
				.strength(2.0F)
				.requiresCorrectToolForDrops(),
			
			UniformInt.of(4, 8)
		)
	);

	public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = RubinatedNetherItems.ITEMS.registerSimpleBlockItem("molten_ruby_ore", MOLTEN_RUBY_ORE);
}
