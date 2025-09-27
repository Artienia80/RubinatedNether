package corundum.rubinated_nether.data;

import corundum.rubinated_nether.content.blocks.BronzeLaserBlock;
import corundum.rubinated_nether.content.blocks.CopperLaserBlock;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import org.apache.commons.lang3.function.TriConsumer;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.blocks.SixWayPillarBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

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
				RNBlocks.BRAZIER.get(),
				this.models()
						.withExistingParent("ruby_brazier", this.modLoc("block/ruby_brazier_base"))
		);

		this.simpleBlock(
				RNBlocks.RUBINATION_ALTAR.get(),
				this.models()
						.withExistingParent("rubination_altar", this.modLoc("block/rubination_altar_base"))
		);


		this.simpleBlock(
				RNBlocks.BRONZE_CHANDELIER.get(),
				this.models()
						.withExistingParent("chandelier", this.modLoc("block/ruby_chandelier_base"))
		);
		this.simpleBlock(
				RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get(),
				this.models()
						.withExistingParent("discolored_chandelier", this.modLoc("block/ruby_chandelier_base"))
		);
		this.simpleBlock(
				RNBlocks.CORRODED_BRONZE_CHANDELIER.get(),
				this.models()
						.withExistingParent("corroded_chandelier", this.modLoc("block/ruby_chandelier_base"))
		);
		this.simpleBlock(
				RNBlocks.TARNISHED_BRONZE_CHANDELIER.get(),
				this.models()
						.withExistingParent("tarnished_chandelier", this.modLoc("block/ruby_chandelier_base"))
		);
		this.simpleBlock(
				RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get(),
				this.models()
						.withExistingParent("crystallized_chandelier", this.modLoc("block/ruby_chandelier_base"))
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

		this.simpleBlock(
				RNBlocks.DRY_ICE.get(),
				this.models()
						.withExistingParent("dry_ice", this.modLoc("block/dry_ice_base"))
		);
		this.simpleBlock(RNBlocks.SOAKSTONE.get());

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

		this.simpleBlock(RNBlocks.SHRINE_STONE.get());
		this.simpleBlock(RNBlocks.POLISHED_SHRINE_STONE.get());
		this.slabBlock(
				RNBlocks.POLISHED_SHRINE_STONE_SLAB.get(),
				modLoc("block/polished_shrine_stone"),
				modLoc("block/polished_shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get(),
				modLoc("block/polished_shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_STAIRS.get(),
				modLoc("block/shrine_stone")
		);
		this.wallBlock(
				RNBlocks.POLISHED_SHRINE_STONE_WALL.get(),
				modLoc("block/polished_shrine_stone")
		);

		this.simpleBlock(RNBlocks.SHRINE_STONE_TILES.get());
		this.simpleBlock(RNBlocks.RUBINATED_SHRINE_STONE_TILES.get());
		this.simpleBlock(RNBlocks.SHRINE_STONE_COFFER.get());

		this.slabBlock(
				RNBlocks.SHRINE_STONE_TILES_SLAB.get(),
				modLoc("block/shrine_stone_tiles"),
				modLoc("block/shrine_stone_tiles")
		);
		this.slabBlock(
				RNBlocks.SHRINE_STONE_SLAB.get(),
				modLoc("block/shrine_stone"),
				modLoc("block/shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_TILES_STAIRS.get(),
				modLoc("block/shrine_stone_tiles")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_TILES_WALL.get(),
				modLoc("block/shrine_stone_tiles")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_WALL.get(),
				modLoc("block/shrine_stone")
		);

		this.axisBlock(RNBlocks.SHRINE_STONE_PILLAR.get());
		this.axisBlock(RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get());

		this.simpleBlock(RNBlocks.SHRINE_STONE_BRICKS.get());
		this.slabBlock(
				RNBlocks.SHRINE_STONE_BRICKS_SLAB.get(),
				modLoc("block/shrine_stone_bricks"),
				modLoc("block/shrine_stone_bricks")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get(),
				modLoc("block/shrine_stone_bricks")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_BRICKS_WALL.get(),
				modLoc("block/shrine_stone_bricks")
		);

		this.simpleBlock(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get());
		this.simpleBlock(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get());
		sixWayPillar(
				RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS,
				modLoc("block/" + blockName(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS) + "_side"),
				modLoc("block/" + blockName(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS) + "_end")
		);


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

		generateLaserFamily("laser");
		generateCopperLaserFamily("laser");
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

		var lantern = models()
				.withExistingParent(name, mcLoc("template_lantern"))
				.texture("lantern", modLoc(location))
				.renderType(mcLoc("cutout"));

		var hangingLantern = models()
				.withExistingParent("hanging_" + name, mcLoc("template_hanging_lantern"))
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
				.texture("particle", modLoc(texturePath));

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

	private static final String[] LASER_STATES = {
			"bronze",
			"discolored_bronze",
			"corroded_bronze",
			"tarnished_bronze",
			"crystallized_bronze"
	};

	private static final String[] COPPER_LASER_STATES = {
			"copper",
			"exposed_copper",
			"weathered_copper",
			"oxidized_copper",
			"waxed_copper",
			"waxed_exposed_copper",
			"waxed_weathered_copper",
			"waxed_oxidized_copper"
	};

	private void generateLaserFamily(String baseName) {
		for (String state : LASER_STATES) {
			// Build the full block name, e.g. "bronze_laser", "discolored_bronze_laser"
			String blockName = state + "_" + baseName.toLowerCase();

			// Look up the corresponding DeferredBlock field from RNBlocks
			try {
				var field = RNBlocks.class.getField(blockName.toUpperCase());
				DeferredBlock<?> block = (DeferredBlock<?>) field.get(null);

				// Generate models + blockstates
				generateLaserModels(blockName);
				generateLaserBlockStates(block);

			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Could not find RNBlocks." + blockName.toUpperCase(), e);
			}
		}
	}

	private void generateCopperLaserFamily(String baseName) {
		for (String state : COPPER_LASER_STATES) {
			// Build the full block name, e.g. "copper_laser", "exposed_copper_laser"
			String blockName = state + "_" + baseName.toLowerCase();

			// Look up the corresponding DeferredBlock field from RNBlocks
			try {
				var field = RNBlocks.class.getField(blockName.toUpperCase());
				DeferredBlock<?> block = (DeferredBlock<?>) field.get(null);

				// Remove waxed_ prefix for texture paths since waxed blocks use unwaxed textures
				String textureBlockName = blockName;
				if (state.startsWith("waxed_")) {
					textureBlockName = blockName.replace("waxed_", "");
				}

				// Generate models + blockstates (reuse the same method, just different texture names)
				generateLaserModels(textureBlockName);
				generateCopperLaserBlockStates(block);

			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Could not find RNBlocks." + blockName.toUpperCase(), e);
			}
		}
	}

	private void generateLaserModels(String blockName) {
		// Define all variants with their texture patterns
		var variants = new Object[][] {
				{"", "laser_lens", blockName + "_front", blockName + "_front"}, // base variant
				{"_ir", "laser_lens_ir", blockName + "_front", blockName + "_front"},
				{"_ir_on", "laser_lens_ir_on", blockName + "_front_on", blockName + "_front"},
				{"_on", "laser_lens_on", blockName + "_front_on", blockName + "_front"},
				{"_uv", "laser_lens_uv", blockName + "_front", blockName + "_front"},
				{"_uv_on", "laser_lens_uv_on", blockName + "_front_on", blockName + "_front"}
		};

		// Generate each variant
		for (var variant : variants) {
			String suffix = (String) variant[0];
			String lensTexture = (String) variant[1];
			String frontTexture = (String) variant[2];
			String particleTexture = (String) variant[3];

			createLaserModel(blockName + suffix, lensTexture, frontTexture, blockName + "_side", particleTexture);
		}
	}

	private void createLaserModel(String modelName, String lensTexture, String frontTexture, String sideTexture, String particleTexture) {
		models().getBuilder(modelName)
				.parent(models().getExistingFile(mcLoc("block/block")))
				.guiLight(BlockModel.GuiLight.SIDE)
				.texture("0", modLoc("block/laser/mode/" + lensTexture))
				.texture("2", modLoc("block/laser/material/" + frontTexture))
				.texture("3", modLoc("block/laser/material/" + sideTexture))
				.texture("particle", modLoc("block/laser/material/" + particleTexture))
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
	}

	private void generateLaserBlockStates(DeferredBlock<?> laserBlock) {
		String blockName = blockName(laserBlock);

		// Generate models first
		generateLaserModels(blockName);

		// Generate blockstates with specific ordering
		VariantBlockStateBuilder builder = getVariantBuilder(laserBlock.get());

		// Order: Power (0-15) -> Mode (spectrum, uv, ir) -> Facing (down, east, north, south, up, west)
		for (int power = 0; power <= 15; power++) {
			for (BronzeLaserBlock.LaserMode mode : BronzeLaserBlock.LaserMode.values()) {
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

	private void generateCopperLaserBlockStates(DeferredBlock<?> laserBlock) {
		String blockName = blockName(laserBlock);

		// Remove waxed_ prefix for texture/model names
		String modelBaseName = blockName;
		if (blockName.startsWith("waxed_")) {
			modelBaseName = blockName.replace("waxed_", "");
		}

		// Generate blockstates with specific ordering (reuse bronze laser logic but with CopperLaserBlock)
		VariantBlockStateBuilder builder = getVariantBuilder(laserBlock.get());

		// Order: Power (0-15) -> Mode (spectrum, uv, ir) -> Facing (down, east, north, south, up, west)
		for (int power = 0; power <= 15; power++) {
			for (CopperLaserBlock.LaserMode mode : CopperLaserBlock.LaserMode.values()) {
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
							.with(CopperLaserBlock.MODE, mode)
							.with(CopperLaserBlock.POWER, power)
							.modelForState()
							.modelFile(models().getExistingFile(modLoc("block/" + modelBaseName + modelSuffix)))
							.rotationX(rotationX)
							.rotationY(rotationY)
							.addModel();
				}
			}
		}
	}

	private String blockName(DeferredBlock<?> block) {
		return block.getId().toString().split(":")[1];
	}
}