package corundum.rubinated_nether.data.sub_providers;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class RNArchaeologyLoot implements LootTableSubProvider {
	public static final ResourceKey<LootTable> SUSPICIOUS_SHRINE_STONE_RUBBLE = RNBlocks.SUSPICIOUS_SHRINE_STONE_RUBBLE_LOOT;

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
	}
}
