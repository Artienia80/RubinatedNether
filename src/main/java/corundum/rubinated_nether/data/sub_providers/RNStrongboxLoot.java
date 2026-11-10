package corundum.rubinated_nether.data.sub_providers;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.entities.BronzeStrongboxBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RNStrongboxLoot implements LootTableSubProvider {
	public static final ResourceKey<LootTable> STRONGBOX = BronzeStrongboxBlockEntity.DISPENSE_LOOT_TABLE;
	public static final ResourceKey<LootTable> GENERAL = key("chests/bronze_strongbox_general");
	public static final ResourceKey<LootTable> CONSUMABLES = key("chests/bronze_strongbox_consumables");
	public static final ResourceKey<LootTable> VALUABLES = key("chests/bronze_strongbox_valuables");

	private final HolderLookup.Provider registries;

	public RNStrongboxLoot(HolderLookup.Provider registries) {
		this.registries = registries;
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		output.accept(STRONGBOX, LootTable.lootTable().withPool(
				LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(NestedLootTable.lootTableReference(GENERAL).setWeight(60))
						.add(NestedLootTable.lootTableReference(CONSUMABLES).setWeight(25))
						.add(NestedLootTable.lootTableReference(VALUABLES).setWeight(15))
		));
		output.accept(GENERAL, build(generalLines()));
		output.accept(CONSUMABLES, build(consumableLines()));
		output.accept(VALUABLES, build(valuableLines()));
	}

	private List<Line> generalLines() {
		List<Line> lines = new ArrayList<>();
		lines.add(simple(Items.ARROW, 8, 16, 12));
		lines.add(simple(RNItems.BRONZE_SHOT.get(), 2, 6, 8));
		lines.add(simple(RNItems.CRYSTALLIZED_BRONZE_SHOT.get(), 1, 3, 4));
		lines.add(simple(Items.GOLD_INGOT, 4, 8, 6));
		lines.add(simple(Items.IRON_INGOT, 4, 10, 10));
		lines.add(simple(Items.COPPER_INGOT, 8, 16, 12));
		lines.add(simple(RNItems.BRONZE_SCRAP.get(), 3, 8, 8));
		lines.add(simple(Items.OBSIDIAN, 4, 8, 6));
		lines.add(simple(RNBlocks.PUMICE.get(), 4, 12, 8));
		lines.add(simple(RNItems.RUBY.get(), 2, 6, 5));
		lines.add(simple(RNItems.MOLTEN_RUBY.get(), 2, 6, 4));
		lines.add(simple(Items.QUARTZ, 4, 10, 7));
		lines.add(simple(Items.MAGMA_CREAM, 2, 4, 5));
		lines.add(gear(5,
				Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS,
				Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE));
		return lines;
	}

	private List<Line> consumableLines() {
		List<Line> lines = new ArrayList<>();
		lines.add(simple(RNItems.POMEGRANATE.get(), 2, 5, 30));
		lines.add(simple(RNItems.RUBINATED_POMEGRANATE.get(), 1, 1, 6));
		lines.add(simple(Items.COOKED_PORKCHOP, 2, 5, 24));
		lines.add(potion(Potions.FIRE_RESISTANCE, 14));
		lines.add(potion(Potions.STRONG_HEALING, 14));
		lines.add(potion(Potions.STRENGTH, 12));
		return lines;
	}

	private List<Line> valuableLines() {
		List<Line> lines = new ArrayList<>();
		lines.add(simple(RNItems.COGWHEEL.get(), 1, 4, 2));
		lines.add(simple(RNBlocks.RUBY_BLOCK.get(), 1, 2, 12));
		lines.add(simple(RNBlocks.MOLTEN_RUBY_BLOCK.get(), 1, 2, 10));
		lines.add(simple(Items.GOLD_BLOCK, 1, 2, 10));
		lines.add(simple(Items.IRON_BLOCK, 1, 3, 12));
		lines.add(simple(Items.COPPER_BLOCK, 3, 6, 30));
		lines.add(gear(24,
				Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS,
				Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE));
		return lines;
	}

	private static Line simple(ItemLike item, int min, int max, int weight) {
		Line line = new Line(weight);
		line.add(count(LootItem.lootTableItem(item), min, max), 1);
		return line;
	}

	private static Line potion(Holder<Potion> potion, int weight) {
		Line line = new Line(weight);
		line.add(LootItem.lootTableItem(Items.POTION).apply(SetPotionFunction.setPotion(potion)), 1);
		return line;
	}

	private static Line gear(int weight, Item... items) {
		Line line = new Line(weight);
		for (Item item : items) {
			line.add(LootItem.lootTableItem(item), 1);
		}
		return line;
	}

	private static LootTable.Builder build(List<Line> lines) {
		long lcm = 1;
		for (Line line : lines) {
			lcm = lcm(lcm, line.total());
		}
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		for (Line line : lines) {
			long scale = lcm / line.total() * line.weight;
			for (Leaf leaf : line.leaves) {
				pool.add(leaf.entry.setWeight(Math.toIntExact(leaf.weight * scale)));
			}
		}
		return LootTable.lootTable().withPool(pool);
	}

	private static LootPoolSingletonContainer.Builder<?> count(LootPoolSingletonContainer.Builder<?> entry, int min, int max) {
		return entry.apply(SetItemCountFunction.setCount(
				min == max ? ConstantValue.exactly(min) : UniformGenerator.between(min, max)
		));
	}

	private static ResourceKey<LootTable> key(String path) {
		return ResourceKey.create(Registries.LOOT_TABLE, RubinatedNether.id(path));
	}

	private static long gcd(long a, long b) {
		return b == 0 ? a : gcd(b, a % b);
	}

	private static long lcm(long a, long b) {
		return a / gcd(a, b) * b;
	}

	private record Leaf(LootPoolSingletonContainer.Builder<?> entry, int weight) {
	}

	private static final class Line {
		private final List<Leaf> leaves = new ArrayList<>();
		private final int weight;

		Line(int weight) {
			this.weight = weight;
		}

		void add(LootPoolSingletonContainer.Builder<?> entry, int weight) {
			this.leaves.add(new Leaf(entry, weight));
		}

		long total() {
			long total = 0;
			for (Leaf leaf : this.leaves) {
				total += leaf.weight;
			}
			return total;
		}
	}
}
