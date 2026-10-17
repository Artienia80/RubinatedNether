package corundum.rubinated_nether.networking;

import corundum.rubinated_nether.RubinatedNether;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CollectDiscsPayload() implements CustomPacketPayload {
    public static final Type<CollectDiscsPayload> TYPE = new Type<>(RubinatedNether.id("collect_discs"));
    public static final StreamCodec<ByteBuf, CollectDiscsPayload> CODEC = StreamCodec.unit(new CollectDiscsPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}