package corundum.rubinated_nether.data;

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

		lantern(
			RNBlocks.RUBY_LANTERN.get(),
			"ruby_lantern"
		);
		this.simpleBlock(
			RNBlocks.CHANDELIER.get(),
			this.models()
				.withExistingParent("ruby_chandelier", this.modLoc("block/ruby_chandelier_base"))
		);
		this.axisBlock(
			RNBlocks.LAVA_LAMP.get(),
			this.models()
				.withExistingParent("lava_lamp", this.modLoc("block/lava_lamp_side_base")),
			this.models()
				.withExistingParent("lava_lamp", this.modLoc("block/lava_lamp_base"))
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

	private String blockName(DeferredBlock<?> block) {
		return block.getId().toString().split(":")[1];
	}
}
