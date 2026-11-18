package corundum.rubinated_nether.data;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.datamaps.RubinationBundle;
import corundum.rubinated_nether.content.datamaps.Tarnishable;
import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import corundum.rubinated_nether.content.items.RuneItem;
import corundum.rubinated_nether.content.datamaps.VaseEngravingMaterialData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RNDataMapProvider extends DataMapProvider {

	public RNDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void gather(HolderLookup.Provider provider) {
		var vase = builder(RNDataMaps.VASE_ENGRAVING_MATERIALS);
		vase.add(RNItems.BRONZE_SCRAP.getId(), new VaseEngravingMaterialData("bronze", Optional.empty()), false);
		vase.add(RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.getId(), new VaseEngravingMaterialData("crystallized_bronze", Optional.empty()), false);
		vase.add(RNItems.RUBY.getId(), new VaseEngravingMaterialData("ruby", Optional.empty()), false);
		vase.add(RNItems.MOLTEN_RUBY.getId(), new VaseEngravingMaterialData("molten_ruby", Optional.of(TextColor.fromRgb(0xFF6A2B))), false);

		var tarnishables = builder(RNDataMaps.TARNISHABLES);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_BLOCK, RNBlocks.DISCOLORED_BRONZE_BLOCK, RNBlocks.CORRODED_BRONZE_BLOCK, RNBlocks.TARNISHED_BRONZE_BLOCK, RNBlocks.CRYSTALLIZED_BRONZE_BLOCK);
		tarnishFamily(tarnishables, RNBlocks.CHISELED_BRONZE, RNBlocks.DISCOLORED_CHISELED_BRONZE, RNBlocks.CORRODED_CHISELED_BRONZE, RNBlocks.TARNISHED_CHISELED_BRONZE, RNBlocks.CRYSTALLIZED_CHISELED_BRONZE);
		tarnishFamily(tarnishables, RNBlocks.CUT_BRONZE_PILLAR, RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR, RNBlocks.CORRODED_CUT_BRONZE_PILLAR, RNBlocks.TARNISHED_CUT_BRONZE_PILLAR, RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR);
		tarnishFamily(tarnishables, RNBlocks.CUT_BRONZE_BRICKS, RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS, RNBlocks.CORRODED_CUT_BRONZE_BRICKS, RNBlocks.TARNISHED_CUT_BRONZE_BRICKS, RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS);
		tarnishFamily(tarnishables, RNBlocks.CUT_BRONZE_BRICKS_STAIRS, RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS, RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS, RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS, RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS);
		tarnishFamily(tarnishables, RNBlocks.CUT_BRONZE_BRICKS_SLAB, RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB, RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB, RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB, RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_BULB, RNBlocks.DISCOLORED_BRONZE_BULB, RNBlocks.CORRODED_BRONZE_BULB, RNBlocks.TARNISHED_BRONZE_BULB, RNBlocks.CRYSTALLIZED_BRONZE_BULB);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_GRATE, RNBlocks.DISCOLORED_BRONZE_GRATE, RNBlocks.CORRODED_BRONZE_GRATE, RNBlocks.TARNISHED_BRONZE_GRATE, RNBlocks.CRYSTALLIZED_BRONZE_GRATE);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_BUTTON, RNBlocks.DISCOLORED_BRONZE_BUTTON, RNBlocks.CORRODED_BRONZE_BUTTON, RNBlocks.TARNISHED_BRONZE_BUTTON, RNBlocks.CRYSTALLIZED_BRONZE_BUTTON);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_CHAIN, RNBlocks.DISCOLORED_BRONZE_CHAIN, RNBlocks.CORRODED_BRONZE_CHAIN, RNBlocks.TARNISHED_BRONZE_CHAIN, RNBlocks.CRYSTALLIZED_BRONZE_CHAIN);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_LANTERN, RNBlocks.DISCOLORED_BRONZE_LANTERN, RNBlocks.CORRODED_BRONZE_LANTERN, RNBlocks.TARNISHED_BRONZE_LANTERN, RNBlocks.CRYSTALLIZED_BRONZE_LANTERN);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_CHANDELIER, RNBlocks.DISCOLORED_BRONZE_CHANDELIER, RNBlocks.CORRODED_BRONZE_CHANDELIER, RNBlocks.TARNISHED_BRONZE_CHANDELIER, RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_LAMP, RNBlocks.DISCOLORED_BRONZE_LAMP, RNBlocks.CORRODED_BRONZE_LAMP, RNBlocks.TARNISHED_BRONZE_LAMP, RNBlocks.CRYSTALLIZED_BRONZE_LAMP);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_LASER, RNBlocks.DISCOLORED_BRONZE_LASER, RNBlocks.CORRODED_BRONZE_LASER, RNBlocks.TARNISHED_BRONZE_LASER, RNBlocks.CRYSTALLIZED_BRONZE_LASER);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_HEAD, RNBlocks.DISCOLORED_BRONZE_HEAD, RNBlocks.CORRODED_BRONZE_HEAD, RNBlocks.TARNISHED_BRONZE_HEAD, RNBlocks.CRYSTALLIZED_BRONZE_HEAD);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_SPRING, RNBlocks.DISCOLORED_BRONZE_SPRING, RNBlocks.CORRODED_BRONZE_SPRING, RNBlocks.TARNISHED_BRONZE_SPRING, RNBlocks.CRYSTALLIZED_BRONZE_SPRING);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_VENT, RNBlocks.DISCOLORED_BRONZE_VENT, RNBlocks.CORRODED_BRONZE_VENT, RNBlocks.TARNISHED_BRONZE_VENT, RNBlocks.CRYSTALLIZED_BRONZE_VENT);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_WINDER, RNBlocks.DISCOLORED_BRONZE_WINDER, RNBlocks.CORRODED_BRONZE_WINDER, RNBlocks.TARNISHED_BRONZE_WINDER, RNBlocks.CRYSTALLIZED_BRONZE_WINDER);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_GEARBOX, RNBlocks.DISCOLORED_BRONZE_GEARBOX, RNBlocks.CORRODED_BRONZE_GEARBOX, RNBlocks.TARNISHED_BRONZE_GEARBOX, RNBlocks.CRYSTALLIZED_BRONZE_GEARBOX);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_VASE, RNBlocks.DISCOLORED_BRONZE_VASE, RNBlocks.CORRODED_BRONZE_VASE, RNBlocks.TARNISHED_BRONZE_VASE, RNBlocks.CRYSTALLIZED_BRONZE_VASE);
		tarnishFamily(tarnishables, RNBlocks.BRONZE_STRONGBOX, RNBlocks.DISCOLORED_BRONZE_STRONGBOX, RNBlocks.CORRODED_BRONZE_STRONGBOX, RNBlocks.TARNISHED_BRONZE_STRONGBOX, RNBlocks.CRYSTALLIZED_BRONZE_STRONGBOX);

		var rubination = builder(RNDataMaps.RUBINATION_BUNDLES);
		rubinationCategory(rubination, provider, List.of(RNItems.SLOTH_RUNE, RNItems.GLUTTONY_RUNE, RNItems.GREED_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.FORTUNE, RNEnchantments.MISFORTUNE_CURSE), new RubinationSlot(Enchantments.EFFICIENCY, RNEnchantments.DEFICIENCY_CURSE));
		rubinationCategory(rubination, provider, List.of(RNItems.VAINGLORY_RUNE, RNItems.WRATH_RUNE, RNItems.ENVY_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.LOOTING, RNEnchantments.RAVAGING_CURSE), new RubinationSlot(Enchantments.SHARPNESS, RNEnchantments.BLUNTNESS_CURSE));
		rubinationCategory(rubination, provider, List.of(RNItems.PRIDE_RUNE, RNItems.ACEDIA_RUNE, RNItems.LUXURIA_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.THORNS, RNEnchantments.LEECHING_CURSE), new RubinationSlot(Enchantments.PROTECTION, RNEnchantments.EXPOSURE_CURSE));
		rubinationCategory(rubination, provider, List.of(RNItems.INSIDIAE_RUNE, RNItems.SUPERBIA_RUNE, RNItems.TRISTIA_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.PUNCH, RNEnchantments.HOOKING_CURSE), new RubinationSlot(Enchantments.POWER, RNEnchantments.DULLNESS_CURSE));
		rubinationCategory(rubination, provider, List.of(RNItems.STUDIOSE_RUNE, RNItems.ARDENTER_RUNE, RNItems.NIMIS_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.MULTISHOT, RNEnchantments.CROOKED_SHOT_CURSE), new RubinationSlot(Enchantments.QUICK_CHARGE, RNEnchantments.SLOW_CHARGE_CURSE));
		rubinationCategory(rubination, provider, List.of(RNItems.IRA_RUNE, RNItems.INVIDIA_RUNE, RNItems.GULA_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.RIPTIDE, RNEnchantments.RECOIL_CURSE), new RubinationSlot(Enchantments.IMPALING, RNEnchantments.BLUNTNESS_CURSE));
		rubinationCategory(rubination, provider, List.of(RNItems.IGNAVIA_RUNE, RNItems.KENODOXIA_RUNE, RNItems.PHILARGYRIA_RUNE),
				new RubinationSlot(Enchantments.UNBREAKING, RNEnchantments.FRAGILITY_CURSE), new RubinationSlot(Enchantments.WIND_BURST, RNEnchantments.SINKING_CURSE), new RubinationSlot(Enchantments.DENSITY, RNEnchantments.BUOYANCY_CURSE));
	}

	private record RubinationSlot(ResourceKey<Enchantment> enchantment, ResourceKey<Enchantment> inverse) {
	}

	private static void rubinationCategory(Builder<RubinationBundle, Item> builder, HolderLookup.Provider provider,
										   List<DeferredItem<RuneItem>> runes, RubinationSlot s0, RubinationSlot s1, RubinationSlot s2) {
		rubinationBundle(builder, provider, runes.get(0), s0, s1, s2);
		rubinationBundle(builder, provider, runes.get(1), s2, s0, s1);
		rubinationBundle(builder, provider, runes.get(2), s1, s2, s0);
	}

	private static void rubinationBundle(Builder<RubinationBundle, Item> builder, HolderLookup.Provider provider,
										 DeferredItem<RuneItem> rune, RubinationSlot major, RubinationSlot minor, RubinationSlot cursedBy) {
		builder.add(rune.getId(), new RubinationBundle(List.of(
				new RubinationBundle.Entry(major.enchantment().location(), maxLevel(provider, major.enchantment()) + 2),
				new RubinationBundle.Entry(minor.enchantment().location(), maxLevel(provider, minor.enchantment()) + 1),
				new RubinationBundle.Entry(cursedBy.inverse().location(), 1)
		)), false);
	}

	private static int maxLevel(HolderLookup.Provider provider, ResourceKey<Enchantment> key) {
		return provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key).value().getMaxLevel();
	}

	private static void tarnishFamily(Builder<Tarnishable, Block> builder, DeferredBlock<?> base, DeferredBlock<?> discolored,
									  DeferredBlock<?> corroded, DeferredBlock<?> tarnished, DeferredBlock<?> crystallized) {
		builder.add(base.getId(), new Tarnishable(Optional.of(discolored.get()), Optional.of(crystallized.get())), false);
		builder.add(discolored.getId(), new Tarnishable(Optional.of(corroded.get()), Optional.of(crystallized.get())), false);
		builder.add(corroded.getId(), new Tarnishable(Optional.of(tarnished.get()), Optional.of(crystallized.get())), false);
		builder.add(tarnished.getId(), new Tarnishable(Optional.empty(), Optional.of(crystallized.get())), false);
	}
}
