package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.TarnishStage;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RNEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, RubinatedNether.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>,EntityDataSerializer<TarnishStage>> TARNISH_STAGE = SERIALIZERS.register(
            "tarnish_stage",
            () -> EntityDataSerializer.forValueType(TarnishStage.STREAM_CODEC)
    );
}
