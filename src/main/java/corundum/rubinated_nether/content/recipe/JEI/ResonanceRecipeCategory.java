package corundum.rubinated_nether.content.recipe.JEI;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.recipe.ResonanceRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ResonanceRecipeCategory implements IRecipeCategory<ResonanceRecipe> {

    public static final ResourceLocation UID = RubinatedNether.id("resonance");
    public static final RecipeType<ResonanceRecipe> RECIPE_TYPE = new RecipeType<>(UID, ResonanceRecipe.class);

    private static final int MIN_COLS  = 3;
    private static final int MAX_COLS  = 8;
    private static final int SLOT_SIZE = 18;
    private static final int GAP       = 8;

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawable slot;

    private final int cols;
    private final int rows;

    public ResonanceRecipeCategory(IGuiHelper helper, List<ResonanceRecipe> recipes) {
        int maxInputs = recipes.stream()
                .mapToInt(r -> r.getIngredient().getItems().length)
                .max().orElse(1);

        this.cols = Math.max(MIN_COLS, Math.min(maxInputs, MAX_COLS));
        this.rows = Math.max(1, (maxInputs + cols - 1) / cols);

        int catalystColW = SLOT_SIZE;
        int totalW = cols * SLOT_SIZE + GAP + catalystColW + GAP + SLOT_SIZE;

        int catalystH = SLOT_SIZE * 2 + 2;
        int totalH    = Math.max(rows * SLOT_SIZE, catalystH);

        this.background = helper.createBlankDrawable(totalW, totalH);

        this.icon = helper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(RNBlocks.RUBINATION_ALTAR.get()));

        this.slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<ResonanceRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public IDrawableStatic getBackground()            { return background; }
    @Override public IDrawable getIcon()                        { return icon; }
    @Override public int getWidth()                             { return background.getWidth(); }
    @Override public int getHeight()                            { return background.getHeight(); }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + RubinatedNether.MODID + ".jei.resonance");
    }

    private int catalystLeft() { return cols * SLOT_SIZE + GAP; }

    private int outputLeft()   { return catalystLeft() + SLOT_SIZE + GAP; }

    private int catalystTopY(int totalH) {
        int catalystH = SLOT_SIZE * 2 + 2;
        return (totalH - catalystH) / 2;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ResonanceRecipe recipe, IFocusGroup focuses) {
        ItemStack[] inputs = recipe.getIngredient().getItems();
        int recipeRows = Math.max(1, (inputs.length + cols - 1) / cols);
        int totalH     = background.getHeight();

        int inputOffY = (totalH - recipeRows * SLOT_SIZE) / 2;
        for (int i = 0; i < inputs.length; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT,
                            (i % cols) * SLOT_SIZE + 1,
                            inputOffY + (i / cols) * SLOT_SIZE + 1)
                    .addItemStack(inputs[i]);
        }

        int cTopY = catalystTopY(totalH);
        builder.addSlot(RecipeIngredientRole.CATALYST, catalystLeft() + 1, cTopY + 1)
                .addItemStack(new ItemStack(RNBlocks.RUBINATION_ALTAR.get()));

        recipe.getOffering().ifPresent($ ->
                builder.addSlot(RecipeIngredientRole.CATALYST,
                                catalystLeft() + 1, cTopY + SLOT_SIZE + 2 + 1)
                        .addItemStack(new ItemStack(RNItems.RITUAL_OFFERING.get()))
        );

        int outputY = (totalH - SLOT_SIZE) / 2;
        builder.addSlot(RecipeIngredientRole.OUTPUT, outputLeft() + 1, outputY + 1)
                .addItemStack(recipe.getResult());
    }

    @Override
    public void draw(ResonanceRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        ItemStack[] inputs = recipe.getIngredient().getItems();
        int recipeRows = Math.max(1, (inputs.length + cols - 1) / cols);
        int totalH     = background.getHeight();

        int inputOffY = (totalH - recipeRows * SLOT_SIZE) / 2;
        for (int i = 0; i < inputs.length; i++) {
            slot.draw(guiGraphics,
                    (i % cols) * SLOT_SIZE,
                    inputOffY + (i / cols) * SLOT_SIZE);
        }

        int cTopY = catalystTopY(totalH);
        slot.draw(guiGraphics, catalystLeft(), cTopY);
        if (recipe.getOffering().isPresent()) {
            slot.draw(guiGraphics, catalystLeft(), cTopY + SLOT_SIZE + 2);
        }

        int outputY = (totalH - SLOT_SIZE) / 2;
        slot.draw(guiGraphics, outputLeft(), outputY);
    }
}