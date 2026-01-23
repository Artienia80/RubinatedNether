package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class RNBannerPatterns {
    // Create a ResourceKey for the banner pattern
    public static final ResourceKey<BannerPattern> COGS_KEY = ResourceKey.create(
            Registries.BANNER_PATTERN,
            RubinatedNether.id("cogs")
    );

    // Tag for the banner pattern item (this is what connects the item to the pattern)
    public static final TagKey<BannerPattern> COGS_PATTERN = TagKey.create(
            Registries.BANNER_PATTERN,
            RubinatedNether.id("pattern_item/cogs")
    );
}