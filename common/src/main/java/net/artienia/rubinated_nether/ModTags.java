package net.artienia.rubinated_nether;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static net.artienia.rubinated_nether.RubinatedNether.id;

/**
 * Utility class for accessing modded tags
 */
public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> RUBY_GLASS = TagKey.create(Registries.BLOCK, id("ruby_glass"));

        public static final TagKey<Block> RUBY_GLASS_PANES = TagKey.create(Registries.BLOCK, id("ruby_glass_panes"));

        public static final TagKey<Block> RUBY_LASER_TRANSPARENT = TagKey.create(Registries.BLOCK, id("ruby_laser_transparent"));
    }

    public static class Items {

        public static final TagKey<Item> RUBIES = TagKey.create(Registries.ITEM, id("rubies"));

        public static final TagKey<Item> RUBY_GLASS = TagKey.create(Registries.ITEM, id("ruby_glass_tag"));

        public static final TagKey<Item> RUBY_GLASS_PANES = TagKey.create(Registries.ITEM, id("ruby_glass_pane_tag"));

        public static final TagKey<Item> RUBY_SHARDS = TagKey.create(Registries.ITEM, id("ruby_shard_tag"));
    }

}
