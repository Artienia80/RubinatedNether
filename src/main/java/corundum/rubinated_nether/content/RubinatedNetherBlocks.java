package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.*;

import java.util.function.Supplier;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
	public static final DeferredBlock<LavaLampBlock> LAVA_LAMP = registerBlockAndItem(
		"lava_lamp",
		() -> new LavaLampBlock(
			Block.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
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
	public static final DeferredBlock<LavaSpongeBlock> SOAKSTONE = registerBlockAndItem(
		"soakstone",
		() -> new LavaSpongeBlock(Block.Properties.ofFullCopy(Blocks.NETHERRACK))
	);

	public static final DeferredBlock<RunestoneBlock> RUNESTONE = registerBlockAndItem(
		"runestone",
		() -> new RunestoneBlock(
			Block.Properties
				.ofFullCopy(Blocks.BASALT)
				.lightLevel($ -> 2)
				.noOcclusion()
		)
	);

	public static final DeferredBlock<Block> ALTAR_STONE = registerBlockAndItem(
		"altar_stone",
		() -> new Block(Block.Properties.ofFullCopy(Blocks.BASALT))
	);
	public static final DeferredBlock<Block> ALTAR_STONE_TILES = registerBlockAndItem(
		"altar_stone_tiles",
		() -> new Block(Block.Properties.ofFullCopy(Blocks.BASALT))
	);
	public static final DeferredBlock<RotatedPillarBlock> ALTAR_STONE_PILLAR = registerBlockAndItem(
		"altar_stone_pillar",
		() -> new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.BASALT))
	);
	public static final DeferredBlock<Block> ALTAR_STONE_BRICKS = registerBlockAndItem(
		"altar_stone_bricks",
		() -> new Block(Block.Properties.ofFullCopy(Blocks.BASALT))
	);
	public static final DeferredBlock<Block> CHISELED_ALTAR_STONE_BRICKS = registerBlockAndItem(
		"chiseled_altar_stone_bricks",
		() -> new Block(Block.Properties.ofFullCopy(Blocks.BASALT))
	);
	public static final DeferredBlock<RotatedPillarBlock> BLEEDING_CHISELED_ALTAR_STONE_BRICKS = registerBlockAndItem(
		"bleeding_chiseled_altar_stone_bricks",
		() -> new RotatedPillarBlock(
			Block.Properties.ofFullCopy(Blocks.BASALT)
				.lightLevel($ -> 15)
		)
	);

	/// Registers a block and an item 
	public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RubinatedNetherItems.ITEMS.registerSimpleBlockItem(
			name, 
			register
		);

		return register;
	}
}
