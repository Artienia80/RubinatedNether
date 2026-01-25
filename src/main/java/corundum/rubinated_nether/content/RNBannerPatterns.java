package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RNBannerPatterns {
    // List to store all banner pattern keys for automatic registration
    public static final List<BannerPatternEntry> BANNER_PATTERN_ENTRIES = new ArrayList<>();

    // Banner Pattern Keys and Tags
    public static final BannerPatternEntry COGS = register("cogs", "Cogs");
    public static final BannerPatternEntry RUNE_TOOL = register("rune_tool", "Instrumenta");
    public static final BannerPatternEntry RUNE_ARMOR = register("rune_armor", "Armatura");
    public static final BannerPatternEntry RUNE_WEAPON = register("rune_weapon", "Gladii");
    public static final BannerPatternEntry RUNE_BOW = register("rune_bow", "Arcus");
    public static final BannerPatternEntry RUNE_CROSSBOW = register("rune_crossbow", "Balistae");
    public static final BannerPatternEntry RUNE_MACE = register("rune_mace", "Clavae");
    public static final BannerPatternEntry RUNE_TRIDENT = register("rune_trident", "Fuscinae");

    private static BannerPatternEntry register(String name, String displayName) {
        ResourceKey<BannerPattern> key = ResourceKey.create(
                Registries.BANNER_PATTERN,
                RubinatedNether.id(name)
        );

        TagKey<BannerPattern> tag = TagKey.create(
                Registries.BANNER_PATTERN,
                RubinatedNether.id("pattern_item/" + name)
        );

        String translationKey = "block.rubinated_nether.banner." + name;

        BannerPatternEntry entry = new BannerPatternEntry(key, tag, translationKey, displayName);
        BANNER_PATTERN_ENTRIES.add(entry);
        return entry;
    }

    public record BannerPatternEntry(
            ResourceKey<BannerPattern> key,
            TagKey<BannerPattern> tag,
            String translationKey,
            String displayName
    ) {}

    // Data provider for banner patterns
    public static class Provider implements DataProvider {
        private final PackOutput output;

        public Provider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (BannerPatternEntry entry : BANNER_PATTERN_ENTRIES) {
                String patternName = entry.key().location().getPath();

                // Generate banner_pattern JSON
                JsonObject patternJson = new JsonObject();
                patternJson.addProperty("asset_id", RubinatedNether.MODID + ":" + patternName);
                patternJson.addProperty("translation_key", entry.translationKey());

                Path patternPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(RubinatedNether.MODID)
                        .resolve("banner_pattern")
                        .resolve(patternName + ".json");

                futures.add(DataProvider.saveStable(cache, patternJson, patternPath));

                // Generate tag JSON
                JsonObject tagJson = new JsonObject();
                JsonArray values = new JsonArray();
                values.add(entry.key().location().toString());
                tagJson.add("values", values);

                Path tagPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(RubinatedNether.MODID)
                        .resolve("tags")
                        .resolve("banner_pattern")
                        .resolve("pattern_item")
                        .resolve(patternName + ".json");

                futures.add(DataProvider.saveStable(cache, tagJson, tagPath));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        }

        @Override
        public String getName() {
            return "Banner Patterns: " + RubinatedNether.MODID;
        }
    }
}