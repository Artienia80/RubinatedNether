package corundum.rubinated_nether.content.recipe.fuel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FreezerFuel(String item, int freezeTime) {
	public static final Codec<FreezerFuel> FREEZER_FUEL_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			Codec.STRING.fieldOf("item").forGetter(FreezerFuel::item),
			Codec.INT.fieldOf("freeze_time").forGetter(FreezerFuel::freezeTime)
		).apply(instance, FreezerFuel::new)	
	);
}