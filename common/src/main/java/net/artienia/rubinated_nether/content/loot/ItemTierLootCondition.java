package net.artienia.rubinated_nether.content.loot;

import com.google.gson.*;
import net.artienia.rubinated_nether.content.RNLootConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record ItemTierLootCondition(int minLevel, int maxLevel) implements LootItemCondition {

	public static LootItemConditionType createType() {
		return new LootItemConditionType(new Serializer());
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public LootItemConditionType getType() {
		return RNLootConditions.ITEM_TIER.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		ItemStack stack = lootContext.getParam(LootContextParams.TOOL);
		int level;
		return stack.getItem() instanceof TieredItem tiered
			&& (level = tiered.getTier().getLevel()) <= maxLevel
			&& level >= minLevel;
	}

	private static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ItemTierLootCondition> {

		@Override
		public void serialize(JsonObject json, ItemTierLootCondition value, JsonSerializationContext serializationContext) {
			json.add("min_level", new JsonPrimitive(value.minLevel));
			json.add("max_level", new JsonPrimitive(value.maxLevel));
		}

		@Override
		public ItemTierLootCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
			return new ItemTierLootCondition(
				json.get("min_level").getAsInt(),
				json.get("max_level").getAsInt()
			);
		}
	}

	public static class Builder implements LootItemCondition.Builder {

		private int minLevel;
		private int maxLevel = Integer.MAX_VALUE;

		private Builder() {}

		public Builder minTier(int tierLevel) {
			this.minLevel = tierLevel;
			return this;
		}

		public Builder minTier(Tier tier) {
			return minTier(tier.getLevel());
		}

		public Builder maxTier(int tierLevel) {
			this.maxLevel = tierLevel;
			return this;
		}

		public Builder maxTier(Tier tier) {
			return maxTier(tier.getLevel());
		}

		@Override
		public @NotNull ItemTierLootCondition build() {
			return new ItemTierLootCondition(minLevel, maxLevel);
		}
	}

}
