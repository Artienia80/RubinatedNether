package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.recipe.JEI.FreezerRecipeCategory;
import corundum.rubinated_nether.content.recipe.JEI.ResonanceRecipeCategory;
import corundum.rubinated_nether.content.recipe.ResonanceRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class RNJeiPlugin implements IModPlugin {

	private List<ResonanceRecipe> resonanceRecipes = List.of();

	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		RubinatedNether.LOGGER.info("Rubinating your JEI");

		var man = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		resonanceRecipes = man.getAllRecipesFor(RNRecipes.RESONANCE.get())
				.stream().map(RecipeHolder::value).toList();

		// Resonance first, then Freezing
		registration.addRecipeCategories(
				new ResonanceRecipeCategory(registration.getJeiHelpers().getGuiHelper(), resonanceRecipes),
				new FreezerRecipeCategory(registration.getJeiHelpers().getGuiHelper())
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		var man = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

		registration.addRecipes(
				FreezerRecipeCategory.RECIPE_TYPE,
				man.getAllRecipesFor(RNRecipes.FREEZING.get()).stream().map(RecipeHolder::value).toList()
		);

		registration.addRecipes(ResonanceRecipeCategory.RECIPE_TYPE, resonanceRecipes);

		var rubyRepairRecipes = generateRubyRepairRecipes(
				registration.getVanillaRecipeFactory(),
				registration.getIngredientManager()
		);
		registration.addRecipes(RecipeTypes.ANVIL, rubyRepairRecipes);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(
				new ItemStack(RNBlocks.FREEZER.get()),
				FreezerRecipeCategory.RECIPE_TYPE
		);
		registration.addRecipeCatalyst(
				new ItemStack(RNBlocks.RUBINATION_ALTAR.get()),
				ResonanceRecipeCategory.RECIPE_TYPE
		);
	}

	private List<IJeiAnvilRecipe> generateRubyRepairRecipes(
			IVanillaRecipeFactory factory,
			IIngredientManager ingredientManager) {

		var recipes = new ArrayList<IJeiAnvilRecipe>();
		var ruby = new ItemStack(RNItems.RUBY.get());

		ingredientManager.getAllItemStacks()
				.stream()
				.filter(ItemStack::isDamageableItem)
				.forEach(itemStack -> {
					var recipe = createRubyRepairRecipe(factory, itemStack, ruby);
					if (recipe != null) {
						recipes.add(recipe);
					}
				});

		RubinatedNether.LOGGER.info("Generated {} ruby repair recipes for JEI", recipes.size());
		return recipes;
	}

	private IJeiAnvilRecipe createRubyRepairRecipe(
			IVanillaRecipeFactory factory,
			ItemStack baseItem,
			ItemStack ruby) {

		var damagedItem = baseItem.copy();
		var maxDamage = damagedItem.getMaxDamage();
		damagedItem.setDamageValue((int)(maxDamage * 0.75));

		var repairAmount = 100 + (int)(maxDamage * 0.05);

		var repairedItem = damagedItem.copy();
		var newDamage = Math.max(0, damagedItem.getDamageValue() - repairAmount);
		repairedItem.setDamageValue(newDamage);

		var itemId = BuiltInRegistries.ITEM.getKey(baseItem.getItem());
		var uid = ResourceLocation.fromNamespaceAndPath(
				RubinatedNether.MODID,
				"ruby_repair/" + itemId.getNamespace() + "/" + itemId.getPath()
		);

		return factory.createAnvilRecipe(
				List.of(damagedItem),
				List.of(ruby),
				List.of(repairedItem),
				uid
		);
	}
}