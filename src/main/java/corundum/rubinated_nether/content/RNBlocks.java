package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.*;
import corundum.rubinated_nether.content.items.WaxableBlockItem;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RubinatedNether.MODID);

	public static final DeferredBlock<Block> NETHER_RUBY_ORE = registerBlockAndItem(
		"nether_ruby_ore", 
		() -> new DropExperienceBlock(
			UniformInt.of(3, 6),
			Block.Properties.ofFullCopy(Blocks.NETHERRACK)
				.strength(2.F)
				.requiresCorrectToolForDrops()
		)
	);

	public static final DeferredBlock<Block> RUBINATED_BLACKSTONE = registerBlockAndItem(
		"rubinated_blackstone", 
		() -> new DropExperienceBlock(
			UniformInt.of(0, 1), // TODO: Figure out actual values
			Block.Properties.ofFullCopy(Blocks.GILDED_BLACKSTONE)
		)
	);

	public static final DeferredBlock<Block> MOLTEN_RUBY_ORE = registerBlockAndItem(
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

	public static final DeferredBlock<Block> RUBY_LANTERN = registerBlockAndItem(
		"ruby_lantern",
		() -> new LanternBlock(Block.Properties.ofFullCopy(Blocks.LANTERN))
	);

	public static final DeferredBlock<Block> CHANDELIER = registerBlockAndItem(
		"ruby_chandelier",
		() -> new ChandelierBlock(
			Block.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
				.noOcclusion()
				.lightLevel($ -> 15)
		)
	);

	public static final DeferredBlock<RotatedPillarBlock> LAVA_LAMP = registerBlockAndItem(
		"lava_lamp",
		() -> new LavaLampBlock(
			Block.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
				.lightLevel($ -> 15)
		)
	);

	public static final DeferredBlock<Block> DRY_ICE = registerBlockAndItem(
		"dry_ice",
		() -> new DryIceBlock(
			Block.Properties
				.ofFullCopy(Blocks.BLUE_ICE)
				.requiresCorrectToolForDrops()
				.friction(0.995f)
		)
	);

	public static final DeferredBlock<Block> SOAKSTONE = registerBlockAndItem(
		"soakstone",
		() -> new LavaSpongeBlock(Block.Properties.ofFullCopy(Blocks.NETHERRACK))
	);

	public static final DeferredBlock<Block> RUBY_GLASS = registerBlockAndItem(
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
	public static final DeferredBlock<IronBarsBlock> RUBY_GLASS_PANE = registerBlockAndItem(
		"ruby_glass_pane", 
		() -> new StainedGlassPaneBlock(
			DyeColor.RED, 
			Block.Properties.ofFullCopy(RUBY_GLASS.get())
		)
	);
	public static final DeferredBlock<Block> ORNATE_RUBY_GLASS = registerBlockAndItem(
		"ornate_ruby_glass",
		() -> new StainedGlassBlock(
			DyeColor.RED,
			Block.Properties.ofFullCopy(RUBY_GLASS.get())
		)
	);
	public static final DeferredBlock<IronBarsBlock> ORNATE_RUBY_GLASS_PANE = registerBlockAndItem(
		"ornate_ruby_glass_pane", 
		() -> new StainedGlassPaneBlock(
			DyeColor.RED, 
			Block.Properties.ofFullCopy(RUBY_GLASS.get())
		)
	);
	public static final DeferredBlock<Block> MOLTEN_RUBY_GLASS = registerBlockAndItem(
		"molten_ruby_glass",
		() -> new StainedGlassBlock(
			DyeColor.RED,
			Block.Properties
				.ofFullCopy(RUBY_GLASS.get())
				.lightLevel($ -> 10)
		)
	);
	public static final DeferredBlock<IronBarsBlock> MOLTEN_RUBY_GLASS_PANE = registerBlockAndItem(
		"molten_ruby_glass_pane", 
		() -> new StainedGlassPaneBlock(
			DyeColor.RED, 
			Block.Properties.ofFullCopy(MOLTEN_RUBY_GLASS.get())
		)
	);

	public static final DeferredBlock<Block> RUNESTONE = registerBlockAndItem(
		"runestone",
		() -> new RunestoneBlock(
			Block.Properties
			.ofFullCopy(Blocks.BASALT)
			.lightLevel($ -> 2)
			.noOcclusion()
		)
	);

	public static final DeferredBlock<Block> SHRINE_STONE = registerBlockAndItem(
		"shrine_stone",
		() -> new Block(Block.Properties.ofFullCopy(Blocks.DEEPSLATE))
	);

	public static final DeferredBlock<Block> POLISHED_SHRINE_STONE = registerBlockAndItem(
			"polished_shrine_stone",
			() -> new Block(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<SlabBlock> POLISHED_SHRINE_STONE_SLAB = registerBlockAndItem(
			"polished_shrine_stone_slab",
			() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.POLISHED_SHRINE_STONE.get())
			)
	);
	public static final DeferredBlock<StairBlock> POLISHED_SHRINE_STONE_STAIRS = registerBlockAndItem(
			"polished_shrine_stone_stairs",
			() -> new StairBlock(
					POLISHED_SHRINE_STONE.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.POLISHED_SHRINE_STONE.get())
			)
	);
	public static final DeferredBlock<WallBlock> POLISHED_SHRINE_STONE_WALL = registerBlockAndItem(
			"polished_shrine_stone_wall",
			() -> new WallBlock(
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.POLISHED_SHRINE_STONE.get())
			)
	);

	public static final DeferredBlock<Block> SHRINE_STONE_TILES = registerBlockAndItem(
		"shrine_stone_tiles",
		() -> new Block(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<SlabBlock> SHRINE_STONE_TILES_SLAB = registerBlockAndItem(
		"shrine_stone_tiles_slab",
		() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_TILES.get()))
	);

	public static final DeferredBlock<StairBlock> SHRINE_STONE_TILES_STAIRS = registerBlockAndItem(
		"shrine_stone_tiles_stairs",
		() -> new StairBlock(
			SHRINE_STONE_TILES.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_TILES.get())
		)
	);

	public static final DeferredBlock<WallBlock> SHRINE_STONE_TILES_WALL = registerBlockAndItem(
		"shrine_stone_tiles_wall",
		() -> new WallBlock(
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_TILES.get())
		)
	);

	public static final DeferredBlock<RotatedPillarBlock> SHRINE_STONE_PILLAR = registerBlockAndItem(
		"shrine_stone_pillar",
		() -> new RotatedPillarBlock(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);
	public static final DeferredBlock<Block> SHRINE_STONE_BRICKS = registerBlockAndItem(
		"shrine_stone_bricks",
		() -> new Block(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<SlabBlock> SHRINE_STONE_BRICKS_SLAB = registerBlockAndItem(
		"shrine_stone_bricks_slab",
		() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_BRICKS.get()))
	);
	public static final DeferredBlock<StairBlock> SHRINE_STONE_BRICKS_STAIRS = registerBlockAndItem(
		"shrine_stone_bricks_stairs",
		() -> new StairBlock(
			SHRINE_STONE_BRICKS.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_BRICKS.get())
		)
	);
	public static final DeferredBlock<WallBlock> SHRINE_STONE_BRICKS_WALL = registerBlockAndItem(
		"shrine_stone_bricks_wall",
		() -> new WallBlock(
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_BRICKS.get())
		)
	);

	public static final DeferredBlock<Block> CHISELED_SHRINE_STONE_BRICKS = registerBlockAndItem(
		"chiseled_shrine_stone_bricks",
		() -> new Block(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<SixWayPillarBlock> RUBINATED_CHISELED_SHRINE_STONE_BRICKS = registerBlockAndItem(
		"rubinated_chiseled_shrine_stone_bricks",
		() -> new SixWayPillarBlock(
			Block.Properties.ofFullCopy(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get())
				.lightLevel($ -> 7)
		)
	);
	public static final DeferredBlock<Block> RUBINATED_SHRINE_STONE_BRICKS = registerBlockAndItem(
		"rubinated_shrine_stone_bricks",
		() -> new Block(
			Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_BRICKS.get())
				.lightLevel($ -> 7)
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

	public static final DeferredBlock<Block> RUBY_LASER = registerBlockAndItem(
		"ruby_laser", 
		() -> new RubyLaserBlock(
			BlockBehaviour.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
				.noOcclusion()
		)
	);

	public static final DeferredBlock<Block> BRAZIER = registerBlockAndItem(
		"ruby_brazier", 
		() -> new BrazierBlock(
			BlockBehaviour.Properties
				.ofFullCopy(Blocks.COPPER_BLOCK)
				.mapColor(MapColor.FIRE)
				.noOcclusion()
		)
	);

	public static final DeferredBlock<Block> RUBINATION_ALTAR = registerBlockAndItem(
			"rubination_altar",
			() -> new RubinationAltarBlock(
					BlockBehaviour.Properties
							.ofFullCopy(Blocks.OBSIDIAN)
							.mapColor(MapColor.NETHER)
			)
	);



	// BRONZE BLOCKS

	public static final DeferredBlock<Block> BRONZE_BLOCK = registerWaxableBlock(
		"bronze_block",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.UNAFFECTED,
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.GOLD)
				.requiresCorrectToolForDrops()
				.strength(5.0F, 150.0F)
				.sound(SoundType.COPPER)
				.randomTicks()
		)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_BLOCK = registerWaxableBlock(
		"discolored_bronze_block",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.DISCOLORED,
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.TERRACOTTA_PINK)
				.requiresCorrectToolForDrops()
				.strength(15.0F, 300.0F)
				.sound(SoundType.COPPER)
				.randomTicks()
		)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_BLOCK = registerWaxableBlock(
		"corroded_bronze_block",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.CORRODED,
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.CRIMSON_STEM)
				.requiresCorrectToolForDrops()
				.strength(25.0F, 600.0F)
				.sound(SoundType.COPPER)
				.randomTicks()
		)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_BLOCK = registerWaxableBlock(
		"tarnished_bronze_block",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.TARNISHED,
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.TERRACOTTA_BLACK)
				.requiresCorrectToolForDrops()
				.strength(50.0F, 1200.0F)
				.sound(SoundType.COPPER)
				.randomTicks()
		)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_BLOCK = registerWaxableBlock(
		"crystallized_bronze_block",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.CRYSTALLIZED,
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.TERRACOTTA_WHITE)
				.requiresCorrectToolForDrops()
				.strength(1.0F, 6.0F)
				.sound(SoundType.COPPER)
				.randomTicks()
		)
	);

	// CUT BRONZE PILLAR BLOCKS

	public static final DeferredBlock<Block> CUT_BRONZE_PILLAR = registerWaxableBlock(
		"cut_bronze_pillar",
		() -> new TarnishingPillarBlock(
			TarnishingBronze.TarnishState.UNAFFECTED,
			BlockBehaviour.Properties.ofFullCopy(BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(5.0F, 150.0F)
		)
	);

	public static final DeferredBlock<Block> DISCOLORED_CUT_BRONZE_PILLAR = registerWaxableBlock(
		"discolored_cut_bronze_pillar",
		() -> new TarnishingPillarBlock(
			TarnishingBronze.TarnishState.DISCOLORED,
			BlockBehaviour.Properties.ofFullCopy(DISCOLORED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(15.0F, 300.0F)
		)
	);

	public static final DeferredBlock<Block> CORRODED_CUT_BRONZE_PILLAR = registerWaxableBlock(
		"corroded_cut_bronze_pillar",
		() -> new TarnishingPillarBlock(
			TarnishingBronze.TarnishState.CORRODED,
			BlockBehaviour.Properties.ofFullCopy(CORRODED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(25.0F, 600.0F)
		)
	);

	public static final DeferredBlock<Block> TARNISHED_CUT_BRONZE_PILLAR = registerWaxableBlock(
		"tarnished_cut_bronze_pillar",
		() -> new TarnishingPillarBlock(
			TarnishingBronze.TarnishState.TARNISHED,
			BlockBehaviour.Properties.ofFullCopy(TARNISHED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(50.0F, 1200.0F)
		)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_CUT_BRONZE_PILLAR = registerWaxableBlock(
		"crystallized_cut_bronze_pillar",
		() -> new TarnishingPillarBlock(
			TarnishingBronze.TarnishState.CRYSTALLIZED,
			BlockBehaviour.Properties.ofFullCopy(CRYSTALLIZED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(1.0F, 6.0F)
		)
	);

	// CUT BRONZE BRICKS BLOCKS

	public static final DeferredBlock<Block> CUT_BRONZE_BRICKS = registerWaxableBlock(
		"cut_bronze_bricks",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.UNAFFECTED,
			BlockBehaviour.Properties.ofFullCopy(BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(5.0F, 150.0F)
		)
	);

	public static final DeferredBlock<Block> DISCOLORED_CUT_BRONZE_BRICKS = registerWaxableBlock(
		"discolored_cut_bronze_bricks",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.DISCOLORED,
			BlockBehaviour.Properties.ofFullCopy(DISCOLORED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(15.0F, 300.0F)
		)
	);

	public static final DeferredBlock<Block> CORRODED_CUT_BRONZE_BRICKS = registerWaxableBlock(
		"corroded_cut_bronze_bricks",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.CORRODED,
			BlockBehaviour.Properties.ofFullCopy(CORRODED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(25.0F, 600.0F)
		)
	);

	public static final DeferredBlock<Block> TARNISHED_CUT_BRONZE_BRICKS = registerWaxableBlock(
		"tarnished_cut_bronze_bricks",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.TARNISHED,
			BlockBehaviour.Properties.ofFullCopy(TARNISHED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(50.0F, 1200.0F)
		)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_CUT_BRONZE_BRICKS = registerWaxableBlock(
		"crystallized_cut_bronze_bricks",
		() -> new TarnishingBronzeBlock(
			TarnishingBronze.TarnishState.CRYSTALLIZED,
			BlockBehaviour.Properties.ofFullCopy(CRYSTALLIZED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(1.0F, 6.0F)
		)
	);

	// CUT BRONZE BRICKS STAIRS AND SLABS

	public static final DeferredBlock<SlabBlock> CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
		"cut_bronze_bricks_slab",
		() -> new TarnishingBronzeSlabBlock(
			TarnishingBronze.TarnishState.UNAFFECTED,
			SlabBlock.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(5.0F, 150.0F)
		)

	);
	public static final DeferredBlock<StairBlock> CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
		"cut_bronze_bricks_stairs",
		() -> new TarnishingBronzeStairBlock(
			TarnishingBronze.TarnishState.UNAFFECTED,
				BRONZE_BLOCK.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(5.0F, 150.0F)
		)
	);

	public static final DeferredBlock<SlabBlock> DISCOLORED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
		"discolored_cut_bronze_bricks_slab",
		() -> new TarnishingBronzeSlabBlock(
			TarnishingBronze.TarnishState.DISCOLORED,
			SlabBlock.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(15.0F, 300.0F)
		)
	);
	public static final DeferredBlock<StairBlock> DISCOLORED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
		"discolored_cut_bronze_bricks_stairs",
		() -> new TarnishingBronzeStairBlock(
			TarnishingBronze.TarnishState.DISCOLORED,
				DISCOLORED_BRONZE_BLOCK.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(15.0F, 300.0F)
		)
	);

	public static final DeferredBlock<SlabBlock> CORRODED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
		"corroded_cut_bronze_bricks_slab",
		() -> new TarnishingBronzeSlabBlock(
			TarnishingBronze.TarnishState.CORRODED,
			SlabBlock.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
				.requiresCorrectToolForDrops()
				.strength(25.0F, 600.0F)
		)
	);
	public static final DeferredBlock<StairBlock> CORRODED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
		"corroded_cut_bronze_bricks_stairs",
		() -> new TarnishingBronzeStairBlock(
			TarnishingBronze.TarnishState.CORRODED,
				CORRODED_BRONZE_BLOCK.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(25.0F, 600.0F)
		)
	);

	public static final DeferredBlock<SlabBlock> TARNISHED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
		"tarnished_cut_bronze_bricks_slab",
		() -> new TarnishingBronzeSlabBlock(
			TarnishingBronze.TarnishState.TARNISHED,
			SlabBlock.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(50.0F, 1200.0F)
		)
	);
	public static final DeferredBlock<StairBlock> TARNISHED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
		"tarnished_cut_bronze_bricks_stairs",
		() -> new TarnishingBronzeStairBlock(
			TarnishingBronze.TarnishState.TARNISHED,
				TARNISHED_BRONZE_BLOCK.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(50.0F, 1200.0F)
		)
	);

	public static final DeferredBlock<SlabBlock> CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
		"crystallized_cut_bronze_bricks_slab",
		() -> new TarnishingBronzeSlabBlock(
			TarnishingBronze.TarnishState.CRYSTALLIZED,
			SlabBlock.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(1.0F, 6.0F)
		)
	);
	public static final DeferredBlock<StairBlock> CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
		"crystallized_cut_bronze_bricks_stairs",
		() -> new TarnishingBronzeStairBlock(
			TarnishingBronze.TarnishState.CRYSTALLIZED,
				CRYSTALLIZED_BRONZE_BLOCK.get().defaultBlockState(),
			BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
					.requiresCorrectToolForDrops()
					.strength(1.0F, 6.0F)
		)
	);

	// BRONZE BULB BLOCKS

	public static final DeferredBlock<Block> BRONZE_BULB = registerWaxableBlock(
			"bronze_bulb",
			() -> new TarnishingBronzeBulbBlock(
					TarnishingBronze.TarnishState.UNAFFECTED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(5.0F, 150.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
							.isRedstoneConductor(RNBlocks::never)
							.lightLevel(litBlockEmission(15))
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_BULB = registerWaxableBlock(
			"discolored_bronze_bulb",
			() -> new TarnishingBronzeBulbBlock(
					TarnishingBronze.TarnishState.DISCOLORED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_PINK)
							.requiresCorrectToolForDrops()
							.strength(15.0F, 300.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
							.isRedstoneConductor(RNBlocks::never)
							.lightLevel(litBlockEmission(12))
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_BULB = registerWaxableBlock(
			"corroded_bronze_bulb",
			() -> new TarnishingBronzeBulbBlock(
					TarnishingBronze.TarnishState.CORRODED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.CRIMSON_STEM)
							.requiresCorrectToolForDrops()
							.strength(25.0F, 600.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
							.isRedstoneConductor(RNBlocks::never)
							.lightLevel(litBlockEmission(8))
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_BULB = registerWaxableBlock(
			"tarnished_bronze_bulb",
			() -> new TarnishingBronzeBulbBlock(
					TarnishingBronze.TarnishState.TARNISHED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_BLACK)
							.requiresCorrectToolForDrops()
							.strength(50.0F, 1200.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
							.isRedstoneConductor(RNBlocks::never)
							.lightLevel(litBlockEmission(4))
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_BULB = registerWaxableBlock(
			"crystallized_bronze_bulb",
			() -> new TarnishingBronzeBulbBlock(
					TarnishingBronze.TarnishState.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
							.isRedstoneConductor(RNBlocks::never)
							.lightLevel(litBlockEmission(15))
			)
	);

	// CHISELED BRONZE

	public static final DeferredBlock<Block> CHISELED_BRONZE = registerWaxableBlock(
			"chiseled_bronze",
			() -> new TarnishingBronzeBlock(
					TarnishingBronze.TarnishState.UNAFFECTED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(5.0F, 150.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_CHISELED_BRONZE = registerWaxableBlock(
			"discolored_chiseled_bronze",
			() -> new TarnishingBronzeBlock(
					TarnishingBronze.TarnishState.DISCOLORED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_PINK)
							.requiresCorrectToolForDrops()
							.strength(15.0F, 300.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> CORRODED_CHISELED_BRONZE = registerWaxableBlock(
			"corroded_chiseled_bronze",
			() -> new TarnishingBronzeBlock(
					TarnishingBronze.TarnishState.CORRODED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.CRIMSON_STEM)
							.requiresCorrectToolForDrops()
							.strength(25.0F, 600.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> TARNISHED_CHISELED_BRONZE = registerWaxableBlock(
			"tarnished_chiseled_bronze",
			() -> new TarnishingBronzeBlock(
					TarnishingBronze.TarnishState.TARNISHED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_BLACK)
							.requiresCorrectToolForDrops()
							.strength(50.0F, 1200.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_CHISELED_BRONZE = registerWaxableBlock(
			"crystallized_chiseled_bronze",
			() -> new TarnishingBronzeBlock(
					TarnishingBronze.TarnishState.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	// BRONZE GRATE

	public static final DeferredBlock<Block> BRONZE_GRATE = registerWaxableBlock(
			"bronze_grate",
			() -> new BronzeGrateBlock(
					TarnishingBronze.TarnishState.UNAFFECTED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(4.0F, 110.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
							.isViewBlocking(RNBlocks::never)
							.isSuffocating(RNBlocks::never)
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_GRATE = registerWaxableBlock(
			"discolored_bronze_grate",
			() -> new BronzeGrateBlock(
					TarnishingBronze.TarnishState.DISCOLORED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(8.0F, 220.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
							.isViewBlocking(RNBlocks::never)
							.isSuffocating(RNBlocks::never)
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_GRATE = registerWaxableBlock(
			"corroded_bronze_grate",
			() -> new BronzeGrateBlock(
					TarnishingBronze.TarnishState.CORRODED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(16.0F, 440.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
							.isViewBlocking(RNBlocks::never)
							.isSuffocating(RNBlocks::never)
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_GRATE = registerWaxableBlock(
			"tarnished_bronze_grate",
			() -> new BronzeGrateBlock(
					TarnishingBronze.TarnishState.TARNISHED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(32.0F, 880.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
							.isViewBlocking(RNBlocks::never)
							.isSuffocating(RNBlocks::never)

			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_GRATE = registerWaxableBlock(
			"crystallized_bronze_grate",
			() -> new BronzeGrateBlock(
					TarnishingBronze.TarnishState.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
							.isViewBlocking(RNBlocks::never)
							.isSuffocating(RNBlocks::never)

			)
	);


	public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.registerSimpleBlockItem(
			name, 
			register
		);

		return register;
	}

	public static <T extends Block> DeferredBlock<T> registerWaxableBlock(String name, Supplier<T> block) {
		var register = registerBlockAndItem(name, block);

		RNItems.ITEMS.register(
			"waxed_" + name,
			() -> new WaxableBlockItem(
				register,
				new Item.Properties()
			)
		);

		return register;
	}

	private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
		return false;
	}

	private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
		return blockState -> blockState.getValue(BlockStateProperties.LIT) ? lightValue : 0;
	}
}
