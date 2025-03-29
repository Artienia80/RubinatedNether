package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class RNItemModels extends ItemModelProvider {
	public RNItemModels(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, RubinatedNether.MODID, fileHelper);
	}

	@Override
	protected void registerModels() {
		// Block items
		simpleBlockItems(
				RNBlocks.NETHER_RUBY_ORE,
				RNBlocks.MOLTEN_RUBY_ORE,
				RNBlocks.RUBINATED_BLACKSTONE,
				RNBlocks.RUBY_BLOCK,
				RNBlocks.MOLTEN_RUBY_BLOCK,
				RNBlocks.BLEEDING_OBSIDIAN,
				RNBlocks.LAVA_LAMP,
				RNBlocks.DRY_ICE,
				RNBlocks.SOAKSTONE,
				RNBlocks.BRAZIER,
				RNBlocks.RUBINATION_ALTAR,
				RNBlocks.RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS,
				RNBlocks.MOLTEN_RUBY_GLASS
		);

		// Handheld items
		handheldItem(RNItems.BRONZE_DRILL, "item/bronze_drill");

		// Basic items
		basicItems(
				RNBlocks.RUBY_LANTERN,
				RNBlocks.CHANDELIER,
				RNBlocks.RUNESTONE,
				RNItems.RUBY_LENS,
				RNItems.RUBY_ITEM,
				RNItems.MOLTEN_RUBY_ITEM,
				RNItems.RUBY_SHARD_ITEM,
				RNItems.MOLTEN_RUBY_NUGGET_ITEM,
				RNItems.MUSIC_DISC_SHIMMER,
				RNItems.BRONZE_ROD,
				RNItems.BRONZE_SCRAP,
				RNItems.BRONZE_SHOT,
				RNItems.RITUAL_OFFERING,
				RNItems.GRAND_RITUAL_OFFERING
		);

		// Runes (all share the same texture)
		runeItem(
				RNItems.GREED_RUNE,
				RNItems.WRATH_RUNE,
				RNItems.SLOTH_RUNE,
				RNItems.GLUTTONY_RUNE,
				RNItems.ENVY_RUNE,
				RNItems.VAINGLORY_RUNE,
				RNItems.PRIDE_RUNE,
				RNItems.ACEDIA_RUNE,
				RNItems.LUXURIA_RUNE,
				RNItems.INSIDIAE_RUNE,
				RNItems.SUPERBIA_RUNE,
				RNItems.TRISTIA_RUNE,
				RNItems.STUDIOSE_RUNE,
				RNItems.ARDENTER_RUNE,
				RNItems.NIMIS_RUNE
		);
	}

	private void paneItem(DeferredBlock<?> block, String texture) {
		withExistingParent(
				block.getId().toString(),
				mcLoc("item/generated")
		)
				.texture("layer0", texture)
				.renderType(mcLoc("translucent"));
	}

	private void handheldItem(DeferredItem<?> item, String texture) {
		withExistingParent(
				item.getId().toString(),
				mcLoc("item/handheld")
		)
				.texture("layer0", texture);
	}

	private void simpleBlockItems(DeferredBlock<?>... blocks) {
		for (var block : blocks)
			simpleBlockItem(block.get());
	}

	private void basicItems(ItemLike... items) {
		for (var item : items)
			basicItem(item.asItem());
	}

	private void runeItem(DeferredItem<?>... runes) {
		for (var rune : runes)
			withExistingParent(
					rune.getId().toString(),
					modLoc("item/rune_base") // All runes use the same model
			);
	}
}
