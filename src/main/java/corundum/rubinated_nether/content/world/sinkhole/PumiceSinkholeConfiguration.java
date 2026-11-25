package corundum.rubinated_nether.content.world.sinkhole;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record PumiceSinkholeConfiguration(
		int minOpeningWidth,
		int maxOpeningWidth,
		int minDepth,
		int maxDepth,
		float bottomWidthMultiplier,
		int magmaPockets,
		float rubyChance
) implements FeatureConfiguration {
	public static final Codec<PumiceSinkholeConfiguration> CODEC = RecordCodecBuilder.<PumiceSinkholeConfiguration>create(instance -> instance.group(
			Codec.intRange(5, 10).fieldOf("min_opening_width").forGetter(PumiceSinkholeConfiguration::minOpeningWidth),
			Codec.intRange(5, 10).fieldOf("max_opening_width").forGetter(PumiceSinkholeConfiguration::maxOpeningWidth),
			Codec.intRange(5, 15).fieldOf("min_depth").forGetter(PumiceSinkholeConfiguration::minDepth),
			Codec.intRange(5, 15).fieldOf("max_depth").forGetter(PumiceSinkholeConfiguration::maxDepth),
			Codec.floatRange(1.0F, 2.0F).fieldOf("bottom_width_multiplier").forGetter(PumiceSinkholeConfiguration::bottomWidthMultiplier),
			Codec.intRange(0, 24).fieldOf("magma_pockets").forGetter(PumiceSinkholeConfiguration::magmaPockets),
			Codec.floatRange(0.0F, 1.0F).fieldOf("ruby_chance").forGetter(PumiceSinkholeConfiguration::rubyChance)
	).apply(instance, PumiceSinkholeConfiguration::new)).validate(config ->
			config.minOpeningWidth <= config.maxOpeningWidth && config.minDepth <= config.maxDepth
					? DataResult.success(config)
					: DataResult.error(() -> "Minimum values must not exceed maximum values")
	);

	public static PumiceSinkholeConfiguration defaults() {
		return new PumiceSinkholeConfiguration(5, 10, 5, 15, 1.5F, 8, 0.25F);
	}
}
