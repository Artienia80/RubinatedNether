package corundum.rubinated_nether.content.recipe;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.trim.RNBronzeTrimTarnishRecipe;
import corundum.rubinated_nether.content.trim.VaseEngravingRecipe;
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

	public static final DeferredHolder<RecipeSerializer<?>, RNBronzeTrimTarnishRecipe.Serializer> BRONZE_TRIM_UPGRADE = RECIPE_SERIALIZERS.register(
			"bronze_trim_upgrade",
			RNBronzeTrimTarnishRecipe.Serializer::new
	);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<VaseEngravingRecipe>> VASE_ENGRAVING =
			RECIPE_SERIALIZERS.register("vase_engraving", VaseEngravingRecipe.Serializer::new);

	public static final DeferredHolder<RecipeSerializer<?>, ResonanceRecipeSerializer> RESONANCE =
			RECIPE_SERIALIZERS.register("resonance", ResonanceRecipeSerializer::new);
}