package corundum.rubinated_nether.content.recipe.JEI;

import corundum.rubinated_nether.RubinatedNether;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Shrine stone in both grindstone slots -> blank rune. */
public class RuneGrindstoneCategory implements IRecipeCategory<RuneGrindstoneRecipe> {
    public static final ResourceLocation UID = RubinatedNether.id("rune_grindstone");
    public static final RecipeType<RuneGrindstoneRecipe> RECIPE_TYPE = new RecipeType<>(UID, RuneGrindstoneRecipe.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 56;
    private static final int INPUT_X = 24;
    private static final int TOP_Y = 0;
    private static final int BOTTOM_Y = 20;
    private static final int ARROW_X = 56;
    private static final int ARROW_Y = 11;
    private static final int OUTPUT_X = 98;
    private static final int OUTPUT_Y = 10;
    private static final int NOTE_Y = 44;

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;

    public RuneGrindstoneCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GRINDSTONE));
        this.slot = helper.getSlotDrawable();
        this.arrow = helper.createAnimatedRecipeArrow(60);
    }

    @Override public RecipeType<RuneGrindstoneRecipe> getRecipeType() { return RECIPE_TYPE; }
    @Override public IDrawable getBackground()                        { return background; }
    @Override public IDrawable getIcon()                              { return icon; }
    @Override public int getWidth()                                   { return WIDTH; }
    @Override public int getHeight()                                  { return HEIGHT; }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + RubinatedNether.MODID + ".jei.rune_grindstone");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RuneGrindstoneRecipe recipe, IFocusGroup focuses) {
        // Both slots cycle through every shrine stone tier: any of them works in either slot
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, TOP_Y + 1)
                .addItemStacks(recipe.stones());
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, BOTTOM_Y + 1)
                .addItemStacks(recipe.stones());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, OUTPUT_Y + 1)
                .addItemStack(recipe.output());
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, OUTPUT_Y + 1)
                .addItemStack(new ItemStack(Items.GRINDSTONE));
    }

    @Override
    public void draw(RuneGrindstoneRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, 0, OUTPUT_Y);
        slot.draw(guiGraphics, INPUT_X, TOP_Y);
        slot.draw(guiGraphics, INPUT_X, BOTTOM_Y);
        slot.draw(guiGraphics, OUTPUT_X, OUTPUT_Y);
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component note = Component.translatable("gui." + RubinatedNether.MODID + ".jei.rune_grindstone.note");
        guiGraphics.drawString(font, note, (WIDTH - font.width(note)) / 2, NOTE_Y, 0xFF808080, false);
    }
}
