package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.recipe.freezing.FreezingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public final class RNRecipes {
	public static final Registrar<RecipeType<?>> RECIPE_TYPES = RubinatedNether.REGISTRIES.getRegistrar(Registries.RECIPE_TYPE);
	public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = RubinatedNether.REGISTRIES.getRegistrar(Registries.RECIPE_SERIALIZER);

	public static final RegistryEntry<RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.<RecipeType<FreezingRecipe>>entry("freezing", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "rubinated_nether:freezing";
		}
	}).register();

	public static final RegistryEntry<FreezingRecipe.Serializer> FREEZING_SERIALIZER = RECIPE_SERIALIZERS.entry("freezing", FreezingRecipe.Serializer::new).register();


	public static void register() {
		RECIPE_TYPES.register();
		RECIPE_SERIALIZERS.register();
	}

}
