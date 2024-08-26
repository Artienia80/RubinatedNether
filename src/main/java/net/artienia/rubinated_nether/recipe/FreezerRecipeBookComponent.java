package net.artienia.rubinated_nether.recipe;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.entity.FreezerBlockEntity;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;
import java.util.Set;

public class FreezerRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    private static final Component FILTER_NAME = Component.translatable("gui." + RubinatedNether.MOD_ID + ".recipebook.toggleRecipes.freezable");

    @Override
    @Nonnull
    protected Component getRecipeFilterName() {
        return FILTER_NAME;
    }

    @Override
    @Nonnull
    protected Set<Item> getFuelItems() {
        return FreezerBlockEntity.getFreezingMap().keySet();
    }
}
