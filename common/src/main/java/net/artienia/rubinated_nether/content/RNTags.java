package net.artienia.rubinated_nether.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;

import static net.artienia.rubinated_nether.RubinatedNether.id;

/**
 * Utility class for accessing modded tags
 */
public final class RNTags {

	public static final class Blocks {

		public static final TagKey<Block> RUBY_GLASS = TagKey.create(Registries.BLOCK, id("ruby_glass"));

		public static final TagKey<Block> RUBY_GLASS_PANES = TagKey.create(Registries.BLOCK, id("ruby_glass_panes"));

		public static final TagKey<Block> RUBY_LASER_TRANSPARENT = TagKey.create(Registries.BLOCK, id("ruby_laser_transparent"));

		public static final TagKey<Block> RUBY_LASER_NO_SIGNAL = TagKey.create(Registries.BLOCK, id("ruby_laser_no_signal"));

		public static final TagKey<Block> COLDEST_ICE = TagKey.create(Registries.BLOCK, id("coldest_ice"));

	}

	public static final class Items {

		public static final TagKey<Item> RUBIES = TagKey.create(Registries.ITEM, id("rubies"));

		public static final TagKey<Item> RUBY_GLASS = TagKey.create(Registries.ITEM, id("ruby_glass_tag"));

		public static final TagKey<Item> RUBY_GLASS_PANES = TagKey.create(Registries.ITEM, id("ruby_glass_pane_tag"));

		public static final TagKey<Item> RUBY_SHARDS = TagKey.create(Registries.ITEM, id("ruby_shard_tag"));

		public static final TagKey<Item> LOW_RUBY = TagKey.create(Registries.ITEM, id("low_ruby"));
	}

	public static final class Dimensions {
		public static final TagKey<DimensionType> COLD_DIMENSION = TagKey.create(Registries.DIMENSION_TYPE, id("cold_dimension"));
	}

}
