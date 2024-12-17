package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.*;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RubinatedNetherLanguage extends LanguageProvider {
	public RubinatedNetherLanguage(PackOutput output) {
		super(output, RubinatedNether.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.addBlock(RubinatedNetherBlocks.MOLTEN_RUBY_ORE, "Molten Ruby Ore");
		this.addBlock(RubinatedNetherBlocks.CHANDELIER, "Ruby Chandelier");
		this.addBlock(RubinatedNetherBlocks.DRY_ICE, "Dry Ice");

		this.addItem(RubinatedNetherItems.RUBY_ITEM, "Ruby");
		this.addItem(RubinatedNetherItems.MOLTEN_RUBY_ITEM, "Molten Ruby");

		this.add(RubinatedNetherCreativeTabs.EXAMPLE_TAB.get().getDisplayName().getString(), "Rubinated Nether");
	}
}
