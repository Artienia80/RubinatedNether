package net.artienia.rubinated_nether.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.inventory.RecipeBookType;

public class ModRecipeBookTypes {

    @ExpectPlatform
    public static RecipeBookType getFreezer() {
        throw new AssertionError();
    }
}
