package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.*;
import corundum.rubinated_nether.content.items.StrongboxBlockItem;
import corundum.rubinated_nether.content.items.VaseBlockItem;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class RNBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RubinatedNether.MODID);
	public static final List<DeferredBlock<? extends Block>> FLIPPABLE_BLOCKS = new ArrayList<>();

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
					UniformInt.of(0, 1),
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
			() -> new MoltenRubyBlock(
					Block.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK)
							.lightLevel($ -> 10)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> MOLTEN_RUBY_CAULDRON = registerBlockAndItem(
			"molten_ruby_cauldron",
			() -> new MoltenRubyCauldronBlock(
					Block.Properties.ofFullCopy(Blocks.LAVA_CAULDRON)
							.lightLevel($ -> 10)
			)
	);

	public static final DeferredBlock<Block> RUBY_GLASS = registerBlockAndItem(
			"ruby_glass",
			() -> new StainedGlassBlock(
					DyeColor.RED,
					Block.Properties
							.ofFullCopy(Blocks.RED_STAINED_GLASS)
							.strength(5.0F, 1000.0F)
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

	public static final DeferredBlock<Block> BLEEDING_OBSIDIAN = registerBlockAndItem(
			"bleeding_obsidian",
			() -> new BleedingObsidianBlock(Block.Properties.ofFullCopy(Blocks.CRYING_OBSIDIAN))
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

	public static final DeferredBlock<Block> PUMICE = registerBlockAndItem(
			"pumice",
			() -> new PumiceBlock(Block.Properties.ofFullCopy(Blocks.NETHERRACK))
	);

	static {
		BLOCKS.addAlias(RubinatedNether.id("soakstone"), RubinatedNether.id("pumice"));
		RNItems.ITEMS.addAlias(RubinatedNether.id("soakstone"), RubinatedNether.id("pumice"));
	}

	public static final DeferredBlock<CinderBlock> CINDER = registerBlockAndItem(
			"cinder",
			() -> new CinderBlock(Block.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.NETHER))
	);

	public static final ResourceKey<LootTable> SUSPICIOUS_CINDER_LOOT = ResourceKey.create(
			Registries.LOOT_TABLE,
			RubinatedNether.id("archaeology/suspicious_cinder")
	);

	public static final DeferredBlock<SuspiciousCinderBlock> SUSPICIOUS_CINDER = registerBlockAndItem(
			"suspicious_cinder",
			() -> new SuspiciousCinderBlock(
					RNBlocks.CINDER.get(),
					SoundEvents.BRUSH_GRAVEL,
					SoundEvents.BRUSH_GRAVEL_COMPLETED,
					BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_GRAVEL).mapColor(MapColor.NETHER)
			)
	);

	public static final DeferredBlock<SoulStarvedSandBlock> SOUL_STARVED_SAND = registerBlockAndItem(
			"soul_starved_sand",
			() -> new SoulStarvedSandBlock(
					Block.Properties.ofFullCopy(Blocks.SOUL_SAND)
							.speedFactor(1.0F)
							.dynamicShape()
							.isRedstoneConductor((state, level, pos) -> false)
							.isViewBlocking((state, level, pos) -> false)
							.isSuffocating((state, level, pos) -> false)
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> STRIPPED_PETRIFIED_SNAG = registerBlockAndItem(
			"stripped_petrified_snag",
			() -> new RotatedPillarBlock(petrified())
	);

	public static final DeferredBlock<PetrifiedLogBlock> PETRIFIED_SNAG = registerBlockAndItem(
			"petrified_snag",
			() -> new PetrifiedLogBlock(STRIPPED_PETRIFIED_SNAG, petrified())
	);

	public static final DeferredBlock<RotatedPillarBlock> STRIPPED_PETRIFIED_RIND = registerBlockAndItem(
			"stripped_petrified_rind",
			() -> new RotatedPillarBlock(petrified())
	);

	public static final DeferredBlock<PetrifiedLogBlock> PETRIFIED_RIND = registerBlockAndItem(
			"petrified_rind",
			() -> new PetrifiedLogBlock(STRIPPED_PETRIFIED_RIND, petrified())
	);

	public static final DeferredBlock<Block> PETRIFIED_FLAGS = registerBlockAndItem(
			"petrified_flags",
			() -> new Block(petrified())
	);

	public static final DeferredBlock<SlabBlock> PETRIFIED_SLAB = registerBlockAndItem(
			"petrified_slab",
			() -> new SlabBlock(petrified())
	);

	public static final DeferredBlock<StairBlock> PETRIFIED_STAIRS = registerBlockAndItem(
			"petrified_stairs",
			() -> new StairBlock(PETRIFIED_FLAGS.get().defaultBlockState(), petrified())
	);

	public static final DeferredBlock<WallBlock> PETRIFIED_WALL = registerBlockAndItem(
			"petrified_wall",
			() -> new WallBlock(petrified())
	);

	public static final DeferredBlock<FenceBlock> PETRIFIED_FENCE = registerBlockAndItem(
			"petrified_fence",
			() -> new FenceBlock(petrified())
	);

	public static final DeferredBlock<FenceGateBlock> PETRIFIED_FENCE_GATE = registerBlockAndItem(
			"petrified_fence_gate",
			() -> new FenceGateBlock(RNBlocks.PETRIFIED_WOOD_TYPE, petrified())
	);

	public static final DeferredBlock<DoorBlock> PETRIFIED_DOOR = registerDoubleHighBlockAndItem(
			"petrified_door",
			() -> new DoorBlock(BlockSetType.STONE, petrified().noOcclusion().pushReaction(PushReaction.DESTROY))
	);

	public static final DeferredBlock<TrapDoorBlock> PETRIFIED_TRAPDOOR = registerBlockAndItem(
			"petrified_trapdoor",
			() -> new TrapDoorBlock(BlockSetType.STONE, petrified().noOcclusion().isValidSpawn((state, level, pos, type) -> false))
	);

	public static final DeferredBlock<PressurePlateBlock> PETRIFIED_PRESSURE_PLATE = registerBlockAndItem(
			"petrified_pressure_plate",
			() -> new PressurePlateBlock(BlockSetType.STONE, petrified().strength(0.5F).noCollission().pushReaction(PushReaction.DESTROY))
	);

	public static final DeferredBlock<ButtonBlock> PETRIFIED_BUTTON = registerBlockAndItem(
			"petrified_button",
			() -> new ButtonBlock(BlockSetType.STONE, 20, petrified().strength(0.5F).noCollission().pushReaction(PushReaction.DESTROY))
	);

	public static final DeferredBlock<Block> MORION_BLOCK = registerBlockAndItem(
			"morion_block",
			() -> new Block(morion())
	);
	public static final DeferredBlock<Block> CHISELED_MORION_BLOCK = registerBlockAndItem(
			"chiseled_morion_block",
			() -> new Block(morion())
	);
	public static final DeferredBlock<RotatedPillarBlock> MORION_PILLAR = registerBlockAndItem(
			"morion_pillar",
			() -> new RotatedPillarBlock(morion())
	);
	public static final DeferredBlock<Block> MORION_BRICKS = registerBlockAndItem(
			"morion_bricks",
			() -> new Block(morion())
	);
	public static final DeferredBlock<Block> SMOOTH_MORION = registerBlockAndItem(
			"smooth_morion",
			() -> new Block(morion().strength(2.0F, 6.0F))
	);
	public static final DeferredBlock<SlabBlock> MORION_SLAB = registerBlockAndItem(
			"morion_slab",
			() -> new SlabBlock(morion())
	);
	public static final DeferredBlock<StairBlock> MORION_STAIRS = registerBlockAndItem(
			"morion_stairs",
			() -> new StairBlock(MORION_BLOCK.get().defaultBlockState(), morion())
	);
	public static final DeferredBlock<SlabBlock> SMOOTH_MORION_SLAB = registerBlockAndItem(
			"smooth_morion_slab",
			() -> new SlabBlock(morion().strength(2.0F, 6.0F))
	);
	public static final DeferredBlock<StairBlock> SMOOTH_MORION_STAIRS = registerBlockAndItem(
			"smooth_morion_stairs",
			() -> new StairBlock(SMOOTH_MORION.get().defaultBlockState(), morion().strength(2.0F, 6.0F))
	);
	public static final DeferredBlock<Block> QUARTZ_TILES = registerBlockAndItem(
			"quartz_tiles",
			() -> new Block(quartz())
	);
	public static final DeferredBlock<Block> MORION_TILES = registerBlockAndItem(
			"morion_tiles",
			() -> new Block(morion())
	);
	public static final DeferredBlock<FlippableBlock> CHECKERED_TILES = registerFlippableBlockAndItem(
			"checkered_tiles",
			() -> new FlippableBlock(checkered())
	);
	public static final DeferredBlock<Block> QUARTZ_MOSAIC = registerBlockAndItem(
			"quartz_mosaic",
			() -> new Block(quartz())
	);
	public static final DeferredBlock<Block> MORION_MOSAIC = registerBlockAndItem(
			"morion_mosaic",
			() -> new Block(morion())
	);
	public static final DeferredBlock<FlippableBlock> CHECKERED_MOSAIC = registerFlippableBlockAndItem(
			"checkered_mosaic",
			() -> new FlippableBlock(checkered())
	);
	public static final DeferredBlock<SlabBlock> QUARTZ_TILES_SLAB = registerBlockAndItem(
			"quartz_tiles_slab",
			() -> new SlabBlock(quartz())
	);
	public static final DeferredBlock<StairBlock> QUARTZ_TILES_STAIRS = registerBlockAndItem(
			"quartz_tiles_stairs",
			() -> new StairBlock(QUARTZ_TILES.get().defaultBlockState(), quartz())
	);
	public static final DeferredBlock<SlabBlock> MORION_TILES_SLAB = registerBlockAndItem(
			"morion_tiles_slab",
			() -> new SlabBlock(morion())
	);
	public static final DeferredBlock<StairBlock> MORION_TILES_STAIRS = registerBlockAndItem(
			"morion_tiles_stairs",
			() -> new StairBlock(MORION_TILES.get().defaultBlockState(), morion())
	);
	public static final DeferredBlock<FlippableSlabBlock> CHECKERED_TILES_SLAB = registerFlippableBlockAndItem(
			"checkered_tiles_slab",
			() -> new FlippableSlabBlock(checkered())
	);
	public static final DeferredBlock<FlippableStairBlock> CHECKERED_TILES_STAIRS = registerFlippableBlockAndItem(
			"checkered_tiles_stairs",
			() -> new FlippableStairBlock(CHECKERED_TILES.get().defaultBlockState(), checkered())
	);
	public static final DeferredBlock<SlabBlock> QUARTZ_MOSAIC_SLAB = registerBlockAndItem(
			"quartz_mosaic_slab",
			() -> new SlabBlock(quartz())
	);
	public static final DeferredBlock<StairBlock> QUARTZ_MOSAIC_STAIRS = registerBlockAndItem(
			"quartz_mosaic_stairs",
			() -> new StairBlock(QUARTZ_MOSAIC.get().defaultBlockState(), quartz())
	);
	public static final DeferredBlock<SlabBlock> MORION_MOSAIC_SLAB = registerBlockAndItem(
			"morion_mosaic_slab",
			() -> new SlabBlock(morion())
	);
	public static final DeferredBlock<StairBlock> MORION_MOSAIC_STAIRS = registerBlockAndItem(
			"morion_mosaic_stairs",
			() -> new StairBlock(MORION_MOSAIC.get().defaultBlockState(), morion())
	);
	public static final DeferredBlock<FlippableSlabBlock> CHECKERED_MOSAIC_SLAB = registerFlippableBlockAndItem(
			"checkered_mosaic_slab",
			() -> new FlippableSlabBlock(checkered())
	);
	public static final DeferredBlock<FlippableStairBlock> CHECKERED_MOSAIC_STAIRS = registerFlippableBlockAndItem(
			"checkered_mosaic_stairs",
			() -> new FlippableStairBlock(CHECKERED_MOSAIC.get().defaultBlockState(), checkered())
	);

	public static final DeferredBlock<SlabBlock> QUARTZ_BRICKS_SLAB = registerBlockAndItem(
			"quartz_bricks_slab",
			() -> new SlabBlock(quartz())
	);

	public static final DeferredBlock<StairBlock> QUARTZ_BRICKS_STAIRS = registerBlockAndItem(
			"quartz_bricks_stairs",
			() -> new StairBlock(Blocks.QUARTZ_BRICKS.defaultBlockState(), quartz())
	);

	public static final DeferredBlock<SlabBlock> MORION_BRICKS_SLAB = registerBlockAndItem(
			"morion_bricks_slab",
			() -> new SlabBlock(morion())
	);

	public static final DeferredBlock<StairBlock> MORION_BRICKS_STAIRS = registerBlockAndItem(
			"morion_bricks_stairs",
			() -> new StairBlock(MORION_BRICKS.get().defaultBlockState(), morion())
	);

	public static final DeferredBlock<RedstoneAccumulatorBlock> REDSTONE_ACCUMULATOR = registerBlockAndItem(
			"redstone_accumulator",
			() -> new RedstoneAccumulatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COMPARATOR))
	);
	public static final DeferredBlock<MoonphaseDetectorBlock> MOONPHASE_DETECTOR = registerBlockAndItem(
			"moonphase_detector",
			() -> new MoonphaseDetectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DAYLIGHT_DETECTOR))
	);

	public static final WoodType PETRIFIED_WOOD_TYPE = WoodType.register(new WoodType(
			RubinatedNether.id("petrified").toString(),
			BlockSetType.STONE,
			SoundType.STONE,
			SoundType.STONE,
			SoundEvents.FENCE_GATE_CLOSE,
			SoundEvents.FENCE_GATE_OPEN
	));

	public static final DeferredBlock<StandingSignBlock> PETRIFIED_SIGN = BLOCKS.register(
			"petrified_sign",
			() -> new PetrifiedSignBlock(PETRIFIED_WOOD_TYPE, petrified().forceSolidOn().noCollission().strength(1.0F))
	);

	public static final DeferredBlock<WallSignBlock> PETRIFIED_WALL_SIGN = BLOCKS.register(
			"petrified_wall_sign",
			() -> new PetrifiedWallSignBlock(PETRIFIED_WOOD_TYPE, petrified().forceSolidOn().noCollission().strength(1.0F))
	);

	public static final DeferredBlock<CeilingHangingSignBlock> PETRIFIED_HANGING_SIGN = BLOCKS.register(
			"petrified_hanging_sign",
			() -> new PetrifiedHangingSignBlock(PETRIFIED_WOOD_TYPE, petrified().forceSolidOn().noCollission().strength(1.0F))
	);

	public static final DeferredBlock<WallHangingSignBlock> PETRIFIED_WALL_HANGING_SIGN = BLOCKS.register(
			"petrified_wall_hanging_sign",
			() -> new PetrifiedWallHangingSignBlock(PETRIFIED_WOOD_TYPE, petrified().forceSolidOn().noCollission().strength(1.0F))
	);

	public static final DeferredItem<SignItem> PETRIFIED_SIGN_ITEM = RNItems.ITEMS.register(
			"petrified_sign",
			() -> new SignItem(new Item.Properties().stacksTo(16), PETRIFIED_SIGN.get(), PETRIFIED_WALL_SIGN.get())
	);

	public static final DeferredItem<HangingSignItem> PETRIFIED_HANGING_SIGN_ITEM = RNItems.ITEMS.register(
			"petrified_hanging_sign",
			() -> new HangingSignItem(PETRIFIED_HANGING_SIGN.get(), PETRIFIED_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
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

	public static final DeferredBlock<Block> SHRINE_STONE = registerShrineStoneBlockAndItem(
			"shrine_stone",
			() -> new Block(Block.Properties.ofFullCopy(Blocks.DEEPSLATE))
	);

	public static final DeferredBlock<SlabBlock> SHRINE_STONE_SLAB = registerBlockAndItem(
			"shrine_stone_slab",
			() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<StairBlock> SHRINE_STONE_STAIRS = registerBlockAndItem(
			"shrine_stone_stairs",
			() -> new StairBlock(
					SHRINE_STONE.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get())
			)
	);

	public static final DeferredBlock<WallBlock> SHRINE_STONE_WALL = registerBlockAndItem(
			"shrine_stone_wall",
			() -> new WallBlock(
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get())
			)
	);

	public static final DeferredBlock<Block> COBBLED_SHRINE_STONE = registerBlockAndItem(
			"cobbled_shrine_stone",
			() -> new Block(Block.Properties.ofFullCopy(Blocks.DEEPSLATE))
	);

	public static final DeferredBlock<SlabBlock> COBBLED_SHRINE_STONE_SLAB = registerBlockAndItem(
			"cobbled_shrine_stone_slab",
			() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<StairBlock> COBBLED_SHRINE_STONE_STAIRS = registerBlockAndItem(
			"cobbled_shrine_stone_stairs",
			() -> new StairBlock(
					COBBLED_SHRINE_STONE.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get())
			)
	);

	public static final DeferredBlock<WallBlock> COBBLED_SHRINE_STONE_WALL = registerBlockAndItem(
			"cobbled_shrine_stone_wall",
			() -> new WallBlock(
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get())
			)
	);

	public static final DeferredBlock<Block> POLISHED_SHRINE_STONE = registerBlockAndItem(
			"polished_shrine_stone",
			() -> new Block(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
	);

	public static final DeferredBlock<SlabBlock> POLISHED_SHRINE_STONE_SLAB = registerBlockAndItem(
			"polished_shrine_stone_slab",
			() -> new SlabBlock(SlabBlock.Properties.ofFullCopy(RNBlocks.POLISHED_SHRINE_STONE.get()))
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

	public static final DeferredBlock<ShrinePillarBlock> SHRINE_STONE_PILLAR = registerBlockAndItem(
			"shrine_stone_pillar",
			() -> new ShrinePillarBlock(Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get()))
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

	public static final DeferredBlock<ShrineRubbleBlock> SHRINE_STONE_RUBBLE = registerBlockAndItem(
			"shrine_stone_rubble",
			() -> new ShrineRubbleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.COLOR_PURPLE))
	);

	public static final DeferredBlock<ShrineRubbleSlabBlock> SHRINE_STONE_RUBBLE_SLAB = registerBlockAndItem(
			"shrine_stone_rubble_slab",
			() -> new ShrineRubbleSlabBlock(BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_RUBBLE.get()))
	);

	public static final DeferredBlock<ShrineRubbleStairBlock> SHRINE_STONE_RUBBLE_STAIRS = registerBlockAndItem(
			"shrine_stone_rubble_stairs",
			() -> new ShrineRubbleStairBlock(
					SHRINE_STONE_RUBBLE.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_RUBBLE.get())
			)
	);

	public static final DeferredBlock<ShrineRubbleWallBlock> SHRINE_STONE_RUBBLE_WALL = registerBlockAndItem(
			"shrine_stone_rubble_wall",
			() -> new ShrineRubbleWallBlock(BlockBehaviour.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_RUBBLE.get()))
	);

	public static final ResourceKey<LootTable> SUSPICIOUS_SHRINE_STONE_RUBBLE_LOOT = ResourceKey.create(
			Registries.LOOT_TABLE,
			RubinatedNether.id("archaeology/suspicious_shrine_stone_rubble")
	);

	public static final DeferredBlock<SuspiciousShrineRubbleBlock> SUSPICIOUS_SHRINE_STONE_RUBBLE = registerBlockAndItem(
			"suspicious_shrine_stone_rubble",
			() -> new SuspiciousShrineRubbleBlock(
					RNBlocks.SHRINE_STONE_RUBBLE.get(),
					SoundEvents.BRUSH_GRAVEL,
					SoundEvents.BRUSH_GRAVEL_COMPLETED,
					BlockBehaviour.Properties.ofFullCopy(Blocks.SUSPICIOUS_GRAVEL).mapColor(MapColor.COLOR_PURPLE)
			)
	);

	public static final DeferredBlock<SixWayPillarBlock> RUBINATED_CHISELED_SHRINE_STONE_BRICKS = registerBlockAndItemWithRarity(
			"rubinated_chiseled_shrine_stone_bricks",
			() -> new SixWayPillarBlock(
					Block.Properties.ofFullCopy(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get())
							.lightLevel($ -> 2)
			),
			RNRarity.RUBINATED_NETHER_RUBY.get()
	);

	public static final DeferredBlock<Block> RUBINATED_SHRINE_STONE_BRICKS = registerBlockAndItemWithRarity(
			"rubinated_shrine_stone_bricks",
			() -> new Block(
					Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE_BRICKS.get())
							.lightLevel($ -> 2)
			),
			RNRarity.RUBINATED_NETHER_RUBY.get()
	);

	public static final DeferredBlock<Block> RUBINATED_SHRINE_STONE_TILES = registerBlockAndItemWithRarity(
			"rubinated_shrine_stone_tiles",
			() -> new Block(
					Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get())
							.lightLevel($ -> 2)
			),
			RNRarity.RUBINATED_NETHER_RUBY.get()
	);

	public static final DeferredBlock<ShrinePillarBlock> RUBINATED_SHRINE_STONE_PILLAR = registerBlockAndItemWithRarity(
			"rubinated_shrine_stone_pillar",
			() -> new ShrinePillarBlock(
					Block.Properties.ofFullCopy(RNBlocks.SHRINE_STONE.get())
							.lightLevel($ -> 2)
			),
			RNRarity.RUBINATED_NETHER_RUBY.get()
	);

	public static final DeferredBlock<Block> SHRINE_STONE_COFFER = registerBlockAndItem(
			"shrine_stone_coffer",
			() -> new CofferBlock(
					BlockBehaviour.Properties
							.ofFullCopy(RNBlocks.SHRINE_STONE.get())
							.mapColor(MapColor.NETHER),
					RNBlockEntities.COFFER::get
			)
	);

	public static final DeferredBlock<Block> RUBINATED_SHRINE_STONE_COFFER = registerBlockAndItemWithRarity(
			"rubinated_shrine_stone_coffer",
			() -> new RubinatedCofferBlock(
					BlockBehaviour.Properties
							.ofFullCopy(RNBlocks.SHRINE_STONE.get())
							.mapColor(MapColor.NETHER)
							.lightLevel($ -> 2),
					RNBlockEntities.RUBINATED_COFFER::get
			),
			RNRarity.RUBINATED_NETHER_RUBY.get()
	);

	public static final DeferredBlock<Block> BRONZE_BLOCK = registerWaxableBlock(
			"bronze_block",
			() -> new TarnishingBronzeBlock(
					TarnishStage.UNAFFECTED,
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
					TarnishStage.DISCOLORED,
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
					TarnishStage.CORRODED,
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
					TarnishStage.TARNISHED,
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
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> CUT_BRONZE_PILLAR = registerWaxableBlock(
			"cut_bronze_pillar",
			() -> new TarnishingPillarBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(5.0F, 150.0F)
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_CUT_BRONZE_PILLAR = registerWaxableBlock(
			"discolored_cut_bronze_pillar",
			() -> new TarnishingPillarBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(15.0F, 300.0F)
			)
	);

	public static final DeferredBlock<Block> CORRODED_CUT_BRONZE_PILLAR = registerWaxableBlock(
			"corroded_cut_bronze_pillar",
			() -> new TarnishingPillarBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(25.0F, 600.0F)
			)
	);

	public static final DeferredBlock<Block> TARNISHED_CUT_BRONZE_PILLAR = registerWaxableBlock(
			"tarnished_cut_bronze_pillar",
			() -> new TarnishingPillarBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(50.0F, 1200.0F)
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_CUT_BRONZE_PILLAR = registerWaxableBlock(
			"crystallized_cut_bronze_pillar",
			() -> new TarnishingPillarBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
			)
	);

	public static final DeferredBlock<Block> CUT_BRONZE_BRICKS = registerWaxableBlock(
			"cut_bronze_bricks",
			() -> new TarnishingBronzeBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(5.0F, 150.0F)
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_CUT_BRONZE_BRICKS = registerWaxableBlock(
			"discolored_cut_bronze_bricks",
			() -> new TarnishingBronzeBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(15.0F, 300.0F)
			)
	);

	public static final DeferredBlock<Block> CORRODED_CUT_BRONZE_BRICKS = registerWaxableBlock(
			"corroded_cut_bronze_bricks",
			() -> new TarnishingBronzeBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(25.0F, 600.0F)
			)
	);

	public static final DeferredBlock<Block> TARNISHED_CUT_BRONZE_BRICKS = registerWaxableBlock(
			"tarnished_cut_bronze_bricks",
			() -> new TarnishingBronzeBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(50.0F, 1200.0F)
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_CUT_BRONZE_BRICKS = registerWaxableBlock(
			"crystallized_cut_bronze_bricks",
			() -> new TarnishingBronzeBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
			)
	);

	public static final DeferredBlock<SlabBlock> CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
			"cut_bronze_bricks_slab",
			() -> new TarnishingBronzeSlabBlock(
					TarnishStage.UNAFFECTED,
					SlabBlock.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(5.0F, 150.0F)
			)
	);

	public static final DeferredBlock<StairBlock> CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
			"cut_bronze_bricks_stairs",
			() -> new TarnishingBronzeStairBlock(
					TarnishStage.UNAFFECTED,
					BRONZE_BLOCK.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(5.0F, 150.0F)
			)
	);

	public static final DeferredBlock<SlabBlock> DISCOLORED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
			"discolored_cut_bronze_bricks_slab",
			() -> new TarnishingBronzeSlabBlock(
					TarnishStage.DISCOLORED,
					SlabBlock.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(15.0F, 300.0F)
			)
	);

	public static final DeferredBlock<StairBlock> DISCOLORED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
			"discolored_cut_bronze_bricks_stairs",
			() -> new TarnishingBronzeStairBlock(
					TarnishStage.DISCOLORED,
					DISCOLORED_BRONZE_BLOCK.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(15.0F, 300.0F)
			)
	);

	public static final DeferredBlock<SlabBlock> CORRODED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
			"corroded_cut_bronze_bricks_slab",
			() -> new TarnishingBronzeSlabBlock(
					TarnishStage.CORRODED,
					SlabBlock.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(25.0F, 600.0F)
			)
	);

	public static final DeferredBlock<StairBlock> CORRODED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
			"corroded_cut_bronze_bricks_stairs",
			() -> new TarnishingBronzeStairBlock(
					TarnishStage.CORRODED,
					CORRODED_BRONZE_BLOCK.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(25.0F, 600.0F)
			)
	);

	public static final DeferredBlock<SlabBlock> TARNISHED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
			"tarnished_cut_bronze_bricks_slab",
			() -> new TarnishingBronzeSlabBlock(
					TarnishStage.TARNISHED,
					SlabBlock.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(50.0F, 1200.0F)
			)
	);

	public static final DeferredBlock<StairBlock> TARNISHED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
			"tarnished_cut_bronze_bricks_stairs",
			() -> new TarnishingBronzeStairBlock(
					TarnishStage.TARNISHED,
					TARNISHED_BRONZE_BLOCK.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(50.0F, 1200.0F)
			)
	);

	public static final DeferredBlock<SlabBlock> CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB = registerWaxableBlock(
			"crystallized_cut_bronze_bricks_slab",
			() -> new TarnishingBronzeSlabBlock(
					TarnishStage.CRYSTALLIZED,
					SlabBlock.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
			)
	);

	public static final DeferredBlock<StairBlock> CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS = registerWaxableBlock(
			"crystallized_cut_bronze_bricks_stairs",
			() -> new TarnishingBronzeStairBlock(
					TarnishStage.CRYSTALLIZED,
					CRYSTALLIZED_BRONZE_BLOCK.get().defaultBlockState(),
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
			)
	);

	public static final DeferredBlock<Block> CHISELED_BRONZE = registerWaxableBlock(
			"chiseled_bronze",
			() -> new TarnishingBronzeBlock(
					TarnishStage.UNAFFECTED,
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
					TarnishStage.DISCOLORED,
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
					TarnishStage.CORRODED,
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
					TarnishStage.TARNISHED,
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
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> BRONZE_BULB = registerWaxableBlock(
			"bronze_bulb",
			() -> new TarnishingBronzeBulbBlock(
					TarnishStage.UNAFFECTED,
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
					TarnishStage.DISCOLORED,
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
					TarnishStage.CORRODED,
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
					TarnishStage.TARNISHED,
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
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER)
							.randomTicks()
							.lightLevel(litBlockEmission(15))
			)
	);

	public static final DeferredBlock<Block> BRONZE_GRATE = registerWaxableBlock(
			"bronze_grate",
			() -> new BronzeGrateBlock(
					TarnishStage.UNAFFECTED,
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
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(12.0F, 220.0F)
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
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(20.0F, 440.0F)
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
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(40.0F, 880.0F)
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
					TarnishStage.CRYSTALLIZED,
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

	public static final DeferredBlock<Block> BRONZE_BUTTON = registerWaxableBlock(
			"bronze_button",
			() -> new TarnishingBronzeButtonBlock(
					TarnishStage.UNAFFECTED,
					5,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.noCollission()
							.strength(1.0F)
							.sound(SoundType.COPPER)
							.pushReaction(PushReaction.DESTROY)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_BUTTON = registerWaxableBlock(
			"discolored_bronze_button",
			() -> new TarnishingBronzeButtonBlock(
					TarnishStage.DISCOLORED,
					10,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_PINK)
							.noCollission()
							.strength(2.0F)
							.sound(SoundType.COPPER)
							.pushReaction(PushReaction.DESTROY)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_BUTTON = registerWaxableBlock(
			"corroded_bronze_button",
			() -> new TarnishingBronzeButtonBlock(
					TarnishStage.CORRODED,
					40,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.CRIMSON_STEM)
							.noCollission()
							.strength(3.0F)
							.sound(SoundType.COPPER)
							.pushReaction(PushReaction.DESTROY)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_BUTTON = registerWaxableBlock(
			"tarnished_bronze_button",
			() -> new TarnishingBronzeButtonBlock(
					TarnishStage.TARNISHED,
					80,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_BLACK)
							.noCollission()
							.strength(5.0F)
							.sound(SoundType.COPPER)
							.pushReaction(PushReaction.DESTROY)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_BUTTON = registerWaxableBlock(
			"crystallized_bronze_button",
			() -> new TarnishingBronzeButtonBlock(
					TarnishStage.CRYSTALLIZED,
					2,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.noCollission()
							.strength(0.5F)
							.sound(SoundType.COPPER)
							.pushReaction(PushReaction.DESTROY)
							.randomTicks()
			)
	);

	public static final DeferredBlock<Block> BRONZE_SPRING = registerWaxableBlock(
			"bronze_spring",
			() -> new BronzeSpringBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.GOLD)
							.requiresCorrectToolForDrops()
							.strength(4.0F, 110.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_SPRING = registerWaxableBlock(
			"discolored_bronze_spring",
			() -> new BronzeSpringBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_PINK)
							.requiresCorrectToolForDrops()
							.strength(12.0F, 220.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_SPRING = registerWaxableBlock(
			"corroded_bronze_spring",
			() -> new BronzeSpringBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.CRIMSON_STEM)
							.requiresCorrectToolForDrops()
							.strength(20.0F, 440.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_SPRING = registerWaxableBlock(
			"tarnished_bronze_spring",
			() -> new BronzeSpringBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_BLACK)
							.requiresCorrectToolForDrops()
							.strength(40.0F, 880.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_SPRING = registerWaxableBlock(
			"crystallized_bronze_spring",
			() -> new BronzeSpringBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.0F, 6.0F)
							.sound(SoundType.COPPER_GRATE)
							.randomTicks()
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> BRONZE_LANTERN = registerWaxableBlock(
			"bronze_lantern",
			() -> new TarnishingBronzeLanternBlock(
					TarnishStage.UNAFFECTED,
					Block.Properties
							.ofFullCopy(Blocks.LANTERN)
							.mapColor(MapColor.GOLD)
							.strength(5.0F, 20.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_LANTERN = registerWaxableBlock(
			"discolored_bronze_lantern",
			() -> new TarnishingBronzeLanternBlock(
					TarnishStage.DISCOLORED,
					Block.Properties
							.ofFullCopy(Blocks.LANTERN)
							.mapColor(MapColor.TERRACOTTA_PINK)
							.strength(15.0F, 40.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_LANTERN = registerWaxableBlock(
			"corroded_bronze_lantern",
			() -> new TarnishingBronzeLanternBlock(
					TarnishStage.CORRODED,
					Block.Properties
							.ofFullCopy(Blocks.LANTERN)
							.mapColor(MapColor.CRIMSON_STEM)
							.strength(25.0F, 80.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_LANTERN = registerWaxableBlock(
			"tarnished_bronze_lantern",
			() -> new TarnishingBronzeLanternBlock(
					TarnishStage.TARNISHED,
					Block.Properties
							.ofFullCopy(Blocks.LANTERN)
							.mapColor(MapColor.TERRACOTTA_BLACK)
							.strength(50.0F, 160.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_LANTERN = registerWaxableBlock(
			"crystallized_bronze_lantern",
			() -> new TarnishingBronzeLanternBlock(
					TarnishStage.CRYSTALLIZED,
					Block.Properties
							.ofFullCopy(Blocks.LANTERN)
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.strength(1.0F, 3.5F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> BRONZE_CHAIN = registerWaxableBlock(
			"bronze_chain",
			() -> new TarnishingBronzeChainBlock(
					TarnishStage.UNAFFECTED,
					Block.Properties
							.ofFullCopy(Blocks.CHAIN)
							.mapColor(MapColor.GOLD)
							.strength(5.0F, 50.0F)
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_CHAIN = registerWaxableBlock(
			"discolored_bronze_chain",
			() -> new TarnishingBronzeChainBlock(
					TarnishStage.DISCOLORED,
					Block.Properties
							.ofFullCopy(Blocks.CHAIN)
							.mapColor(MapColor.TERRACOTTA_PINK)
							.strength(15.0F, 100.0F)
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_CHAIN = registerWaxableBlock(
			"corroded_bronze_chain",
			() -> new TarnishingBronzeChainBlock(
					TarnishStage.CORRODED,
					Block.Properties
							.ofFullCopy(Blocks.CHAIN)
							.mapColor(MapColor.CRIMSON_STEM)
							.strength(25.0F, 200.0F)
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_CHAIN = registerWaxableBlock(
			"tarnished_bronze_chain",
			() -> new TarnishingBronzeChainBlock(
					TarnishStage.TARNISHED,
					Block.Properties
							.ofFullCopy(Blocks.CHAIN)
							.mapColor(MapColor.TERRACOTTA_BLACK)
							.strength(50.0F, 400.0F)
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_CHAIN = registerWaxableBlock(
			"crystallized_bronze_chain",
			() -> new TarnishingBronzeChainBlock(
					TarnishStage.CRYSTALLIZED,
					Block.Properties
							.ofFullCopy(Blocks.CHAIN)
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.strength(1.0F, 6.0F)
			)
	);

	public static final DeferredBlock<Block> BRONZE_CHANDELIER = registerWaxableBlock(
			"bronze_chandelier",
			() -> new ChandelierBlock(
					TarnishStage.UNAFFECTED,
					Block.Properties
							.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.strength(5.0F, 20.0F)
							.noOcclusion()
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_CHANDELIER = registerWaxableBlock(
			"discolored_bronze_chandelier",
			() -> new ChandelierBlock(
					TarnishStage.DISCOLORED,
					Block.Properties
							.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.strength(15.0F, 40.0F)
							.noOcclusion()
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_CHANDELIER = registerWaxableBlock(
			"corroded_bronze_chandelier",
			() -> new ChandelierBlock(
					TarnishStage.CORRODED,
					Block.Properties
							.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.strength(25.0F, 80.0F)
							.noOcclusion()
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_CHANDELIER = registerWaxableBlock(
			"tarnished_bronze_chandelier",
			() -> new ChandelierBlock(
					TarnishStage.TARNISHED,
					Block.Properties
							.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.strength(50.0F, 160.0F)
							.noOcclusion()
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_CHANDELIER = registerWaxableBlock(
			"crystallized_bronze_chandelier",
			() -> new ChandelierBlock(
					TarnishStage.CRYSTALLIZED,
					Block.Properties
							.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.strength(1.0F, 3.5F)
							.noOcclusion()
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> BRONZE_LAMP = registerWaxableBlock(
			"bronze_lamp",
			() -> new TarnishingBronzeLampBlock(
					TarnishStage.UNAFFECTED,
					Block.Properties
							.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.strength(5.0F, 150.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> DISCOLORED_BRONZE_LAMP = registerWaxableBlock(
			"discolored_bronze_lamp",
			() -> new TarnishingBronzeLampBlock(
					TarnishStage.DISCOLORED,
					Block.Properties
							.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.strength(15.0F, 300.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> CORRODED_BRONZE_LAMP = registerWaxableBlock(
			"corroded_bronze_lamp",
			() -> new TarnishingBronzeLampBlock(
					TarnishStage.CORRODED,
					Block.Properties
							.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.strength(25.0F, 600.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> TARNISHED_BRONZE_LAMP = registerWaxableBlock(
			"tarnished_bronze_lamp",
			() -> new TarnishingBronzeLampBlock(
					TarnishStage.TARNISHED,
					Block.Properties
							.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.strength(50.0F, 1200.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<RotatedPillarBlock> CRYSTALLIZED_BRONZE_LAMP = registerWaxableBlock(
			"crystallized_bronze_lamp",
			() -> new TarnishingBronzeLampBlock(
					TarnishStage.CRYSTALLIZED,
					Block.Properties
							.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.strength(1.0F, 6.0F)
							.lightLevel($ -> 15)
			)
	);

	public static final DeferredBlock<Block> BRONZE_LASER = registerWaxableBlock(
			"bronze_laser",
			() -> new BronzeLaserBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get()).noOcclusion()
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_LASER = registerWaxableBlock(
			"discolored_bronze_laser",
			() -> new BronzeLaserBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get()).noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_LASER = registerWaxableBlock(
			"corroded_bronze_laser",
			() -> new BronzeLaserBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get()).noOcclusion()
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_LASER = registerWaxableBlock(
			"tarnished_bronze_laser",
			() -> new BronzeLaserBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get()).noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_LASER = registerWaxableBlock(
			"crystallized_bronze_laser",
			() -> new BronzeLaserBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get()).noOcclusion()
			)
	);

	public static final DeferredBlock<Block> BRONZE_WINDER = registerWaxableBlock(
			"bronze_winder",
			() -> new BronzeWinderBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get()).noCollission().noOcclusion()
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_WINDER = registerWaxableBlock(
			"discolored_bronze_winder",
			() -> new BronzeWinderBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get()).noCollission().noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_WINDER = registerWaxableBlock(
			"corroded_bronze_winder",
			() -> new BronzeWinderBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get()).noCollission().noOcclusion()
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_WINDER = registerWaxableBlock(
			"tarnished_bronze_winder",
			() -> new BronzeWinderBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get()).noCollission().noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_WINDER = registerWaxableBlock(
			"crystallized_bronze_winder",
			() -> new BronzeWinderBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get()).noCollission().noOcclusion()
			)
	);

	public static final DeferredBlock<GearboxBlock> BRONZE_GEARBOX = registerWaxableBlock(
			"bronze_gearbox",
			() -> new ActiveGearboxBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.isViewBlocking(GearboxBlock::noViewBlocking)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<GearboxBlock> DISCOLORED_BRONZE_GEARBOX = registerWaxableBlock(
			"discolored_bronze_gearbox",
			() -> new ActiveGearboxBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.isViewBlocking(GearboxBlock::noViewBlocking)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<GearboxBlock> CORRODED_BRONZE_GEARBOX = registerWaxableBlock(
			"corroded_bronze_gearbox",
			() -> new ActiveGearboxBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.isViewBlocking(GearboxBlock::noViewBlocking)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<GearboxBlock> TARNISHED_BRONZE_GEARBOX = registerWaxableBlock(
			"tarnished_bronze_gearbox",
			() -> new ActiveGearboxBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.isViewBlocking(GearboxBlock::noViewBlocking)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<GearboxBlock> CRYSTALLIZED_BRONZE_GEARBOX = registerWaxableBlock(
			"crystallized_bronze_gearbox",
			() -> new ActiveGearboxBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.isViewBlocking(GearboxBlock::noViewBlocking)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> BRONZE_STRONGBOX = registerStrongbox(
			"bronze_strongbox",
			() -> new BronzeStrongboxBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_STRONGBOX = registerStrongbox(
			"discolored_bronze_strongbox",
			() -> new BronzeStrongboxBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_STRONGBOX = registerStrongbox(
			"corroded_bronze_strongbox",
			() -> new BronzeStrongboxBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_STRONGBOX = registerStrongbox(
			"tarnished_bronze_strongbox",
			() -> new BronzeStrongboxBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_STRONGBOX = registerStrongbox(
			"crystallized_bronze_strongbox",
			() -> new BronzeStrongboxBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
							.noOcclusion()
			)
	);

	// Winders and gearboxes used to be registered without the "bronze_" part of their id. These aliases keep
	// blocks in existing worlds, items in inventories and structure data working under the new ids.
	static {
		for (String stage : new String[]{"", "discolored_", "corroded_", "tarnished_", "crystallized_"}) {
			for (String kind : new String[]{"winder", "gearbox"}) {
				BLOCKS.addAlias(RubinatedNether.id(stage + kind), RubinatedNether.id(stage + "bronze_" + kind));
				RNItems.ITEMS.addAlias(RubinatedNether.id(stage + kind), RubinatedNether.id(stage + "bronze_" + kind));
				RNItems.ITEMS.addAlias(RubinatedNether.id("waxed_" + stage + kind), RubinatedNether.id("waxed_" + stage + "bronze_" + kind));
			}
		}
	}

	public static final DeferredBlock<Block> BRONZE_VENT = registerWaxableBlock(
			"bronze_vent",
			() -> new TarnishingBronzeVentBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_VENT = registerWaxableBlock(
			"discolored_bronze_vent",
			() -> new TarnishingBronzeVentBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_VENT = registerWaxableBlock(
			"corroded_bronze_vent",
			() -> new TarnishingBronzeVentBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_VENT = registerWaxableBlock(
			"tarnished_bronze_vent",
			() -> new TarnishingBronzeVentBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_VENT = registerWaxableBlock(
			"crystallized_bronze_vent",
			() -> new TarnishingBronzeVentBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> BRONZE_VASE = registerVase(
			"bronze_vase",
			() -> new BronzeVaseBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_VASE = registerVase(
			"discolored_bronze_vase",
			() -> new BronzeVaseBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_VASE = registerVase(
			"corroded_bronze_vase",
			() -> new BronzeVaseBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_VASE = registerVase(
			"tarnished_bronze_vase",
			() -> new BronzeVaseBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_VASE = registerVase(
			"crystallized_bronze_vase",
			() -> new BronzeVaseBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get())
			)
	);

	public static final DeferredBlock<CrystallizedBronzeBlightBlock> CRYSTALLIZED_BRONZE_BLIGHT = registerBlockAndItem(
			"crystallized_bronze_blight",
			() -> new CrystallizedBronzeBlightBlock(
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.TERRACOTTA_WHITE)
							.requiresCorrectToolForDrops()
							.strength(1.5F)
							.sound(SoundType.AMETHYST_CLUSTER)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<Block> BRONZE_HEAD = registerWaxableBlockWithRarity(
			"bronze_head",
			() -> new BronzeHeadBlock(
					TarnishStage.UNAFFECTED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.BRONZE_BLOCK.get()).noOcclusion()
			),
			Rarity.UNCOMMON
	);

	public static final DeferredBlock<Block> DISCOLORED_BRONZE_HEAD = registerWaxableBlockWithRarity(
			"discolored_bronze_head",
			() -> new BronzeHeadBlock(
					TarnishStage.DISCOLORED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.DISCOLORED_BRONZE_BLOCK.get()).noOcclusion()
			),
			Rarity.UNCOMMON
	);

	public static final DeferredBlock<Block> CORRODED_BRONZE_HEAD = registerWaxableBlockWithRarity(
			"corroded_bronze_head",
			() -> new BronzeHeadBlock(
					TarnishStage.CORRODED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CORRODED_BRONZE_BLOCK.get()).noOcclusion()
			),
			Rarity.UNCOMMON
	);

	public static final DeferredBlock<Block> TARNISHED_BRONZE_HEAD = registerWaxableBlockWithRarity(
			"tarnished_bronze_head",
			() -> new BronzeHeadBlock(
					TarnishStage.TARNISHED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.TARNISHED_BRONZE_BLOCK.get()).noOcclusion()
			),
			Rarity.UNCOMMON
	);

	public static final DeferredBlock<Block> CRYSTALLIZED_BRONZE_HEAD = registerWaxableBlockWithRarity(
			"crystallized_bronze_head",
			() -> new BronzeHeadBlock(
					TarnishStage.CRYSTALLIZED,
					BlockBehaviour.Properties.ofFullCopy(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get()).noOcclusion()
			),
			Rarity.UNCOMMON
	);

	public static final DeferredBlock<Block> FREEZER = registerBlockAndItem(
			"freezer",
			() -> new FreezerBlock(
					BlockBehaviour.Properties
							.ofFullCopy(Blocks.COPPER_BLOCK)
							.noOcclusion()
			)
	);

	public static final DeferredBlock<FrostedGlassBlock> FROSTED_GLASS = registerBlockAndItem(
			"frosted_glass",
			() -> new FrostedGlassBlock(Block.Properties.ofFullCopy(Blocks.GLASS).isRedstoneConductor((state, level, pos) -> true))
	);

	public static final Map<DyeColor, DeferredBlock<FrostedGlassBlock>> STAINED_FROSTED_GLASS = registerStainedFrostedGlass();

	public static final DeferredBlock<Block> RUBINATION_ALTAR = registerBlockAndItem(
			"rubination_altar",
			() -> new RubinationAltarBlock(
					BlockBehaviour.Properties
							.ofFullCopy(Blocks.OBSIDIAN)
							.mapColor(MapColor.NETHER)
			)
	);

	public static final DeferredBlock<Block> BRAZIER = registerBlockAndItem(
			"ruby_brazier",
			() -> new BrazierBlock(
					BlockBehaviour.Properties
							.ofFullCopy(RNBlocks.BRONZE_BLOCK.get())
							.mapColor(MapColor.FIRE)
							.noOcclusion()
							.lightLevel(state -> state.getValue(BrazierBlock.LEVEL))
			)
	);

	private static Map<DyeColor, DeferredBlock<FrostedGlassBlock>> registerStainedFrostedGlass() {
		Map<DyeColor, DeferredBlock<FrostedGlassBlock>> blocks = new EnumMap<>(DyeColor.class);
		for (DyeColor color : DyeColor.values()) {
			blocks.put(
					color,
					registerBlockAndItem(
							color.getName() + "_frosted_glass",
							() -> new FrostedGlassBlock(
									Block.Properties.ofFullCopy(Blocks.GLASS)
											.mapColor(color)
											.isRedstoneConductor((state, level, pos) -> true)
							)
					)
			);
		}
		return blocks;
	}

	public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.registerSimpleBlockItem(
				name,
				register
		);

		return register;
	}

	public static <T extends Block> DeferredBlock<T> registerFlippableBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new corundum.rubinated_nether.content.items.FlippableBlockItem(register.get(), new Item.Properties())
		);
		FLIPPABLE_BLOCKS.add(register);

		return register;
	}

	/** Like registerBlockAndItem, but the item can be placed in a grindstone (see RuneGrindstoneHandler). */
	public static <T extends Block> DeferredBlock<T> registerShrineStoneBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new corundum.rubinated_nether.content.items.ShrineStoneBlockItem(register.get(), new Item.Properties())
		);

		return register;
	}

	public static <T extends Block> DeferredBlock<T> registerDoubleHighBlockAndItem(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new DoubleHighBlockItem(register.get(), new Item.Properties())
		);

		return register;
	}

	private static BlockBehaviour.Properties morion() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.COLOR_BLACK)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.requiresCorrectToolForDrops()
				.strength(0.8F);
	}

	private static BlockBehaviour.Properties quartz() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK);
	}

	private static BlockBehaviour.Properties checkered() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.COLOR_BLACK)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.requiresCorrectToolForDrops()
				.strength(1.4F)
				.pushReaction(PushReaction.PUSH_ONLY);
	}

	private static BlockBehaviour.Properties petrified() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.WOOD)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.sound(SoundType.STONE)
				.strength(2.0F, 6.0F)
				.requiresCorrectToolForDrops();
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

	public static <T extends Block> DeferredBlock<T> registerWaxableBlockWithRarity(String name, Supplier<T> block, Rarity rarity) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new BlockItem(register.get(), new Item.Properties().rarity(rarity))
		);

		RNItems.ITEMS.register(
				"waxed_" + name,
				() -> new WaxableBlockItem(
						register,
						new Item.Properties().rarity(rarity)
				)
		);

		return register;
	}

	public static <T extends Block> DeferredBlock<T> registerVase(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new VaseBlockItem(register.get(), new Item.Properties())
		);

		RNItems.ITEMS.register(
				"waxed_" + name,
				() -> new WaxableBlockItem(
						register,
						new Item.Properties()
				)
		);

		return register;
	}

	public static <T extends Block> DeferredBlock<T> registerStrongbox(String name, Supplier<T> block) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new StrongboxBlockItem(register.get(), new Item.Properties())
		);

		RNItems.ITEMS.register(
				"waxed_" + name,
				() -> new WaxableBlockItem(
						register,
						new Item.Properties()
				)
		);

		return register;
	}

	public static <T extends Block> DeferredBlock<T> registerBlockAndItemWithRarity(String name, Supplier<T> block, Rarity rarity) {
		var register = BLOCKS.register(name, block);

		RNItems.ITEMS.register(
				name,
				() -> new BlockItem(register.get(), new Item.Properties().rarity(rarity))
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