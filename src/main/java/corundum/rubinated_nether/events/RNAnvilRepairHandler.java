package corundum.rubinated_nether.events;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.entity.player.AnvilEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class RNAnvilRepairHandler {

	public static void register() {
		AnvilEvents.UPDATE.register(RNAnvilRepairHandler::onAnvilUpdate);
		AnvilEvents.USE.register(RNAnvilRepairHandler::onAnvilUse);
	}

	private static EventResult onAnvilUpdate(
			ItemStack primaryItem,
			ItemStack secondaryItem,
			fuzs.puzzleslib.api.event.v1.data.MutableValue<ItemStack> outputItem,
			String itemName,
			fuzs.puzzleslib.api.event.v1.data.MutableInt enchantmentCost,
			fuzs.puzzleslib.api.event.v1.data.MutableInt materialCost,
			net.minecraft.world.entity.player.Player player) {

		float efficiencyMultiplier;
		float levelCostPerUnit;
		if (secondaryItem.is(RNItems.RUBY_ITEM.get())) {
			efficiencyMultiplier = 1.0f;
			levelCostPerUnit = 1.0f;
		} else if (secondaryItem.is(RNItems.RUBY_SHARD_ITEM.get())) {
			efficiencyMultiplier = 1.0f / 9.0f;
			levelCostPerUnit = 0.1f;
		} else if (secondaryItem.is(RNBlocks.RUBY_BLOCK.get().asItem())) {
			efficiencyMultiplier = 9.0f;
			levelCostPerUnit = 10.0f;
		} else {
			return EventResult.PASS;
		}

		if (!primaryItem.isDamageableItem() || !primaryItem.isDamaged()) {
			return EventResult.PASS;
		}

		if (!hasRubination(primaryItem)) {
			return EventResult.PASS;
		}

		boolean hasFragility = hasFragilityCurse(primaryItem);

		ItemStack repairedItem = primaryItem.copy();
		int maxDamage = repairedItem.getMaxDamage();
		int currentDamage = repairedItem.getDamageValue();

		int materialCount = secondaryItem.getCount();

		float baseRepairPerRuby = 114.0f;

		if (hasFragility) {
			baseRepairPerRuby *= 3;
		}

		float repairPerUnit = baseRepairPerRuby * efficiencyMultiplier;

		int unitsNeeded = (int) Math.ceil(currentDamage / repairPerUnit);
		int unitsToConsume = Math.min(unitsNeeded, materialCount);

		int totalRepair = (int) (repairPerUnit * unitsToConsume);

		int newDamage = Math.max(0, currentDamage - totalRepair);
		repairedItem.setDamageValue(newDamage);

		if (itemName != null && !itemName.isEmpty() && !itemName.equals(primaryItem.getHoverName().getString())) {
			repairedItem.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
		}

		outputItem.accept(repairedItem);

		int cost = (int) Math.ceil(unitsToConsume * levelCostPerUnit);
		if (itemName != null && !itemName.isEmpty() && !itemName.equals(primaryItem.getHoverName().getString())) {
			cost += 1;
		}
		enchantmentCost.accept(cost);

		materialCost.accept(unitsToConsume);

		return EventResult.ALLOW;
	}

	private static boolean hasRubination(ItemStack stack) {
		var enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

		for (var entry : enchantments.entrySet()) {
			if (entry.getKey().is(RNTags.Enchantments.RUBINATED_CURSES) && entry.getIntValue() > 0) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasFragilityCurse(ItemStack stack) {
		var enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

		for (var entry : enchantments.entrySet()) {
			if (entry.getKey().is(RNEnchantments.FRAGILITY_CURSE) && entry.getIntValue() > 0) {
				return true;
			}
		}

		return false;
	}

	private static void onAnvilUse(
			net.minecraft.world.entity.player.Player player,
			ItemStack primaryItem,
			ItemStack secondaryItem,
			ItemStack outputItem,
			fuzs.puzzleslib.api.event.v1.data.MutableFloat breakChance) {

		if (secondaryItem.is(RNItems.RUBY_ITEM.get()) ||
				secondaryItem.is(RNItems.RUBY_SHARD_ITEM.get()) ||
				secondaryItem.is(RNBlocks.RUBY_BLOCK.get().asItem())) {
			breakChance.mapFloat(f -> 0.08f);
		}
	}
}