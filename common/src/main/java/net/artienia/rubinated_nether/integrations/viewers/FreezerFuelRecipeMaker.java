package net.artienia.rubinated_nether.integrations.viewers;

import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.block.entity.FreezerBlockEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class FreezerFuelRecipeMaker {
    private FreezerFuelRecipeMaker() {}

    public static List<FuelRecipe> getFuelRecipes() {
        List<FuelRecipe> fuelRecipes = new ArrayList<>();
        FreezerBlockEntity.getFreezingMap().forEach((item, burnTime) -> fuelRecipes.add(new FuelRecipe(List.of(new ItemStack(item)), burnTime, ModBlocks.FREEZER.get())));
        return fuelRecipes;
    }
}