package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RubinatedNetherBlockStates extends BlockStateProvider {
	public RubinatedNetherBlockStates(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, RubinatedNether.MODID, fileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.simpleBlock(RubinatedNetherBlocks.NETHER_RUBY_ORE.get());
		this.simpleBlock(RubinatedNetherBlocks.MOLTEN_RUBY_ORE.get());
		this.simpleBlock(RubinatedNetherBlocks.RUBINATED_BLACKSTONE.get());

		this.simpleBlock(RubinatedNetherBlocks.RUBY_BLOCK.get());
		this.simpleBlock(RubinatedNetherBlocks.BLEEDING_OBSIDIAN.get());

		lantern(
			RubinatedNetherBlocks.RUBY_LANTERN.get(), 
			"ruby_lantern"
		);
		this.simpleBlock(
			RubinatedNetherBlocks.CHANDELIER.get(),
			this.models()
				.withExistingParent("ruby_chandelier", this.modLoc("block/ruby_chandelier_base"))
		);
		this.axisBlock(
			RubinatedNetherBlocks.LAVA_LAMP.get(),
			this.models()
				.withExistingParent("lava_lamp", this.modLoc("block/lava_lamp_side_base")),
			this.models()
				.withExistingParent("lava_lamp", this.modLoc("block/lava_lamp_base"))
		);
		this.simpleBlock(
			RubinatedNetherBlocks.DRY_ICE.get(),
			this.models()
				.withExistingParent("dry_ice", this.modLoc("block/dry_ice_base"))
		);
		this.simpleBlock(RubinatedNetherBlocks.SOAKSTONE.get());

		glassWithPane(
			RubinatedNetherBlocks.RUBY_GLASS.get(), 
			RubinatedNetherBlocks.RUBY_GLASS_PANE.get(), 
			"ruby_glass", 
			modLoc("block/ruby_glass_pane_top")
		);
		glassWithPane(
			RubinatedNetherBlocks.ORNATE_RUBY_GLASS.get(), 
			RubinatedNetherBlocks.ORNATE_RUBY_GLASS_PANE.get(), 
			"ornate_ruby_glass", 
			modLoc("block/ornate_ruby_glass_pane_top")
		);
		glassWithPane(
			RubinatedNetherBlocks.MOLTEN_RUBY_GLASS.get(), 
			RubinatedNetherBlocks.MOLTEN_RUBY_GLASS_PANE.get(), 
			"molten_ruby_glass", 
			modLoc("block/molten_ruby_glass_pane_top")
		);

		this.simpleBlock(RubinatedNetherBlocks.ALTAR_STONE.get());
		this.simpleBlock(RubinatedNetherBlocks.ALTAR_STONE_TILES.get());
		this.axisBlock(RubinatedNetherBlocks.ALTAR_STONE_PILLAR.get());
		this.simpleBlock(RubinatedNetherBlocks.ALTAR_STONE_BRICKS.get());
		this.simpleBlock(RubinatedNetherBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
		this.axisBlock(RubinatedNetherBlocks.BLEEDING_CHISELED_ALTAR_STONE_BRICKS.get());

		this.simpleBlock(
			RubinatedNetherBlocks.RUNESTONE.get(),
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
	}
}
