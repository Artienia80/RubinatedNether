package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PocketwatchSavePayload() implements CustomPacketPayload {
    public static final PocketwatchSavePayload INSTANCE = new PocketwatchSavePayload();

    public static final Type<PocketwatchSavePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "pocketwatch_save"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PocketwatchSavePayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}