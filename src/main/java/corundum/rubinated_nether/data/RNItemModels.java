package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.content.trim.RNTrimMaterials;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;
import java.util.Map;

public class RNItemModels extends ItemModelProvider {

	private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
	static {
		trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
		trimMaterials.put(TrimMaterials.IRON, 0.2F);
		trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
		trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
		trimMaterials.put(TrimMaterials.COPPER, 0.5F);
		trimMaterials.put(TrimMaterials.GOLD, 0.6F);
		trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
		trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
		trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
		trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
	}

	// Fixed CMD values per category — must be in ascending order for ≥ matching to work correctly
	private static final Map<String, Integer> CATEGORY_CMD = new LinkedHashMap<>();
	static {
		CATEGORY_CMD.put("tool",     1);
		CATEGORY_CMD.put("weapon",   2);
		CATEGORY_CMD.put("armor",    3);
		CATEGORY_CMD.put("bow",      4);
		CATEGORY_CMD.put("crossbow", 5);
		CATEGORY_CMD.put("trident",  6);
		CATEGORY_CMD.put("mace",     7);
	}

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
				RNBlocks.MOLTEN_RUBY_CAULDRON,
				RNBlocks.DRY_ICE,
				RNBlocks.SOAKSTONE,
				RNBlocks.RUBINATION_ALTAR,
				RNBlocks.RUBY_GLASS,
				RNBlocks.ORNATE_RUBY_GLASS,
				RNBlocks.MOLTEN_RUBY_GLASS,
				RNBlocks.SHRINE_STONE,
				RNBlocks.SHRINE_STONE_STAIRS,
				RNBlocks.SHRINE_STONE_SLAB,
				RNBlocks.COBBLED_SHRINE_STONE,
				RNBlocks.COBBLED_SHRINE_STONE_STAIRS,
				RNBlocks.COBBLED_SHRINE_STONE_SLAB,
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
				RNBlocks.CRYSTALLIZED_BRONZE_LASER,

				RNBlocks.BRONZE_VENT,
				RNBlocks.DISCOLORED_BRONZE_VENT,
				RNBlocks.CORRODED_BRONZE_VENT,
				RNBlocks.TARNISHED_BRONZE_VENT,
				RNBlocks.CRYSTALLIZED_BRONZE_VENT
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
				RNBlocks.COBBLED_SHRINE_STONE_WALL.getId().toString(),
				modLoc("block/cobbled_shrine_stone")
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
				RNItems.MUSIC_DISC_SINNER,
				RNItems.BRONZE_ROD,
				RNItems.BRONZE_POWDER,
				RNItems.BRONZE_SCRAP,
				RNItems.BRONZE_SHOT,
				RNItems.CRYSTALLIZED_BRONZE_SHOT,
				RNItems.RITUAL_OFFERING,
				RNItems.COGWHEEL,
				RNItems.WINDING_KEY,
				RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL,
				RNItems.MOLTEN_RUBY_BUCKET,

				RNItems.COGS_BANNER_PATTERN.get()
		);

		vaseItemModel(RNBlocks.BRONZE_VASE, "block/bronze/bronze_vase/bronze_vase", "block/bronze/bronze_block/bronze_block");
		vaseItemModel(RNBlocks.DISCOLORED_BRONZE_VASE, "block/bronze/bronze_vase/discolored_bronze_vase", "block/bronze/bronze_block/discolored_bronze_block");
		vaseItemModel(RNBlocks.CORRODED_BRONZE_VASE, "block/bronze/bronze_vase/corroded_bronze_vase", "block/bronze/bronze_block/corroded_bronze_block");
		vaseItemModel(RNBlocks.TARNISHED_BRONZE_VASE, "block/bronze/bronze_vase/tarnished_bronze_vase", "block/bronze/bronze_block/tarnished_bronze_block");
		vaseItemModel(RNBlocks.CRYSTALLIZED_BRONZE_VASE, "block/bronze/bronze_vase/crystallized_bronze_vase", "block/bronze/bronze_block/crystallized_bronze_block");

		waxableBlockItemsCopyOnly(
				RNBlocks.BRONZE_VASE,
				RNBlocks.DISCOLORED_BRONZE_VASE,
				RNBlocks.CORRODED_BRONZE_VASE,
				RNBlocks.TARNISHED_BRONZE_VASE,
				RNBlocks.CRYSTALLIZED_BRONZE_VASE
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

		carvedRuneItem();

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

	private void carvedRuneItem() {
		var builder = withExistingParent(
				RNItems.RUNE.getId().toString(),
				mcLoc("item/generated")
		).texture("layer0", modLoc("item/rune"));

		// One override per category, in ascending CMD order (required for ≥ predicate matching)
		for (var entry : CATEGORY_CMD.entrySet()) {
			builder.override()
					.predicate(mcLoc("custom_model_data"), entry.getValue())
					.model(getExistingFile(modLoc("item/rune_" + entry.getKey() + "_carved")));
		}
	}

	private void customItemTextures(String baseTexture, DeferredBlock<?>... blocks) {
		for (var block : blocks) {
			String blockName = block.getId().getPath();

			withExistingParent(
					block.getId().toString(),
					mcLoc("item/generated")
			).texture("layer0", modLoc(baseTexture + "/" + blockName));

			withExistingParent(
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

	private void waxableBlockItemsCopyOnly(DeferredBlock<?>... blocks) {
		for (var block : blocks) {
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


	private void vaseItemModel(DeferredBlock<?> vaseBlock, String vaseTexturePath, String bronzeBlockTexturePath) {
		String name = vaseBlock.getId().getPath();

		ItemModelBuilder base = getBuilder(name + "_base")
				.texture("4", modLoc(vaseTexturePath + "_bottom"))
				.texture("5", modLoc(vaseTexturePath + "_head"))
				.texture("6", modLoc(vaseTexturePath + "_neck"))
				.texture("7", modLoc(vaseTexturePath + "_top"))
				.texture("8", modLoc(bronzeBlockTexturePath))
				.texture("particle", modLoc(vaseTexturePath + "_bottom"));

		base.element()
				.from(0, 0, 0).to(16, 16, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.EAST).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.WEST).uvs(0, 0, 16, 16).texture("#4").end()
				.face(Direction.UP).uvs(0, 0, 16, 16).texture("#6").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#8").end()
				.end();

		base.element()
				.from(0, 16, 0).to(16, 20, 16)
				.face(Direction.NORTH).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.EAST).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.SOUTH).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.WEST).uvs(0, 12, 16, 16).texture("#7").end()
				.face(Direction.UP).uvs(0, 0, 16, 16).texture("#6").end()
				.end();

		base.element()
				.from(1, 22, 1).to(15, 24, 15)
				.face(Direction.NORTH).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.EAST).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.SOUTH).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.WEST).uvs(1, 8, 15, 10).texture("#7").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).texture("#5").end()
				.face(Direction.DOWN).uvs(1, 1, 15, 15).texture("#6").end()
				.end();

		base.element()
				.from(2, 20, 2).to(14, 22, 14)
				.face(Direction.NORTH).uvs(2, 10, 14, 12).texture("#7").end()
				.face(Direction.EAST).uvs(2, 10, 14, 12).texture("#7").end()
				.face(Direction.SOUTH).uvs(2, 10, 14, 12).texture("#7").end()
				.face(Direction.WEST).uvs(2, 10, 14, 12).texture("#7").end()
				.end();

		ItemModelBuilder wrapper = getBuilder(name)
				.parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")))
				.texture("particle", modLoc(vaseTexturePath + "_bottom"));

		wrapper.transforms()
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
				.rotation(75, 45, 0).translation(0, 1.5f, 0).scale(0.35f, 0.35f, 0.35f).end()
				.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
				.rotation(75, 45, 0).translation(0, 1.5f, 0).scale(0.35f, 0.35f, 0.35f).end()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
				.rotation(0, 45, 0).scale(0.4f, 0.4f, 0.4f).end()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
				.rotation(0, 225, 0).scale(0.4f, 0.4f, 0.4f).end()
				.transform(ItemDisplayContext.GROUND)
				.translation(0, 3, 0).scale(0.25f, 0.25f, 0.25f).end()
				.transform(ItemDisplayContext.GUI)
				.rotation(30, 225, 0).translation(0, -1.5f, 0).scale(0.46f, 0.46f, 0.46f).end()
				.transform(ItemDisplayContext.FIXED)
				.scale(0.4f, 0.4f, 0.4f).end();
	}

}