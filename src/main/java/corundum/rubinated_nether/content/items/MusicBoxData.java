package corundum.rubinated_nether.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MusicBoxData(int current, boolean playing, long songEndTick, boolean sinner, boolean looping) {
    public static final MusicBoxData EMPTY = new MusicBoxData(0, false, 0L, false, false);

    public static final Codec<MusicBoxData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("current", 0).forGetter(MusicBoxData::current),
            Codec.BOOL.optionalFieldOf("playing", false).forGetter(MusicBoxData::playing),
            Codec.LONG.optionalFieldOf("song_end_tick", 0L).forGetter(MusicBoxData::songEndTick),
            Codec.BOOL.optionalFieldOf("sinner", false).forGetter(MusicBoxData::sinner),
            Codec.BOOL.optionalFieldOf("looping", false).forGetter(MusicBoxData::looping)
    ).apply(instance, MusicBoxData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MusicBoxData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MusicBoxData::current,
            ByteBufCodecs.BOOL, MusicBoxData::playing,
            ByteBufCodecs.VAR_LONG, MusicBoxData::songEndTick,
            ByteBufCodecs.BOOL, MusicBoxData::sinner,
            ByteBufCodecs.BOOL, MusicBoxData::looping,
            MusicBoxData::new
    );

    public MusicBoxData withNowPlaying(int index, long endTick, boolean sinner) {
        return new MusicBoxData(index, true, endTick, sinner, looping);
    }

    public MusicBoxData stopped() {
        return new MusicBoxData(current, false, 0L, false, looping);
    }

    public MusicBoxData withLooping(boolean looping) {
        return new MusicBoxData(current, playing, songEndTick, sinner, looping);
    }
}