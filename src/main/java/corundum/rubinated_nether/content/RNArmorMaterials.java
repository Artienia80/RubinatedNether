package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class RNArmorMaterials {
	public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(
		Registries.ARMOR_MATERIAL, 
		RubinatedNether.MODID
	);
	
	public static final Holder<ArmorMaterial> RUBY_LENS_MATERIAL = ARMOR_MATERIALS.register(
		"ruby_lens", 
		() -> new ArmorMaterial(
			Util.make(new EnumMap<>(ArmorItem.Type.class), map -> { // Defense
				map.put(ArmorItem.Type.BOOTS, 0);
				map.put(ArmorItem.Type.LEGGINGS, 0);
				map.put(ArmorItem.Type.CHESTPLATE, 0);
				map.put(ArmorItem.Type.HELMET, 0);
				map.put(ArmorItem.Type.BODY, 0);
			}),
			0, // Enchantability
			SoundEvents.ARMOR_EQUIP_GENERIC, // Sound 
			() -> Ingredient.of(RNTags.Items.RUBIES), // Repair item
			List.of(),
			0, // Toughness
			0  // Knockback Resistance
		)
	);
}
