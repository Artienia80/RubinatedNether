package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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

		public static final TagKey<Block> RUBY_LASER_TRANSPARENT = createTag(Registries.BLOCK, "ruby_laser_transparent");

		public static final TagKey<Block> RUBY_LASER_NO_SIGNAL = createTag(Registries.BLOCK, "ruby_laser_no_signal");

		public static final TagKey<Block> COLDEST_ICE = createTag(Registries.BLOCK, "coldest_ice");

		public static final TagKey<Block> LIT_SOUL_BLOCKS = createTag(Registries.BLOCK, "lit_soul_blocks");

		public static final TagKey<Block> CRYSTALLIZED_BLOCKS = createTag(Registries.BLOCK, "crystallized_blocks");

		public static final TagKey<Block> CRYSTALLIZATION_CATALYST = createTag(Registries.BLOCK, "crystallization_catalyst");

		public static final TagKey<Block> RAINBOW_LASER = createTag(Registries.BLOCK, "rainbow_laser");

		public static final TagKey<Block> MINEABLE_WITH_DRILL = createTag(Registries.BLOCK, "mineable_with_drill");
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

		public static final TagKey<Item> RUBINATABLE = createTag(Registries.ITEM, "rubinatable");

		public static final TagKey<Item> AXES = createTag(Registries.ITEM, "axes");


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
