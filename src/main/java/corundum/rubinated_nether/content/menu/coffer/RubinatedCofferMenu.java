package corundum.rubinated_nether.content.menu.coffer;

import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.utils.RNConfig;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedSimpleContainer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedSlot;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RubinatedCofferMenu extends CofferMenu {
    private static final int CACHE_START = PLAYER_SLOTS_END;

    public RubinatedCofferMenu(int containerId, Inventory inventory) {
        this(containerId, inventory,
                new MultipliedSimpleContainer(RNConfig.cofferStackMultiplier, CofferBlockEntity.CONTAINER_SIZE),
                new SimpleContainer(CofferBlockEntity.CONTAINER_SIZE)
        );
    }

    public RubinatedCofferMenu(int containerId, Inventory inventory, MultipliedContainer container, Container cache) {
        super(RNMenuTypes.RUBINATED_COFFER_MENU.get(), containerId, inventory, container);
        checkContainerSize(cache, CofferBlockEntity.CONTAINER_SIZE);
        for (int index = 0; index < CofferBlockEntity.CONTAINER_SIZE; index++) {
            this.addSlot(new CacheSlot(cache, index));
        }
    }

    @Override
    protected Slot createContainerSlot(MultipliedContainer container, int index, int x, int y) {
        return new CachingSlot(container, index, x, y);
    }

    public ItemStack getCachedStack(int index) {
        int slotIndex = CACHE_START + index;
        return index >= 0 && slotIndex < this.slots.size() ? this.slots.get(slotIndex).getItem() : ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (!this.tryRemoveCache(slotId, button, clickType, player)) {
            super.clicked(slotId, button, clickType, player);
        }
    }

    private boolean tryRemoveCache(int slotId, int button, ClickType clickType, Player player) {
        if (clickType != ClickType.PICKUP || button != 1) {
            return false;
        }
        if (slotId < 0 || slotId >= CofferBlockEntity.CONTAINER_SIZE) {
            return false;
        }
        if (!this.getCarried().isEmpty() || this.slots.get(slotId).hasItem()) {
            return false;
        }
        Slot cacheSlot = this.slots.get(CACHE_START + slotId);
        if (!cacheSlot.hasItem()) {
            return false;
        }
        cacheSlot.set(ItemStack.EMPTY);
        if (!player.level().isClientSide) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.6F, 1.4F);
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index >= CACHE_START) {
            return ItemStack.EMPTY;
        }
        if (index >= CofferBlockEntity.CONTAINER_SIZE) {
            return super.quickMoveStack(player, index);
        }
        Slot slot = this.slots.get(index);
        ItemStack template = slot.hasItem() ? slot.getItem().copyWithCount(1) : ItemStack.EMPTY;
        ItemStack moved = super.quickMoveStack(player, index);
        if (!template.isEmpty() && !slot.hasItem()) {
            this.slots.get(CACHE_START + index).set(template);
        }
        return moved;
    }

    private final class CachingSlot extends MultipliedSlot {

        private CachingSlot(MultipliedContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (!super.mayPlace(stack)) {
                return false;
            }
            if (this.hasItem()) {
                return true;
            }
            ItemStack cached = RubinatedCofferMenu.this.getCachedStack(this.getContainerSlot());
            return cached.isEmpty() || ItemStack.isSameItemSameComponents(cached, stack);
        }
    }

    private static final class CacheSlot extends Slot {

        private CacheSlot(Container container, int index) {
            super(container, index, -2000, -2000);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }
}
