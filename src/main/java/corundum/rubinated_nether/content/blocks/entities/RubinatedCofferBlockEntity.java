package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.RubinatedCofferBlock;
import corundum.rubinated_nether.content.menu.coffer.RubinatedCofferMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RubinatedCofferBlockEntity extends CofferBlockEntity {
    private static final String CACHE_KEY = "Cache";
    private static final String SLOT_KEY = "Slot";
    private static final String ITEM_KEY = "Item";

    private final NonNullList<ItemStack> cache = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private final Container cacheContainer = new CacheContainer();

    public RubinatedCofferBlockEntity(BlockPos pos, BlockState state) {
        super(RNBlockEntities.RUBINATED_COFFER.get(), pos, state);
    }

    private static Component title() {
        return Component.translatable("container.rubinated_shrine_stone_coffer").withStyle(ChatFormatting.DARK_RED);
    }

    @Override
    public Component getDisplayName() {
        return title();
    }

    @Override
    protected Component getDefaultName() {
        return title();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new RubinatedCofferMenu(containerId, inventory, this.getContainer(), this.cacheContainer);
    }

    public Container getCacheContainer() {
        return this.cacheContainer;
    }

    public void clearCache() {
        boolean changed = false;
        for (int slot = 0; slot < CONTAINER_SIZE; slot++) {
            if (!this.cache.get(slot).isEmpty()) {
                this.cache.set(slot, ItemStack.EMPTY);
                changed = true;
            }
        }
        if (changed) {
            this.setChanged();
        }
    }

    private boolean isCacheSuspended() {
        return this.getBlockState().getOptionalValue(RubinatedCofferBlock.POWERED).orElse(false);
    }

    @Override
    protected void onSlotFilled(int slot) {
        if (!this.cache.get(slot).isEmpty()) {
            this.cache.set(slot, ItemStack.EMPTY);
            this.setChanged();
        }
    }

    @Override
    protected void onSlotEmptied(int slot, ItemStack previous) {
        if (!this.isCacheSuspended()) {
            this.cache.set(slot, previous);
            this.setChanged();
        }
    }

    @Override
    protected boolean canPlaceInSlot(int slot, ItemStack stack) {
        if (slot < 0 || slot >= CONTAINER_SIZE) {
            return false;
        }
        if (this.isCacheSuspended() || !this.getItems().get(slot).isEmpty()) {
            return true;
        }
        ItemStack cached = this.cache.get(slot);
        return cached.isEmpty() || ItemStack.isSameItemSameComponents(cached, stack);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cache.clear();
        ListTag list = tag.getList(CACHE_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getByte(SLOT_KEY) & 255;
            if (slot < CONTAINER_SIZE && entry.contains(ITEM_KEY, Tag.TAG_COMPOUND)) {
                ItemStack.parse(registries, entry.getCompound(ITEM_KEY))
                        .ifPresent(stack -> this.cache.set(slot, stack.copyWithCount(1)));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (int slot = 0; slot < CONTAINER_SIZE; slot++) {
            ItemStack stack = this.cache.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putByte(SLOT_KEY, (byte) slot);
                entry.put(ITEM_KEY, stack.save(registries));
                list.add(entry);
            }
        }
        tag.put(CACHE_KEY, list);
    }

    private final class CacheContainer implements Container {

        private boolean inRange(int slot) {
            return slot >= 0 && slot < CONTAINER_SIZE;
        }

        @Override
        public int getContainerSize() {
            return CONTAINER_SIZE;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : RubinatedCofferBlockEntity.this.cache) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return this.inRange(slot) ? RubinatedCofferBlockEntity.this.cache.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (!this.inRange(slot)) {
                return;
            }
            if (stack.isEmpty()) {
                RubinatedCofferBlockEntity.this.cache.set(slot, ItemStack.EMPTY);
            } else if (!RubinatedCofferBlockEntity.this.isCacheSuspended()) {
                RubinatedCofferBlockEntity.this.cache.set(slot, stack.copyWithCount(1));
            }
            RubinatedCofferBlockEntity.this.setChanged();
        }

        @Override
        public void setChanged() {
            RubinatedCofferBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            RubinatedCofferBlockEntity.this.clearCache();
        }
    }
}
