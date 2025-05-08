package corundum.rubinated_nether.content.enchantment;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.enchantment.custom.FragilityCurseEffect;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class RNEnchantments {
	public static final ResourceKey<Enchantment> FRAGILITY_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "fragility_curse"));

	public static void bootstrap(BootstrapContext<Enchantment> context) {
		var enchantments = context.lookup(Registries.ENCHANTMENT);
		var items = context.lookup(Registries.ITEM);

		HolderGetter<DamageType> holdergetter = context.lookup(Registries.DAMAGE_TYPE);
		HolderGetter<Enchantment> holdergetter1 = context.lookup(Registries.ENCHANTMENT);
		HolderGetter<Item> holdergetter2 = context.lookup(Registries.ITEM);
		HolderGetter<Block> holdergetter3 = context.lookup(Registries.BLOCK);

		register(context, FRAGILITY_CURSE, Enchantment.enchantment(Enchantment.definition(holdergetter2.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE), 5, 3, Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(55, 8), 2, new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY})).withEffect(EnchantmentEffectComponents.ITEM_DAMAGE, new RemoveBinomial(new LevelBasedValue.Fraction(LevelBasedValue.perLevel(2.0F), LevelBasedValue.perLevel(10.0F, 5.0F))), MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.ARMOR_ENCHANTABLE))).withEffect(EnchantmentEffectComponents.ITEM_DAMAGE, new RemoveBinomial(new LevelBasedValue.Fraction(LevelBasedValue.perLevel(1.0F), LevelBasedValue.perLevel(2.0F, 1.0F))), InvertedLootItemCondition.invert(MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.ARMOR_ENCHANTABLE)))));


	}

	private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key,
								 Enchantment.Builder builder) {
		registry.register(key, builder.build(key.location()));
	}
}