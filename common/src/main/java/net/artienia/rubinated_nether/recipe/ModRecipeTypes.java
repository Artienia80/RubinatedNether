package net.artienia.rubinated_nether.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(RubinatedNether.MOD_ID, Registries.RECIPE_TYPE);

    public static final RegistrySupplier<RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.register("freezing", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "rubinated_nether:freezing";
        }
    });

    public static void register() {
        RECIPE_TYPES.register();
    }

}
