package net.artienia.rubinated_nether.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.recipe.FreezingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FreezingCategory implements IRecipeCategory<FreezingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(RubinatedNether.MOD_ID, "freezing");
    public static final ResourceLocation TEXTURE = new ResourceLocation(RubinatedNether.MOD_ID, "textures/gui/freezer_gui.png");

    public static final RecipeType<FreezingRecipe> FREEZING_RECIPE_TYPE = new RecipeType<>(UID, FreezingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable fuelIndicator;
    private final IDrawableAnimated animatedProgressArrow;

    public FreezingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 55, 16, 82, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FREEZER.get().asItem()));
        this.fuelIndicator = helper.createDrawable(TEXTURE, 176, 0, 14, 13);
        this.animatedProgressArrow = helper.createAnimatedDrawable(helper.createDrawable(TEXTURE, 176, 14, 23, 16), 100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<FreezingRecipe> getRecipeType() {
        return FREEZING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui."+ RubinatedNether.MOD_ID +".jei.freezer");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FreezingRecipe freezingRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(freezingRecipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(freezingRecipe.getResult());
    }

    @Override
    public void draw(FreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.animatedProgressArrow.draw(guiGraphics, 24, 18);
        this.fuelIndicator.draw(guiGraphics, 1, 20);
        this.drawExperience(recipe, guiGraphics, 1, this.background);
        this.drawCookingTime(guiGraphics, 45, recipe.getCookingTime(), this.background);
    }

    protected void drawExperience(FreezingRecipe recipe, GuiGraphics guiGraphics, int y, IDrawable background) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Font fontRenderer = Minecraft.getInstance().font;
            int stringWidth = fontRenderer.width(experienceString);
            guiGraphics.drawString(fontRenderer, experienceString, background.getWidth() - stringWidth, y, 0xFF808080, false);
        }
    }

    protected void drawCookingTime(GuiGraphics guiGraphics, int y, int time, IDrawable background) {
        if (time > 0) {
            int cookTimeSeconds = time / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Font fontRenderer = Minecraft.getInstance().font;
            int stringWidth = fontRenderer.width(timeString);
            guiGraphics.drawString(fontRenderer, timeString, background.getWidth() - stringWidth, y, 0xFF808080, false);
        }
    }
}