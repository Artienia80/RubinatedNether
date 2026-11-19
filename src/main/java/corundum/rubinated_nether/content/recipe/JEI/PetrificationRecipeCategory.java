
package corundum.rubinated_nether.content.recipe.JEI;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.recipe.PetrificationRecipe;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Displays a petrifiable block, its result, and the Soul Starved Sand catalyst. */
public class PetrificationRecipeCategory implements IRecipeCategory<PetrificationRecipe> {
	public static final ResourceLocation UID = RubinatedNether.id("petrification");
	public static final RecipeType<PetrificationRecipe> RECIPE_TYPE = new RecipeType<>(UID, PetrificationRecipe.class);

	private static final ResourceLocation SOUL_FIRE_SPRITE =
			ResourceLocation.withDefaultNamespace("block/soul_fire_0");

	private static final int SLOT_SIZE = 18;
	private static final int ICON_SIZE = 16;
	private static final int ARROW_WIDTH = 24;
	private static final int GAP = 8;
	private static final int WIDTH = SLOT_SIZE + GAP + ARROW_WIDTH + GAP + SLOT_SIZE;
	private static final int HEIGHT = SLOT_SIZE * 3 + 4;

	private static final int INPUT_X = 0;
	private static final int ARROW_X = SLOT_SIZE + GAP;
	private static final int FIRE_Y = 0;
	private static final int STARVED_SAND_Y = 20;
	private static final int ROW_Y = 40;
	private static final int OUTPUT_X = ARROW_X + ARROW_WIDTH + GAP;
	private static final int CENTER_SLOT_X = (WIDTH - SLOT_SIZE) / 2;
	private static final int ARROW_Y = ROW_Y + 1;

	private final IDrawableStatic background;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;

	public PetrificationRecipeCategory(IGuiHelper helper) {
		background = helper.createBlankDrawable(WIDTH, HEIGHT);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(RNBlocks.SOUL_STARVED_SAND.get()));
		slot = helper.getSlotDrawable();
		arrow = helper.createAnimatedRecipeArrow(60);
	}

	@Override
	public RecipeType<PetrificationRecipe> getRecipeType() {
		return RECIPE_TYPE;
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
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("gui." + RubinatedNether.MODID + ".jei.petrification");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, PetrificationRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, ROW_Y + 1)
				.addItemStacks(inputStacks(recipe));

		builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, ROW_Y + 1)
				.addItemStack(new ItemStack(recipe.getResultBlock()));

		builder.addSlot(RecipeIngredientRole.CATALYST, CENTER_SLOT_X + 1, STARVED_SAND_Y + 1)
				.addItemStack(new ItemStack(RNBlocks.SOUL_STARVED_SAND.get()));
	}

	public static List<ItemStack> inputStacks(PetrificationRecipe recipe) {
		return inputItems(recipe).stream().map(ItemStack::new).toList();
	}

	public static Set<Item> inputItems(PetrificationRecipe recipe) {
		Set<Item> items = new LinkedHashSet<>();
		BuiltInRegistries.BLOCK.getTag(recipe.getSource()).ifPresent(set -> set.forEach(holder -> {
			if (holder.value() == recipe.getResultBlock()) return;
			Item item = holder.value().asItem();
			if (item != Items.AIR) items.add(item);
		}));
		return items;
	}

	@Override
	public void draw(PetrificationRecipe recipe, IRecipeSlotsView slotsView,
	                 GuiGraphics graphics, double mouseX, double mouseY) {
		slot.draw(graphics, CENTER_SLOT_X, FIRE_Y);
		slot.draw(graphics, CENTER_SLOT_X, STARVED_SAND_Y);
		slot.draw(graphics, INPUT_X, ROW_Y);
		slot.draw(graphics, OUTPUT_X, ROW_Y);

		arrow.draw(graphics, ARROW_X, ARROW_Y);

		graphics.blit(
				CENTER_SLOT_X + 1,
				FIRE_Y + 1,
				0,
				ICON_SIZE,
				ICON_SIZE,
				Minecraft.getInstance()
						.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
						.apply(SOUL_FIRE_SPRITE)
		);
	}

	@Override
	public List<Component> getTooltipStrings(PetrificationRecipe recipe,
	                                         IRecipeSlotsView slotsView, double mouseX, double mouseY) {
		if (mouseX >= CENTER_SLOT_X && mouseX < CENTER_SLOT_X + SLOT_SIZE
				&& mouseY >= FIRE_Y && mouseY < FIRE_Y + SLOT_SIZE) {
			return List.of(Component.translatable("block.minecraft.soul_fire"));
		}

		return List.of();
	}
}
