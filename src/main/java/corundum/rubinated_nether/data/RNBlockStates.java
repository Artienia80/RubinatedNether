package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.blocks.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import corundum.rubinated_nether.content.RNBlockStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.function.TriConsumer;
import java.util.HashMap;
import java.util.Map;

public class RNBlockStates extends BlockStateProvider {
	public RNBlockStates(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, RubinatedNether.MODID, fileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.simpleBlock(RNBlocks.NETHER_RUBY_ORE.get());
		this.simpleBlock(RNBlocks.MOLTEN_RUBY_ORE.get());
		this.simpleBlock(RNBlocks.RUBINATED_BLACKSTONE.get());

		this.simpleBlock(RNBlocks.RUBY_BLOCK.get());
		this.axisBlock(RNBlocks.MOLTEN_RUBY_BLOCK.get());
		this.simpleBlock(RNBlocks.BLEEDING_OBSIDIAN.get());

		this.simpleBlock(
				RNBlocks.MOLTEN_RUBY_CAULDRON.get(),
				this.models()
						.withExistingParent("molten_ruby_cauldron", mcLoc("block/template_cauldron_full"))
						.texture("particle", modLoc("block/molten_ruby_block_end"))
						.texture("content", modLoc("block/molten_ruby_block_end"))
						.texture("inside", mcLoc("block/cauldron_inner"))
						.texture("top", mcLoc("block/cauldron_top"))
						.texture("bottom", mcLoc("block/cauldron_bottom"))
						.texture("side", mcLoc("block/cauldron_side"))
		);

		this.simpleBlock(
				RNBlocks.RUBINATION_ALTAR.get(),
				this.models()
						.withExistingParent("rubination_altar", this.modLoc("block/rubination_altar_base"))
		);


		subfolder("bronze/bronze_chandelier/",
				(rloc, name, block) -> {
					generateChandelierModel(name);

					this.simpleBlock(
							block.get(),
							this.models().getExistingFile(this.modLoc("block/" + name))
					);
				},
				RNBlocks.BRONZE_CHANDELIER,
				RNBlocks.DISCOLORED_BRONZE_CHANDELIER,
				RNBlocks.CORRODED_BRONZE_CHANDELIER,
				RNBlocks.TARNISHED_BRONZE_CHANDELIER,
				RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER
		);

		subfolder("bronze/bronze_lamp/",
				(rloc, name, block) -> {
					this.axisBlock(
							(RotatedPillarBlock) block.get(),
							this.models()
									.withExistingParent(name, this.modLoc(rloc + "_side_base")), // Remove "_side"
							this.models()
									.withExistingParent(name, this.modLoc(rloc + "_base"))
					);
				},
				RNBlocks.BRONZE_LAMP,
				RNBlocks.DISCOLORED_BRONZE_LAMP,
				RNBlocks.CORRODED_BRONZE_LAMP,
				RNBlocks.TARNISHED_BRONZE_LAMP,
				RNBlocks.CRYSTALLIZED_BRONZE_LAMP
		);

		subfolder("bronze/bronze_spring/",
				(rloc, name, block) -> {
					springBlock(block, rloc);
				},
				RNBlocks.BRONZE_SPRING,
				RNBlocks.DISCOLORED_BRONZE_SPRING,
				RNBlocks.CORRODED_BRONZE_SPRING,
				RNBlocks.TARNISHED_BRONZE_SPRING,
				RNBlocks.CRYSTALLIZED_BRONZE_SPRING
		);

		this.simpleBlock(
				RNBlocks.DRY_ICE.get(),
				this.models()
						.withExistingParent("dry_ice", this.modLoc("block/dry_ice_base"))
		);
		this.simpleBlock(RNBlocks.PUMICE.get());
		this.getVariantBuilder(RNBlocks.CINDER.get()).partialState().setModels(
				this.randomRotations(this.cubeAll(RNBlocks.CINDER.get()))
		);
		this.getVariantBuilder(RNBlocks.SUSPICIOUS_CINDER.get()).forAllStates(state -> {
			int dusted = state.getValue(BlockStateProperties.DUSTED);
			String name = "suspicious_cinder_" + dusted;
			return this.randomRotations(this.models().cubeAll(name, modLoc("block/" + name)));
		});
		this.simpleBlock(RNBlocks.SOUL_STARVED_SAND.get());
		this.axisBlock(RNBlocks.PETRIFIED_SNAG.get(), modLoc("block/petrified/petrified_snag"), modLoc("block/petrified/petrified_snag_top"));
		this.axisBlock(RNBlocks.STRIPPED_PETRIFIED_SNAG.get(), modLoc("block/petrified/stripped_petrified_snag"), modLoc("block/petrified/stripped_petrified_snag_top"));
		this.axisBlock(RNBlocks.PETRIFIED_RIND.get(), modLoc("block/petrified/petrified_snag"), modLoc("block/petrified/petrified_snag"));
		this.axisBlock(RNBlocks.STRIPPED_PETRIFIED_RIND.get(), modLoc("block/petrified/stripped_petrified_snag"), modLoc("block/petrified/stripped_petrified_snag"));
		this.subfolder("petrified/", RNBlocks.PETRIFIED_FLAGS);
		this.slabBlock(
				RNBlocks.PETRIFIED_SLAB.get(),
				modLoc("block/petrified_flags"),
				modLoc("block/petrified/petrified_flags")
		);
		this.stairsBlock(
				RNBlocks.PETRIFIED_STAIRS.get(),
				modLoc("block/petrified/petrified_flags")
		);
		this.wallBlock(
				RNBlocks.PETRIFIED_WALL.get(),
				modLoc("block/petrified/petrified_flags")
		);
		this.fenceBlock(
				RNBlocks.PETRIFIED_FENCE.get(),
				modLoc("block/petrified/petrified_flags")
		);
		this.fenceGateBlock(
				RNBlocks.PETRIFIED_FENCE_GATE.get(),
				modLoc("block/petrified/petrified_flags")
		);
		this.doorBlockWithRenderType(
				RNBlocks.PETRIFIED_DOOR.get(),
				modLoc("block/petrified/petrified_door_bottom"),
				modLoc("block/petrified/petrified_door_top"),
				"cutout"
		);
		this.trapdoorBlockWithRenderType(
				RNBlocks.PETRIFIED_TRAPDOOR.get(),
				modLoc("block/petrified/petrified_trapdoor"),
				true,
				"cutout"
		);
		this.pressurePlateBlock(RNBlocks.PETRIFIED_PRESSURE_PLATE.get(), modLoc("block/petrified/petrified_flags"));
		this.buttonBlock(RNBlocks.PETRIFIED_BUTTON.get(), modLoc("block/petrified/petrified_flags"));
		this.models().buttonInventory("petrified_button_inventory", modLoc("block/petrified/petrified_flags"));
		var petrifiedSign = this.models().sign("petrified_sign", modLoc("block/petrified/petrified_flags"));
		this.simpleBlock(RNBlocks.PETRIFIED_SIGN.get(), petrifiedSign);
		this.simpleBlock(RNBlocks.PETRIFIED_WALL_SIGN.get(), petrifiedSign);
		var petrifiedHangingSign = this.models().sign("petrified_hanging_sign", modLoc("block/petrified/stripped_petrified_snag"));
		this.simpleBlock(RNBlocks.PETRIFIED_HANGING_SIGN.get(), petrifiedHangingSign);
		this.simpleBlock(RNBlocks.PETRIFIED_WALL_HANGING_SIGN.get(), petrifiedHangingSign);
		this.subfolder("morion/", RNBlocks.MORION_BLOCK);
		this.simpleBlock(
				RNBlocks.CHISELED_MORION_BLOCK.get(),
				this.models().cubeColumn("chiseled_morion_block", modLoc("block/morion/chiseled_morion_block"), modLoc("block/morion/chiseled_morion_block_top"))
		);
		this.axisBlock(RNBlocks.MORION_PILLAR.get(), modLoc("block/morion/morion_pillar"), modLoc("block/morion/morion_pillar_top"));
		this.subfolder("morion/", RNBlocks.MORION_BRICKS);
		this.subfolder("morion/", RNBlocks.SMOOTH_MORION);
		this.slabBlock(RNBlocks.MORION_SLAB.get(), modLoc("block/morion_block"), modLoc("block/morion/morion_block"));
		this.stairsBlock(RNBlocks.MORION_STAIRS.get(), modLoc("block/morion/morion_block"));
		this.slabBlock(RNBlocks.SMOOTH_MORION_SLAB.get(), modLoc("block/smooth_morion"), modLoc("block/morion/smooth_morion"));
		this.stairsBlock(RNBlocks.SMOOTH_MORION_STAIRS.get(), modLoc("block/morion/smooth_morion"));
		this.subfolder("morion/", RNBlocks.QUARTZ_TILES);
		this.subfolder("morion/", RNBlocks.MORION_TILES);
		this.subfolder("morion/", RNBlocks.MOSAIC_QUARTZ);
		this.subfolder("morion/", RNBlocks.MOSAIC_MORION);
		this.flippableCube(RNBlocks.CHECKERED_TILES, "checkered_tiles");
		this.flippableCube(RNBlocks.CHECKERED_MOSAIC, "checkered_mosaic");
		this.slabBlock(RNBlocks.QUARTZ_TILES_SLAB.get(), modLoc("block/quartz_tiles"), modLoc("block/morion/quartz_tiles"));
		this.stairsBlock(RNBlocks.QUARTZ_TILES_STAIRS.get(), modLoc("block/morion/quartz_tiles"));
		this.slabBlock(RNBlocks.MORION_TILES_SLAB.get(), modLoc("block/morion_tiles"), modLoc("block/morion/morion_tiles"));
		this.stairsBlock(RNBlocks.MORION_TILES_STAIRS.get(), modLoc("block/morion/morion_tiles"));
		this.slabBlock(RNBlocks.MOSAIC_QUARTZ_SLAB.get(), modLoc("block/mosaic_quartz"), modLoc("block/morion/mosaic_quartz"));
		this.stairsBlock(RNBlocks.MOSAIC_QUARTZ_STAIRS.get(), modLoc("block/morion/mosaic_quartz"));
		this.slabBlock(RNBlocks.MOSAIC_MORION_SLAB.get(), modLoc("block/mosaic_morion"), modLoc("block/morion/mosaic_morion"));
		this.stairsBlock(RNBlocks.MOSAIC_MORION_STAIRS.get(), modLoc("block/morion/mosaic_morion"));
		this.flippableSlab(RNBlocks.CHECKERED_TILES_SLAB, "checkered_tiles", true);
		this.flippableStairs(RNBlocks.CHECKERED_TILES_STAIRS, "checkered_tiles", true);
		this.flippableSlab(RNBlocks.CHECKERED_MOSAIC_SLAB, "checkered_mosaic", false);
		this.flippableStairs(RNBlocks.CHECKERED_MOSAIC_STAIRS, "checkered_mosaic", false);

		redstoneAccumulator();
		moonphaseDetector();

		glassWithPane(
				RNBlocks.RUBY_GLASS.get(),
				RNBlocks.RUBY_GLASS_PANE.get(),
				"ruby_glass",
				modLoc("block/ruby_glass_pane_top")
		);
		glassWithPane(
				RNBlocks.ORNATE_RUBY_GLASS.get(),
				RNBlocks.ORNATE_RUBY_GLASS_PANE.get(),
				"ornate_ruby_glass",
				modLoc("block/ornate_ruby_glass_pane_top")
		);
		glassWithPane(
				RNBlocks.MOLTEN_RUBY_GLASS.get(),
				RNBlocks.MOLTEN_RUBY_GLASS_PANE.get(),
				"molten_ruby_glass",
				modLoc("block/molten_ruby_glass_pane_top")
		);

		this.frostedGlass(RNBlocks.FROSTED_GLASS.get(), "frosted_glass");
		RNBlocks.STAINED_FROSTED_GLASS.forEach((color, block) -> this.frostedGlass(block.get(), color.getName() + "_frosted_glass"));

		this.subfolder("shrine_stone/", RNBlocks.SHRINE_STONE);
		this.slabBlock(
				RNBlocks.SHRINE_STONE_SLAB.get(),
				modLoc("block/shrine_stone"),
				modLoc("block/shrine_stone/shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_STAIRS.get(),
				modLoc("block/shrine_stone/shrine_stone")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_WALL.get(),
				modLoc("block/shrine_stone/shrine_stone")
		);

		this.subfolder("shrine_stone/", RNBlocks.COBBLED_SHRINE_STONE);
		this.slabBlock(
				RNBlocks.COBBLED_SHRINE_STONE_SLAB.get(),
				modLoc("block/cobbled_shrine_stone"),
				modLoc("block/shrine_stone/cobbled_shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.COBBLED_SHRINE_STONE_STAIRS.get(),
				modLoc("block/shrine_stone/cobbled_shrine_stone")
		);
		this.wallBlock(
				RNBlocks.COBBLED_SHRINE_STONE_WALL.get(),
				modLoc("block/shrine_stone/cobbled_shrine_stone")
		);

		this.subfolder("shrine_stone/", RNBlocks.POLISHED_SHRINE_STONE);
		this.slabBlock(
				RNBlocks.POLISHED_SHRINE_STONE_SLAB.get(),
				modLoc("block/polished_shrine_stone"),
				modLoc("block/shrine_stone/polished_shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get(),
				modLoc("block/shrine_stone/polished_shrine_stone")
		);
		this.wallBlock(
				RNBlocks.POLISHED_SHRINE_STONE_WALL.get(),
				modLoc("block/shrine_stone/polished_shrine_stone")
		);

		this.subfolder("shrine_stone/", RNBlocks.SHRINE_STONE_TILES);
		this.slabBlock(
				RNBlocks.SHRINE_STONE_TILES_SLAB.get(),
				modLoc("block/shrine_stone_tiles"),
				modLoc("block/shrine_stone/shrine_stone_tiles")
		);
		this.slabBlock(
				RNBlocks.SHRINE_STONE_SLAB.get(),
				modLoc("block/shrine_stone"),
				modLoc("block/shrine_stone/shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_TILES_STAIRS.get(),
				modLoc("block/shrine_stone/shrine_stone_tiles")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_TILES_WALL.get(),
				modLoc("block/shrine_stone/shrine_stone_tiles")
		);

		this.subfolder("shrine_stone/", RNBlocks.SHRINE_STONE_RUBBLE);
		this.slabBlock(
				RNBlocks.SHRINE_STONE_RUBBLE_SLAB.get(),
				modLoc("block/shrine_stone_rubble"),
				modLoc("block/shrine_stone/shrine_stone_rubble")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_RUBBLE_STAIRS.get(),
				modLoc("block/shrine_stone/shrine_stone_rubble")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_RUBBLE_WALL.get(),
				modLoc("block/shrine_stone/shrine_stone_rubble")
		);

		this.getVariantBuilder(RNBlocks.SUSPICIOUS_SHRINE_STONE_RUBBLE.get()).forAllStates(state -> {
			int dusted = state.getValue(BlockStateProperties.DUSTED);
			String name = "suspicious_shrine_stone_rubble_" + dusted;
			return ConfiguredModel.builder()
					.modelFile(this.models().cubeAll(name, modLoc("block/shrine_stone/" + name)))
					.build();
		});

		this.shrinePillar(
				RNBlocks.SHRINE_STONE_PILLAR.get(),
				modLoc("block/shrine_stone/shrine_stone_pillar_side"),
				modLoc("block/shrine_stone/shrine_stone_pillar_end")
		);

		this.subfolder("shrine_stone/", RNBlocks.SHRINE_STONE_BRICKS);
		this.slabBlock(
				RNBlocks.SHRINE_STONE_BRICKS_SLAB.get(),
				modLoc("block/shrine_stone_bricks"),
				modLoc("block/shrine_stone/shrine_stone_bricks")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get(),
				modLoc("block/shrine_stone/shrine_stone_bricks")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_BRICKS_WALL.get(),
				modLoc("block/shrine_stone/shrine_stone_bricks")
		);

		this.subfolder("shrine_stone/", RNBlocks.CHISELED_SHRINE_STONE_BRICKS);



		subfolder(
				"bronze/bronze_block/",
				RNBlocks.BRONZE_BLOCK,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,
				RNBlocks.CORRODED_BRONZE_BLOCK,
				RNBlocks.TARNISHED_BRONZE_BLOCK,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK
		);

		subfolder(
				"bronze/chiseled_bronze/",
				RNBlocks.CHISELED_BRONZE,
				RNBlocks.DISCOLORED_CHISELED_BRONZE,
				RNBlocks.CORRODED_CHISELED_BRONZE,
				RNBlocks.TARNISHED_CHISELED_BRONZE,
				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE
		);

		subfolder("bronze/cut_bronze_pillar/",
				(rloc, name, block) -> {
					sixWayPillar(
							block,
							modLoc(rloc + "_side"),
							modLoc(rloc + "_end")
					);
				},
				RNBlocks.CUT_BRONZE_PILLAR,
				RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR,
				RNBlocks.CORRODED_CUT_BRONZE_PILLAR,
				RNBlocks.TARNISHED_CUT_BRONZE_PILLAR,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR
		);

		subfolder("bronze/cut_bronze_bricks/",
				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS
		);

		this.slabBlock(
				RNBlocks.CUT_BRONZE_BRICKS_SLAB.get(),
				modLoc("block/cut_bronze_bricks"),
				modLoc("block/bronze/cut_bronze_bricks/cut_bronze_bricks")
		);
		this.stairsBlock(
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get(),
				modLoc("block/bronze/cut_bronze_bricks/cut_bronze_bricks")
		);
		this.slabBlock(
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get(),
				modLoc("block/discolored_cut_bronze_bricks"),
				modLoc("block/bronze/cut_bronze_bricks/discolored_cut_bronze_bricks")
		);
		this.stairsBlock(
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get(),
				modLoc("block/bronze/cut_bronze_bricks/discolored_cut_bronze_bricks")
		);
		this.slabBlock(
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get(),
				modLoc("block/corroded_cut_bronze_bricks"),
				modLoc("block/bronze/cut_bronze_bricks/corroded_cut_bronze_bricks")
		);
		this.stairsBlock(
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get(),
				modLoc("block/bronze/cut_bronze_bricks/corroded_cut_bronze_bricks")
		);
		this.slabBlock(
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get(),
				modLoc("block/tarnished_cut_bronze_bricks"),
				modLoc("block/bronze/cut_bronze_bricks/tarnished_cut_bronze_bricks")
		);
		this.stairsBlock(
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get(),
				modLoc("block/bronze/cut_bronze_bricks/tarnished_cut_bronze_bricks")
		);
		this.slabBlock(
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get(),
				modLoc("block/crystallized_cut_bronze_bricks"),
				modLoc("block/bronze/cut_bronze_bricks/crystallized_cut_bronze_bricks")
		);
		this.stairsBlock(
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get(),
				modLoc("block/bronze/cut_bronze_bricks/crystallized_cut_bronze_bricks")
		);

		subfolder("bronze/bronze_lantern/",
				(rloc, name, block) -> {
					String texturePath = rloc.substring("block/".length());
					lantern(block.get(), texturePath);
				},
				RNBlocks.BRONZE_LANTERN,
				RNBlocks.DISCOLORED_BRONZE_LANTERN,
				RNBlocks.CORRODED_BRONZE_LANTERN,
				RNBlocks.TARNISHED_BRONZE_LANTERN,
				RNBlocks.CRYSTALLIZED_BRONZE_LANTERN
		);

		subfolder("bronze/bronze_chain/",
				(rloc, name, block) -> {
					chain(block.get(), rloc);
				},
				RNBlocks.BRONZE_CHAIN,
				RNBlocks.DISCOLORED_BRONZE_CHAIN,
				RNBlocks.CORRODED_BRONZE_CHAIN,
				RNBlocks.TARNISHED_BRONZE_CHAIN,
				RNBlocks.CRYSTALLIZED_BRONZE_CHAIN
		);

		generateBronzeLaserFamily("laser");
		generateWinderFamily();
		brazierBlock();

        simpleBlock(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get(), models().getExistingFile(modLoc("block/blight_carpet/up_0_0")));

		subfolder("bronze/bronze_vent/",
				(rloc, name, block) -> ventBlock(block, rloc),
				RNBlocks.BRONZE_VENT,
				RNBlocks.DISCOLORED_BRONZE_VENT,
				RNBlocks.CORRODED_BRONZE_VENT,
				RNBlocks.TARNISHED_BRONZE_VENT,
				RNBlocks.CRYSTALLIZED_BRONZE_VENT
		);

		vaseBlock(RNBlocks.BRONZE_VASE, "block/bronze/bronze_vase/bronze_vase", "block/bronze/bronze_block/bronze_block");
		vaseBlock(RNBlocks.DISCOLORED_BRONZE_VASE, "block/bronze/bronze_vase/discolored_bronze_vase", "block/bronze/bronze_block/discolored_bronze_block");
		vaseBlock(RNBlocks.CORRODED_BRONZE_VASE, "block/bronze/bronze_vase/corroded_bronze_vase", "block/bronze/bronze_block/corroded_bronze_block");
		vaseBlock(RNBlocks.TARNISHED_BRONZE_VASE, "block/bronze/bronze_vase/tarnished_bronze_vase", "block/bronze/bronze_block/tarnished_bronze_block");
		vaseBlock(RNBlocks.CRYSTALLIZED_BRONZE_VASE, "block/bronze/bronze_vase/crystallized_bronze_vase", "block/bronze/bronze_block/crystallized_bronze_block");

	}

	private void redstoneAccumulator() {
		getVariantBuilder(RNBlocks.REDSTONE_ACCUMULATOR.get()).forAllStatesExcept(
				state -> {
					boolean subtract = state.getValue(RedstoneAccumulatorBlock.MODE) == RedstoneAccumulatorBlock.Mode.WRAPPED;
					String model = "block/comparator"
							+ (state.getValue(RedstoneAccumulatorBlock.POWERED) ? "_on" : "")
							+ (subtract ? "_subtract" : "");
					return ConfiguredModel.builder()
							.modelFile(this.models().getExistingFile(mcLoc(model)))
							.rotationY(((int) state.getValue(RedstoneAccumulatorBlock.FACING).toYRot() + 180) % 360)
							.build();
				},
				RedstoneAccumulatorBlock.POWER
		);
	}

	private void moonphaseDetector() {
		ModelFile rising = this.models()
				.withExistingParent("moonphase_detector_rising", mcLoc("block/template_daylight_detector"))
				.texture("top", modLoc("block/moonphase_detector_top"))
				.texture("side", modLoc("block/moonphase_detector_side"));
		ModelFile looping = this.models()
				.withExistingParent("moonphase_detector_looping", mcLoc("block/template_daylight_detector"))
				.texture("top", modLoc("block/moonphase_detector_looping_top"))
				.texture("side", modLoc("block/moonphase_detector_side"));
		getVariantBuilder(RNBlocks.MOONPHASE_DETECTOR.get()).forAllStatesExcept(
				state -> ConfiguredModel.builder()
						.modelFile(state.getValue(MoonphaseDetectorBlock.MODE) == MoonphaseDetectorBlock.Mode.LOOPING ? looping : rising)
						.build(),
				MoonphaseDetectorBlock.POWER
		);
	}

	private void glassWithPane(Block glass, IronBarsBlock pane, String name, ResourceLocation edge) {
		this.simpleBlock(
				glass,
				this.models()
						.cubeAll(name, this.modLoc("block/" + name))
						.renderType(mcLoc("translucent"))
		);
		this.paneBlockWithRenderType(
				pane,
				this.modLoc("block/" + name),
				edge,
				mcLoc("translucent")
		);
	}

	public void shrinePillar(ShrinePillarBlock block, ResourceLocation side, ResourceLocation end) {
		var name = BuiltInRegistries.BLOCK.getKey(block).getPath();
		var vertical = models().cubeColumn(name, side, end);
		var horizontal = models().cubeColumnHorizontal(name + "_horizontal", side, end);

		getVariantBuilder(block).forAllStates(state -> {
			boolean inverted = state.getValue(ShrinePillarBlock.INVERTED);
			return switch (state.getValue(RotatedPillarBlock.AXIS)) {
				case X -> ConfiguredModel.builder()
						.modelFile(horizontal)
						.rotationX(90)
						.rotationY(inverted ? 270 : 90)
						.build();
				case Z -> ConfiguredModel.builder()
						.modelFile(horizontal)
						.rotationX(90)
						.rotationY(inverted ? 180 : 0)
						.build();
				default -> ConfiguredModel.builder()
						.modelFile(vertical)
						.rotationX(inverted ? 180 : 0)
						.build();
			};
		});
	}

	public void sixWayPillar(DeferredBlock<?> block, ResourceLocation side, ResourceLocation end) {
		getVariantBuilder(block.get()).forAllStates((state) -> ConfiguredModel.builder()
				.modelFile(
						models().withExistingParent(
										blockName(block),
										mcLoc("block/cube_column")
								)
								.texture("side", side)
								.texture("end", end)
				)
				.rotationX(switch(state.getValue(SixWayPillarBlock.FACING)) {
					case UP -> 0;
					case DOWN -> 180;
					default -> 90;
				})
				.rotationY(switch(state.getValue(SixWayPillarBlock.FACING)) {
					case NORTH -> 0;
					case SOUTH -> 180;
					case EAST -> 90;
					case WEST -> 270;
					default -> 0;
				})
				.build()
		);
	}


	public void lantern(Block lamp, String name) {
		var location = "block/" + name;

		// Extract just the lantern name from the full path for model naming
		String modelName = name.substring(name.lastIndexOf('/') + 1);

		var lantern = models()
				.withExistingParent(modelName, mcLoc("template_lantern"))
				.texture("lantern", modLoc(location))
				.renderType(mcLoc("cutout"));

		var hangingLantern = models()
				.withExistingParent("hanging_" + modelName, mcLoc("template_hanging_lantern"))
				.texture("lantern", modLoc(location))
				.renderType(mcLoc("cutout"));

		this.getVariantBuilder(lamp).forAllStates((state) -> ConfiguredModel.builder()
				.modelFile(state.getValue(LanternBlock.HANGING) ? hangingLantern : lantern)
				.build()
		);
	}

	public void chain(Block chain, String texturePath) {
		var name = BuiltInRegistries.BLOCK.getKey(chain).getPath();

		var chainModel = models()
				.withExistingParent(name, mcLoc("block/chain"))
				.texture("all", modLoc(texturePath))
				.texture("particle", modLoc(texturePath))
				.renderType(mcLoc("cutout"));

		this.getVariantBuilder(chain).forAllStates((state) -> ConfiguredModel.builder()
				.modelFile(chainModel)
				.rotationX(switch(state.getValue(RotatedPillarBlock.AXIS)) {
					case Y -> 0;
					case Z -> 90;
					case X -> 90;
				})
				.rotationY(switch(state.getValue(RotatedPillarBlock.AXIS)) {
					case Y -> 0;
					case Z -> 0;
					case X -> 90;
				})
				.build()
		);
	}

	public void springBlock(DeferredBlock<?> spring, String texturePath) {
		var name = BuiltInRegistries.BLOCK.getKey(spring.get()).getPath();

		var squishedModel = models().getBuilder(name + "_squished")
				.parent(models().getExistingFile(modLoc("block/bronze_spring_squished_base")))
				.texture("all", modLoc(texturePath))
				.texture("particle", modLoc(texturePath))
				.renderType(mcLoc("cutout"))
				.ao(false);

		var extendedModel = models().getBuilder(name + "_extended")
				.parent(models().getExistingFile(modLoc("block/bronze_spring_extended_base")))
				.texture("all", modLoc(texturePath))
				.texture("particle", modLoc(texturePath))
				.renderType(mcLoc("cutout"))
				.ao(false);

		this.getVariantBuilder(spring.get()).forAllStatesExcept((state) -> {
			boolean extended = state.getValue(BlockStateProperties.EXTENDED);
			Direction facing = state.getValue(BlockStateProperties.FACING);

			int rotationX = switch(facing) {
				case UP -> 0;
				case DOWN -> 180;
				default -> 90;
			};

			int rotationY = switch(facing) {
				case NORTH -> 0;
				case SOUTH -> 180;
				case EAST -> 90;
				case WEST -> 270;
				default -> 0;
			};

			return ConfiguredModel.builder()
					.modelFile(extended ? extendedModel : squishedModel)
					.rotationX(rotationX)
					.rotationY(rotationY)
					.build();
		}, BlockStateProperties.WATERLOGGED);
	}

	public void subfolder(String folder, DeferredBlock<?>... blocks) {
		for (var block : blocks) {
			var name = blockName(block);

			this.simpleBlock(
					block.get(),
					this.models()
							.cubeAll(name, modLoc("block/" + folder + name))
			);
		}
	}

	public void subfolder(
			String folder,
			TriConsumer<String, String, DeferredBlock<?>> fn,
			DeferredBlock<?>... blocks
	) {
		for (var block : blocks) {
			var name = blockName(block);
			fn.accept(
					"block/" + folder + name,
					name,
					block
			);
		}
	}

	private void brazierBlock() {
		for (int level = 0; level <= 9; level++) {
			generateBrazierModel(level);
		}

		getVariantBuilder(RNBlocks.BRAZIER.get())
				.forAllStates(state -> {
					int level = state.getValue(BrazierBlock.LEVEL);
					return ConfiguredModel.builder()
							.modelFile(models().getExistingFile(modLoc("block/ruby_brazier_" + level)))
							.build();
				});
	}

	public void vaseBlock(DeferredBlock<?> block, String vaseTexturePath, String bronzeBlockTexturePath) {
		var name = BuiltInRegistries.BLOCK.getKey(block.get()).getPath();

		var baseModel = models().getBuilder(name + "_base")
				.texture("4", modLoc(vaseTexturePath + "_bottom"))
				.texture("6", modLoc(vaseTexturePath + "_neck"))
				.texture("8", modLoc(bronzeBlockTexturePath))
				.texture("particle", modLoc(vaseTexturePath + "_bottom"))
				.element()
				.from(0, 0, 0).to(16, 16, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.EAST).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.WEST).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.UP).uvs(0, 0, 16, 16).texture("#6").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#8").end()
				.end();

		var lidModel = models().getBuilder(name + "_lid")
				.texture("5", modLoc(vaseTexturePath + "_head"))
				.texture("6", modLoc(vaseTexturePath + "_neck"))
				.texture("7", modLoc(vaseTexturePath + "_top"))
				.texture("particle", modLoc(vaseTexturePath + "_bottom"))
				.element()
				.from(2, 4, 2).to(14, 6, 14)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(0, -4, 0).end()
				.face(Direction.NORTH).uvs(2, 10, 14, 12).texture("#7").end()
				.face(Direction.EAST).uvs(2, 10, 14, 12).texture("#7").end()
				.face(Direction.SOUTH).uvs(2, 10, 14, 12).texture("#7").end()
				.face(Direction.WEST).uvs(2, 10, 14, 12).texture("#7").end()
				.end()
				.element()
				.from(1, 6, 1).to(15, 8, 15)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(0, -2, 0).end()
				.face(Direction.NORTH).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.EAST).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.SOUTH).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.WEST).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).texture("#5").end()
				.face(Direction.DOWN).uvs(1, 1, 15, 15).texture("#6").end()
				.end()
				.element()
				.from(0, 0, 0).to(16, 4, 16)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(0, -16, 0).end()
				.face(Direction.NORTH).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.EAST).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.SOUTH).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.WEST).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.UP).uvs(0, 0, 16, 16).texture("#6").end()
				.end();

		getVariantBuilder(block.get()).forAllStatesExcept(state -> {
			var half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
			return ConfiguredModel.builder()
					.modelFile(half == DoubleBlockHalf.LOWER ? baseModel : lidModel)
					.build();
		}, BlockStateProperties.WATERLOGGED);
	}

	private void generateBrazierModel(int level) {
		String modelName = "ruby_brazier_" + level;

		float surfaceY = 4 + level;

		var builder = models().getBuilder(modelName)
				.parent(models().getExistingFile(mcLoc("block/block")))
				.renderType("cutout")
				.texture("0", mcLoc("block/obsidian"))
				.texture("1", modLoc("block/brazier/brazier_bars_side"))
				.texture("2", modLoc("block/brazier/brazier_bars_top"))
				.texture("3", modLoc("block/altar/null"))
				.texture("6", modLoc("block/brazier/cauldron_bottom"))
				.texture("7", modLoc("block/brazier/cauldron_side"))
				.texture("8", modLoc("block/brazier/brazier_base"))
				.texture("9", modLoc("block/molten_ruby_block_end"))
				.texture("particle", mcLoc("block/obsidian"))

				// Base
				.element()
				.from(0, 0, 0).to(16, 3, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 3).texture("#8").end()
				.face(Direction.EAST).uvs(0, 0, 16, 3).texture("#8").end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 3).texture("#8").end()
				.face(Direction.WEST).uvs(0, 0, 16, 3).texture("#8").end()
				.face(Direction.UP).uvs(0, 0, 16, 16).texture("#8").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#0").end()
				.end()

				// Outer cauldron walls
				.element()
				.from(2, 5, 2).to(14, 16, 14)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(2, 5, 2).end()
				.face(Direction.NORTH).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.EAST).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.SOUTH).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.WEST).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.UP).uvs(0, 0, 12, 12).texture("#3").end()
				.face(Direction.DOWN).uvs(2, 2, 14, 14).texture("#6").end()
				.end()

				// Inner cauldron walls (crossed)
				.element()
				.from(14, 5, 2).to(2, 16, 14)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(14, 5, 2).end()
				.face(Direction.NORTH).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.EAST).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.SOUTH).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.WEST).uvs(2, 3, 14, 14).texture("#7").end()
				.face(Direction.UP).uvs(0, 0, 12, 12).texture("#3").end()
				.face(Direction.DOWN).uvs(2, 2, 14, 14).texture("#6").end()
				.end()

				// Bars - vertical
				.element()
				.from(1, 3, 1).to(15, 11, 15)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(1, 3, 1).end()
				.face(Direction.NORTH).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.EAST).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.SOUTH).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.WEST).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).texture("#2").end()
				.face(Direction.DOWN).uvs(0, 0, 14, 14).texture("#1").end()
				.end()

				// Bars - crossed
				.element()
				.from(15, 3, 1).to(1, 11, 15)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(15, 3, 1).end()
				.face(Direction.NORTH).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.EAST).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.SOUTH).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.WEST).uvs(1, 0, 15, 8).texture("#1").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).texture("#2").end()
				.face(Direction.DOWN).uvs(0, 0, 14, 14).texture("#3").end()
				.end();

		// Only add molten ruby surface if level > 0
		if (level > 0) {
			builder.element()
					.from(2, surfaceY, 2).to(14, surfaceY, 14)
					.rotation().angle(0).axis(Direction.Axis.Y).origin(2, surfaceY, 2).end()
					.face(Direction.UP).uvs(0, 0, 12, 12).texture("#9").emissivity(15, 15).end()
					.face(Direction.DOWN).uvs(0, 0, 12, 12).texture("#9").emissivity(15, 15).end()
					.end();
		}
	}

	// Constants for laser families
	private static final String[] BRONZE_LASER_STATES = {
			"bronze",
			"discolored_bronze",
			"corroded_bronze",
			"tarnished_bronze",
			"crystallized_bronze"
	};

	// ===== BRONZE LASER METHODS =====
	private void generateBronzeLaserFamily(String baseName) {
		for (String state : BRONZE_LASER_STATES) {
			String blockName = state + "_" + baseName.toLowerCase();

			try {
				var field = RNBlocks.class.getField(blockName.toUpperCase());
				DeferredBlock<?> block = (DeferredBlock<?>) field.get(null);

				generateLaserModels(blockName, blockName); // Same name for both model and texture
				generateBronzeLaserBlockStates(block);

			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Could not find RNBlocks." + blockName.toUpperCase(), e);
			}
		}
	}

	private void generateBronzeLaserBlockStates(DeferredBlock<?> laserBlock) {
		String blockName = blockName(laserBlock);
		VariantBlockStateBuilder builder = getVariantBuilder(laserBlock.get());

		// Order: Power (0-15) -> Mode (spectrum, uv, ir) -> Facing (down, east, north, south, up, west)
		for (int power = 0; power <= 15; power++) {
			for (LaserMode mode : LaserMode.values()) {
				for (Direction facing : Direction.values()) {
					String modeStr = mode.getSerializedName();

					// Determine model suffix based on mode and power
					String modelSuffix = "";
					if (!modeStr.equals("spectrum")) {
						modelSuffix += "_" + modeStr;
					}
					if (power > 0) {
						modelSuffix += "_on";
					}

					// Determine rotations based on facing direction
					int rotationX = switch(facing) {
						case UP -> 0;
						case DOWN -> 180;
						default -> 90;
					};

					int rotationY = switch(facing) {
						case NORTH -> 0;
						case SOUTH -> 180;
						case EAST -> 90;
						case WEST -> 270;
						default -> 0;
					};

					builder.partialState()
							.with(BlockStateProperties.FACING, facing)
							.with(BronzeLaserBlock.MODE, mode)
							.with(BronzeLaserBlock.POWER, power)
							.modelForState()
							.modelFile(models().getExistingFile(modLoc("block/" + blockName + modelSuffix)))
							.rotationX(rotationX)
							.rotationY(rotationY)
							.addModel();
				}
			}
		}
	}

	private static final String[] WINDER_NAMES = {
			"bronze_winder",
			"discolored_bronze_winder",
			"corroded_bronze_winder",
			"tarnished_bronze_winder",
			"crystallized_bronze_winder"
	};

	private static final String[] WINDER_BLOCK_TEXTURES = {
			"bronze_block",
			"discolored_bronze_block",
			"corroded_bronze_block",
			"tarnished_bronze_block",
			"crystallized_bronze_block"
	};

	private static final String[] WINDER_KEY_TEXTURES = {
			"bronze",
			"bronze_discolored",
			"bronze_corroded",
			"bronze_tarnished",
			"bronze_crystallized"
	};

	private static final int[][] WINDER_STEM_UVS = {
			{51, 26, 53, 31},
			{49, 26, 51, 31},
			{55, 26, 57, 31},
			{53, 26, 55, 31}
	};

	private static final Direction[] WINDER_SIDES = {
			Direction.NORTH,
			Direction.EAST,
			Direction.SOUTH,
			Direction.WEST
	};

	private void generateWinderFamily() {
		for (int i = 0; i < WINDER_NAMES.length; i++) {
			String name = WINDER_NAMES[i];
			String blockTexture = WINDER_BLOCK_TEXTURES[i];
			String keyTexture = "entity/bronze/" + WINDER_KEY_TEXTURES[i];

			try {
				var field = RNBlocks.class.getField(name.toUpperCase());
				DeferredBlock<?> block = (DeferredBlock<?>) field.get(null);

				winderBaseModel(models().getBuilder(name + "_base"), blockTexture);
				winderBaseModel(models().getBuilder(name + "_base_on"), blockTexture);
				for (int position = 0; position < 16; position++) {
					BlockModelBuilder keyModel = models().getBuilder(name + "_key_" + position)
							.parent(models().getExistingFile(mcLoc("block/block")))
							.texture("key", modLoc(keyTexture))
							.renderType("cutout");
					winderKeyElements(keyModel, position);
				}

				BlockModelBuilder itemModel = models().getBuilder(name + "_item")
						.texture("key", modLoc(keyTexture));
				winderBaseModel(itemModel, blockTexture);
				winderKeyElements(itemModel, 0);

				generateWinderBlockStates(block, name);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Could not find RNBlocks." + name.toUpperCase(), e);
			}
		}
	}

	private void winderBaseModel(BlockModelBuilder model, String blockTexture) {
		String block = "block/bronze/bronze_block/" + blockTexture;
		model.parent(models().getExistingFile(mcLoc("block/block")))
				.guiLight(BlockModel.GuiLight.SIDE)
				.texture("top", modLoc(block))
				.texture("under", modLoc(block))
				.texture("side", modLoc(block))
				.texture("particle", modLoc(block))
				.renderType("cutout")
				.element()
				.from(4, 0, 4).to(12, 4, 12)
				.face(Direction.NORTH).uvs(4, 6, 12, 10).texture("#side").end()
				.face(Direction.EAST).uvs(4, 6, 12, 10).texture("#side").end()
				.face(Direction.SOUTH).uvs(4, 6, 12, 10).texture("#side").end()
				.face(Direction.WEST).uvs(4, 6, 12, 10).texture("#side").end()
				.face(Direction.UP).uvs(4, 4, 12, 12).texture("#top").end()
				.face(Direction.DOWN).uvs(4, 4, 12, 12).texture("#under").end()
				.end();
	}

	private void winderKeyElements(BlockModelBuilder model, int position) {
		float angle = position * 22.5F;
		int quarter = Math.round(angle / 90.0F);
		float residual = angle - quarter * 90.0F;
		quarter %= 4;
		float elementAngle = 0.0F - residual;

		var stem = model.element()
				.from(7, 4, 7).to(9, 9, 9);
		stem.rotation().angle(elementAngle).axis(Direction.Axis.Y).origin(8, 4, 8).end();
		for (int side = 0; side < 4; side++) {
			int[] uv = WINDER_STEM_UVS[Math.floorMod(side - quarter, 4)];
			stem.face(WINDER_SIDES[side]).uvs(uv[0] / 4.0F, uv[1] / 4.0F, uv[2] / 4.0F, uv[3] / 4.0F).texture("#key").end();
		}
		stem.face(Direction.UP).uvs(51 / 4.0F, 24 / 4.0F, 53 / 4.0F, 26 / 4.0F).texture("#key").end();
		stem.end();

		boolean alongX = quarter % 2 == 0;
		var plane = model.element();
		if (alongX) {
			plane.from(3, 6, 8).to(13, 10, 8);
		} else {
			plane.from(8, 6, 3).to(8, 10, 13);
		}
		plane.rotation().angle(elementAngle).axis(Direction.Axis.Y).origin(8, 4, 8).end();
		Direction first = alongX ? Direction.NORTH : Direction.EAST;
		Direction second = alongX ? Direction.SOUTH : Direction.WEST;
		plane.face(first).uvs(49 / 4.0F, 14 / 4.0F, 59 / 4.0F, 18 / 4.0F).texture("#key").end();
		plane.face(second).uvs(49 / 4.0F, 14 / 4.0F, 59 / 4.0F, 18 / 4.0F).texture("#key").end();
		plane.end();
	}

	private void generateWinderBlockStates(DeferredBlock<?> block, String name) {
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block.get());
		ModelFile base = models().getExistingFile(modLoc("block/" + name + "_base"));
		ModelFile baseOn = models().getExistingFile(modLoc("block/" + name + "_base_on"));
		Integer[] poweredLevels = new Integer[15];
		for (int power = 1; power <= 15; power++) {
			poweredLevels[power - 1] = power;
		}

		for (Direction facing : Direction.values()) {
			int rotationX = switch (facing) {
				case UP -> 0;
				case DOWN -> 180;
				default -> 90;
			};

			int rotationY = switch (facing) {
				case NORTH -> 0;
				case SOUTH -> 180;
				case EAST -> 90;
				case WEST -> 270;
				default -> 0;
			};

			builder.part().modelFile(base).rotationX(rotationX).rotationY(rotationY).addModel()
					.condition(BlockStateProperties.FACING, facing)
					.condition(BlockStateProperties.POWER, 0)
					.end();

			builder.part().modelFile(baseOn).rotationX(rotationX).rotationY(rotationY).addModel()
					.condition(BlockStateProperties.FACING, facing)
					.condition(BlockStateProperties.POWER, poweredLevels)
					.end();

			for (int power = 0; power < 16; power++) {
				ModelFile key = models().getExistingFile(modLoc("block/" + name + "_key_" + power));
				builder.part().modelFile(key).rotationX(rotationX).rotationY(rotationY).addModel()
						.condition(BlockStateProperties.FACING, facing)
						.condition(BlockStateProperties.POWER, power)
						.end();
			}
		}
	}

	// ===== SHARED LASER MODEL METHODS =====
	private void generateLaserModels(String modelBlockName, String textureBlockName) {
		// Define all variants with their texture patterns
		var variants = new Object[][] {
				{"", "laser_lens", textureBlockName + "_front", textureBlockName + "_front", null}, // base variant
				{"_ir", "laser_lens_ir", textureBlockName + "_front", textureBlockName + "_front", null},
				{"_ir_on", "laser_lens_ir_on", textureBlockName + "_front_on", textureBlockName + "_front", "laser_lens_ir_on_e"},
				{"_on", "laser_lens_on", textureBlockName + "_front_on", textureBlockName + "_front", "laser_lens_on_e"},
				{"_uv", "laser_lens_uv", textureBlockName + "_front", textureBlockName + "_front", null},
				{"_uv_on", "laser_lens_uv_on", textureBlockName + "_front_on", textureBlockName + "_front", "laser_lens_uv_on_e"}
		};

		// Generate each variant
		for (var variant : variants) {
			String suffix = (String) variant[0];
			String lensTexture = (String) variant[1];
			String frontTexture = (String) variant[2];
			String particleTexture = (String) variant[3];
			String lensGlowTexture = (String) variant[4]; // null unless the laser is observing

			createLaserModel(modelBlockName + suffix, lensTexture, frontTexture, textureBlockName + "_side", particleTexture, lensGlowTexture);
		}
	}

	private void createLaserModel(String modelName, String lensTexture, String frontTexture, String sideTexture, String particleTexture, String lensGlowTexture) {
		BlockModelBuilder model = models().getBuilder(modelName)
				.parent(models().getExistingFile(mcLoc("block/block")))
				.guiLight(BlockModel.GuiLight.SIDE)
				.texture("0", modLoc("block/bronze/bronze_laser/mode/" + lensTexture))
				.texture("2", modLoc("block/bronze/bronze_laser/material/" + frontTexture))
				.texture("3", modLoc("block/bronze/bronze_laser/material/" + sideTexture))
				.texture("particle", modLoc("block/bronze/bronze_laser/material/" + particleTexture))
				.renderType("cutout")
				// Element 1: Base
				.element()
				.from(0, 0, 0).to(16, 6, 16)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(8, 8, 8).end()
				.face(Direction.NORTH).uvs(0, 0, 6, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#3").end()
				.face(Direction.EAST).uvs(0, 0, 6, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#3").end()
				.face(Direction.SOUTH).uvs(0, 0, 6, 16).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.WEST).uvs(0, 0, 6, 16).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.UP).uvs(0, 0, 16, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#2").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#2").end()
				.end()
				// Element 2: Inner ring
				.element()
				.from(2, 7, 2).to(14, 14, 14)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(8, 8, 8).end()
				.face(Direction.NORTH).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.EAST).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.SOUTH).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.WEST).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.end()
				// Element 3: Crossed element (note the inverted from/to coordinates)
				.element()
				.from(14, 7, 2).to(2, 14, 14)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(20, 8, 31).end()
				.face(Direction.NORTH).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.EAST).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.SOUTH).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.face(Direction.WEST).uvs(7, 2, 14, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#3").end()
				.end()
				// Element 4: Lens/crystal top
				.element()
				.from(3, 6, 3).to(13, 16, 13)
				.rotation().angle(0).axis(Direction.Axis.Y).origin(8, 8, 8).end()
				.face(Direction.NORTH).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).texture("#0").end()
				.face(Direction.EAST).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#0").end()
				.face(Direction.SOUTH).uvs(3, 3, 13, 13).texture("#0").end()
				.face(Direction.WEST).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#0").end()
				.face(Direction.UP).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).texture("#0").end()
				.face(Direction.DOWN).uvs(3, 3, 13, 13).texture("#0").end()
				.end();

		// Element 5: emissive lens overlay, only while observing (same box as the lens, full-bright faces)
		if (lensGlowTexture != null) {
			model.texture("1", modLoc("block/bronze/bronze_laser/mode/" + lensGlowTexture));
			model.element()
					.from(3, 6, 3).to(13, 16, 13)
					.rotation().angle(0).axis(Direction.Axis.Y).origin(8, 8, 8).end()
					.face(Direction.NORTH).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).texture("#1").emissivity(15, 15).end()
					.face(Direction.EAST).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#1").emissivity(15, 15).end()
					.face(Direction.SOUTH).uvs(3, 3, 13, 13).texture("#1").emissivity(15, 15).end()
					.face(Direction.WEST).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#1").emissivity(15, 15).end()
					.face(Direction.UP).uvs(3, 3, 13, 13).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).texture("#1").emissivity(15, 15).end()
					.face(Direction.DOWN).uvs(3, 3, 13, 13).texture("#1").emissivity(15, 15).end()
					.end();
		}
	}



	private void generateChandelierModel(String variantName) {
		// Extract the bronze state prefix (e.g., "bronze", "discolored_bronze", etc.)
		String bronzeState = variantName.replace("_chandelier", "");

		models().getBuilder(variantName)
        .parent(models().getExistingFile(mcLoc("block/block")))
        .renderType("cutout")
        .texture("0", modLoc("block/bronze/bronze_chandelier/chandelier_light"))  // Constant light texture
        .texture("1", modLoc("block/bronze/bronze_chandelier/" + variantName + "_frame"))  // Variant-specific frame
        .texture("2", modLoc("block/bronze/bronze_chandelier/" + variantName + "_frame_top"))  // Variant-specific frame top
        .texture("3", modLoc("block/bronze/bronze_chandelier/" + variantName + "_chain"))  // Variant-specific chain
        .texture("particle", modLoc("block/bronze/bronze_chandelier/" + variantName + "_frame"))

        // Frame NS1
        .element()
        .from(2, -2, -8).to(2, 14, 24)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(2, 0, -8).end()
        .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#1").end()
        .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#1").end()
        .end()

        // Frame NS2
        .element()
        .from(14, -2, -8).to(14, 14, 24)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(14, 0, -8).end()
        .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#1").end()
        .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#1").end()
        .end()

        // Frame EW1
        .element()
        .from(-8, -2, 2).to(24, 14, 2)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(-8, 0, 2).end()
        .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#1").end()
        .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#1").end()
        .end()

        // Frame EW2
        .element()
        .from(-8, -2, 14).to(24, 14, 14)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(-8, 0, 14).end()
        .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#1").end()
        .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#1").end()
        .end()

        // Light 1
        .element()
        .from(-9.1f, 10.9f, -0.1f).to(-4.9f, 17.1f, 4.1f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(-9, 12, 0).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).texture("#0").end()
        .end()

        // Light 2
        .element()
        .from(-9.1f, 10.9f, 11.9f).to(-4.9f, 17.1f, 16.1f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(-9, 12, 12).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).texture("#0").end()
        .end()

        // Light 3
        .element()
        .from(20.9f, 10.9f, -0.1f).to(25.1f, 17.1f, 4.1f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(21, 12, 0).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).texture("#0").end()
        .end()

        // Light 4
        .element()
        .from(20.9f, 10.9f, 11.9f).to(25.1f, 17.1f, 16.1f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(21, 12, 12).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).texture("#0").end()
        .end()

        // Light 5
        .element()
        .from(11.9f, 10.9f, -9.1f).to(16.1f, 17.1f, -4.9f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(8, 12, 9).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#0").end()
        .end()

        // Light 6
        .element()
        .from(-0.1f, 10.9f, -9.1f).to(4.1f, 17.1f, -4.9f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(8, 12, 9).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 7, 10, 11).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#0").end()
        .end()

        // Light 7
        .element()
        .from(11.9f, 10.9f, 20.9f).to(16.1f, 17.1f, 25.1f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(8, 12, 7).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#0").end()
        .end()

        // Light 8
        .element()
        .from(-0.1f, 10.9f, 20.9f).to(4.1f, 17.1f, 25.1f)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(8, 12, 7).end()
        .face(Direction.NORTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.EAST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.SOUTH).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.WEST).uvs(6, 5, 10, 11).texture("#0").end()
        .face(Direction.UP).uvs(6, 5, 10, 9).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#0").end()
        .face(Direction.DOWN).uvs(6, 7, 10, 11).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).texture("#0").end()
        .end()

        // Frame center top/bottom
        .element()
        .from(2, -2, 2).to(14, 10, 14)
        .rotation().angle(0).axis(Direction.Axis.Y).origin(2, 0, 2).end()
        .face(Direction.UP).uvs(2, 2, 14, 14).texture("#2").end()
        .face(Direction.DOWN).uvs(2, 2, 14, 14).texture("#2").end()
        .end()

        // Chain 1
        .element()
        .from(8, 10, 0).to(8, 16, 16)
        .rotation().angle(45).axis(Direction.Axis.Y).origin(8, 10, 8).end()
        .face(Direction.EAST).uvs(0, 5, 16, 11).texture("#3").end()
        .face(Direction.WEST).uvs(0, 5, 16, 11).texture("#3").end()
        .end()

        // Chain 2
        .element()
        .from(8, 10, 0).to(8, 16, 16)
        .rotation().angle(-45).axis(Direction.Axis.Y).origin(8, 10, 8).end()
        .face(Direction.EAST).uvs(0, 5, 16, 11).texture("#3").end()
        .face(Direction.WEST).uvs(0, 5, 16, 11).texture("#3").end()
        .end();
	}

	public void ventBlock(DeferredBlock<?> block, String texturePath) {
		var name = BuiltInRegistries.BLOCK.getKey(block.get()).getPath();

		var model = models().getBuilder(name)
				.parent(models().getExistingFile(mcLoc("block/block")))
				.texture("front", modLoc(texturePath + "_front"))
				.texture("back", modLoc(texturePath + "_back"))
				.texture("side", modLoc(texturePath + "_side"))
				.texture("particle", modLoc(texturePath + "_front"))
				.element()
				.from(0, 0, 0).to(16, 16, 16)
				.face(Direction.UP).uvs(0, 0, 16, 16).texture("#front").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#back").end()
				.face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#side").end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#side").end()
				.face(Direction.EAST).uvs(0, 0, 16, 16).texture("#side").end()
				.face(Direction.WEST).uvs(0, 0, 16, 16).texture("#side").end()
				.end();

		getVariantBuilder(block.get()).forAllStates(state -> {
			Direction facing = state.getValue(BlockStateProperties.FACING);

			int rotationX = switch (facing) {
				case UP -> 0;
				case DOWN -> 180;
				default -> 90;
			};

			int rotationY = switch (facing) {
				case NORTH -> 0;
				case SOUTH -> 180;
				case EAST -> 90;
				case WEST -> 270;
				default -> 0;
			};

			return ConfiguredModel.builder()
					.modelFile(model)
					.rotationX(rotationX)
					.rotationY(rotationY)
					.build();
		});
	}


	private void frostedGlass(Block block, String name) {
		this.models()
				.withExistingParent(name, mcLoc("block/block"))
				.texture("particle", modLoc("block/frosted_glass/" + name))
				.texture("glass", modLoc("block/frosted_glass/" + name))
				.texture("frost", modLoc("block/frosted_glass/" + name + "_frosted"))
				.renderType(mcLoc("translucent"))
				.element()
				.from(0, 0, 0)
				.to(16, 16, 16)
				.allFaces((direction, face) -> face
						.texture(direction == Direction.NORTH ? "#frost" : "#glass")
						.cullface(direction))
				.end();
		ModelFile glassFace = this.faceModel(name + "_face_glass", "block/frosted_glass/" + name);
		ModelFile frostFace = this.faceModel(name + "_face_frost", "block/frosted_glass/" + name + "_frosted");
		MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block);
		for (Direction direction : Direction.values()) {
			BooleanProperty property = FrostedGlassBlock.property(direction);
			int x = direction == Direction.UP ? 270 : direction == Direction.DOWN ? 90 : 0;
			int y = switch (direction) {
				case EAST -> 90;
				case SOUTH -> 180;
				case WEST -> 270;
				default -> 0;
			};
			builder.part().modelFile(glassFace).rotationX(x).rotationY(y).addModel()
					.condition(FrostedGlassBlock.POWERED, false)
					.condition(property, false)
					.end();
			builder.part().modelFile(frostFace).rotationX(x).rotationY(y).addModel()
					.useOr()
					.condition(FrostedGlassBlock.POWERED, true)
					.condition(property, true)
					.end();
		}
	}

	private ModelFile faceModel(String name, String texture) {
		return this.models()
				.getBuilder(name)
				.texture("particle", modLoc(texture))
				.texture("all", modLoc(texture))
				.renderType(mcLoc("translucent"))
				.element()
				.from(0, 0, 0)
				.to(16, 16, 16)
				.face(Direction.NORTH)
				.texture("#all")
				.cullface(Direction.NORTH)
				.end()
				.end();
	}

	private String blockName(DeferredBlock<?> block) {
		return block.getId().toString().split(":")[1];
	}

	private static String flippedSuffix(boolean flipped) {
		return flipped ? "_flipped" : "";
	}

	private ResourceLocation flippableTexture(String base, boolean flipped) {
		return modLoc("block/morion/" + base + flippedSuffix(flipped));
	}

	private static boolean checkerFaceSwapped(Direction face, SlabType type, boolean coarse) {
		return switch (face) {
			case NORTH, SOUTH -> true;
			case UP -> coarse && type == SlabType.BOTTOM;
			case DOWN -> coarse && type == SlabType.TOP;
			default -> false;
		};
	}

	private ResourceLocation checkerFaceTexture(String base, boolean flipped, Direction face, SlabType type, boolean coarse) {
		return flippableTexture(base, flipped ^ checkerFaceSwapped(face, type, coarse));
	}

	private BlockModelBuilder checkerCube(String name, String base, boolean flipped) {
		return this.models().cube(
				name,
				checkerFaceTexture(base, flipped, Direction.DOWN, SlabType.DOUBLE, false),
				checkerFaceTexture(base, flipped, Direction.UP, SlabType.DOUBLE, false),
				checkerFaceTexture(base, flipped, Direction.NORTH, SlabType.DOUBLE, false),
				checkerFaceTexture(base, flipped, Direction.SOUTH, SlabType.DOUBLE, false),
				checkerFaceTexture(base, flipped, Direction.EAST, SlabType.DOUBLE, false),
				checkerFaceTexture(base, flipped, Direction.WEST, SlabType.DOUBLE, false)
		).texture("particle", checkerFaceTexture(base, flipped, Direction.NORTH, SlabType.DOUBLE, false));
	}

	private BlockModelBuilder checkerSlab(String name, String base, boolean flipped, SlabType type, boolean coarse) {
		BlockModelBuilder model = this.models().withExistingParent(name, mcLoc("block/block"));
		for (Direction face : Direction.values()) {
			model.texture(face.getName(), checkerFaceTexture(base, flipped, face, type, coarse));
		}
		model.texture("particle", checkerFaceTexture(base, flipped, Direction.NORTH, type, coarse));
		boolean top = type == SlabType.TOP;
		float y0 = top ? 8 : 0;
		ModelBuilder<BlockModelBuilder>.ElementBuilder element = model.element().from(0, y0, 0).to(16, y0 + 8, 16);
		for (Direction face : Direction.values()) {
			boolean vertical = face.getAxis().isVertical();
			float v0 = vertical ? 0 : top ? 0 : 8;
			float v1 = vertical ? 16 : top ? 8 : 16;
			boolean cull = !vertical || face == (top ? Direction.UP : Direction.DOWN);
			var faceBuilder = element.face(face).uvs(0, v0, 16, v1).texture("#" + face.getName());
			if (cull) {
				faceBuilder.cullface(face);
			}
			faceBuilder.end();
		}
		return element.end();
	}

	private void flippableCube(DeferredBlock<? extends Block> block, String base) {
		ModelFile normal = checkerCube(base, base, false);
		ModelFile flipped = checkerCube(base + "_flipped", base, true);
		this.getVariantBuilder(block.get()).forAllStates(
				state -> ConfiguredModel.builder()
						.modelFile(state.getValue(RNBlockStateProperties.FLIPPED) ? flipped : normal)
						.build()
		);
	}

	private void flippableSlab(DeferredBlock<? extends SlabBlock> block, String base, boolean coarse) {
		String name = base + "_slab";
		ModelFile[] bottom = new ModelFile[2];
		ModelFile[] top = new ModelFile[2];
		ModelFile[] full = new ModelFile[2];
		for (int i = 0; i < 2; i++) {
			boolean flipped = i == 1;
			bottom[i] = checkerSlab(name + flippedSuffix(flipped), base, flipped, SlabType.BOTTOM, coarse);
			top[i] = checkerSlab(name + "_top" + flippedSuffix(flipped), base, flipped, SlabType.TOP, coarse);
			full[i] = this.models().getExistingFile(modLoc("block/" + base + flippedSuffix(flipped)));
		}
		this.getVariantBuilder(block.get()).forAllStatesExcept(
				state -> {
					int i = state.getValue(RNBlockStateProperties.FLIPPED) ? 1 : 0;
					SlabType type = state.getValue(SlabBlock.TYPE);
					ModelFile model = type == SlabType.BOTTOM ? bottom[i] : type == SlabType.TOP ? top[i] : full[i];
					return ConfiguredModel.builder().modelFile(model).build();
				},
				SlabBlock.WATERLOGGED
		);
	}

	private BlockModelBuilder checkerStairs(String name, String base, boolean flipped, String shape, int xRot, int yRot, boolean coarse) {
		String parent = switch (shape) {
			case "inner" -> "inner_stairs";
			case "outer" -> "outer_stairs";
			default -> "stairs";
		};
		BlockModelBuilder model = this.models().withExistingParent(name, mcLoc("block/" + parent))
				.texture("normal", flippableTexture(base, false))
				.texture("flipped", flippableTexture(base, true))
				.texture("particle", flippableTexture(base, false));
		for (CheckerStairLayout.Part part : CheckerStairLayout.parts(shape, xRot, yRot, coarse, flipped)) {
			ModelBuilder<BlockModelBuilder>.ElementBuilder element = model.element()
					.from(part.from()[0], part.from()[1], part.from()[2])
					.to(part.to()[0], part.to()[1], part.to()[2]);
			for (CheckerStairLayout.Face face : part.faces()) {
				Direction direction = Direction.byName(face.direction());
				var faceBuilder = element.face(direction)
						.uvs(face.u1(), face.v1(), face.u2(), face.v2())
						.texture(face.flipped() ? "#flipped" : "#normal");
				if (face.cull()) {
					faceBuilder.cullface(direction);
				}
				faceBuilder.end();
			}
			element.end();
		}
		return model;
	}

	private void flippableStairs(DeferredBlock<? extends StairBlock> block, String base, boolean coarse) {
		String name = base + "_stairs";
		Map<String, ModelFile> models = new HashMap<>();
		this.getVariantBuilder(block.get()).forAllStatesExcept(
				state -> {
					boolean flipped = state.getValue(RNBlockStateProperties.FLIPPED);
					Direction facing = state.getValue(StairBlock.FACING);
					Half half = state.getValue(StairBlock.HALF);
					StairsShape shape = state.getValue(StairBlock.SHAPE);
					int yRot = (int) facing.getClockWise().toYRot();
					if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
						yRot += 270;
					}
					if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
						yRot += 90;
					}
					int rotY = yRot % 360;
					int xRot = half == Half.BOTTOM ? 0 : 180;
					String kind = shape == StairsShape.STRAIGHT
							? "straight"
							: shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? "inner" : "outer";
					String modelName = name + (kind.equals("straight") ? "" : "_" + kind)
							+ (xRot == 0 && rotY == 0 ? "" : "_x" + xRot + "_y" + rotY)
							+ flippedSuffix(flipped);
					ModelFile model = models.computeIfAbsent(
							modelName,
							key -> checkerStairs(key, base, flipped, kind, xRot, rotY, coarse)
					);
					return ConfiguredModel.builder()
							.modelFile(model)
							.rotationX(xRot)
							.rotationY(rotY)
							.build();
				},
				StairBlock.WATERLOGGED
		);
	}

	private ConfiguredModel[] randomRotations(ModelFile model) {
		ConfiguredModel[] models = new ConfiguredModel[16];
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				models[x * 4 + y] = new ConfiguredModel(model, x * 90, y * 90, false);
			}
		}
		return models;
	}
}
