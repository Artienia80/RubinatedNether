package corundum.rubinated_nether.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum CrossbowRodType {
    BRONZE,
    BREEZE,
    BLAZE;

    public int modelValue() {
        return this.ordinal() + 1;
    }

    public Item item() {
        return switch (this) {
            case BRONZE -> RNItems.BRONZE_ROD.get();
            case BREEZE -> Items.BREEZE_ROD;
            case BLAZE -> Items.BLAZE_ROD;
        };
    }

    public static CrossbowRodType of(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (CrossbowRodType type : values()) {
            if (stack.is(type.item())) {
                return type;
            }
        }
        return null;
    }

    public static CrossbowRodType byId(int id) {
        CrossbowRodType[] values = values();
        return values[Math.floorMod(id, values.length)];
    }

    public static CrossbowRodType byName(String name) {
        for (CrossbowRodType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
