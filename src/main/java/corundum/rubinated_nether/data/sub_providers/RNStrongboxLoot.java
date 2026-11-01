package corundum.rubinated_nether.data.sub_providers;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBannerPatterns;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.entities.BronzeStrongboxBlockEntity;
import corundum.rubinated_nether.content.trim.RNTrimMaterials;
import corundum.rubinated_nether.content.trim.RNTrimPatterns;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RNStrongboxLoot implements LootTableSubProvider {
	public static final ResourceKey<LootTable> STRONGBOX = BronzeStrongboxBlockEntity.DISPENSE_LOOT_TABLE;
	public static final ResourceKey<LootTable> BASIC = key("chests/bronze_strongbox_basic");
	public static final ResourceKey<LootTable> VALUABLES = key("chests/bronze_strongbox_valuables");

	private static final String[] STAGES = {"", "discolored_", "corroded_", "tarnished_", "crystallized_"};

	private static final List<List<ResourceKey<TrimPattern>>> TRIM_PATTERNS_BY_CATEGORY = List.of(
			List.of(RNTrimPatterns.GREED_RUNE, RNTrimPatterns.GLUTTONY_RUNE, RNTrimPatterns.SLOTH_RUNE),
			List.of(RNTrimPatterns.WRATH_RUNE, RNTrimPatterns.ENVY_RUNE, RNTrimPatterns.VAINGLORY_RUNE),
			List.of(RNTrimPatterns.PRIDE_RUNE, RNTrimPatterns.ACEDIA_RUNE, RNTrimPatterns.LUXURIA_RUNE),
			List.of(RNTrimPatterns.INSIDIAE_RUNE, RNTrimPatterns.SUPERBIA_RUNE, RNTrimPatterns.TRISTIA_RUNE),
			List.of(RNTrimPatterns.STUDIOSE_RUNE, RNTrimPatterns.ARDENTER_RUNE, RNTrimPatterns.NIMIS_RUNE),
			List.of(RNTrimPatterns.IRA_RUNE, RNTrimPatterns.INVIDIA_RUNE, RNTrimPatterns.GULA_RUNE),
			List.of(RNTrimPatterns.IGNAVIA_RUNE, RNTrimPatterns.KENODOXIA_RUNE, RNTrimPatterns.PHILARGYRIA_RUNE)
	);

	private static final List<ResourceKey<TrimMaterial>> TRIM_MATERIALS = List.of(
			TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE,
			TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND,
			TrimMaterials.LAPIS, TrimMaterials.AMETHYST,
			RNTrimMaterials.BRONZE, RNTrimMaterials.DISCOLORED_BRONZE, RNTrimMaterials.CORRODED_BRONZE,
			RNTrimMaterials.TARNISHED_BRONZE, RNTrimMaterials.CRYSTALLIZED_BRONZE
	);

	private static final List<RNBannerPatterns.BannerPatternEntry> BANNER_PATTERNS = List.of(
			RNBannerPatterns.RUNE_TOOL, RNBannerPatterns.RUNE_ARMOR, RNBannerPatterns.RUNE_WEAPON,
			RNBannerPatterns.RUNE_BOW, RNBannerPatterns.RUNE_CROSSBOW, RNBannerPatterns.RUNE_MACE,
			RNBannerPatterns.RUNE_TRIDENT, RNBannerPatterns.COGS
	);

	private final HolderLookup.Provider registries;

	public RNStrongboxLoot(HolderLookup.Provider registries) {
		this.registries = registries;
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		output.accept(STRONGBOX, LootTable.lootTable().withPool(
				LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(NestedLootTable.lootTableReference(BASIC).setWeight(2))
						.add(NestedLootTable.lootTableReference(VALUABLES).setWeight(1))
		));
		output.accept(BASIC, build(basicLines()));
		output.accept(VALUABLES, build(valuableLines()));
	}

	private List<Line> basicLines() {
		List<Line> lines = new ArrayList<>();
		lines.add(simple(Items.ARROW, 4, 16));
		lines.add(simple(RNItems.BRONZE_SHOT.get(), 1, 6));
		lines.add(simple(RNItems.CRYSTALLIZED_BRONZE_SHOT.get(), 1, 2));
		lines.add(simple(Items.GOLD_INGOT, 8, 12));
		lines.add(simple(Items.IRON_INGOT, 8, 12));
		lines.add(simple(Items.COPPER_INGOT, 8, 12));
		lines.add(simple(RNItems.BRONZE_SCRAP.get(), 4, 12));
		lines.add(splashPotion(Potions.STRONG_HEALING));
		lines.add(splashPotion(Potions.STRONG_REGENERATION));
		lines.add(splashPotion(Potions.FIRE_RESISTANCE));
		lines.add(simple(RNItems.BRONZE_POWDER.get(), 8, 16));
		lines.add(simple(RNItems.BRONZE_ROD.get(), 1, 6));
		lines.add(simple(Items.NETHER_WART, 8, 16));
		lines.add(simple(RNItems.COGS_BANNER_PATTERN.get()));
		lines.add(goldenGear());
		lines.add(simple(Items.OBSIDIAN, 12, 12));
		lines.add(simple(Items.FLINT, 1, 3));
		lines.add(simple(RNBlocks.SOAKSTONE.get(), 4, 12));
		lines.add(simple(Items.NETHERRACK, 32, 48));
		lines.add(simple(RNItems.MOLTEN_RUBY.get(), 4, 16));
		lines.add(simple(RNBlocks.MOLTEN_RUBY_BLOCK.get()));
		lines.add(randomBanner());
		lines.add(bronzeVariants(new String[]{"lantern", "lamp", "bulb", "chandelier"}));
		return lines;
	}

	private List<Line> valuableLines() {
		List<Line> lines = new ArrayList<>();
		lines.add(simple(RNItems.RUBY.get(), 8, 24));
		lines.add(simple(RNBlocks.RUBY_BLOCK.get(), 1, 3));

		lines.add(simple(RNItems.COGWHEEL.get(), 1, 4));

		lines.add(ominousBottle());

		Line blocks = new Line();
		for (Item block : List.of(Items.IRON_BLOCK, Items.COPPER_BLOCK, Items.GOLD_BLOCK)) {
			blocks.add(count(LootItem.lootTableItem(block), 1, 4), 1);
		}
		lines.add(blocks);

		lines.add(trimmedDiamondArmor());

		Line tools = new Line();
		for (Item tool : List.of(Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE)) {
			tools.add(LootItem.lootTableItem(tool), 1);
		}
		lines.add(tools);

		lines.add(redstoneBlock());
		return lines;
	}

	private static Line simple(ItemLike item) {
		Line line = new Line();
		line.add(LootItem.lootTableItem(item), 1);
		return line;
	}

	private static Line simple(ItemLike item, int min, int max) {
		Line line = new Line();
		line.add(count(LootItem.lootTableItem(item), min, max), 1);
		return line;
	}

	private static Line splashPotion(Holder<Potion> potion) {
		Line line = new Line();
		line.add(count(LootItem.lootTableItem(Items.SPLASH_POTION).apply(SetPotionFunction.setPotion(potion)), 1, 3), 1);
		return line;
	}

	private static Line goldenGear() {
		Line line = new Line();
		for (Item item : List.of(
				Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS,
				Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE)) {
			line.add(LootItem.lootTableItem(item), 1);
		}
		return line;
	}

	private static Line ominousBottle() {
		Line line = new Line();
		for (int amplifier = 0; amplifier <= 4; amplifier++) {
			line.add(LootItem.lootTableItem(Items.OMINOUS_BOTTLE)
					.apply(SetComponentsFunction.setComponent(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, amplifier)), 1);
		}
		return line;
	}

	private Line randomBanner() {
		Line line = new Line();
		for (DyeColor base : DyeColor.values()) {
			for (RNBannerPatterns.BannerPatternEntry pattern : BANNER_PATTERNS) {
				Holder<BannerPattern> patternHolder = this.registries.lookupOrThrow(Registries.BANNER_PATTERN).getOrThrow(pattern.key());
				for (DyeColor patternColor : DyeColor.values()) {
					if (patternColor == base) {
						continue;
					}
					line.add(LootItem.lootTableItem(BannerBlock.byColor(base))
							.apply(SetComponentsFunction.setComponent(
									DataComponents.BANNER_PATTERNS,
									new BannerPatternLayers(List.of(new BannerPatternLayers.Layer(patternHolder, patternColor)))
							)), 1);
				}
			}
		}
		return line;
	}

	private Line trimmedDiamondArmor() {
		var materials = this.registries.lookupOrThrow(Registries.TRIM_MATERIAL);
		var patterns = this.registries.lookupOrThrow(Registries.TRIM_PATTERN);
		Line line = new Line();
		for (Item piece : List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS)) {
			for (List<ResourceKey<TrimPattern>> category : TRIM_PATTERNS_BY_CATEGORY) {
				for (ResourceKey<TrimPattern> pattern : category) {
					for (ResourceKey<TrimMaterial> material : TRIM_MATERIALS) {
						line.add(LootItem.lootTableItem(piece)
								.apply(SetComponentsFunction.setComponent(
										DataComponents.TRIM,
										new ArmorTrim(materials.getOrThrow(material), patterns.getOrThrow(pattern))
								)), 1);
					}
				}
			}
		}
		return line;
	}

	private static Line bronzeVariants(String[] kinds) {
		Line line = new Line();
		for (String kind : kinds) {
			for (String stage : STAGES) {
				line.add(LootItem.lootTableItem(item("waxed_" + stage + "bronze_" + kind)), 1);
			}
		}
		return line;
	}

	private static Line redstoneBlock() {
		Line line = new Line();
		for (String kind : new String[]{"winder", "spring", "vent"}) {
			for (String stage : STAGES) {
				line.add(LootItem.lootTableItem(item("waxed_" + stage + "bronze_" + kind)), 9);
			}
		}
		for (String stage : STAGES) {
			line.add(LootItem.lootTableItem(item("waxed_" + stage + "bronze_laser")), 5);
		}
		for (String copper : new String[]{"copper_laser", "exposed_copper_laser", "weathered_copper_laser", "oxidized_copper_laser"}) {
			line.add(LootItem.lootTableItem(item("waxed_" + copper)), 5);
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
			long scale = lcm / line.total();
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

	private static Item item(String path) {
		ResourceLocation id = RubinatedNether.id(path);
		return BuiltInRegistries.ITEM.getOptional(id).orElseThrow(() -> new IllegalStateException("Unknown item in strongbox loot: " + id));
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
