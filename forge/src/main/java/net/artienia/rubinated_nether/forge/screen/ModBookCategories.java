package net.artienia.rubinated_nether.forge.screen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.item.ModItems;
import net.artienia.rubinated_nether.recipe.ModRecipeTypes;
import net.artienia.rubinated_nether.platform.ModRecipeBookTypes;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = RubinatedNether.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBookCategories {
    public static final Supplier<RecipeBookCategories> FREEZABLE_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("FREEZABLE_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> FREEZABLE_MISC = Suppliers.memoize(() -> RecipeBookCategories.create("FREEZABLE_MISC", new ItemStack(ModItems.RUBY.get())));


    /**
     * Registers the mod's categories to be used in-game, along with functions to sort items.
     * To add sub-categories to be used by the search, use addAggregateCategories with the
     * search category as the first parameter.
     */
    @SubscribeEvent
    public static void registerRecipeCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(ModRecipeBookTypes.getFreezer(), ImmutableList.of(FREEZABLE_SEARCH.get(), FREEZABLE_MISC.get()));
        event.registerAggregateCategory(FREEZABLE_SEARCH.get(), ImmutableList.of(FREEZABLE_MISC.get()));
        event.registerRecipeCategoryFinder(ModRecipeTypes.FREEZING.get(), recipe -> FREEZABLE_MISC.get());
    }
}
