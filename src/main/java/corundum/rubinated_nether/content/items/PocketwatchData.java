package corundum.rubinated_nether.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PocketwatchData(int storedTicks, boolean ticking, long startedAtGameTime) {
    public static final PocketwatchData EMPTY = new PocketwatchData(0, false, 0L);

    public static final Codec<PocketwatchData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("stored_ticks").forGetter(PocketwatchData::storedTicks),
            Codec.BOOL.fieldOf("ticking").forGetter(PocketwatchData::ticking),
            Codec.LONG.fieldOf("started_at_game_time").forGetter(PocketwatchData::startedAtGameTime)
    ).apply(instance, PocketwatchData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PocketwatchData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PocketwatchData::storedTicks,
            ByteBufCodecs.BOOL, PocketwatchData::ticking,
            ByteBufCodecs.VAR_LONG, PocketwatchData::startedAtGameTime,
            PocketwatchData::new
    );
}