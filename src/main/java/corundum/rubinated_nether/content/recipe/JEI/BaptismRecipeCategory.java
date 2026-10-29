package corundum.rubinated_nether.content.recipe.JEI;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import corundum.rubinated_nether.content.recipe.BaptismRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

import java.util.Arrays;
import java.util.List;

public class BaptismRecipeCategory implements IRecipeCategory<BaptismRecipe> {

    public static final ResourceLocation UID = RubinatedNether.id("baptism");
    public static final RecipeType<BaptismRecipe> RECIPE_TYPE = new RecipeType<>(UID, BaptismRecipe.class);

    private static final int SLOT_SIZE = 18;
    private static final int ARROW_WIDTH = 24;
    private static final int GAP = 8;
    private static final int WIDTH = SLOT_SIZE + GAP + ARROW_WIDTH + GAP + SLOT_SIZE;
    private static final int HEIGHT = SLOT_SIZE * 2 + 2;
    private static final int INPUT_X = 0;
    private static final int ARROW_X = SLOT_SIZE + GAP;
    private static final int ARROW_Y = 1;
    private static final int OUTPUT_X = ARROW_X + ARROW_WIDTH + GAP;
    private static final int CATALYST_X = (WIDTH - SLOT_SIZE) / 2;
    private static final int CATALYST_Y = SLOT_SIZE + 2;

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;

    public BaptismRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, filledBrazier());
        this.slot = helper.getSlotDrawable();
        this.arrow = helper.createAnimatedRecipeArrow(60);
    }

    public static ItemStack filledBrazier() {
        ItemStack brazier = new ItemStack(RNBlocks.BRAZIER.get());
        brazier.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        return brazier;
    }

    @Override public RecipeType<BaptismRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public IDrawable getBackground()                 { return background; }
    @Override public IDrawable getIcon()                       { return icon; }
    @Override public int getWidth()                            { return WIDTH; }
    @Override public int getHeight()                           { return HEIGHT; }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + RubinatedNether.MODID + ".jei.baptism");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BaptismRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, 1)
                .addItemStacks(inputStacks(recipe));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, 1)
                .addItemStack(recipe.getResult());
        builder.addSlot(RecipeIngredientRole.CATALYST, CATALYST_X + 1, CATALYST_Y + 1)
                .addItemStack(filledBrazier());
    }

    private static List<ItemStack> inputStacks(BaptismRecipe recipe) {
        return Arrays.stream(recipe.getIngredient().getItems())
                .map(stack -> {
                    Rubination carving = RuneCarvingHelper.getCarving(stack);
                    return carving == Rubination.EMPTY ? stack : RuneCarvingHelper.withCarving(stack, carving);
                })
                .toList();
    }

    @Override
    public void draw(BaptismRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, INPUT_X, 0);
        slot.draw(guiGraphics, OUTPUT_X, 0);
        slot.draw(guiGraphics, CATALYST_X, CATALYST_Y);
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
    }
}
