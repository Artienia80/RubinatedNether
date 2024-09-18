package net.artienia.rubinated_nether.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;

public class ModFuelCategory implements IRecipeCategory<FuelRecipe> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(RubinatedNether.MOD_ID, "textures/gui/freezer_gui.png");
    public static final RecipeType<FuelRecipe> RECIPE_TYPE = RecipeType.create(RubinatedNether.MOD_ID, "fuel", FuelRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedFuelIndicator;

    public ModFuelCategory(IGuiHelper helper) {
        this(helper, List.of(ModBlocks.FREEZER.get().getName().getString()));
    }

    public ModFuelCategory(IGuiHelper helper, List<String> craftingStations) {
        String longestString = craftingStations.stream().max(Comparator.comparingInt(String::length)).get();
        Component longestStationName = Component.literal(longestString);

        Font fontRenderer = Minecraft.getInstance().font;
        Component maxBurnTimeText = createBurnTimeText(10000, longestStationName);
        int maxStringWidth = fontRenderer.width(maxBurnTimeText.getString());
        int backgroundHeight = 34;
        int textPadding = 20;

        this.background = helper.drawableBuilder(this.getTexture(), 55, 36, 18, backgroundHeight).addPadding(0, 0, 0, textPadding + maxStringWidth).build();
        this.icon = helper.createDrawable(this.getTexture(), 176, 0, 14, 13);

        this.cachedFuelIndicator = CacheBuilder.newBuilder().maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer burnTime) {
                        return helper.drawableBuilder(ModFuelCategory.this.getTexture(), 176, 0, 14, 13).buildAnimated(burnTime, IDrawableAnimated.StartDirection.TOP, true);
                    }
                });
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + RubinatedNether.MOD_ID + ".jei.fuel");
    }

    @Override
    public RecipeType<FuelRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FuelRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 17).addItemStacks(recipe.getInput());
    }

    @Override
    public void draw(FuelRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int burnTime = recipe.getBurnTime();
        IDrawableAnimated fuelIndicator = this.cachedFuelIndicator.getUnchecked(burnTime);
        fuelIndicator.draw(guiGraphics, 1, 0);

        Font font = Minecraft.getInstance().font;
        Component burnTimeText = createBurnTimeText(recipe.getBurnTime(), recipe.getUsage().getName());
        int stringWidth = font.width(burnTimeText);
        guiGraphics.drawString(font, burnTimeText, this.background.getWidth() - stringWidth, 14, 0xFF808080, false);
    }

    private static Component createBurnTimeText(int burnTime, Component usage) {
        return Component.translatable("gui.jei.category.smelting.time.seconds", burnTime / 20).append(" (").append(usage).append(")");
    }
}
