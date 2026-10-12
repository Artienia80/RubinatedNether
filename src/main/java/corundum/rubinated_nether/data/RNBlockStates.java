package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.blocks.*;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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
		this.slabBlock(
				RNBlocks.SHRINE_STONE_SLAB.get(),
				modLoc("block/shrine_stone"),
				modLoc("block/shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.SHRINE_STONE_STAIRS.get(),
				modLoc("block/shrine_stone")
		);
		this.wallBlock(
				RNBlocks.SHRINE_STONE_WALL.get(),
				modLoc("block/shrine_stone")
		);

		this.simpleBlock(RNBlocks.COBBLED_SHRINE_STONE.get());
		this.slabBlock(
				RNBlocks.COBBLED_SHRINE_STONE_SLAB.get(),
				modLoc("block/cobbled_shrine_stone"),
				modLoc("block/cobbled_shrine_stone")
		);
		this.stairsBlock(
				RNBlocks.COBBLED_SHRINE_STONE_STAIRS.get(),
				modLoc("block/cobbled_shrine_stone")
		);
		this.wallBlock(
				RNBlocks.COBBLED_SHRINE_STONE_WALL.get(),
				modLoc("block/cobbled_shrine_stone")
		);

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
		this.wallBlock(
				RNBlocks.POLISHED_SHRINE_STONE_WALL.get(),
				modLoc("block/polished_shrine_stone")
		);

		this.simpleBlock(RNBlocks.SHRINE_STONE_TILES.get());
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

		this.axisBlock(RNBlocks.SHRINE_STONE_PILLAR.get());

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
		brazierBlock();

        generateCrystalModels();
        crystalBlockState(RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.get(), "crystallized_bronze_crystal");
        crystalBlockState(RNBlocks.CRYSTALLIZED_BRONZE_CLUSTER.get(), "crystallized_bronze_cluster");

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

    private void generateCrystalModels() {
        models().getBuilder("crystallized_bronze_crystal")
                .parent(models().getExistingFile(mcLoc("block/cross")))
                .texture("cross", modLoc("block/crystallized_bronze_crystal"))
                .element()
                .from(5, 0, 8).to(11, 7, 8)
                .rotation().angle(45).axis(Direction.Axis.Y).origin(8, 8, 8).end()
                .face(Direction.NORTH).uvs(5, 9, 11, 16).texture("#cross").end()
                .face(Direction.SOUTH).uvs(5, 9, 11, 16).texture("#cross").end()
                .end()
                .element()
                .from(8, 0, 5).to(8, 7, 11)
                .rotation().angle(45).axis(Direction.Axis.Y).origin(8, 8, 8).end()
                .face(Direction.WEST).uvs(5, 9, 11, 16).texture("#cross").end()
                .face(Direction.EAST).uvs(5, 9, 11, 16).texture("#cross").end()
                .end()
                .renderType(mcLoc("cutout"));

        models().getBuilder("crystallized_bronze_cluster")
                .parent(models().getExistingFile(mcLoc("block/cross")))
                .texture("cross", modLoc("block/crystallized_bronze_cluster"))
                .element()
                .from(4, 0, 8).to(12, 9, 8)
                .rotation().angle(45).axis(Direction.Axis.Y).origin(8, 8, 8).end()
                .face(Direction.NORTH).uvs(4, 7, 12, 16).texture("#cross").end()
                .face(Direction.SOUTH).uvs(4, 7, 12, 16).texture("#cross").end()
                .end()
                .element()
                .from(8, 0, 4).to(8, 9, 12)
                .rotation().angle(45).axis(Direction.Axis.Y).origin(8, 8, 8).end()
                .face(Direction.WEST).uvs(4, 7, 12, 16).texture("#cross").end()
                .face(Direction.EAST).uvs(4, 7, 12, 16).texture("#cross").end()
                .end()
                .renderType(mcLoc("cutout"));
    }

    private void crystalBlockState(Block crystal, String modelName) {
        getVariantBuilder(crystal).forAllStates(state -> {
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
                    .modelFile(models().getExistingFile(modLoc("block/" + modelName)))
                    .rotationX(rotationX)
                    .rotationY(rotationY)
                    .build();
        });
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

		getVariantBuilder(block.get()).forAllStates(state -> {
			var half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
			return ConfiguredModel.builder()
					.modelFile(half == DoubleBlockHalf.LOWER ? baseModel : lidModel)
					.build();
		});
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


	private String blockName(DeferredBlock<?> block) {
		return block.getId().toString().split(":")[1];
	}
}