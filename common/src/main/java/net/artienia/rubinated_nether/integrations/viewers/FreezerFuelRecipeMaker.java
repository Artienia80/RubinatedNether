package net.artienia.rubinated_nether.integrations.viewers;

import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class FreezerFuelRecipeMaker {
    private FreezerFuelRecipeMaker() {}

    public static List<FuelRecipe> getFuelRecipes() {
        List<FuelRecipe> fuelRecipes = new ArrayList<>();
        FreezerBlockEntity.getFreezingMap().forEach((item, burnTime) -> fuelRecipes.add(new FuelRecipe(List.of(new ItemStack(item)), burnTime, RNBlocks.FREEZER.get())));
        return fuelRecipes;
    }
}