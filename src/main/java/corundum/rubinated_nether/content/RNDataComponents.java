package corundum.rubinated_nether.content;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RubinatedNether.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_COMBO = DATA_COMPONENTS.register("is_combo",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DRILL_MULTIPLIER = DATA_COMPONENTS.register("drill_multiplier",
            () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LAST_TICK = DATA_COMPONENTS.register("last_tick",
            () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
}
