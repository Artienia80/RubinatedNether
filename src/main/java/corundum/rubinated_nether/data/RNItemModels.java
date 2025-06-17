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
				RNBlocks.MOLTEN_RUBY_GLASS,
				RNBlocks.SHRINE_STONE,
				RNBlocks.POLISHED_SHRINE_STONE,
				RNBlocks.POLISHED_SHRINE_STONE_STAIRS,
				RNBlocks.POLISHED_SHRINE_STONE_SLAB,
				RNBlocks.SHRINE_STONE_TILES,
				RNBlocks.SHRINE_STONE_TILES_STAIRS,
				RNBlocks.SHRINE_STONE_TILES_SLAB,
				RNBlocks.SHRINE_STONE_PILLAR,
				RNBlocks.SHRINE_STONE_BRICKS,
				RNBlocks.SHRINE_STONE_BRICKS_STAIRS,
				RNBlocks.SHRINE_STONE_BRICKS_SLAB,
				RNBlocks.CHISELED_SHRINE_STONE_BRICKS,
				RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS,
				RNBlocks.RUBINATED_SHRINE_STONE_BRICKS
						);

		waxableBlockItems(
				RNBlocks.BRONZE_BLOCK,
				RNBlocks.DISCOLORED_BRONZE_BLOCK,
				RNBlocks.CORRODED_BRONZE_BLOCK,
				RNBlocks.TARNISHED_BRONZE_BLOCK,
				RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,

				RNBlocks.CHISELED_BRONZE,
				RNBlocks.DISCOLORED_CHISELED_BRONZE,
				RNBlocks.CORRODED_CHISELED_BRONZE,
				RNBlocks.TARNISHED_CHISELED_BRONZE,
				RNBlocks.CRYSTALLIZED_CHISELED_BRONZE,

				RNBlocks.CUT_BRONZE_PILLAR,
				RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR,
				RNBlocks.CORRODED_CUT_BRONZE_PILLAR,
				RNBlocks.TARNISHED_CUT_BRONZE_PILLAR,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR,

				RNBlocks.CUT_BRONZE_BRICKS,
				RNBlocks.CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS,
				RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB,

				RNBlocks.BRONZE_BULB,
				RNBlocks.DISCOLORED_BRONZE_BULB,
				RNBlocks.CORRODED_BRONZE_BULB,
				RNBlocks.TARNISHED_BRONZE_BULB,
				RNBlocks.CRYSTALLIZED_BRONZE_BULB,

				RNBlocks.BRONZE_GRATE,
				RNBlocks.DISCOLORED_BRONZE_GRATE,
				RNBlocks.CORRODED_BRONZE_GRATE,
				RNBlocks.TARNISHED_BRONZE_GRATE,
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE
						 );

		paneItem(
				RNBlocks.RUBY_GLASS_PANE,
				"block/ruby_glass"
				);
		paneItem(
				RNBlocks.ORNATE_RUBY_GLASS_PANE,
				"block/ornate_ruby_glass"
				);
		paneItem(
				RNBlocks.MOLTEN_RUBY_GLASS_PANE,
				"block/molten_ruby_glass"
				);

		wallInventory(
				RNBlocks.POLISHED_SHRINE_STONE_WALL.getId().toString(),
				modLoc("block/polished_shrine_stone")
					 );
		wallInventory(
				RNBlocks.SHRINE_STONE_TILES_WALL.getId().toString(),
				modLoc("block/shrine_stone_tiles")
					 );
		wallInventory(
				RNBlocks.SHRINE_STONE_BRICKS_WALL.getId().toString(),
				modLoc("block/shrine_stone_bricks")
					 );

		paneItem(
				RNBlocks.MOLTEN_RUBY_GLASS_PANE,
				"block/molten_ruby_glass"
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
				RNItems.BRONZE_POWDER,
				RNItems.BRONZE_SHOT,
				RNItems.RITUAL_OFFERING,
				RNItems.GRAND_RITUAL_OFFERING
		);

		// Runes
		runeItem("tool",
				RNItems.GREED_RUNE,
				RNItems.GLUTTONY_RUNE,
				RNItems.SLOTH_RUNE
		);
		runeItem("weapon",
				RNItems.WRATH_RUNE,
				RNItems.ENVY_RUNE,
				RNItems.VAINGLORY_RUNE
		);
		runeItem("armor",
				RNItems.PRIDE_RUNE,
				RNItems.ACEDIA_RUNE,
				RNItems.LUXURIA_RUNE
		);
		runeItem("bow",
				RNItems.INSIDIAE_RUNE,
				RNItems.SUPERBIA_RUNE,
				RNItems.TRISTIA_RUNE
		);
		runeItem("crossbow",
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

	private void runeItem(String type, DeferredItem<?>... runes) {
		for (var rune : runes)
			withExistingParent(
					rune.getId().toString(),
					modLoc("item/" + type + "_rune")
			);
	}

	private void waxableBlockItems(DeferredBlock<?>... blocks) {
		for (var block : blocks) {
			simpleBlockItem(block.get());
			withExistingParent(
					modLoc(WaxableBlockItem.getWaxableItem(block)).toString(),
					block.getId()
							  );
		}
	}
}
