package corundum.rubinated_nether.content.world.structures.tree;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum PetrifiedTreeVariant implements StringRepresentable {
	STANDING("standing"),
	CHOPPED("chopped"),
	FALLEN("fallen");

	public static final Codec<PetrifiedTreeVariant> CODEC = StringRepresentable.fromEnum(PetrifiedTreeVariant::values);

	private final String name;

	PetrifiedTreeVariant(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public static PetrifiedTreeVariant byName(String name) {
		for (PetrifiedTreeVariant variant : values()) {
			if (variant.name.equals(name)) {
				return variant;
			}
		}
		return STANDING;
	}
}
