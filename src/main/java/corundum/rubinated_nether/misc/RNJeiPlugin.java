package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNCreativeTabs;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.recipe.JEI.BaptismRecipeCategory;
import corundum.rubinated_nether.content.recipe.JEI.FreezerRecipeCategory;
import corundum.rubinated_nether.content.recipe.JEI.ResonanceRecipeCategory;
import corundum.rubinated_nether.content.recipe.JEI.RuneGrindstoneCategory;
import corundum.rubinated_nether.content.recipe.JEI.RuneGrindstoneRecipe;
import net.minecraft.world.item.Items;
import corundum.rubinated_nether.content.recipe.ResonanceRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class RNJeiPlugin implements IModPlugin {

	private List<ResonanceRecipe> resonanceRecipes = List.of();

	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "jei");
	}

	/**
	 * Carved runes are the plain rune item plus a carving component. Without this JEI sees one single "rune"
	 * and hides the carved ones; with it every carving is its own entry.
	 */
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(RNItems.RUNE.get(), RuneSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		arrangeCategorySortFile();
		RubinatedNether.LOGGER.info("Rubinating your JEI");

		var man = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		resonanceRecipes = man.getAllRecipesFor(RNRecipes.RESONANCE.get())
				.stream().map(RecipeHolder::value).toList();

		// Resonance first, then Freezing
		registration.addRecipeCategories(
				new ResonanceRecipeCategory(registration.getJeiHelpers().getGuiHelper(), resonanceRecipes),
				new FreezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
				new RuneGrindstoneCategory(registration.getJeiHelpers().getGuiHelper()),
				new BaptismRecipeCategory(registration.getJeiHelpers().getGuiHelper())
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

		registration.addRecipes(
				BaptismRecipeCategory.RECIPE_TYPE,
				man.getAllRecipesFor(RNRecipes.BAPTISM.get()).stream().map(RecipeHolder::value).toList()
		);

		// Grindstone: shrine stone in one slot -> blank rune, in both slots -> runestone (rules live in RuneGrindstoneHandler)
		ItemStack shrineStone = new ItemStack(RNBlocks.SHRINE_STONE.get());
		registration.addRecipes(
				RuneGrindstoneCategory.RECIPE_TYPE,
				List.of(
						new RuneGrindstoneRecipe(shrineStone, 1, new ItemStack(RNItems.RUNE.get())),
						new RuneGrindstoneRecipe(shrineStone, 2, new ItemStack(RNBlocks.RUNESTONE.get()))
				)
		);

		var rubyRepairRecipes = generateRubyRepairRecipes(
				registration.getVanillaRecipeFactory(),
				registration.getIngredientManager()
		);
		registration.addRecipes(RecipeTypes.ANVIL, rubyRepairRecipes);

		sortIngredientsByCreativeTabs(registration.getIngredientManager());
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
		registration.addRecipeCatalyst(
				new ItemStack(Items.GRINDSTONE),
				RuneGrindstoneCategory.RECIPE_TYPE
		);
		registration.addRecipeCatalyst(
				BaptismRecipeCategory.filledBrazier(),
				BaptismRecipeCategory.RECIPE_TYPE
		);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		arrangeCategorySortFile();
	}

	private static void arrangeCategorySortFile() {
		Path path = FMLPaths.CONFIGDIR.get().resolve("jei").resolve("recipe-category-sort-order.ini");
		if (!Files.exists(path)) {
			return;
		}
		try {
			List<String> lines = Files.readAllLines(path);
			List<String> ours = List.of(
					ResonanceRecipeCategory.RECIPE_TYPE.getUid().toString(),
					FreezerRecipeCategory.RECIPE_TYPE.getUid().toString(),
					RuneGrindstoneCategory.RECIPE_TYPE.getUid().toString(),
					BaptismRecipeCategory.RECIPE_TYPE.getUid().toString()
			);
			List<String> tags = lines.stream().filter(line -> line.contains(":tag_recipes/")).toList();
			List<String> arranged = new ArrayList<>(lines);
			arranged.removeAll(tags);
			arranged.removeAll(ours);
			int position = 0;
			while (position < arranged.size() && arranged.get(position).startsWith("minecraft:")) {
				position++;
			}
			arranged.addAll(position, ours);
			arranged.addAll(tags);
			if (!arranged.equals(lines)) {
				Files.write(path, arranged);
			}
		} catch (IOException e) {
			RubinatedNether.LOGGER.error("Could not arrange the JEI category order", e);
		}
	}

	private static void sortIngredientsByCreativeTabs(IIngredientManager ingredientManager) {
		List<ItemStack> tabOrder = new ArrayList<>();
		tabOrder.addAll(RNCreativeTabs.RN_GENERAL_TAB.get().getDisplayItems());
		tabOrder.addAll(RNCreativeTabs.RN_RUBINATION_TAB.get().getDisplayItems());
		tabOrder.addAll(RNCreativeTabs.RN_BRONZE_TAB.get().getDisplayItems());
		if (tabOrder.isEmpty()) {
			return;
		}

		record Entry(ItemStack stack, int position) {
		}

		List<Entry> entries = new ArrayList<>();
		for (ItemStack stack : ingredientManager.getAllItemStacks()) {
			if (!RubinatedNether.MODID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace())) {
				continue;
			}
			int position = tabOrder.size();
			for (int i = 0; i < tabOrder.size(); i++) {
				if (ItemStack.isSameItemSameComponents(stack, tabOrder.get(i))) {
					position = i;
					break;
				}
			}
			entries.add(new Entry(stack, position));
		}
		if (entries.isEmpty()) {
			return;
		}

		entries.sort(Comparator.comparingInt(Entry::position));
		List<ItemStack> sorted = entries.stream().map(Entry::stack).toList();

		ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, sorted);
		ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, sorted);
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