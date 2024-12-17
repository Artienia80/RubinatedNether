package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.*;

import java.util.function.Supplier;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RubinatedNetherBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RubinatedNether.MODID);

	public static final DeferredBlock<MagmaExperienceBlock> MOLTEN_RUBY_ORE = registerBlockAndItem(
		"molten_ruby_ore",
		() -> new MagmaExperienceBlock(
			Block.Properties
				.ofFullCopy(Blocks.MAGMA_BLOCK)
				.strength(2.0F)
				.requiresCorrectToolForDrops(),
			
			UniformInt.of(4, 8)
		)
	);

	public static final DeferredBlock<ChandelierBlock> CHANDELIER = registerBlockAndItem(
		"ruby_chandelier",
		() -> new ChandelierBlock(
			Block.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
				.noOcclusion()
				.lightLevel($ -> 15)
		)
	);

	public static final DeferredBlock<DryIceBlock> DRY_ICE = registerBlockAndItem(
		"dry_ice",
		() -> new DryIceBlock(
			Block.Properties
				.ofFullCopy(Blocks.BLUE_ICE)
				.requiresCorrectToolForDrops()
				.friction(0.995f)
		)
	);

	public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RubinatedNetherItems.ITEMS.registerSimpleBlockItem(
			name, 
			register
		);

		return register;
	}
}
