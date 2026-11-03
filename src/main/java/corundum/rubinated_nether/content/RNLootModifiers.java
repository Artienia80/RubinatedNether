package corundum.rubinated_nether.content;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.loot.PomegranateSnifferLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RNLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RubinatedNether.MODID);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> POMEGRANATE_SNIFFER_DIGGING =
            LOOT_MODIFIERS.register("pomegranate_sniffer_digging", PomegranateSnifferLootModifier.CODEC);
}
