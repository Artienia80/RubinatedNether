package corundum.rubinated_nether.networking;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** The box changed hands (dropped / picked up): the song already playing now follows another entity, without restarting. */
public record MusicBoxRetargetPayload(int fromEntityId, int toEntityId) implements CustomPacketPayload {
    public static final Type<MusicBoxRetargetPayload> TYPE = new Type<>(RubinatedNether.id("music_box_retarget"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MusicBoxRetargetPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MusicBoxRetargetPayload::fromEntityId,
            ByteBufCodecs.VAR_INT, MusicBoxRetargetPayload::toEntityId,
            MusicBoxRetargetPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
