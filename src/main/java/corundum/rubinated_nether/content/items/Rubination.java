package corundum.rubinated_nether.content.items;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.datamaps.RubinationBundle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Rubination implements StringRepresentable {
	SLOTH("sloth", RNTags.Items.RUBINATION_TOOL),
	GLUTTONY("gluttony", RNTags.Items.RUBINATION_TOOL),
	GREED("greed", RNTags.Items.RUBINATION_TOOL),
	VAINGLORY("vainglory", RNTags.Items.RUBINATION_WEAPON),
	WRATH("wrath", RNTags.Items.RUBINATION_WEAPON),
	ENVY("envy", RNTags.Items.RUBINATION_WEAPON),
	PRIDE("pride", RNTags.Items.RUBINATION_ARMOR),
	ACEDIA("acedia", RNTags.Items.RUBINATION_ARMOR),
	LUXURIA("luxuria", RNTags.Items.RUBINATION_ARMOR),
	INSIDIAE("insidiae", RNTags.Items.RUBINATION_BOW),
	SUPERBIA("superbia", RNTags.Items.RUBINATION_BOW),
	TRISTIA("tristia", RNTags.Items.RUBINATION_BOW),
	STUDIOSE("studiose", RNTags.Items.RUBINATION_CROSSBOW),
	ARDENTER("ardenter", RNTags.Items.RUBINATION_CROSSBOW),
	NIMIS("nimis", RNTags.Items.RUBINATION_CROSSBOW),
	IRA("ira", RNTags.Items.RUBINATION_TRIDENT),
	INVIDIA("invidia", RNTags.Items.RUBINATION_TRIDENT),
	GULA("gula", RNTags.Items.RUBINATION_TRIDENT),
	IGNAVIA("ignavia", RNTags.Items.RUBINATION_MACE),
	KENODOXIA("kenodoxia", RNTags.Items.RUBINATION_MACE),
	PHILARGYRIA("philargyria", RNTags.Items.RUBINATION_MACE),
	EMPTY("empty", Tags.Items.BRICKS);

	public static final Codec<Rubination> CODEC = StringRepresentable.fromEnum(Rubination::values)
			.orElse(Rubination.EMPTY);

	private final String name;
	private final TagKey<Item> itemKey;

	Rubination(String name, TagKey<Item> itemKey) {
		this.name = name;
		this.itemKey = itemKey;
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

	@SuppressWarnings("deprecation")
	public List<EnchantmentInstance> getEnchantments(HolderLookup.Provider registries) {
		List<EnchantmentInstance> result = new ArrayList<>();
		DeferredItem<RuneItem> rune = RNItems.getRuneFor(this);
		if (rune == null) return result;

		RubinationBundle bundle = rune.get().builtInRegistryHolder().getData(RNDataMaps.RUBINATION_BUNDLES);
		if (bundle == null) return result;

		var lookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
		for (RubinationBundle.Entry entry : bundle.enchantments()) {
			var holder = lookup.get(ResourceKey.create(Registries.ENCHANTMENT, entry.enchantment())).orElse(null);
			result.add(holder != null ? new EnchantmentInstance(holder, entry.level()) : null);
		}
		return result;
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
			if (entry == EMPTY) continue;
			List<EnchantmentInstance> bundle = entry.getEnchantments(registry);
			if (bundle.isEmpty() || bundle.contains(null)) continue;
			if (bundle.stream().map(enchantmentInstance -> enchantmentInstance.enchantment)
					.allMatch(enchantment -> enchantments.stream()
							.anyMatch(optional -> optional.isPresent() && optional.get().equals(enchantment))))
				return entry;
		}
		return EMPTY;
	}
}