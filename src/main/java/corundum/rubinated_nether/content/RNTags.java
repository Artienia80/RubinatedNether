package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;

/**
 * Custom Tags definition
 */
public final class RNTags {

	public static final class Blocks {

		public static final TagKey<Block> RUBY_GLASS = createTag(Registries.BLOCK, "ruby_glass");

		public static final TagKey<Block> RUBY_GLASS_PANES = createTag(Registries.BLOCK, "ruby_glass_panes");

		public static final TagKey<Block> LASER_TRANSPARENT = createTag(Registries.BLOCK, "laser_transparent");

		public static final TagKey<Block> LASER_NO_SIGNAL = createTag(Registries.BLOCK, "laser_no_signal");

		public static final TagKey<Block> COLDEST_ICE = createTag(Registries.BLOCK, "coldest_ice");

		public static final TagKey<Block> LIT_SOUL_BLOCKS = createTag(Registries.BLOCK, "lit_soul_blocks");

		public static final TagKey<Block> CRYSTALLIZED_BLOCKS = createTag(Registries.BLOCK, "crystallized_blocks");

		public static final TagKey<Block> CRYSTALLIZATION_CATALYST = createTag(Registries.BLOCK, "crystallization_catalyst");

		public static final TagKey<Block> SILLY_LASER = createTag(Registries.BLOCK, "silly_laser");

		public static final TagKey<Block> MINEABLE_WITH_DRILL = createTag(Registries.BLOCK, "mineable_with_drill");

		public static final TagKey<Block> SHRINE_STONE_BLOCKS = createTag(Registries.BLOCK, "shrine_stone_blocks");

		public static final TagKey<Block> GRATES = createTag(Registries.BLOCK, "grates");

		public static final TagKey<Block> SPRINGS = createTag(Registries.BLOCK, "springs");

		public static final TagKey<Block> SMOKE_PASSTHROUGH = createTag(Registries.BLOCK, "smoke_passthrough");


	}

	public static final class Items {

		public static final TagKey<Item> RUBIES = createTag(Registries.ITEM, "rubies");

		public static final TagKey<Item> RUBY_GLASS = createTag(Registries.ITEM, "ruby_glass_tag");

		public static final TagKey<Item> RUBY_GLASS_PANES = createTag(Registries.ITEM, "ruby_glass_pane_tag");

		public static final TagKey<Item> RUBY_SHARDS = createTag(Registries.ITEM, "ruby_shard_tag");

		public static final TagKey<Item> LOW_RUBY = createTag(Registries.ITEM, "low_ruby");

		public static final TagKey<Item> RUNES = createTag(Registries.ITEM, "runes");

		public static final TagKey<Item> RUBINATION_TOOL = createTag(Registries.ITEM, "rubination_tool");
		public static final TagKey<Item> RUBINATION_WEAPON = createTag(Registries.ITEM, "rubination_weapon");
		public static final TagKey<Item> RUBINATION_ARMOR = createTag(Registries.ITEM, "rubination_armor");
		public static final TagKey<Item> RUBINATION_BOW = createTag(Registries.ITEM, "rubination_bow");
		public static final TagKey<Item> RUBINATION_CROSSBOW = createTag(Registries.ITEM, "rubination_crossbow");
		public static final TagKey<Item> RUBINATION_TRIDENT = createTag(Registries.ITEM, "rubination_trident");
		public static final TagKey<Item> RUBINATION_MACE = createTag(Registries.ITEM, "rubination_mace");

		public static final TagKey<Item> RUBINATABLE = createTag(Registries.ITEM, "rubinatable");

		public static final TagKey<Item> SMALL_BRAZIER_FUEL = createTag(Registries.ITEM, "small_brazier_fuel");
		public static final TagKey<Item> STANDARD_BRAZIER_FUEL = createTag(Registries.ITEM, "standard_brazier_fuel");
		public static final TagKey<Item> GREAT_BRAZIER_FUEL = createTag(Registries.ITEM, "great_brazier_fuel");
		public static final TagKey<Item> OFFERING_BRAZIER_ITEM = createTag(Registries.ITEM, "offering_brazier_item");

		public static final TagKey<Item> ALTAR_OFFERING_ITEM = createTag(Registries.ITEM, "altar_offering_item");
		public static final TagKey<Item> ALTAR_INSCRIPTION_ITEM = createTag(Registries.ITEM, "altar_inscription_item");
		public static final TagKey<Item> ALTAR_RUBINATION_ITEM = createTag(Registries.ITEM, "altar_rubination_item");

		public static final TagKey<Item> SHRINE_STONE_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_candidate");
		public static final TagKey<Item> SHRINE_STONE_STAIRS_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_stairs_candidate");
		public static final TagKey<Item> SHRINE_STONE_SLAB_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_slab_candidate");
		public static final TagKey<Item> SHRINE_STONE_WALL_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_wall_candidate");

		public static final TagKey<Item> COBBLED_SHRINE_STONE_CANDIDATE = createTag(Registries.ITEM, "cobbled_shrine_stone_candidate");
		public static final TagKey<Item> COBBLED_SHRINE_STONE_STAIRS_CANDIDATE = createTag(Registries.ITEM, "cobbled_shrine_stone_stairs_candidate");
		public static final TagKey<Item> COBBLED_SHRINE_STONE_SLAB_CANDIDATE = createTag(Registries.ITEM, "cobbled_shrine_stone_slab_candidate");
		public static final TagKey<Item> COBBLED_SHRINE_STONE_WALL_CANDIDATE = createTag(Registries.ITEM, "cobbled_shrine_stone_wall_candidate");

		public static final TagKey<Item> POLISHED_SHRINE_STONE_CANDIDATE = createTag(Registries.ITEM, "polished_shrine_stone_candidate");
		public static final TagKey<Item> POLISHED_SHRINE_STONE_STAIRS_CANDIDATE = createTag(Registries.ITEM, "polished_shrine_stone_stairs_candidate");
		public static final TagKey<Item> POLISHED_SHRINE_STONE_SLAB_CANDIDATE = createTag(Registries.ITEM, "polished_shrine_stone_slab_candidate");
		public static final TagKey<Item> POLISHED_SHRINE_STONE_WALL_CANDIDATE = createTag(Registries.ITEM, "polished_shrine_stone_wall_candidate");

		public static final TagKey<Item> SHRINE_STONE_PILLAR_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_pillar_candidate");

		public static final TagKey<Item> CHISELED_SHRINE_STONE_BRICKS_CANDIDATE = createTag(Registries.ITEM, "chiseled_shrine_stone_bricks_candidate");

		public static final TagKey<Item> SHRINE_STONE_BRICKS_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_bricks_candidate");
		public static final TagKey<Item> SHRINE_STONE_BRICKS_STAIRS_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_bricks_stairs_candidate");
		public static final TagKey<Item> SHRINE_STONE_BRICKS_SLAB_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_bricks_slab_candidate");
		public static final TagKey<Item> SHRINE_STONE_BRICKS_WALL_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_bricks_wall_candidate");

		public static final TagKey<Item> SHRINE_STONE_TILES_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_tiles_candidate");
		public static final TagKey<Item> SHRINE_STONE_TILES_STAIRS_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_tiles_stairs_candidate");
		public static final TagKey<Item> SHRINE_STONE_TILES_SLAB_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_tiles_slab_candidate");
		public static final TagKey<Item> SHRINE_STONE_TILES_WALL_CANDIDATE = createTag(Registries.ITEM, "shrine_stone_tiles_wall_candidate");

		public static final TagKey<Item> RUBINATION_RUNES = createTag(Registries.ITEM, "rubination_runes");
		public static final TagKey<Item> VASES = createTag(Registries.ITEM, "vases");
		public static final TagKey<Item> VASE_ENGRAVING_MATERIALS = createTag(Registries.ITEM, "vase_engraving_materials");

	}

	public static final class Enchantments {
		public static final TagKey<Enchantment> RUBINATED_CURSES = createTag(Registries.ENCHANTMENT, "rubinated_curses");
	}

	public static final class Dimensions {
		public static final TagKey<DimensionType> COLD_DIMENSION = createTag(Registries.DIMENSION_TYPE, "cold_dimension");
	}

	public static final class Fluids {
		public static final TagKey<Fluid> COLDEST_FLUID = createTag(Registries.FLUID, "coldest_fluid");
	}

	private static <T> TagKey<T> createTag(ResourceKey<? extends Registry<T>> registry, String str) {
		return TagKey.create(registry, RubinatedNether.id(str));
	}
}