package corundum.rubinated_nether.content.items;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: Add custom debuff enchants
//TODO: Maybe add tool-tag checking here?

public enum Rubination implements StringRepresentable {
    SLOTH("sloth", Map.of("minecraft:unbreaking", 5, "minecraft:fortune", 4, "minecraft:infinity", 1)),
    GLUTTONY("gluttony", Map.of("minecraft:unbreaking", 4, "minecraft:efficency", 7, "minecraft:infinity", 1)),
    GREED("greed", Map.of("minecraft:fortune", 5, "minecraft:efficency", 4, "minecraft:infinity", 1)),
    VAINGLORY("vainglory", Map.of("minecraft:unbreaking", 5, "minecraft:looting", 4, "minecraft:infinity", 1)),
    WRATH("wrath", Map.of("minecraft:sharpness", 7, "minecraft:unbreaking", 4, "minecraft:infinity", 1)),
    ENVY("envy", Map.of("minecraft:looting", 5, "minecraft:sharpness", 6, "minecraft:infinity", 1)),
    PRIDE("pride", Map.of("minecraft:unbreaking", 5, "minecraft:protection", 6, "minecraft:infinity", 1)),
    ACEDIA("acedia", Map.of("minecraft:thorns", 5, "minecraft:unbreaking", 4, "minecraft:infinity", 1)),
    LUXURIA("luxuria", Map.of("minecraft:protection", 7, "minecraft:thorns", 6, "minecraft:infinity", 1)),
    INSIDIAE("insidiae", Map.of("minecraft:unbreaking", 5, "minecraft:punch", 3, "minecraft:infinity", 1)),
    SUPERBIA("superbia", Map.of("minecraft:power", 7, "minecraft:unbreaking", 4, "minecraft:infinity", 1)),
    TRISTIA("tristia", Map.of("minecraft:punch", 4, "minecraft:power", 6, "minecraft:infinity", 1)),
    STUDIOSE("studiose", Map.of("minecraft:unbreaking", 5, "minecraft:multishot", 2, "minecraft:infinity", 1)),
    ARDENTER("ardenter", Map.of("minecraft:quick_charge", 5, "minecraft:unbreaking", 4, "minecraft:infinity", 1)),
    NIMIS("nimis", Map.of("minecraft:multishot", 3, "minecraft:quick_charge", 4, "minecraft:infinity", 1)),
    EMPTY("empty", Map.of());

    private final String name;
    private final Map<ResourceLocation, Integer> enchantmentData;

    Rubination(String name, Map<String, Integer> enchantmentData) {
        this.name = name;
        this.enchantmentData = enchantmentData.entrySet().stream()
                // Parsing Strings to ResourceLocations for simplicity
                .collect(Collectors.toMap(e -> ResourceLocation.parse(e.getKey()), Map.Entry::getValue));
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    /*
        I didn't like doing this, but I'm forced to make it work like we want.
        Also, I don't really have much experience with streams, so I had some help here.
    */
    public List<EnchantmentInstance> getEnchantments(RegistryAccess registryAccess) {
        return enchantmentData
                .entrySet()                                                                     // We convert the map into a set
                .stream()                                                                       // We convert the set into a stream (so we can do stuff below)
                .map(entry -> {                                        // Enables us to apply a function to each element, returning new stuff
                    var enchantment = registryAccess.registryOrThrow(Registries.ENCHANTMENT)
                            .getHolder(entry.getKey()).get();
                    return new EnchantmentInstance(enchantment, entry.getValue());
                })
                .collect(Collectors.toList());                                                  // The resulting list of EnchantmentInstances we got above
    }
}
