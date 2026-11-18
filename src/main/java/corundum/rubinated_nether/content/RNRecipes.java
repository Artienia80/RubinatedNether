package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.recipe.BaptismRecipe;
import corundum.rubinated_nether.content.recipe.FreezingRecipe;
import corundum.rubinated_nether.content.recipe.CrackingRecipe;
import corundum.rubinated_nether.content.recipe.DerubinationRecipe;
import corundum.rubinated_nether.content.recipe.PetrificationRecipe;
import corundum.rubinated_nether.content.recipe.ResonanceRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNRecipes {
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
		BuiltInRegistries.RECIPE_TYPE, 
		RubinatedNether.MODID
	);

	public static final DeferredHolder<RecipeType<?>, RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.register(
		"freezing", 
		() -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "freezing"))
		);

	public static final DeferredHolder<RecipeType<?>, RecipeType<ResonanceRecipe>> RESONANCE =
			RECIPE_TYPES.register("resonance", RecipeType::simple);

	public static final DeferredHolder<RecipeType<?>, RecipeType<BaptismRecipe>> BAPTISM =
			RECIPE_TYPES.register("baptism", RecipeType::simple);

	public static final DeferredHolder<RecipeType<?>, RecipeType<DerubinationRecipe>> DERUBINATION =
			RECIPE_TYPES.register("derubination", RecipeType::simple);

	public static final DeferredHolder<RecipeType<?>, RecipeType<CrackingRecipe>> CRACKING =
			RECIPE_TYPES.register("cracking", RecipeType::simple);

	public static final DeferredHolder<RecipeType<?>, RecipeType<PetrificationRecipe>> PETRIFICATION =
			RECIPE_TYPES.register("petrification", RecipeType::simple);
}
