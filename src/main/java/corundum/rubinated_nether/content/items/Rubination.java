package corundum.rubinated_nether.content.items;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.Tags;
import org.codehaus.plexus.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Rubination implements StringRepresentable {
	SLOTH("sloth", ordered("minecraft:unbreaking", 5, "minecraft:fortune", 4, "rubinated_nether:deficiency_curse", 1), RNTags.Items.RUBINATION_TOOL),
	GLUTTONY("gluttony", ordered("minecraft:efficiency", 7, "minecraft:unbreaking", 4, "rubinated_nether:misfortune_curse", 1), RNTags.Items.RUBINATION_TOOL),
	GREED("greed", ordered("minecraft:fortune", 5, "minecraft:efficiency", 4, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_TOOL),
	VAINGLORY("vainglory", ordered("minecraft:unbreaking", 5, "minecraft:looting", 4, "rubinated_nether:bluntness_curse", 1), RNTags.Items.RUBINATION_WEAPON),
	WRATH("wrath", ordered("minecraft:sharpness", 7, "minecraft:unbreaking", 4, "rubinated_nether:ravaging_curse", 1), RNTags.Items.RUBINATION_WEAPON),
	ENVY("envy", ordered("minecraft:looting", 5, "minecraft:sharpness", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_WEAPON),
	PRIDE("pride", ordered("minecraft:unbreaking", 5, "minecraft:thorns", 4, "rubinated_nether:exposure_curse", 1), RNTags.Items.RUBINATION_ARMOR),
	ACEDIA("acedia", ordered("minecraft:protection", 7, "minecraft:unbreaking", 4, "rubinated_nether:leeching_curse", 1), RNTags.Items.RUBINATION_ARMOR),
	LUXURIA("luxuria", ordered("minecraft:thorns", 5, "minecraft:protection", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_ARMOR),
	INSIDIAE("insidiae", ordered("minecraft:unbreaking", 5, "minecraft:punch", 3, "rubinated_nether:dullness_curse", 1), RNTags.Items.RUBINATION_BOW),
	SUPERBIA("superbia", ordered("minecraft:power", 7, "minecraft:unbreaking", 4, "rubinated_nether:hooking_curse", 1), RNTags.Items.RUBINATION_BOW),
	TRISTIA("tristia", ordered("minecraft:punch", 4, "minecraft:power", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_BOW),
	STUDIOSE("studiose", ordered("minecraft:unbreaking", 5, "minecraft:multishot", 2, "rubinated_nether:slow_charge_curse", 1), RNTags.Items.RUBINATION_CROSSBOW),
	ARDENTER("ardenter", ordered("minecraft:quick_charge", 5, "minecraft:unbreaking", 4, "rubinated_nether:crooked_shot_curse", 1), RNTags.Items.RUBINATION_CROSSBOW),
	NIMIS("nimis", ordered("minecraft:multishot", 3, "minecraft:quick_charge", 4, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_CROSSBOW),
	IRA("ira", ordered("minecraft:unbreaking", 5, "minecraft:riptide", 4, "rubinated_nether:bluntness_curse", 1), RNTags.Items.RUBINATION_TRIDENT),
	INVIDIA("invidia", ordered("minecraft:impaling", 7, "minecraft:unbreaking", 4, "rubinated_nether:recoil_curse", 1), RNTags.Items.RUBINATION_TRIDENT),
	GULA("gula", ordered("minecraft:riptide", 5, "minecraft:impaling", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_TRIDENT),
	IGNAVIA("ignavia", ordered("minecraft:unbreaking", 5, "minecraft:wind_burst", 4, "rubinated_nether:buoyancy_curse", 1), RNTags.Items.RUBINATION_MACE),
	KENODOXIA("kenodoxia", ordered("minecraft:density", 7, "minecraft:unbreaking", 4, "rubinated_nether:sinking_curse", 1), RNTags.Items.RUBINATION_MACE),
	PHILARGYRIA("philargyria", ordered("minecraft:wind_burst", 5, "minecraft:density", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_MACE),

	EMPTY("empty", new LinkedHashMap<>(), Tags.Items.BRICKS);

	public static final Codec<Rubination> CODEC = StringRepresentable.fromEnum(Rubination::values)
			.orElse(Rubination.EMPTY);

	private final String name;
	private final Map<ResourceLocation, Integer> enchantmentData;
	private final TagKey<Item> itemKey;

	Rubination(String name, Map<String, Integer> enchantmentData, TagKey<Item> itemKey) {
		this.name = name;
		this.enchantmentData = enchantmentData.entrySet().stream()
				.collect(Collectors.toMap(
						e -> ResourceLocation.parse(e.getKey()),
						Map.Entry::getValue,
						(a, b) -> a,
						LinkedHashMap::new
				));
		this.itemKey = itemKey;
	}

	private static Map<String, Integer> ordered(String k1, int v1, String k2, int v2, String k3, int v3) {
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		return map;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public String getCapitalisedName() {
		return StringUtils.capitalise(this.name);
	}

	public static Rubination byNameOrEmpty(String name) {
		for (Rubination value : values()) {
			if (value.name.equals(name)) {
				return value;
			}
		}
		return EMPTY;
	}

	public static List<Rubination> carvableValues() {
		return java.util.Arrays.stream(values())
				.filter(value -> value != EMPTY)
				.collect(Collectors.toList());
	}

	public List<EnchantmentInstance> getEnchantments(RegistryAccess registryAccess) {
		return enchantmentData
				.entrySet()
				.stream()
				.map(entry -> {
					var enchantment = registryAccess.registryOrThrow(Registries.ENCHANTMENT)
							.getHolder(entry.getKey()).orElse(null);
					return enchantment != null ? new EnchantmentInstance(enchantment, entry.getValue()) : null;
				})
				.collect(Collectors.toList());
	}

	public TagKey<Item> getItemTag() {
		return this.itemKey;
	}
	public static String parseRubinationTextureName(Rubination rubination) {
		if (rubination.getItemTag() == RNTags.Items.RUBINATION_TOOL)
			return "tool";
		else if (rubination.getItemTag() == RNTags.Items.RUBINATION_WEAPON)
			return "weapon";
		else if (rubination.getItemTag() == RNTags.Items.RUBINATION_ARMOR)
			return "armor";
		else if (rubination.getItemTag() == RNTags.Items.RUBINATION_BOW)
			return "bow";
		else if (rubination.getItemTag() == RNTags.Items.RUBINATION_CROSSBOW)
			return "crossbow";
		else if (rubination.getItemTag() == RNTags.Items.RUBINATION_TRIDENT)
			return "trident";
		else if (rubination.getItemTag() == RNTags.Items.RUBINATION_MACE)
			return "mace";
		return "tool";
	}

	public static Rubination parseRubinationFromEnchantList(RegistryAccess registry, List<Optional<Holder.Reference<Enchantment>>> enchantments) {
		for (var entry : Rubination.values()) {
			if (entry.getEnchantments(registry).stream().map(enchantmentInstance -> enchantmentInstance.enchantment)
					.allMatch(enchantment -> enchantments.stream()
							.anyMatch(optional -> optional.isPresent() && optional.get().equals(enchantment))))
				return entry;
		}
		return EMPTY;
	}
}