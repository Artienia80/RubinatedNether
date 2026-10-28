package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.menu.coffer.CofferMenu;
import corundum.rubinated_nether.utils.RNConfig;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CofferBlockEntity extends ChestBlockEntity implements LidBlockEntity, MenuProvider {
    public static final int CONTAINER_SIZE = 8;

    private final NonNullList<ItemStack> items;
    private final ContainerOpenersCounter openersCounter;
    private final MultipliedContainer container = new CofferContainer();
    private final ChestLidController chestLidController = new ChestLidController();

    public CofferBlockEntity(BlockPos pos, BlockState state) {
        this(RNBlockEntities.COFFER.get(), pos, state);
    }

    protected CofferBlockEntity(BlockEntityType<? extends CofferBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
                CofferBlockEntity.playSound(level, blockPos, blockState, SoundEvents.SHULKER_BOX_OPEN);
            }

            protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
                CofferBlockEntity.playSound(level, blockPos, blockState, SoundEvents.SHULKER_BOX_CLOSE);
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos blockPos, BlockState blockState, int i, int i1) {
                level.blockEvent(pos, state.getBlock(), 1, this.getOpenerCount());
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof CofferMenu cofferMenu) {
                    Container container = cofferMenu.getContainer();
                    return container == CofferBlockEntity.this.container;
                } else {
                    return false;
                }
            }
        };

        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    }

    // Menu

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

    // Data handling

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        LimitlessContainerUtils.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.remove("Items");
        LimitlessContainerUtils.saveAllItems(tag, this.items, true, registries);
    }

    public @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        ContainerMenuHelper.copyItemsIntoList(items, this.items);
    }

    public CompoundTag extractContents(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        LimitlessContainerUtils.saveAllItems(tag, this.items, true, registries);
        this.items.clear();
        return tag;
    }

    public void insertContents(CompoundTag tag, HolderLookup.Provider registries) {
        this.items.clear();
        LimitlessContainerUtils.loadAllItems(tag, this.items, registries);
        this.setChanged();
    }

    protected void onSlotFilled(int slot) {
    }

    protected void onSlotEmptied(int slot, ItemStack previous) {
    }

    protected boolean canPlaceInSlot(int slot, ItemStack stack) {
        return true;
    }

    private ItemStack snapshot(int slot) {
        if (slot < 0 || slot >= CONTAINER_SIZE) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = this.items.get(slot);
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    private void afterSlotChange(int slot, ItemStack previous) {
        if (slot < 0 || slot >= CONTAINER_SIZE) {
            return;
        }
        if (!this.items.get(slot).isEmpty()) {
            this.onSlotFilled(slot);
        } else if (!previous.isEmpty()) {
            this.onSlotEmptied(slot, previous);
        }
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack previous = this.snapshot(slot);
        ItemStack removed = super.removeItem(slot, amount);
        this.afterSlotChange(slot, previous);
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack previous = this.snapshot(slot);
        ItemStack removed = super.removeItemNoUpdate(slot);
        this.afterSlotChange(slot, previous);
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previous = this.snapshot(slot);
        super.setItem(slot, stack);
        this.afterSlotChange(slot, previous);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.canPlaceInSlot(slot, stack);
    }

    // Lid handling

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, CofferBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.chestLidController.shouldBeOpen(type > 0);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    private static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
        level.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    // Container

    public MultipliedContainer getContainer() {
        return this.container;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 64 * RNConfig.cofferStackMultiplier;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.getMaxStackSize() * RNConfig.cofferStackMultiplier;
    }

    public int getAnalogSignal() {
        float fill = 0.0F;
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                fill += (float) stack.getCount() / (float) this.getMaxStackSize(stack);
            }
        }
        return Mth.lerpDiscrete(fill / CONTAINER_SIZE, 0, 15);
    }


    public class CofferContainer implements ListBackedContainer, MultipliedContainer {

        @Override
        public ItemStack removeItem(int slot, int count) {
            ItemStack previous = CofferBlockEntity.this.snapshot(slot);
            ItemStack removed = ListBackedContainer.super.removeItem(slot, count);
            CofferBlockEntity.this.afterSlotChange(slot, previous);
            return removed;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack previous = CofferBlockEntity.this.snapshot(slot);
            ItemStack removed = ListBackedContainer.super.removeItemNoUpdate(slot);
            CofferBlockEntity.this.afterSlotChange(slot, previous);
            return removed;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            ItemStack previous = CofferBlockEntity.this.snapshot(slot);
            ListBackedContainer.super.setItem(slot, stack);
            CofferBlockEntity.this.afterSlotChange(slot, previous);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return CofferBlockEntity.this.canPlaceInSlot(slot, stack);
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(CofferBlockEntity.this, player);
        }

        @Override
        public void startOpen(Player player) {
            if (!CofferBlockEntity.this.remove && !player.isSpectator()) {
                CofferBlockEntity.this.openersCounter.incrementOpeners(player, CofferBlockEntity.this.getLevel(), CofferBlockEntity.this.getBlockPos(), CofferBlockEntity.this.getBlockState());
            }
        }

        @Override
        public void stopOpen(Player player) {
            if (!CofferBlockEntity.this.remove && !player.isSpectator()) {
                CofferBlockEntity.this.openersCounter.decrementOpeners(player, CofferBlockEntity.this.getLevel(), CofferBlockEntity.this.getBlockPos(), CofferBlockEntity.this.getBlockState());
            }
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
        public int getStackSizeMultiplier() {
            return RNConfig.cofferStackMultiplier;
        }
    }
}