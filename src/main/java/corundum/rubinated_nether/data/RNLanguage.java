package corundum.rubinated_nether.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RNLanguage implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final PackOutput output;

    public RNLanguage(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return CompletableFuture.allOf(
                generateLanguageFile("aurichalcum", "en_us", "Bronze", "Aurichalcum"),
                generateLanguageFile("aurichalcum", "zh_cn", "青铜", "山铜"),
                generateLanguageFile("aurichalcum", "pt_br", "Bronze", "Oricalco")
                // Add more language files here as needed
        );
    }

    private CompletableFuture<Void> generateLanguageFile(String packName, String langFile, String searchTerm, String replaceTerm) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Get the project root by going up from the output folder
                Path projectRoot = output.getOutputFolder();
                while (projectRoot != null && !Files.exists(projectRoot.resolve("src/main/resources"))) {
                    projectRoot = projectRoot.getParent();
                }

                if (projectRoot == null) {
                    RubinatedNether.LOGGER.error("Could not find project root directory");
                    return;
                }

                // Read the base language file
                Path baseLanguageFile = projectRoot.resolve("src/main/resources/assets/rubinated_nether/lang/" + langFile + ".json");

                if (!Files.exists(baseLanguageFile)) {
                    RubinatedNether.LOGGER.warn("Base language file not found at: {} (skipping)", baseLanguageFile);
                    return;
                }

                String jsonContent = Files.readString(baseLanguageFile);
                JsonObject baseTranslations = GSON.fromJson(jsonContent, JsonObject.class);

                // Create new translations with replacements
                JsonObject modifiedTranslations = new JsonObject();

                for (Map.Entry<String, com.google.gson.JsonElement> entry : baseTranslations.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().getAsString();

                    // Skip translation for resourcepack keys (these are pack names/descriptions)
                    if (key.startsWith("resourcepack.")) {
                        continue;
                    }

                    // Replace search term with replace term (case-sensitive)
                    String modifiedValue = value.replace(searchTerm, replaceTerm);

                    // Also handle lowercase versions
                    String searchLower = searchTerm.toLowerCase();
                    String replaceLower = replaceTerm.toLowerCase();
                    modifiedValue = modifiedValue.replace(searchLower, replaceLower);

                    // Only include entries where the value actually changed
                    if (!value.equals(modifiedValue)) {
                        modifiedTranslations.addProperty(key, modifiedValue);
                    }
                }

                // Write to the resource pack location
                Path resourcePackPath = projectRoot.resolve("src/main/resources/resourcepacks/" + packName + "/assets/rubinated_nether/lang/" + langFile + ".json");

                // Create directories if they don't exist
                Files.createDirectories(resourcePackPath.getParent());

                // Write the modified JSON
                String outputJson = GSON.toJson(modifiedTranslations);
                Files.writeString(resourcePackPath, outputJson);

                RubinatedNether.LOGGER.info("Successfully generated {} language file ({}) at: {}", packName, langFile, resourcePackPath);

            } catch (IOException e) {
                RubinatedNether.LOGGER.error("Failed to generate {} language file ({})", packName, langFile, e);
            }
        });
    }

    @Override
    public @NotNull String getName() {
        return "Resource Pack Languages";
    }
}