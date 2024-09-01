package net.artienia.rubinated_nether.recipe;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, RubinatedNether.MOD_ID);

    public static final RegistryObject<RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.register("freezing", () -> RecipeType.simple(new ResourceLocation(RubinatedNether.MOD_ID, "freezing")));

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }

}
