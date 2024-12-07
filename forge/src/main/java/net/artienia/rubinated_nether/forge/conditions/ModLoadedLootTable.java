package net.artienia.rubinated_nether.forge.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.fml.ModList;

public class ModLoadedLootTable implements LootItemCondition {
	public static final LootItemConditionType TYPE = new LootItemConditionType(new ModLoadedLootTable.Serializer());

	private final String id;
	private final boolean isLoaded;

	private ModLoadedLootTable(final String id, final boolean isLoaded)
	{
		this.id = id;
		this.isLoaded = isLoaded;
	}

	@Override
	public LootItemConditionType getType() {
		return TYPE;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return isLoaded == ModList.get().isLoaded(id);
	}

	public static Builder builder(final String id, final boolean isLoaded) {
		return new Builder(id, isLoaded);
	}

	public static class Builder implements LootItemCondition.Builder {
		private final String id;
		private final boolean isLoaded;

		public Builder(String id, boolean isLoaded) {
			if (id == null)
				throw new IllegalArgumentException("Target loot table must not be null");
			this.id = id;
			this.isLoaded = isLoaded;
		}

		@Override
		public LootItemCondition build() {
			return new ModLoadedLootTable(this.id, this.isLoaded);
		}
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ModLoadedLootTable>
	{
		@Override
		public void serialize(JsonObject object, ModLoadedLootTable instance, JsonSerializationContext ctx)
		{
			object.addProperty("mod", instance.id);
			object.addProperty("is_mod_loaded", instance.isLoaded);
		}

		@Override
		public ModLoadedLootTable deserialize(JsonObject object, JsonDeserializationContext ctx)
		{
			return new ModLoadedLootTable(GsonHelper.getAsString(object, "mod"), GsonHelper.getAsBoolean(object, "is_mod_loaded"));
		}
	}
}
