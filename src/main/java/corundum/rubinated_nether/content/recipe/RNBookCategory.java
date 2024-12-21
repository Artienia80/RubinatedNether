package corundum.rubinated_nether.content.recipe;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RNBookCategory implements StringRepresentable {
    FREEZABLE_MISC("freezable_misc"),
    UNKNOWN("unknown");

    /**
     * Warning for "deprecation" is suppressed because using {@link StringRepresentable.EnumCodec} is necessary.
     */
    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<RNBookCategory> CODEC = StringRepresentable.fromEnum(RNBookCategory::values);
    private final String name;

    RNBookCategory(String name) {
        this.name = name;
    }

    @NotNull
    public String getSerializedName() {
        return this.name;
    }
}
