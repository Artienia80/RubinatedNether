package corundum.rubinated_nether.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public record Tarnishable(Optional<Block> nextTarnishmentStage, Optional<Block> crystallized) {
	public static final Codec<Tarnishable> CODEC = RecordCodecBuilder.create(in -> in.group(
			BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("next_tarnishment_stage").forGetter(Tarnishable::nextTarnishmentStage),
			BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("crystallized").forGetter(Tarnishable::crystallized)
	).apply(in, Tarnishable::new));
}
