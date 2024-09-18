package net.artienia.rubinated_nether.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(RubinatedNether.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<ModCookingSerializer<FreezingRecipe>> FREEZING = RECIPE_SERIALIZERS.register("freezing", FreezingRecipe.Serializer::new);

    public static void register() {
        RECIPE_SERIALIZERS.register();
    }

}
