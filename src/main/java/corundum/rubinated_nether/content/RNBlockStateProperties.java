package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.items.Rubination;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RNBlockStateProperties {
    public static final EnumProperty<Rubination> HAS_RUNE = EnumProperty.create("has_rune", Rubination.class);
}
