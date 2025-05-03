package corundum.rubinated_nether.content;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import static corundum.rubinated_nether.content.RNItems.*;

public class RNLootInjector {

    public static void init() {
        NeoForge.EVENT_BUS.register(RNLootInjector.class);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();

        // Add runes to loot tables with 2.5% chance
        switch (name.toString()) {
            case "minecraft:chests/end_city":
                addRune(event, GREED_RUNE.get(), 0.025f);
                addRune(event, ENVY_RUNE.get(), 0.025f);
                break;
            case "minecraft:chests/ancient_city":
                addRune(event, GLUTTONY_RUNE.get(), 0.025f);
                addRune(event, WRATH_RUNE.get(), 0.025f);
                break;
            case "minecraft:chests/jungle_temple":
                addRune(event, LUXURIA_RUNE.get(), 0.025f);
                addRune(event, PRIDE_RUNE.get(), 0.025f);
                break;
            case "minecraft:chests/stronghold_library":
                addRune(event, SUPERBIA_RUNE.get(), 0.025f);
                addRune(event, ARDENTER_RUNE.get(), 0.025f);
                break;
            case "minecraft:chests/nether_bridge":
            case "minecraft:chests/bastion_treasure":
            case "minecraft:chests/underwater_ruin_big":
                addRune(event, SLOTH_RUNE.get(), 0.025f);
                addRune(event, VAINGLORY_RUNE.get(), 0.025f);
                addRune(event, INSIDIAE_RUNE.get(), 0.025f);
                addRune(event, STUDIOSE_RUNE.get(), 0.025f);
                break;
            case "minecraft:chests/trial_chambers_reward":
                addRune(event, NIMIS_RUNE.get(), 0.025f);
                addRune(event, TRISTIA_RUNE.get(), 0.025f);
                addRune(event, ACEDIA_RUNE.get(), 0.025f);
                break;
        }
    }

    private static void addRune(LootTableLoadEvent event, Item item, float chance) {
        LootPool pool = LootPool.lootPool()
                .add(LootItem.lootTableItem(item)
                        .when(LootItemRandomChanceCondition.randomChance(chance)))
                .setRolls(ConstantValue.exactly(1))
                .build();

        // Add the pool to the loot table
        event.getTable().addPool(pool);
    }
}
