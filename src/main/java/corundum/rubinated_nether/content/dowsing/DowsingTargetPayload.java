package corundum.rubinated_nether.content.dowsing;

import corundum.rubinated_nether.RubinatedNether;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DowsingTargetPayload(int entityId, boolean found, BlockPos pos) implements CustomPacketPayload {
    public static final Type<DowsingTargetPayload> TYPE = new Type<>(RubinatedNether.id("dowsing_target"));
    public static final StreamCodec<ByteBuf, DowsingTargetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DowsingTargetPayload::entityId,
            ByteBufCodecs.BOOL, DowsingTargetPayload::found,
            BlockPos.STREAM_CODEC, DowsingTargetPayload::pos,
            DowsingTargetPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
