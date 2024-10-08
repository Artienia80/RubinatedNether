package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.loot.ConfigUniform;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public final class RNLootNumberProviders {

    public static final Registrar<LootNumberProviderType> NUMBER_PROVIDERS = RubinatedNether.REGISTRIES.getRegistrar(Registries.LOOT_NUMBER_PROVIDER_TYPE);

    public static final RegistryEntry<LootNumberProviderType> CONFIG_UNIFORM = NUMBER_PROVIDERS.entry("config_uniform", () -> new LootNumberProviderType(ConfigUniform.SERIALIZER))
        .register();

    public static void register() {
        NUMBER_PROVIDERS.register();
    }
}
