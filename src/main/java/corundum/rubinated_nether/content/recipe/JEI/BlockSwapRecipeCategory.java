package corundum.rubinated_nether.content.recipe.JEI;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.recipe.BlockSwapRecipe;
import corundum.rubinated_nether.content.recipe.CrackingRecipe;
import corundum.rubinated_nether.content.recipe.DerubinationRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * Shows a block-to-block transformation (Cracking or Derubination): the input block, the output block, and the tools
 * that apply it. Hovering the arrow explains the interaction requirements.
 */
public class BlockSwapRecipeCategory<T extends BlockSwapRecipe> implements IRecipeCategory<T> {

    public static final RecipeType<CrackingRecipe> CRACKING =
            new RecipeType<>(RubinatedNether.id("cracking"), CrackingRecipe.class);
    public static final RecipeType<DerubinationRecipe> DERUBINATION =
            new RecipeType<>(RubinatedNether.id("derubination"), DerubinationRecipe.class);

    private static final int SLOT_SIZE = 18;
    private static final int ARROW_WIDTH = 24;
    private static final int GAP = 8;
    private static final int WIDTH = SLOT_SIZE + GAP + ARROW_WIDTH + GAP + SLOT_SIZE;
    private static final int HEIGHT = SLOT_SIZE * 2 + 2;
    private static final int INPUT_X = 0;
    private static final int ARROW_X = SLOT_SIZE + GAP;
    private static final int ARROW_Y = 1;
    private static final int OUTPUT_X = ARROW_X + ARROW_WIDTH + GAP;
    private static final int TOOL_X = (WIDTH - SLOT_SIZE) / 2;
    private static final int TOOL_Y = SLOT_SIZE + 2;

    private final RecipeType<T> recipeType;
    private final String titleKey;
    private final List<String> tooltipKeys;
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;

    private BlockSwapRecipeCategory(IGuiHelper helper, RecipeType<T> recipeType, String titleKey,
                                    ItemStack iconStack, List<String> tooltipKeys) {
        this.recipeType = recipeType;
        this.titleKey = titleKey;
        this.tooltipKeys = tooltipKeys;
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
        this.slot = helper.getSlotDrawable();
        this.arrow = helper.createAnimatedRecipeArrow(60);
    }

    public static BlockSwapRecipeCategory<CrackingRecipe> cracking(IGuiHelper helper) {
        return new BlockSwapRecipeCategory<>(
                helper, CRACKING, "cracking",
                new ItemStack(Items.CRACKED_STONE_BRICKS),
                List.of("cracking.info")
        );
    }

    public static BlockSwapRecipeCategory<DerubinationRecipe> derubination(IGuiHelper helper) {
        return new BlockSwapRecipeCategory<>(
                helper, DERUBINATION, "derubination",
                new ItemStack(RNItems.RUBY_SHARD.get()),
                List.of("derubination.info", "derubination.altar")
        );
    }

    /** The tools that apply these transformations; shown as the catalyst and registered as JEI catalysts. */
    public static List<ItemStack> tools() {
        return List.of(
                new ItemStack(RNItems.BRONZE_DRILL.get()),
                new ItemStack(RNItems.BRONZE_JACKHAMMER.get())
        );
    }

    @Override public RecipeType<T> getRecipeType() { return recipeType; }
    @Override public IDrawable getBackground()     { return background; }
    @Override public IDrawable getIcon()           { return icon; }
    @Override public int getWidth()                { return WIDTH; }
    @Override public int getHeight()               { return HEIGHT; }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + RubinatedNether.MODID + ".jei." + titleKey);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, 1)
                .addItemStack(new ItemStack(recipe.getSource()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, 1)
                .addItemStack(new ItemStack(recipe.getResultBlock()));
        builder.addSlot(RecipeIngredientRole.CATALYST, TOOL_X + 1, TOOL_Y + 1)
                .addItemStacks(tools());
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, INPUT_X, 0);
        slot.draw(guiGraphics, OUTPUT_X, 0);
        slot.draw(guiGraphics, TOOL_X, TOOL_Y);
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
    }

    @Override
    public List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH && mouseY >= ARROW_Y && mouseY < ARROW_Y + SLOT_SIZE) {
            return tooltipKeys.stream()
                    .<Component>map(key -> Component.translatable("gui." + RubinatedNether.MODID + ".jei." + key))
                    .toList();
        }
        return List.of();
    }
}
