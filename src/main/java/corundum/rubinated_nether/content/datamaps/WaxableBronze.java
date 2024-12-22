package corundum.rubinated_nether.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public record WaxableBronze(Block waxed) {
    public static final Codec<WaxableBronze> WAXABLE_CODEC = BuiltInRegistries.BLOCK.byNameCodec()
            .xmap(WaxableBronze::new, WaxableBronze::waxed);
    public static final Codec<WaxableBronze> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(in -> in.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("waxed").forGetter(WaxableBronze::waxed)).apply(in, WaxableBronze::new)),
            WAXABLE_CODEC);
}

