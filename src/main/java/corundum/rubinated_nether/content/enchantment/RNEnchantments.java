package corundum.rubinated_nether.content.enchantment;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.enchantment.custom.HookingCurseEffect;
import corundum.rubinated_nether.content.enchantment.custom.LeechingCurseEffect;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;

public class RNEnchantments {
	public static final ResourceKey<Enchantment> FRAGILITY_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "fragility_curse"));

	public static final ResourceKey<Enchantment> BLUNTNESS_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "bluntness_curse"));

	public static final ResourceKey<Enchantment> EXPOSURE_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "exposure_curse"));

	public static final ResourceKey<Enchantment> POWERLESSNESS_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "powerlessness_curse"));

	public static final ResourceKey<Enchantment> HOOKING_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "hooking_curse"));

	public static final ResourceKey<Enchantment> DEFICIENCY_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "deficiency_curse"));

	public static final ResourceKey<Enchantment> SLOW_CHARGE_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "slow_charge_curse"));

	public static final ResourceKey<Enchantment> DESTRUCTION_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "destruction_curse"));

	public static final ResourceKey<Enchantment> LEECHING_CURSE = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "leeching_curse"));



	public static void bootstrap(BootstrapContext<Enchantment> context) {
		var enchantments = context.lookup(Registries.ENCHANTMENT);
		var items = context.lookup(Registries.ITEM);

		HolderGetter<DamageType> holdergetter = context.lookup(Registries.DAMAGE_TYPE);
		HolderGetter<Enchantment> holdergetter1 = context.lookup(Registries.ENCHANTMENT);
		HolderGetter<Item> holdergetter2 = context.lookup(Registries.ITEM);
		HolderGetter<Block> holdergetter3 = context.lookup(Registries.BLOCK);

		register(context, FRAGILITY_CURSE, Enchantment.enchantment(
				Enchantment.definition(holdergetter2.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE), 10, 1, Enchantment.dynamicCost(1, 10),
				Enchantment.dynamicCost(51, 10), 1, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.DAMAGE,
						new EnchantmentValueEffect() {
							@Override
							public float process(int i, RandomSource randomSource, float v) {
								return 0;
							}

							@Override
							public MapCodec<? extends EnchantmentValueEffect> codec() {
								return null;
							}
						}));

		register(context, BLUNTNESS_CURSE, Enchantment.enchantment(
						Enchantment.definition(holdergetter2.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE), 10, 1, Enchantment.dynamicCost(1, 10),
								Enchantment.dynamicCost(51, 10), 1, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
						new EnchantmentAttributeEffect(ResourceLocation.withDefaultNamespace("enchantment.rubinated_nether.bluntness_curse"),
								Attributes.ATTACK_DAMAGE, new LevelBasedValue.LevelsSquared(-3.0F), AttributeModifier.Operation.ADD_VALUE)));

		register(context, EXPOSURE_CURSE, Enchantment.enchantment(
						Enchantment.definition(holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 10, 1, Enchantment.dynamicCost(1, 10),
								Enchantment.dynamicCost(51, 10), 1, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
						new EnchantmentAttributeEffect(ResourceLocation.withDefaultNamespace("enchantment.rubinated_nether.exposure_curse"),
								Attributes.ARMOR_TOUGHNESS, new LevelBasedValue.LevelsSquared(-3.0F), AttributeModifier.Operation.ADD_VALUE)));

		register(context, POWERLESSNESS_CURSE, Enchantment.enchantment(
						Enchantment.definition(holdergetter2.getOrThrow(ItemTags.BOW_ENCHANTABLE), 10, 1, Enchantment.dynamicCost(1, 10),
								Enchantment.dynamicCost(51, 10), 1, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
						new EnchantmentAttributeEffect(ResourceLocation.withDefaultNamespace("enchantment.rubinated_nether.powerlessness_curse"),
								Attributes.ATTACK_DAMAGE, new LevelBasedValue.LevelsSquared(-3.0F), AttributeModifier.Operation.ADD_VALUE)));

		register(context, HOOKING_CURSE, Enchantment.enchantment(Enchantment.definition(
						items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
						5,
						1,
						Enchantment.dynamicCost(5, 7),
						Enchantment.dynamicCost(25, 7),
						2,
						EquipmentSlotGroup.MAINHAND))
				.exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
				.withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
						EnchantmentTarget.VICTIM, new HookingCurseEffect()));

		register(context, DEFICIENCY_CURSE, Enchantment.enchantment(
						Enchantment.definition(holdergetter2.getOrThrow(ItemTags.MINING_ENCHANTABLE), 10, 3,
								Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(51, 10), 1,
								new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
						new EnchantmentAttributeEffect(
								ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "enchantment.deficiency_curse"),
								Attributes.MINING_EFFICIENCY,
								new LevelBasedValue.LevelsSquared(-20.0F), // Negative value now works
								AttributeModifier.Operation.ADD_VALUE)));

		register(context, SLOW_CHARGE_CURSE, Enchantment.enchantment(
						Enchantment.definition(holdergetter2.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE), 10, 1, Enchantment.dynamicCost(1, 10),
								Enchantment.dynamicCost(51, 10), 1, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
						new EnchantmentAttributeEffect(ResourceLocation.withDefaultNamespace("enchantment.rubinated_nether.slow_charge_curse"),
								Attributes.ATTACK_SPEED, new LevelBasedValue.LevelsSquared(-1.0F), AttributeModifier.Operation.ADD_VALUE)));

		register(context, DESTRUCTION_CURSE, Enchantment.enchantment(
						Enchantment.definition(holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE), 10, 1, Enchantment.dynamicCost(1, 10),
								Enchantment.dynamicCost(51, 10), 1, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}))
				.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
						new EnchantmentAttributeEffect(ResourceLocation.withDefaultNamespace("enchantment.rubinated_nether.destruction_curse"),
								Attributes.ATTACK_SPEED, new LevelBasedValue.LevelsSquared(-1.0F), AttributeModifier.Operation.ADD_VALUE)));

		register(context, LEECHING_CURSE, Enchantment.enchantment(
						Enchantment.definition(
								holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
								holdergetter2.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
								1, 3,
								Enchantment.dynamicCost(10, 20),
								Enchantment.dynamicCost(60, 20),
								8,
								new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}))
				.withEffect(EnchantmentEffectComponents.POST_ATTACK,
						EnchantmentTarget.VICTIM,
						EnchantmentTarget.ATTACKER,
						new LeechingCurseEffect(
						),
						LootItemRandomChanceCondition.randomChance(
								EnchantmentLevelProvider.forEnchantmentLevel(
										LevelBasedValue.perLevel(0.15F) // 15% chance per level
								)
						)
				)
		);

	}


	private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key,
								 Enchantment.Builder builder) {
		registry.register(key, builder.build(key.location()));
	}
}