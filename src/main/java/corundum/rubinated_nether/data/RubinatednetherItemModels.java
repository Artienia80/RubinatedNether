package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RubinatedNetherBlocks;
import corundum.rubinated_nether.content.RubinatedNetherItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RubinatednetherItemModels extends ItemModelProvider {
	public RubinatednetherItemModels(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, RubinatedNether.MODID, fileHelper);
	}

	@Override
	protected void registerModels() {
		// Block items
		this.simpleBlockItem(RubinatedNetherBlocks.MOLTEN_RUBY_ORE.get());
		this.simpleBlockItem(RubinatedNetherBlocks.DRY_ICE.get());

		this.basicItem(RubinatedNetherBlocks.CHANDELIER.asItem());

		this.simpleBlockItem(RubinatedNetherBlocks.ALTAR_STONE.get());
		this.simpleBlockItem(RubinatedNetherBlocks.ALTAR_STONE_TILES.get());
		this.simpleBlockItem(RubinatedNetherBlocks.ALTAR_STONE_PILLAR.get());
		this.simpleBlockItem(RubinatedNetherBlocks.ALTAR_STONE_BRICKS.get());
		this.simpleBlockItem(RubinatedNetherBlocks.CHISELED_ALTAR_STONE_BRICKS.get());
		this.simpleBlockItem(RubinatedNetherBlocks.BLEEDING_CHISELED_ALTAR_STONE_BRICKS.get());
		this.basicItem(RubinatedNetherBlocks.RUNESTONE.asItem());

		// Non-block items
		this.basicItem(RubinatedNetherItems.RUBY_ITEM.get());
		this.basicItem(RubinatedNetherItems.MOLTEN_RUBY_ITEM.get());
	}
}
