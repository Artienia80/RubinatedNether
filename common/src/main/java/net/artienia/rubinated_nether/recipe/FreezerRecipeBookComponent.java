package net.artienia.rubinated_nether.recipe;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.entity.FreezerBlockEntity;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FreezerRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    private static final Component FILTER_NAME = Component.translatable("gui." + RubinatedNether.MOD_ID + ".recipebook.toggleRecipes.freezable");

    @Override
    @NotNull
    protected Component getRecipeFilterName() {
        return FILTER_NAME;
    }

    @Override
    @NotNull
    protected Set<Item> getFuelItems() {
        return FreezerBlockEntity.getFreezingMap().keySet();
    }
}
