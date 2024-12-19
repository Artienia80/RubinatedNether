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
		this.addBlock(RubinatedNetherBlocks.LAVA_LAMP, "Molten Ruby Lava Lamp");
		this.addBlock(RubinatedNetherBlocks.DRY_ICE, "Dry Ice");
		this.addBlock(RubinatedNetherBlocks.SOAKSTONE, "Soakstone");
		this.addBlock(RubinatedNetherBlocks.ALTAR_STONE, "Altar Stone");
		this.addBlock(RubinatedNetherBlocks.ALTAR_STONE_TILES, "Altar Stone Tiles");
		this.addBlock(RubinatedNetherBlocks.ALTAR_STONE_PILLAR, "Altar Stone Pillar");
		this.addBlock(RubinatedNetherBlocks.ALTAR_STONE_BRICKS, "Altar Stone Bricks");
		this.addBlock(RubinatedNetherBlocks.CHISELED_ALTAR_STONE_BRICKS, "Chiseled Altar Stone Bricks");
		this.addBlock(RubinatedNetherBlocks.BLEEDING_CHISELED_ALTAR_STONE_BRICKS, "Bleeding Chiseled Altar Stone Bricks");
		this.addBlock(RubinatedNetherBlocks.RUNESTONE, "Runestone");

		this.addItem(RubinatedNetherItems.RUBY_ITEM, "Ruby");
		this.addItem(RubinatedNetherItems.MOLTEN_RUBY_ITEM, "Molten Ruby");

		this.add(RubinatedNetherCreativeTabs.EXAMPLE_TAB.get().getDisplayName().getString(), "Rubinated Nether");
	}
}
