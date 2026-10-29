package corundum.rubinated_nether.networking;

import corundum.rubinated_nether.RubinatedNether;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.HashMap;

public record ConfigSyncPayload(HashMap<String, String> values) implements CustomPacketPayload {
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(RubinatedNether.id("config_sync"));
    public static final StreamCodec<ByteBuf, ConfigSyncPayload> CODEC =
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8)
                    .map(ConfigSyncPayload::new, ConfigSyncPayload::values);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
