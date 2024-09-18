package net.artienia.rubinated_nether.platform.fabric;

import net.minecraft.world.inventory.RecipeBookType;

public class ModRecipeBookTypesImpl {
    public static RecipeBookType getFreezer() {
        // TODO: Scary mixins or reflective shit to modify an enum
        return RecipeBookType.FURNACE;
    }
}
