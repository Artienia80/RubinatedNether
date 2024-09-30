package net.artienia.rubinated_nether.content.recipe;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

public interface RecipeBookBehavior<T extends RecipeBookMenu<?>, V extends AbstractContainerScreen<T> & RecipeUpdateListener> {
    default void containerTick(V screen) {
        screen.getRecipeBookComponent().tick();
    }

    default void slotClicked(V screen, Slot slot) {
        screen.getRecipeBookComponent().slotClicked(slot);
    }

    default boolean keyPressed(V screen, int keyCode, int scanCode, int modifiers) {
        return !screen.getRecipeBookComponent().keyPressed(keyCode, scanCode, modifiers);
    }

    default boolean hasClickedOutside(V screen, double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        boolean flag = mouseX < (double) guiLeft || mouseY < (double) guiTop || mouseX >= (double) (guiLeft + screen.imageWidth) || mouseY >= (double) (guiTop + screen.imageHeight);
        return screen.getRecipeBookComponent().hasClickedOutside(mouseX, mouseY, screen.leftPos, screen.topPos, screen.width, screen.height, mouseButton) && flag;
    }

    default boolean charTyped(V screen, char codePoint, int modifiers) {
        return screen.getRecipeBookComponent().charTyped(codePoint, modifiers);
    }

    default void recipesUpdated(V screen) {
        screen.getRecipeBookComponent().recipesUpdated();
    }
}
