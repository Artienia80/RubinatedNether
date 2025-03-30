package corundum.rubinated_nether.content.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;

public enum Rubination implements StringRepresentable {
    GREED("greed"),
    WRATH("wrath"),
    SLOTH("sloth"),
    GLUTTONY("gluttony"),
    ENVY("envy"),
    VAINGLORY("vainglory"),
    PRIDE("pride"),
    ACEDIA("acedia"),
    LUXURIA("luxuria"),
    INSIDIAE("insidiae"),
    SUPERBIA("superbia"),
    TRISTIA("tristia"),
    STUDIOSE("studiose"),
    ARDENTER("ardenter"),
    NIMIS("nimis"),
    EMPTY("empty");

    private final String name;

    Rubination(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
