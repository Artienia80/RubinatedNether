package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
		BuiltInRegistries.RECIPE_SERIALIZER, 
		RubinatedNether.MODID
	);

	public static final DeferredHolder<RecipeSerializer<?>, FreezerCookingSerializer<FreezingRecipe>> FREEZING = RECIPE_SERIALIZERS.register(
		"freezing", 
		FreezingRecipe.Serializer::new
	);
}
