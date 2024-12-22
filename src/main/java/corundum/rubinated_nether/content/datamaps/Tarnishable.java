package corundum.rubinated_nether.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public record Tarnishable(Block nextTarnishmentStage) {
    public static final Codec<Tarnishable> TARNISHABLE_CODEC = BuiltInRegistries.BLOCK
		.byNameCodec()
        .xmap(Tarnishable::new, Tarnishable::nextTarnishmentStage);

    public static final Codec<Tarnishable> CODEC = Codec.withAlternative(
		RecordCodecBuilder.create(in -> in.group(
				BuiltInRegistries.BLOCK
					.byNameCodec()
					.fieldOf("next_tarnishment_stage")
					.forGetter(Tarnishable::nextTarnishmentStage)
			).apply(in, Tarnishable::new)
		),
		TARNISHABLE_CODEC
	);
}
