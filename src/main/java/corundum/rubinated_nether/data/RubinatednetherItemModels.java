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
		this.withExistingParent(
			RubinatedNetherBlocks.MOLTEN_RUBY_ORE_ITEM.get().toString(), 
			modLoc("block/molten_ruby_ore")
		);

		this.basicItem(RubinatedNetherItems.RUBY_ITEM.get());
		this.basicItem(RubinatedNetherItems.MOLTEN_RUBY_ITEM.get());
	}
}
