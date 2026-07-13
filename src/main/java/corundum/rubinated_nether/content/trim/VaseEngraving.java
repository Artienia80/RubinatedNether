package corundum.rubinated_nether.content.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.items.Rubination;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record VaseEngraving(Rubination pattern, VaseEngravingMaterial material) {
    public static final Codec<VaseEngraving> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Rubination.CODEC.fieldOf("pattern").forGetter(VaseEngraving::pattern),
            VaseEngravingMaterial.CODEC.fieldOf("material").forGetter(VaseEngraving::material)
    ).apply(instance, VaseEngraving::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VaseEngraving> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(Rubination::byNameOrEmpty, Rubination::getSerializedName), VaseEngraving::pattern,
            VaseEngravingMaterial.STREAM_CODEC, VaseEngraving::material,
            VaseEngraving::new
    );
}