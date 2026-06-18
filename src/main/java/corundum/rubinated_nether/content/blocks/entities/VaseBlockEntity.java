package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.BronzeVaseBlock;
import corundum.rubinated_nether.content.menu.VaseMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.IntStream;

public class VaseBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    public static final int COLUMNS = 3;
    public static final int ROWS = 3;
    public static final int CONTAINER_SIZE = 9;
    private static final int[] SLOTS = IntStream.range(0, CONTAINER_SIZE).toArray();
    private NonNullList<ItemStack> itemStacks;

    protected VaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public VaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.BRONZE_VASE.get(), pos, blockState);
        this.itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    protected Component getDefaultName() {
        return Component.translatable("container.bronzeVase");
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadFromTag(tag, registries);
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.itemStacks, false, registries);
        }

    }

    public void loadFromTag(CompoundTag tag, HolderLookup.Provider levelRegistry) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag) && tag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(tag, this.itemStacks, levelRegistry);
        }

    }

    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    protected void setItems(NonNullList<ItemStack> items) {
        this.itemStacks = items;
    }

    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @javax.annotation.Nullable Direction direction) {
        return !(Block.byItem(itemStack.getItem()) instanceof BronzeVaseBlock) && itemStack.canFitInsideContainerItems();
    }

    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new VaseMenu(id, player, this);
    }
}
