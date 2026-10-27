package corundum.rubinated_nether.content.blocks;

import net.minecraft.util.StringRepresentable;

public enum LaserMode implements StringRepresentable {
	SPECTRUM("spectrum"), // Blocks + Entities
	ULTRAVIOLET("uv"),    // Blocks Only
	INFRARED("ir");       // Entities Only

	private final String name;
	LaserMode(String name) { this.name = name; }

	@Override public String getSerializedName() { return this.name; }

	public LaserMode cycle() {
		return switch (this) {
			case SPECTRUM -> ULTRAVIOLET;
			case ULTRAVIOLET -> INFRARED;
			case INFRARED -> SPECTRUM;
		};
	}

	public boolean detectsBlocks() { return this == SPECTRUM || this == ULTRAVIOLET; }
	public boolean detectsEntities() { return this == SPECTRUM || this == INFRARED; }
}
