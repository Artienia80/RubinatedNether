package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.blocks.BronzeLaserBlock;
import corundum.rubinated_nether.content.blocks.CopperLaserBlock;
import corundum.rubinated_nether.content.blocks.SixWayPillarBlock;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.function.TriConsumer;

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

		generateBronzeLaserFamily("laser");
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

		this.getVariantBuilder(spring.get()).forAllStates((state) -> {
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
		});
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

	// Constants for laser families
	private static final String[] BRONZE_LASER_STATES = {
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
			"oxidized_copper"
	};

	private static final String[] WAXED_COPPER_LASER_STATES = {
			"waxed_copper",
			"waxed_exposed_copper",
			"waxed_weathered_copper",
			"waxed_oxidized_copper"
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

	// ===== COPPER LASER METHODS =====
	private void generateCopperLaserFamily(String baseName) {
		// Generate unwaxed copper lasers
		for (String state : COPPER_LASER_STATES) {
			String blockName = state + "_" + baseName.toLowerCase();

			try {
				var field = RNBlocks.class.getField(blockName.toUpperCase());
				DeferredBlock<?> block = (DeferredBlock<?>) field.get(null);

				generateLaserModels(blockName, blockName); // Same name for both model and texture
				generateCopperLaserBlockStates(block);

			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Could not find RNBlocks." + blockName.toUpperCase(), e);
			}
		}

		// Generate waxed copper lasers (separate models, unwaxed textures)
		for (String state : WAXED_COPPER_LASER_STATES) {
			String blockName = state + "_" + baseName.toLowerCase();
			String textureBlockName = blockName.replace("waxed_", ""); // Use unwaxed textures

			try {
				var field = RNBlocks.class.getField(blockName.toUpperCase());
				DeferredBlock<?> block = (DeferredBlock<?>) field.get(null);

				generateLaserModels(blockName, textureBlockName); // Different model name, unwaxed texture name
				generateCopperLaserBlockStates(block);

			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Could not find RNBlocks." + blockName.toUpperCase(), e);
			}
		}
	}

	private void generateCopperLaserBlockStates(DeferredBlock<?> laserBlock) {
		String blockName = blockName(laserBlock);
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
							.modelFile(models().getExistingFile(modLoc("block/" + blockName + modelSuffix)))
							.rotationX(rotationX)
							.rotationY(rotationY)
							.addModel();
				}
			}
		}
	}

	// ===== SHARED LASER MODEL METHODS =====
	private void generateLaserModels(String modelBlockName, String textureBlockName) {
		// Define all variants with their texture patterns
		var variants = new Object[][] {
				{"", "laser_lens", textureBlockName + "_front", textureBlockName + "_front"}, // base variant
				{"_ir", "laser_lens_ir", textureBlockName + "_front", textureBlockName + "_front"},
				{"_ir_on", "laser_lens_ir_on", textureBlockName + "_front_on", textureBlockName + "_front"},
				{"_on", "laser_lens_on", textureBlockName + "_front_on", textureBlockName + "_front"},
				{"_uv", "laser_lens_uv", textureBlockName + "_front", textureBlockName + "_front"},
				{"_uv_on", "laser_lens_uv_on", textureBlockName + "_front_on", textureBlockName + "_front"}
		};

		// Generate each variant
		for (var variant : variants) {
			String suffix = (String) variant[0];
			String lensTexture = (String) variant[1];
			String frontTexture = (String) variant[2];
			String particleTexture = (String) variant[3];

			createLaserModel(modelBlockName + suffix, lensTexture, frontTexture, textureBlockName + "_side", particleTexture);
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

	private String blockName(DeferredBlock<?> block) {
		return block.getId().toString().split(":")[1];
	}
}