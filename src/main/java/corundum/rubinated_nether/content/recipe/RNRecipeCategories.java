package corundum.rubinated_nether.content.recipe;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import corundum.rubinated_nether.content.RNRecipes;
import net.minecraft.client.RecipeBookCategories;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

public class RNRecipeCategories {
        public static final Supplier<RecipeBookCategories> RUBINATED_NETHER_FREEZABLE_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.valueOf("RUBINATED_NETHER_FREEZABLE_SEARCH"));
        public static final Supplier<RecipeBookCategories> RUBINATED_NETHER_FREEZABLE_MISC = Suppliers.memoize(() -> RecipeBookCategories.valueOf("RUBINATED_NETHER_FREEZABLE_MISC"));

        /**
         * Registers the mod's categories to be used in-game, along with functions to sort items.
         * To add sub-categories to be used by the search, use addAggregateCategories with the
         * search category as the first parameter.
         */
        public static void registerRecipeCategories(RegisterRecipeBookCategoriesEvent event) {
        // Freezing
        event.registerBookCategories(RNRecipeBookTypes.FREEZER, ImmutableList.of(RUBINATED_NETHER_FREEZABLE_SEARCH.get(), RUBINATED_NETHER_FREEZABLE_MISC.get()));
        event.registerAggregateCategory(RUBINATED_NETHER_FREEZABLE_SEARCH.get(), ImmutableList.of(RUBINATED_NETHER_FREEZABLE_MISC.get()));

        event.registerRecipeCategoryFinder(RNRecipes.FREEZING.get(), recipe -> RUBINATED_NETHER_FREEZABLE_MISC.get());
    }
}
