package corundum.rubinated_nether.content.menu;

import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.recipe.FreezerServerPlaceRecipe;
import corundum.rubinated_nether.content.recipe.FreezingRecipe;
import corundum.rubinated_nether.content.recipe.RNRecipeBookTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class FreezerMenu extends RecipeBookMenu<SingleRecipeInput, FreezingRecipe> {
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    private final RecipeType<? extends FreezingRecipe> recipeType;

    @SuppressWarnings("unchecked")
    @Override
    public void handlePlacement(boolean flag, RecipeHolder<?> holder, ServerPlayer serverPlayer) {
        this.beginPlacingRecipe();

        try {
            new FreezerServerPlaceRecipe(this).recipeClicked(serverPlayer, (RecipeHolder<FreezingRecipe>) holder, flag);
        } finally {
            this.finishPlacingRecipe((RecipeHolder<FreezingRecipe>)holder);
        }
    }

    public FreezerMenu(int containerId, Inventory playerInventory) {
        this(RNMenuTypes.FREEZER_MENU.get(), RNRecipes.FREEZING.get(), RecipeBookType.FURNACE, containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public FreezerMenu(int containerId, Inventory playerInventory, Container freezerContainer, ContainerData data) {
        this(RNMenuTypes.FREEZER_MENU.get(), RNRecipes.FREEZING.get(), RecipeBookType.FURNACE, containerId, playerInventory, freezerContainer, data);
    }

    public FreezerMenu(MenuType<?> menuType, RecipeType<? extends FreezingRecipe> recipeType, RecipeBookType recipeBookType, int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, containerId);
        this.recipeType = recipeType;
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        this.level = playerInventory.player.level();
        this.addSlot(new Slot(container, 0, 56, 17));
        this.addSlot(new FreezerFuelSlot(this, container, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(playerInventory.player, container, 2, 116, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean hasFuel() {
        return data.get(2) > 0;
    }

    public int getScaledProgress() {
        // these are from the cases set in the matching block entity class file
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        // the pixel length or height of the progress arrow/bar in this GUI
        // if your arrow/bar is vertical, you need the height, otherwise you need the length
        int progressArrowSize = 22;

        // this calculates how much of that progress arrow/bar needs to be rendered in the GUI
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledFuelProgress() {
        // these are from the cases set in the matching block entity class file
        int fuelTime = this.data.get(2);
        int fuelDuration = this.data.get(3);
        // the pixel length or height of the progress arrow/bar in this GUI
        // if your arrow/bar is vertical, you need the height, otherwise you need the length
        int fuelScaleSize = 60;

        // this calculates how much of that progress arrow/bar needs to be rendered in the GUI
        return fuelDuration != 0 ? (int)(((float)fuelTime / (float)fuelDuration) * fuelScaleSize) : 0;
    }

    public boolean isFuel(ItemStack stack) {
        return FreezerBlockEntity.getFreezingMap().containsKey(stack.getItem());
    }

    /**
     * Warning for "ConstantConditions" is suppressed because of being based on vanilla code.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(itemStack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack1, itemStack);
            } else if (index != 1 && index != 0) {
                if (this.canSmelt(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemStack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemStack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack1);
        }
        return itemStack;
    }

    /**
     * Warning for "unchecked" is suppressed because of being based on vanilla code.
     */
    @SuppressWarnings("unchecked")
    protected boolean canSmelt(ItemStack stack) {
        return this.level.getRecipeManager().getRecipeFor((RecipeType<FreezingRecipe>) this.recipeType, new SingleRecipeInput(stack), this.level).isPresent();
    }


    public int getBurnProgress() {
        int i = this.data.get(2);
        int j = this.data.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.data.get(0) * 13 / i;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RNRecipeBookTypes.FREEZER;
    }

    @Override
    public boolean shouldMoveToInventory(int slot) {
        return slot != 1;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents contents) {
        if (this.container instanceof StackedContentsCompatible stackedContentsCompatible) {
            stackedContentsCompatible.fillStackedContents(contents);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(0).set(ItemStack.EMPTY);
        this.getSlot(2).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(RecipeHolder recipe) {
        return recipe.value().matches(new SingleRecipeInput(this.container.getItem(0)), this.level);
    }

    @Override
    public int getResultSlotIndex() {
        return 2;
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}