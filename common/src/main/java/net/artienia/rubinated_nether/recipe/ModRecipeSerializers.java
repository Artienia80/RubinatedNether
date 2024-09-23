package net.artienia.rubinated_nether.recipe;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public class ModRecipeSerializers {
    public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = RubinatedNether.REGISTRIES.getRegistrar(Registries.RECIPE_SERIALIZER);

    public static final RegistryEntry<FreezingRecipe.Serializer> FREEZING = RECIPE_SERIALIZERS.entry("freezing", FreezingRecipe.Serializer::new).register();

    public static void register() {
        RECIPE_SERIALIZERS.register();
    }

}
