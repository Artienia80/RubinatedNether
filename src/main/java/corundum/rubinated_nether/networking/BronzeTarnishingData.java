package corundum.rubinated_nether.networking;

import corundum.rubinated_nether.RubinatedNether;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BronzeTarnishingData(int id, int tarnishingLevel) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BronzeTarnishingData> TYPE = new CustomPacketPayload.Type<>(
            RubinatedNether.id("bronze_tarnishing_data")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BronzeTarnishingData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            BronzeTarnishingData::id,
            ByteBufCodecs.INT,
            BronzeTarnishingData::tarnishingLevel,
            BronzeTarnishingData::new
    );

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(tarnishingLevel);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
