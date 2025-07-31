package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.menu.coffer.CofferMenu;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CofferBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    public static final int COINTAINER_SIZE = 8;

    private NonNullList<ItemStack> items = NonNullList.withSize(COINTAINER_SIZE, net.minecraft.world.item.ItemStack.EMPTY);
    private final MultipliedContainer container = new CofferContainer();


    public CofferBlockEntity(BlockPos pos, BlockState state) {
        super(RNBlockEntities.COFFER.get(), pos, state);
    }

    public @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        ContainerMenuHelper.copyItemsIntoList(items, this.items);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.shrine_stone_coffer");
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.shrine_stone_coffer");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new CofferMenu(i, inventory, this.container);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        LimitlessContainerUtils.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        LimitlessContainerUtils.saveAllItems(tag, this.items, true, registries);
    }

    @Override
    public int getContainerSize() {
        return 8;
    }

    @Override
    public float getOpenNess(float v) {
        return 0;
    }

    public MultipliedContainer getContainer() {
        return this.container;
    }

    public class CofferContainer implements ListBackedContainer, MultipliedContainer {

        public CofferContainer() {
        }

        @Override
        public NonNullList<ItemStack> getContainerItems() {
            return CofferBlockEntity.this.items;
        }

        @Override
        public void setChanged() {
            CofferBlockEntity.this.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return 256;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return getMaxStackSize();
        }

        @Override
        public int getStackSizeMultiplier() {
            return 4;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            this.getContainerItems().set(index, stack);
            stack.limitSize(this.getMaxStackSize());
            this.setChanged();
        }
    }
}

