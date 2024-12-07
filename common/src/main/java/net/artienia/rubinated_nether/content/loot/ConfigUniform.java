package net.artienia.rubinated_nether.content.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.artienia.rubinated_nether.content.RNLootNumberProviders;
import net.artienia.rubinated_nether.utils.NamedIntSupplier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.IntSupplier;

public record ConfigUniform(NamedIntSupplier min, NamedIntSupplier max) implements NumberProvider {

	public static final net.minecraft.world.level.storage.loot.Serializer<ConfigUniform> SERIALIZER = new Serializer();

	public static ConfigUniform of(String minField, String maxField) {
		return new ConfigUniform(
			new NamedIntSupplier(minField, ofConfigField(minField)),
			new NamedIntSupplier(maxField, ofConfigField(maxField))
		);
	}

	@Override
	public float getFloat(LootContext lootContext) {
		return lootContext.getRandom().nextIntBetweenInclusive(min.getAsInt(), max.getAsInt());
	}

	@Override
	public LootNumberProviderType getType() {
		return RNLootNumberProviders.CONFIG_UNIFORM.get();
	}

	private static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ConfigUniform> {

		@Override
		public void serialize(JsonObject json, ConfigUniform value, JsonSerializationContext serializationContext) {
			json.addProperty("min_field", value.min.name());
			json.addProperty("max_field", value.max.name());
		}

		@Override
		public ConfigUniform deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
			String minField = json.get("min_field").getAsString();
			String maxField = json.get("max_field").getAsString();

			return new ConfigUniform(
				new NamedIntSupplier(minField, ofConfigField(minField)),
				new NamedIntSupplier(maxField, ofConfigField(maxField))
			);
		}
	}

	private static IntSupplier ofConfigField(String fieldName) {
		return RubinatedNether.CONFIGURATOR.getConfig(RNConfig.class)
			.getEntry(fieldName)
			.<IntSupplier>map(entry -> () -> {
				try {
					return entry.field().getInt(null);
				} catch (IllegalAccessException e) {
					RubinatedNether.LOGGER.error("Error reading config field {}", fieldName, e);
					return 0;
				}
			}).orElse(() -> 0);
	}

}
