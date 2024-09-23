package net.artienia.rubinated_nether.recipe;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public class ModRecipeTypes {
    public static final Registrar<RecipeType<?>> RECIPE_TYPES = RubinatedNether.REGISTRIES.getRegistrar(Registries.RECIPE_TYPE);

    public static final RegistryEntry<? extends RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.entry("freezing", () -> new RecipeType<FreezingRecipe>() {
        @Override
        public String toString() {
            return "rubinated_nether:freezing";
        }
    }).register();

    public static void register() {
        RECIPE_TYPES.register();
    }

}
