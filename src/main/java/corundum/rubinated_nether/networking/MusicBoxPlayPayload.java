package corundum.rubinated_nether.networking;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Start a music box song that follows the entity with the given id (the player carrying it, or the dropped item). */
public record MusicBoxPlayPayload(int entityId, ResourceLocation sound) implements CustomPacketPayload {
    public static final Type<MusicBoxPlayPayload> TYPE = new Type<>(RubinatedNether.id("music_box_play"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MusicBoxPlayPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MusicBoxPlayPayload::entityId,
            ResourceLocation.STREAM_CODEC, MusicBoxPlayPayload::sound,
            MusicBoxPlayPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
