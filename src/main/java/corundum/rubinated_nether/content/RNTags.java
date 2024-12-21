package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;

/**
 * Utility class for accessing modded tags
 */
public final class RNTags {

	public static final class Blocks {

		public static final TagKey<Block> RUBY_GLASS = createTag(Registries.BLOCK, "ruby_glass");

		public static final TagKey<Block> RUBY_GLASS_PANES = createTag(Registries.BLOCK, "ruby_glass_panes");

		public static final TagKey<Block> RUBY_LASER_TRANSPARENT = createTag(Registries.BLOCK, "ruby_laser_transparent");

		public static final TagKey<Block> RUBY_LASER_NO_SIGNAL = createTag(Registries.BLOCK, "ruby_laser_no_signal");

		public static final TagKey<Block> COLDEST_ICE = createTag(Registries.BLOCK, "coldest_ice");

	}

	public static final class Items {

		public static final TagKey<Item> RUBIES = createTag(Registries.ITEM, "rubies");

		public static final TagKey<Item> RUBY_GLASS = createTag(Registries.ITEM, "ruby_glass_tag");

		public static final TagKey<Item> RUBY_GLASS_PANES = createTag(Registries.ITEM, "ruby_glass_pane_tag");

		public static final TagKey<Item> RUBY_SHARDS = createTag(Registries.ITEM, "ruby_shard_tag");

		public static final TagKey<Item> LOW_RUBY = createTag(Registries.ITEM, "low_ruby");
	}

	public static final class Dimensions {
		public static final TagKey<DimensionType> COLD_DIMENSION = createTag(Registries.DIMENSION_TYPE, "cold_dimension");
	}

	public static final class Fluids {
		public static final TagKey<Fluid> COLDEST_FLUID = createTag(Registries.FLUID, "coldest_fluid");
	}

	private static <T> TagKey<T> createTag(ResourceKey<? extends Registry<T>> registry, String str) {
		return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, str));
	}
}
