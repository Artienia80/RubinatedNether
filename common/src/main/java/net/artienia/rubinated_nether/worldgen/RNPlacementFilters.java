package net.artienia.rubinated_nether.worldgen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public class RNPlacementFilters {

    public static final Registrar<PlacementModifierType<?>> MODIFIERS = RubinatedNether.REGISTRIES.getRegistrar(Registries.PLACEMENT_MODIFIER_TYPE);

    public static final RegistryEntry<PlacementModifierType<ConfigPlacementFilter>> CONFIG_FILTER =
        MODIFIERS.<PlacementModifierType<ConfigPlacementFilter>>entry("config_filter", () -> () -> ConfigPlacementFilter.CODEC).register();


    public static void register() {
        MODIFIERS.register();
    }
}
