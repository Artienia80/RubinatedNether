package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.*;

import java.util.function.Supplier;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RubinatedNether.MODID);

	public static final DeferredBlock<DropExperienceBlock> NETHER_RUBY_ORE = registerBlockAndItem(
		"nether_ruby_ore", 
		() -> new DropExperienceBlock(
			UniformInt.of(3, 6),
			Block.Properties.ofFullCopy(Blocks.NETHERRACK)
				.strength(2.F)
				.requiresCorrectToolForDrops()
		)
	);
	public static final DeferredBlock<DropExperienceBlock> RUBINATED_BLACKSTONE = registerBlockAndItem(
		"rubinated_blackstone", 
		() -> new DropExperienceBlock(
			UniformInt.of(0, 1), // TODO: Figure out actual values
			Block.Properties.ofFullCopy(Blocks.GILDED_BLACKSTONE)
		)
	);
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

	public static final DeferredBlock<Block> RUBY_BLOCK = registerBlockAndItem(
		"ruby_block", 
		() -> new Block(
			Block.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK)
				.mapColor(MapColor.FIRE)
		)
	);

	public static final DeferredBlock<RotatedPillarBlock> MOLTEN_RUBY_BLOCK = registerBlockAndItem(
			"molten_ruby_block",
			() -> new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK))
	);
	public static final DeferredBlock<Block> BLEEDING_OBSIDIAN = registerBlockAndItem(
		"bleeding_obsidian", 
		() -> new Block(Block.Properties.ofFullCopy(Blocks.CRYING_OBSIDIAN))
	);

	public static final DeferredBlock<LanternBlock> RUBY_LANTERN = registerBlockAndItem(
		"ruby_lantern",
		() -> new LanternBlock(Block.Properties.ofFullCopy(Blocks.LANTERN))
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

	public static final DeferredBlock<StainedGlassBlock> RUBY_GLASS = registerBlockAndItem(
		"ruby_glass",
		() -> new StainedGlassBlock(
			DyeColor.RED,
			Block.Properties
				.ofFullCopy(Blocks.RED_STAINED_GLASS)
				.explosionResistance(1000)
				.strength(5.0F, 6.0F)
				.isViewBlocking(RNBlocks::never)
		)
	);
	public static final DeferredBlock<StainedGlassPaneBlock> RUBY_GLASS_PANE = registerBlockAndItem(
		"ruby_glass_pane", 
		() -> new StainedGlassPaneBlock(
			DyeColor.RED, 
			Block.Properties.ofFullCopy(RUBY_GLASS.get())
		)
	);
	public static final DeferredBlock<StainedGlassBlock> ORNATE_RUBY_GLASS = registerBlockAndItem(
		"ornate_ruby_glass",
		() -> new StainedGlassBlock(
			DyeColor.RED,
			Block.Properties.ofFullCopy(RUBY_GLASS.get())
		)
	);
	public static final DeferredBlock<StainedGlassPaneBlock> ORNATE_RUBY_GLASS_PANE = registerBlockAndItem(
		"ornate_ruby_glass_pane", 
		() -> new StainedGlassPaneBlock(
			DyeColor.RED, 
			Block.Properties.ofFullCopy(RUBY_GLASS.get())
		)
	);
	public static final DeferredBlock<StainedGlassBlock> MOLTEN_RUBY_GLASS = registerBlockAndItem(
		"molten_ruby_glass",
		() -> new StainedGlassBlock(
			DyeColor.RED,
			Block.Properties
				.ofFullCopy(RUBY_GLASS.get())
				.lightLevel($ -> 10)
		)
	);
	public static final DeferredBlock<StainedGlassPaneBlock> MOLTEN_RUBY_GLASS_PANE = registerBlockAndItem(
		"molten_ruby_glass_pane", 
		() -> new StainedGlassPaneBlock(
			DyeColor.RED, 
			Block.Properties.ofFullCopy(MOLTEN_RUBY_GLASS.get())
		)
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

	public static final DeferredBlock<Block> POLISHED_ALTAR_STONE = registerBlockAndItem(
			"polished_altar_stone",
			() -> new Block(Block.Properties.ofFullCopy(RNBlocks.ALTAR_STONE.get()))
	);

	public static final DeferredBlock<Block> ALTAR_STONE_TILES = registerBlockAndItem(
		"altar_stone_tiles",
		() -> new Block(Block.Properties.ofFullCopy(RNBlocks.ALTAR_STONE.get()))
	);
	public static final DeferredBlock<SlabBlock> ALTAR_STONE_TILES_SLAB = registerBlockAndItem(
			"altar_stone_tiles_slab",
			() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.ALTAR_STONE_TILES.get())
			)
	);
	public static final DeferredBlock<StairBlock> ALTAR_STONE_TILES_STAIRS = registerBlockAndItem(
			"altar_stone_tiles_stairs",
			() -> new StairBlock(
					ALTAR_STONE_TILES.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.ALTAR_STONE_TILES.get())
			)
	);
	public static final DeferredBlock<WallBlock> ALTAR_STONE_TILES_WALL = registerBlockAndItem(
			"altar_stone_tiles_wall",
			() -> new WallBlock(
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.ALTAR_STONE_TILES.get())
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> ALTAR_STONE_PILLAR = registerBlockAndItem(
		"altar_stone_pillar",
		() -> new RotatedPillarBlock(Block.Properties.ofFullCopy(RNBlocks.ALTAR_STONE.get()))
	);
	public static final DeferredBlock<Block> ALTAR_STONE_BRICKS = registerBlockAndItem(
		"altar_stone_bricks",
		() -> new Block(Block.Properties.ofFullCopy(RNBlocks.ALTAR_STONE.get()))
	);

	public static final DeferredBlock<SlabBlock> ALTAR_STONE_BRICKS_SLAB = registerBlockAndItem(
			"altar_stone_bricks_slab",
			() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.ALTAR_STONE_BRICKS.get())
			)
	);
	public static final DeferredBlock<StairBlock> ALTAR_STONE_BRICKS_STAIRS = registerBlockAndItem(
			"altar_stone_bricks_stairs",
			() -> new StairBlock(
					ALTAR_STONE_BRICKS.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.ALTAR_STONE_BRICKS.get())
			)
	);
	public static final DeferredBlock<WallBlock> ALTAR_STONE_BRICKS_WALL = registerBlockAndItem(
			"altar_stone_bricks_wall",
			() -> new WallBlock(
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.ALTAR_STONE_BRICKS.get())
			)
	);

	public static final DeferredBlock<Block> CHISELED_ALTAR_STONE_BRICKS = registerBlockAndItem(
		"chiseled_altar_stone_bricks",
		() -> new Block(Block.Properties.ofFullCopy(RNBlocks.ALTAR_STONE.get()))
	);

	public static final DeferredBlock<RotatedPillarBlock> RUBINATED_CHISELED_ALTAR_STONE_BRICKS = registerBlockAndItem(
		"rubinated_chiseled_altar_stone_bricks",
		() -> new RotatedPillarBlock(
			Block.Properties.ofFullCopy(RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get())
				.lightLevel($ -> 15)
		)
	);

	public static final DeferredBlock<Block> FREEZER = registerBlockAndItem(
		"freezer", 
		() -> new FreezerBlock(
			BlockBehaviour.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
				.noOcclusion()
		)
	);

	/// Registers a block and an item 
	public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.registerSimpleBlockItem(
			name, 
			register
		);

		return register;
	}

	private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
		return false;
	}
}
