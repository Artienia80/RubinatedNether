package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RubinatedNetherBlockStates extends BlockStateProvider {
	public RubinatedNetherBlockStates(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, RubinatedNether.MODID, fileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.simpleBlock(RubinatedNetherBlocks.MOLTEN_RUBY_ORE.get());

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
}
