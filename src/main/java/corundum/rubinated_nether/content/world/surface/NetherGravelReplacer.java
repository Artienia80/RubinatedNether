package corundum.rubinated_nether.content.world.surface;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class NetherGravelReplacer {
	private static final String GRAVEL = "minecraft:gravel";
	private static final Set<String> NOISES = Set.of("minecraft:patch", "minecraft:gravel_layer");
	private static final Set<String> BIOMES = Set.of(
			"minecraft:basalt_deltas",
			"minecraft:soul_sand_valley",
			"minecraft:nether_wastes"
	);

	private static final Map<SurfaceRules.RuleSource, SurfaceRules.RuleSource> CACHE =
			Collections.synchronizedMap(new IdentityHashMap<>());

	private NetherGravelReplacer() {
	}

	public static SurfaceRules.RuleSource apply(SurfaceRules.RuleSource original) {
		SurfaceRules.RuleSource cached = CACHE.get(original);
		if (cached != null) {
			return cached;
		}
		SurfaceRules.RuleSource result = convert(original);
		CACHE.put(original, result);
		return result;
	}

	private static SurfaceRules.RuleSource convert(SurfaceRules.RuleSource original) {
		try {
			DataResult<JsonElement> encoded = SurfaceRules.RuleSource.CODEC.encodeStart(JsonOps.INSTANCE, original);
			JsonElement json = encoded.result().orElse(null);
			if (json == null) {
				return original;
			}
			String cinder = BuiltInRegistries.BLOCK.getKey(RNBlocks.CINDER.get()).toString();
			int[] replaced = {0};
			JsonElement rewritten = walk(json, false, false, cinder, replaced);
			if (replaced[0] == 0) {
				return original;
			}
			return SurfaceRules.RuleSource.CODEC.parse(JsonOps.INSTANCE, rewritten).result().orElse(original);
		} catch (RuntimeException e) {
			RubinatedNether.LOGGER.warn("Could not rewrite Nether gravel surface rule, keeping original", e);
			return original;
		}
	}

	private static JsonElement walk(JsonElement node, boolean inBiome, boolean inNoise, String cinder, int[] replaced) {
		if (!node.isJsonObject()) {
			return node;
		}
		JsonObject object = node.getAsJsonObject();
		String type = typeOf(object);
		JsonObject copy = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			copy.add(entry.getKey(), entry.getValue());
		}
		if ("minecraft:sequence".equals(type) && object.has("sequence") && object.get("sequence").isJsonArray()) {
			JsonArray array = new JsonArray();
			for (JsonElement child : object.getAsJsonArray("sequence")) {
				array.add(walk(child, inBiome, inNoise, cinder, replaced));
			}
			copy.add("sequence", array);
		} else if ("minecraft:condition".equals(type) && object.has("then_run") && object.has("if_true")) {
			boolean biome = inBiome || isVanillaGravelBiome(object.get("if_true"));
			boolean noise = inNoise || isGravelNoise(object.get("if_true"));
			copy.add("then_run", walk(object.get("then_run"), biome, noise, cinder, replaced));
		} else if ("minecraft:block".equals(type) && inBiome && inNoise && isGravel(object)) {
			JsonObject state = new JsonObject();
			state.addProperty("Name", cinder);
			copy.add("result_state", state);
			replaced[0]++;
		}
		return copy;
	}

	private static String typeOf(JsonObject object) {
		if (!object.has("type") || !object.get("type").isJsonPrimitive()) {
			return "";
		}
		String type = object.get("type").getAsString();
		return type.contains(":") ? type : "minecraft:" + type;
	}

	private static boolean isVanillaGravelBiome(JsonElement condition) {
		if (!condition.isJsonObject()) {
			return false;
		}
		JsonObject object = condition.getAsJsonObject();
		if (!"minecraft:biome".equals(typeOf(object)) || !object.has("biome_is") || !object.get("biome_is").isJsonArray()) {
			return false;
		}
		JsonArray biomes = object.getAsJsonArray("biome_is");
		if (biomes.isEmpty()) {
			return false;
		}
		for (JsonElement biome : biomes) {
			if (!biome.isJsonPrimitive() || !BIOMES.contains(biome.getAsString())) {
				return false;
			}
		}
		return true;
	}

	private static boolean isGravelNoise(JsonElement condition) {
		if (!condition.isJsonObject()) {
			return false;
		}
		JsonObject object = condition.getAsJsonObject();
		return "minecraft:noise_threshold".equals(typeOf(object))
				&& object.has("noise")
				&& object.get("noise").isJsonPrimitive()
				&& NOISES.contains(object.get("noise").getAsString());
	}

	private static boolean isGravel(JsonObject block) {
		if (!block.has("result_state") || !block.get("result_state").isJsonObject()) {
			return false;
		}
		JsonObject state = block.getAsJsonObject("result_state");
		return state.has("Name")
				&& GRAVEL.equals(state.get("Name").getAsString())
				&& !state.has("Properties");
	}
}
