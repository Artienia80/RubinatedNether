package net.artienia.rubinated_nether.recipe;

import net.minecraft.util.StringRepresentable;

public enum ModBookCategory implements StringRepresentable {
    FREEZABLE_MISC("freezable_misc"),
    UNKNOWN("unknown");

    /**
     * Warning for "deprecation" is suppressed because using {@link EnumCodec} is necessary.
     */
    @SuppressWarnings("deprecation")
    public static final EnumCodec<ModBookCategory> CODEC = StringRepresentable.fromEnum(ModBookCategory::values);
    private final String name;

    ModBookCategory(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
