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
				RNBlocks.DRY_ICE,
				RNBlocks.SOAKSTONE,
				RNBlocks.RUBINATION_ALTAR,
				RNBlocks.RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS,
				RNBlocks.MOLTEN_RUBY_GLASS,
				RNBlocks.SHRINE_STONE,
				RNBlocks.SHRINE_STONE_STAIRS,
				RNBlocks.SHRINE_STONE_SLAB,
				RNBlocks.POLISHED_SHRINE_STONE_SLAB,
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
				RNBlocks.RUBINATED_SHRINE_STONE_BRICKS,
				RNBlocks.RUBINATED_SHRINE_STONE_PILLAR,
				RNBlocks.RUBINATED_SHRINE_STONE_TILES,

				RNBlocks.COPPER_LASER,
				RNBlocks.EXPOSED_COPPER_LASER,
				RNBlocks.WEATHERED_COPPER_LASER,
				RNBlocks.OXIDIZED_COPPER_LASER,
				RNBlocks.WAXED_COPPER_LASER,
				RNBlocks.WAXED_EXPOSED_COPPER_LASER,
				RNBlocks.WAXED_WEATHERED_COPPER_LASER,
				RNBlocks.WAXED_OXIDIZED_COPPER_LASER
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
				RNBlocks.CRYSTALLIZED_BRONZE_GRATE,

				RNBlocks.BRONZE_LAMP,
				RNBlocks.DISCOLORED_BRONZE_LAMP,
				RNBlocks.CORRODED_BRONZE_LAMP,
				RNBlocks.TARNISHED_BRONZE_LAMP,
				RNBlocks.CRYSTALLIZED_BRONZE_LAMP,

				RNBlocks.BRONZE_LASER,
				RNBlocks.DISCOLORED_BRONZE_LASER,
				RNBlocks.CORRODED_BRONZE_LASER,
				RNBlocks.TARNISHED_BRONZE_LASER,
				RNBlocks.CRYSTALLIZED_BRONZE_LASER
		);

		customItemTextures("item/bronze/bronze_chandelier",
				RNBlocks.BRONZE_CHANDELIER,
				RNBlocks.DISCOLORED_BRONZE_CHANDELIER,
				RNBlocks.CORRODED_BRONZE_CHANDELIER,
				RNBlocks.TARNISHED_BRONZE_CHANDELIER,
				RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER
		);

		customItemTextures("item/bronze/bronze_lantern",
				RNBlocks.BRONZE_LANTERN,
				RNBlocks.DISCOLORED_BRONZE_LANTERN,
				RNBlocks.CORRODED_BRONZE_LANTERN,
				RNBlocks.TARNISHED_BRONZE_LANTERN,
				RNBlocks.CRYSTALLIZED_BRONZE_LANTERN
		);

		customItemTextures("item/bronze/bronze_chain",
				RNBlocks.BRONZE_CHAIN,
				RNBlocks.DISCOLORED_BRONZE_CHAIN,
				RNBlocks.CORRODED_BRONZE_CHAIN,
				RNBlocks.TARNISHED_BRONZE_CHAIN,
				RNBlocks.CRYSTALLIZED_BRONZE_CHAIN
		);

		springBlockItems(
				RNBlocks.BRONZE_SPRING,
				RNBlocks.DISCOLORED_BRONZE_SPRING,
				RNBlocks.CORRODED_BRONZE_SPRING,
				RNBlocks.TARNISHED_BRONZE_SPRING,
				RNBlocks.CRYSTALLIZED_BRONZE_SPRING
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
				RNBlocks.SHRINE_STONE_WALL.getId().toString(),
				modLoc("block/shrine_stone")
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

                RNBlocks.RUNESTONE,
				RNItems.RUBY_LENS,
				RNItems.RUBY,
				RNItems.MOLTEN_RUBY,
				RNItems.RUBY_SHARD,
				RNItems.MOLTEN_RUBY_NUGGET,
				RNItems.MUSIC_DISC_SHIMMER,
				RNItems.BRONZE_ROD,
				RNItems.BRONZE_POWDER,
				RNItems.BRONZE_SCRAP,
				RNItems.BRONZE_SHOT,
				RNItems.CRYSTALLIZED_BRONZE_SHOT,
				RNItems.RITUAL_OFFERING,
				RNItems.COGWHEEL,
				RNItems.WINDING_KEY,
				RNItems.RUNE,
				RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL,
				RNItems.MOLTEN_RUBY_BUCKET,
				RNItems.COGS_BANNER_PATTERN.get()
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

		runeItem("trident",
				RNItems.IRA_RUNE,
				RNItems.INVIDIA_RUNE,
				RNItems.GULA_RUNE
		);

		runeItem("mace",
				RNItems.IGNAVIA_RUNE,
				RNItems.KENODOXIA_RUNE,
				RNItems.PHILARGYRIA_RUNE
		);

		withExistingParent(
				RNBlocks.BRAZIER.getId().toString(),
				modLoc("block/ruby_brazier_0")
		);

		spawnEggItems(
				RNItems.BRONZE_SPAWN_EGG,
				RNItems.DISCOLORED_BRONZE_SPAWN_EGG,
				RNItems.CORRODED_BRONZE_SPAWN_EGG,
				RNItems.TARNISHED_BRONZE_SPAWN_EGG,
				RNItems.CRYSTALLIZED_BRONZE_SPAWN_EGG
		);
	}



	private void customItemTextures(String baseTexture, DeferredBlock<?>... blocks) {
		for (var block : blocks) {
			String blockName = block.getId().getPath();

			var model = withExistingParent(
					block.getId().toString(),
					mcLoc("item/generated")
			).texture("layer0", modLoc(baseTexture + "/" + blockName));


			var waxableModel = withExistingParent(
					modLoc(WaxableBlockItem.getWaxableItem(block)).toString(),
					mcLoc("item/generated")
			).texture("layer0", modLoc(baseTexture + "/" + blockName));

		}
	}

	private void springBlockItems(DeferredBlock<?>... blocks) {
		for (var block : blocks) {
			String blockName = block.getId().getPath();

			withExistingParent(
					block.getId().toString(),
					modLoc("block/" + blockName + "_squished")
			);

			withExistingParent(
					modLoc(WaxableBlockItem.getWaxableItem(block)).toString(),
					modLoc("block/" + blockName + "_squished")
			);
		}
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
					modLoc("item/" + "rune_" + type)
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

	private void spawnEggItems(DeferredItem<?>... items) {
		for (var item : items) {
			withExistingParent(
					item.getId().toString(),
					mcLoc("item/template_spawn_egg")
			);
		}
	}
}