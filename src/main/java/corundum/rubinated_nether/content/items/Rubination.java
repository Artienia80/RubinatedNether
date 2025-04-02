package corundum.rubinated_nether.content.items;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Rubination implements StringRepresentable {
    GREED("greed", Map.of("minecraft:unbreaking", 5, "minecraft:knockback", 5, "minecraft:fire_aspect", 5)),
    WRATH("wrath", Map.of()),
    SLOTH("sloth", Map.of()),
    GLUTTONY("gluttony", Map.of()),
    ENVY("envy", Map.of()),
    VAINGLORY("vainglory", Map.of()),
    PRIDE("pride", Map.of()),
    ACEDIA("acedia", Map.of()),
    LUXURIA("luxuria", Map.of()),
    INSIDIAE("insidiae", Map.of()),
    SUPERBIA("superbia", Map.of()),
    TRISTIA("tristia", Map.of()),
    STUDIOSE("studiose", Map.of()),
    ARDENTER("ardenter", Map.of()),
    NIMIS("nimis", Map.of()),
    EMPTY("empty", Map.of());

    private final String name;
    private final Map<ResourceLocation, Integer> enchantmentData;

    Rubination(String name, Map<String, Integer> enchantmentData) {
        this.name = name;
        this.enchantmentData = enchantmentData.entrySet().stream()
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
