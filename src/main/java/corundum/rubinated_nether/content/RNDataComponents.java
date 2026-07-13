package corundum.rubinated_nether.content;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.trim.VaseEngraving;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RubinatedNether.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLOCKS_BROKEN = DATA_COMPONENTS.register("blocks_broken",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LAST_TICK = DATA_COMPONENTS.register("last_tick",
            () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Rubination>> RUNE_CARVING = DATA_COMPONENTS.register("rune_carving",
            () -> DataComponentType.<Rubination>builder()
                    .persistent(Rubination.CODEC)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8.map(Rubination::byNameOrEmpty, Rubination::getSerializedName))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VaseEngraving>> VASE_ENGRAVING = DATA_COMPONENTS.register("vase_engraving",
            () -> DataComponentType.<VaseEngraving>builder()
                    .persistent(VaseEngraving.CODEC)
                    .networkSynchronized(VaseEngraving.STREAM_CODEC)
                    .build());
}