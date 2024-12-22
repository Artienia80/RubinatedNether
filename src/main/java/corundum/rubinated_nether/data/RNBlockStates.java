package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

		this.simpleBlock(RNBlocks.ALTAR_STONE.get());
		this.simpleBlock(RNBlocks.POLISHED_ALTAR_STONE.get());
		this.slabBlock(
				RNBlocks.POLISHED_ALTAR_STONE_SLAB.get(),
				modLoc("block/polished_altar_stone"),
				modLoc("block/polished_altar_stone")
		);
		this.stairsBlock(
				RNBlocks.POLISHED_ALTAR_STONE_STAIRS.get(),
				modLoc("block/polished_altar_stone")
		);
		this.wallBlock(
				RNBlocks.POLISHED_ALTAR_STONE_WALL.get(),
				modLoc("block/polished_altar_stone")
		);

		this.simpleBlock(RNBlocks.ALTAR_STONE_TILES.get());
		this.slabBlock(
				RNBlocks.ALTAR_STONE_TILES_SLAB.get(),
				modLoc("block/altar_stone_tiles"),
				modLoc("block/altar_stone_tiles")
		);
		this.stairsBlock(
				RNBlocks.ALTAR_STONE_TILES_STAIRS.get(),
				modLoc("block/altar_stone_tiles")
		);
		this.wallBlock(
				RNBlocks.ALTAR_STONE_TILES_WALL.get(),
				modLoc("block/altar_stone_tiles")
		);

		this.axisBlock(RNBlocks.ALTAR_STONE_PILLAR.get());
		this.simpleBlock(RNBlocks.ALTAR_STONE_BRICKS.get());
		this.slabBlock(
				RNBlocks.ALTAR_STONE_BRICKS_SLAB.get(),
				modLoc("block/altar_stone_bricks"),
				modLoc("block/altar_stone_bricks")
		);
		this.stairsBlock(
				RNBlocks.ALTAR_STONE_BRICKS_STAIRS.get(),
				modLoc("block/altar_stone_bricks")
		);
		this.wallBlock(
				RNBlocks.ALTAR_STONE_BRICKS_WALL.get(),
				modLoc("block/altar_stone_bricks")
		);

		this.simpleBlock(RNBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
		this.axisBlock(RNBlocks.RUBINATED_CHISELED_ALTAR_STONE_BRICKS.get());

		this.simpleBlock(
			RNBlocks.RUNESTONE.get(),
			this.models()
				.withExistingParent("runestone", this.modLoc("block/runestone_base"))
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

	public void lantern(LanternBlock lamp, String name) {
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

		this.simpleBlock(RNBlocks.BRONZE_BLOCK.get());
		this.simpleBlock(RNBlocks.DISCOLORED_BRONZE_BLOCK.get());
		this.simpleBlock(RNBlocks.CORRODED_BRONZE_BLOCK.get());
		this.simpleBlock(RNBlocks.TARNISHED_BRONZE_BLOCK.get());
		this.simpleBlock(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());


	}


}
