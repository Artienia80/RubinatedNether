package corundum.rubinated_nether.data.sub_providers;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class RNArchaeologyLoot implements LootTableSubProvider {
	private final HolderLookup.Provider registries;

	public RNArchaeologyLoot(HolderLookup.Provider registries) {
		this.registries = registries;
	}

	public static final ResourceKey<LootTable> SUSPICIOUS_SHRINE_STONE_RUBBLE = RNBlocks.SUSPICIOUS_SHRINE_STONE_RUBBLE_LOOT;
	public static final ResourceKey<LootTable> SUSPICIOUS_CINDER = RNBlocks.SUSPICIOUS_CINDER_LOOT;

	private static final int CINDER_BOOK_WEIGHT = 12;
	private static final int RUBY_SHARD_WEIGHT = 250;
	private static final int IRON_NUGGET_WEIGHT = 250;
	private static final int BRONZE_SCRAP_WEIGHT = 180;
	private static final int COGS_BANNER_PATTERN_WEIGHT = 60;
	private static final int CARVED_RUNE_WEIGHT_EACH = 1;

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));

		pool.add(LootItem.lootTableItem(RNItems.RUBY_SHARD.get())
				.setWeight(RUBY_SHARD_WEIGHT)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))));
		pool.add(LootItem.lootTableItem(Items.IRON_NUGGET)
				.setWeight(IRON_NUGGET_WEIGHT)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))));
		pool.add(LootItem.lootTableItem(RNItems.BRONZE_SCRAP.get())
				.setWeight(BRONZE_SCRAP_WEIGHT)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))));
		pool.add(LootItem.lootTableItem(RNItems.COGS_BANNER_PATTERN.get())
				.setWeight(COGS_BANNER_PATTERN_WEIGHT));

		for (Rubination rubination : Rubination.carvableValues()) {
			ItemStack carved = RuneCarvingHelper.withCarving(new ItemStack(RNItems.RUNE.get()), rubination);
			var entry = LootItem.lootTableItem((Item) RNItems.RUNE.get())
					.setWeight(CARVED_RUNE_WEIGHT_EACH)
					.apply(SetComponentsFunction.setComponent(RNDataComponents.RUNE_CARVING.get(), rubination));
			var customModelData = carved.get(DataComponents.CUSTOM_MODEL_DATA);
			if (customModelData != null) {
				entry.apply(SetComponentsFunction.setComponent(DataComponents.CUSTOM_MODEL_DATA, customModelData));
			}
			pool.add(entry);
		}

		output.accept(SUSPICIOUS_SHRINE_STONE_RUBBLE, LootTable.lootTable().withPool(pool));
		output.accept(SUSPICIOUS_CINDER, LootTable.lootTable().withPool(cinderPool()));
	}

	private LootPool.Builder cinderPool() {
		HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));

		pool.add(stack(Items.FLINT, 200, 1, 3));
		pool.add(stack(Items.GOLD_NUGGET, 160, 2, 6));
		pool.add(stack(Items.GLOWSTONE_DUST, 140, 1, 3));
		pool.add(stack(Items.QUARTZ, 140, 1, 3));
		pool.add(stack(Items.CHARCOAL, 120, 1, 3));
		pool.add(stack(RNBlocks.CINDER.get(), 100, 1, 3));

		pool.add(stack(Items.GOLD_INGOT, 50, 1, 2));
		pool.add(stack(Items.MAGMA_CREAM, 40, 1, 2));
		pool.add(stack(Items.BLAZE_POWDER, 40, 1, 3));
		pool.add(stack(Items.NETHER_WART, 40, 1, 3));
		pool.add(stack(Items.FIRE_CHARGE, 35, 1, 2));
		pool.add(stack(Items.GOLDEN_CARROT, 30, 1, 3));
		pool.add(stack(Items.GOLDEN_BOOTS, 12, 1, 1));
		pool.add(stack(Items.GOLDEN_SWORD, 12, 1, 1));
		pool.add(stack(Items.FLINT_AND_STEEL, 10, 1, 1));
		pool.add(stack(Items.GOLDEN_APPLE, 6, 1, 1));
		pool.add(stack(Items.PIGLIN_BANNER_PATTERN, 6, 1, 1));
		pool.add(stack(Items.PIGLIN_HEAD, 3, 1, 1));

		pool.add(book(enchantments, Enchantments.FIRE_PROTECTION, 4));
		pool.add(book(enchantments, Enchantments.FEATHER_FALLING, 4));
		pool.add(book(enchantments, Enchantments.SOUL_SPEED, 3));
		return pool;
	}

	private static LootPoolSingletonContainer.Builder<?> stack(ItemLike item, int weight, int min, int max) {
		LootPoolSingletonContainer.Builder<?> entry = LootItem.lootTableItem(item).setWeight(weight);
		if (max > 1) {
			entry.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
		}
		return entry;
	}

	private static LootPoolSingletonContainer.Builder<?> book(HolderLookup.RegistryLookup<Enchantment> enchantments, ResourceKey<Enchantment> enchantment, int maxLevel) {
		return LootItem.lootTableItem(Items.ENCHANTED_BOOK)
				.setWeight(CINDER_BOOK_WEIGHT)
				.apply(new SetEnchantmentsFunction.Builder()
						.withEnchantment(enchantments.getOrThrow(enchantment), UniformGenerator.between(1, maxLevel)));
	}
}
