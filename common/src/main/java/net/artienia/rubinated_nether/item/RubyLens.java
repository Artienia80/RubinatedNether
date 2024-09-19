package net.artienia.rubinated_nether.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

public class RubyLens extends Item implements Equipable {
    public RubyLens(Properties properties) {
        super(properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
