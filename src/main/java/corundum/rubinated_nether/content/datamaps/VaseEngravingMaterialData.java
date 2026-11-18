package corundum.rubinated_nether.content.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextColor;

import java.util.Optional;

public record VaseEngravingMaterialData(String palette, Optional<TextColor> color) {
	public static final Codec<VaseEngravingMaterialData> CODEC = RecordCodecBuilder.create(in -> in.group(
			Codec.STRING.fieldOf("palette").forGetter(VaseEngravingMaterialData::palette),
			TextColor.CODEC.optionalFieldOf("color").forGetter(VaseEngravingMaterialData::color)
	).apply(in, VaseEngravingMaterialData::new));
}
