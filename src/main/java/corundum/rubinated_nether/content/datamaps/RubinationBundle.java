package corundum.rubinated_nether.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record RubinationBundle(List<Entry> enchantments) {
	public static final Codec<RubinationBundle> CODEC = RecordCodecBuilder.create(in -> in.group(
			Entry.CODEC.listOf().fieldOf("enchantments").forGetter(RubinationBundle::enchantments)
	).apply(in, RubinationBundle::new));

	public record Entry(ResourceLocation enchantment, int level) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(in -> in.group(
				ResourceLocation.CODEC.fieldOf("enchantment").forGetter(Entry::enchantment),
				Codec.intRange(1, 255).fieldOf("level").forGetter(Entry::level)
		).apply(in, Entry::new));
	}
}
