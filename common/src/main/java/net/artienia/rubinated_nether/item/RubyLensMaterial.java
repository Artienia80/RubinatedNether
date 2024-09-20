package net.artienia.rubinated_nether.item;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum RubyLensMaterial implements ArmorMaterial {
	RUBY_LENS("ruby_lens", 15, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 2);
		map.put(ArmorItem.Type.LEGGINGS, 5);
		map.put(ArmorItem.Type.CHESTPLATE, 6);
		map.put(ArmorItem.Type.HELMET, 1);
	}), 30, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, () -> Ingredient.of(ModItems.RUBY.get()));

	private static final EnumMap<ArmorItem.Type, Integer> DURABILITY_MAP = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 13);
		map.put(ArmorItem.Type.LEGGINGS, 15);
		map.put(ArmorItem.Type.CHESTPLATE, 16);
		map.put(ArmorItem.Type.HELMET, 11);
	});
	private final String name;
	private final int maxDamageFactor;
	private final EnumMap<ArmorItem.Type, Integer> protectionAmountMap;
	private final int enchantability;
	private final SoundEvent sound;
	private final float toughness;
	private final Supplier<Ingredient> repairMaterial;

	RubyLensMaterial(String name, int maxDamageFactor, EnumMap<ArmorItem.Type, Integer> protectionAmountMap, int enchantability, SoundEvent sound, float toughness, Supplier<Ingredient> repairMaterial) {
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.protectionAmountMap = protectionAmountMap;
		this.enchantability = enchantability;
		this.sound = sound;
		this.toughness = toughness;
		this.repairMaterial = repairMaterial;
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return DURABILITY_MAP.get(type) * this.maxDamageFactor;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return this.protectionAmountMap.get(type);
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.sound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairMaterial.get();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}
}
