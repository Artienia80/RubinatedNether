package corundum.rubinated_nether.content.items;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO: Add custom debuff enchants
//TODO: Maybe add tool-tag checking here?

public enum Rubination implements StringRepresentable {
	SLOTH("sloth", Map.of("minecraft:unbreaking", 5, "minecraft:fortune", 4, "rubinated_nether:deficiency_curse", 1), RNTags.Items.RUBINATION_TOOL),
	GLUTTONY("gluttony", Map.of("minecraft:efficiency", 7,"minecraft:unbreaking", 4,  "minecraft:vanishing_curse", 1), RNTags.Items.RUBINATION_TOOL),
	GREED("greed", Map.of("minecraft:fortune", 5, "minecraft:efficiency", 4, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_TOOL),
	VAINGLORY("vainglory", Map.of("minecraft:unbreaking", 5, "minecraft:looting", 4, "rubinated_nether:bluntness_curse", 1), RNTags.Items.RUBINATION_WEAPON),
	WRATH("wrath", Map.of("minecraft:sharpness", 7, "minecraft:unbreaking", 4, "minecraft:vanishing_curse", 1), RNTags.Items.RUBINATION_WEAPON),
	ENVY("envy", Map.of("minecraft:looting", 5, "minecraft:sharpness", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_WEAPON),
	PRIDE("pride", Map.of("minecraft:unbreaking", 5, "rubinated_nether:leeching_curse", 6, "minecraft:vanishing_curse", 1), RNTags.Items.RUBINATION_ARMOR),
	ACEDIA("acedia", Map.of("minecraft:thorns", 5, "minecraft:unbreaking", 4, "rubinated_nether:leeching_curse", 1), RNTags.Items.RUBINATION_ARMOR),
	LUXURIA("luxuria", Map.of("minecraft:protection", 7, "minecraft:thorns", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_ARMOR),
	INSIDIAE("insidiae", Map.of("minecraft:unbreaking", 5, "minecraft:punch", 3, "rubinated_nether:powerlessness_curse", 1), RNTags.Items.RUBINATION_BOW),
	SUPERBIA("superbia", Map.of("minecraft:power", 7, "minecraft:unbreaking", 4, "rubinated_nether:hooking_curse", 1), RNTags.Items.RUBINATION_BOW),
	TRISTIA("tristia", Map.of("minecraft:punch", 4, "minecraft:power", 6, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_BOW),
	STUDIOSE("studiose", Map.of("minecraft:unbreaking", 5, "minecraft:multishot", 2, "rubinated_nether:slow_charge_curse", 1), RNTags.Items.RUBINATION_CROSSBOW),
	ARDENTER("ardenter", Map.of("minecraft:quick_charge", 5, "minecraft:unbreaking", 4, "minecraft:vanishing_curse", 1), RNTags.Items.RUBINATION_CROSSBOW),
	NIMIS("nimis", Map.of("minecraft:multishot", 3, "minecraft:quick_charge", 4, "rubinated_nether:fragility_curse", 1), RNTags.Items.RUBINATION_CROSSBOW),

	EMPTY("empty", Map.of(), Tags.Items.BRICKS),;

	private final String name;
	private final Map<ResourceLocation, Integer> enchantmentData;
	private final TagKey<Item> itemKey;

	Rubination(String name, Map<String, Integer> enchantmentData, TagKey<Item> itemKey) {
		this.name = name;
		this.enchantmentData = enchantmentData.entrySet().stream()
				// Parsing String to ResourceLocation for simplicity
				.collect(Collectors.toMap(e -> ResourceLocation.parse(e.getKey()), Map.Entry::getValue));
		this.itemKey = itemKey;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public String getCapitalisedName() {
		return StringUtils.capitalise(this.name);
	}

	/*
		I didn't like doing this, but I'm forced to make it work like we want.
		Also, I really have little experience with streams, so I had some help here.
	*/
	public List<EnchantmentInstance> getEnchantments(RegistryAccess registryAccess) {
		return enchantmentData
				.entrySet()																	 // We convert the map into a set
				.stream()																	   // We convert the set into a stream (so we can do stuff below)
				.map(entry -> {										// Enables us to apply a function to each element, returning new stuff
					var enchantment = registryAccess.registryOrThrow(Registries.ENCHANTMENT)
							.getHolder(entry.getKey()).orElse(null);
					return enchantment != null ? new EnchantmentInstance(enchantment, entry.getValue()) : null;
				})
				.collect(Collectors.toList());												  // The resulting list of EnchantmentInstances we got above
	}

	public TagKey<Item> getItemTag() {
		return this.itemKey;
	}
	
	public static String parseRubinationTextureName(Rubination rubination){
		if(rubination.getItemTag() == RNTags.Items.RUBINATION_TOOL)
			return "tool";
		else if(rubination.getItemTag() == RNTags.Items.RUBINATION_WEAPON)
			return "weapon";
		else if(rubination.getItemTag() == RNTags.Items.RUBINATION_ARMOR)
			return "armor";
		else if(rubination.getItemTag() == RNTags.Items.RUBINATION_BOW)
			return "bow";
		else if(rubination.getItemTag() == RNTags.Items.RUBINATION_CROSSBOW)
			return "crossbow";
		return "tool";
	}

	public static Rubination parseRubinationFromEnchantList(RegistryAccess registry, List<Optional<Holder.Reference<Enchantment>>> enchantments){
		for(var entry : Rubination.values()){
			if(entry.getEnchantments(registry).stream().map(enchantmentInstance -> enchantmentInstance.enchantment)
					.allMatch(enchantment -> enchantments.stream()
							.anyMatch(optional -> optional.isPresent() && optional.get().equals(enchantment))))
				return entry;
		}
		return EMPTY;
	}



//			if (this.minecraft != null && this.minecraft.player != null) {
//		return this.minecraft.player.connection.getAdvancements()
//				.getAdvancements()
//				.stream()
//				.anyMatch(advancement ->
//						advancement.getId().equals(new ResourceLocation("rubinated_nether", "rubinous_ritual")));
//	}

}
