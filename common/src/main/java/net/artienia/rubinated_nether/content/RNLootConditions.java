package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.loot.ItemTierLootCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public final class RNLootConditions {

    public static final Registrar<LootItemConditionType> CONDITIONS = RubinatedNether.REGISTRIES.getRegistrar(Registries.LOOT_CONDITION_TYPE);

    public static final RegistryEntry<LootItemConditionType> ITEM_TIER = CONDITIONS.entry("item_tier", ItemTierLootCondition::createType).register();

}
