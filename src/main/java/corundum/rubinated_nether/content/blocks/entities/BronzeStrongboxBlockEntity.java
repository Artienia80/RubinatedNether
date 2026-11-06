package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.content.menu.StrongboxMenu;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BronzeStrongboxBlockEntity extends ChestBlockEntity {
    public static final ResourceKey<LootTable> DISPENSE_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "chests/bronze_strongbox")
    );

    public static final int CONTAINER_SIZE = StrongboxMenu.CONTAINER_SIZE;
    public static final int MAX_STACK_SIZE = 16;

    private static final String CRANKS_TAG = "CranksRemaining";
    private static final String PASSWORD_TAG = "Password";
    private static final String KEY_TAG = "InsertedKey";
    private static final String DISPLAY_TAG = "DisplayItems";
    private static final int PREVIEW_SAMPLES = 48;
    private static final int MAX_PREVIEW_STACKS = 64;

    private int cranksRemaining;

    private List<ItemStack> lootPreview = List.of();

    private List<ItemStack> displayStacks = List.of();

    private final NonNullList<ItemStack> contents = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    private final Container menuContainer = new Container() {
        @Override
        public int getContainerSize() {
            return CONTAINER_SIZE;
        }

        @Override
        public boolean isEmpty() {
            return !BronzeStrongboxBlockEntity.this.hasContents();
        }

        @Override
        public ItemStack getItem(int slot) {
            return BronzeStrongboxBlockEntity.this.contents.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack removed = ContainerHelper.removeItem(BronzeStrongboxBlockEntity.this.contents, slot, amount);
            if (!removed.isEmpty()) {
                this.setChanged();
            }
            return removed;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(BronzeStrongboxBlockEntity.this.contents, slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            BronzeStrongboxBlockEntity.this.contents.set(slot, stack);
            if (stack.getCount() > MAX_STACK_SIZE) {
                stack.setCount(MAX_STACK_SIZE);
            }
            this.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return MAX_STACK_SIZE;
        }

        @Override
        public void setChanged() {
            BronzeStrongboxBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(BronzeStrongboxBlockEntity.this, player);
        }

        @Override
        public void startOpen(Player player) {
            BronzeStrongboxBlockEntity.this.startOpen(player);
        }

        @Override
        public void stopOpen(Player player) {
            BronzeStrongboxBlockEntity.this.stopOpen(player);
        }

        @Override
        public void clearContent() {
            BronzeStrongboxBlockEntity.this.contents.clear();
        }
    };

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            BronzeStrongboxBlockEntity.playSound(level, pos, SoundEvents.SHULKER_BOX_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            BronzeStrongboxBlockEntity.playSound(level, pos, SoundEvents.SHULKER_BOX_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof StrongboxMenu menu && menu.getContainer() == BronzeStrongboxBlockEntity.this.menuContainer;
        }
    };

    @Nullable
    private String password;

    private ItemStack insertedKey = ItemStack.EMPTY;

    public BronzeStrongboxBlockEntity(BlockPos pos, BlockState state) {
        super(RNBlockEntities.BRONZE_STRONGBOX.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
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
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void clearContent() {
    }

    public boolean hasContents() {
        for (ItemStack stack : this.contents) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void clearContents() {
        this.contents.clear();
        this.setChanged();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new StrongboxMenu(containerId, inventory, this.menuContainer);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.bronze_strongbox");
    }

    private Component strongboxName() {
        Block block = this.getBlockState().getBlock();
        if (this.getBlockState().getValue(TarnishingBronze.WAXED)) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            return Component.translatable("item." + id.getNamespace() + ".waxed_" + id.getPath());
        }
        return Component.translatable(block.getDescriptionId());
    }

    @Override
    public Component getName() {
        return this.password != null ? StrongboxLock.sealedName(this.strongboxName()) : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        if (this.password == null) {
            return this.getDefaultName();
        }
        if (this.getBlockState().getValue(BronzeStrongboxBlock.KEY) != 0) {
            return Component.literal(this.password);
        }
        return StrongboxLock.sealedTitle(this.password, this.strongboxName());
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.password != null ? StrongboxLock.sealedName(this.strongboxName()) : null;
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        String storedPassword = componentInput.get(RNDataComponents.STRONGBOX_PASSWORD.get());
        if (storedPassword != null && !storedPassword.isEmpty()) {
            this.password = storedPassword;
        }
        componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.contents);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(RNDataComponents.STRONGBOX_PASSWORD.get(), this.password);
        components.set(DataComponents.CUSTOM_NAME, this.password != null ? StrongboxLock.sealedName(this.strongboxName()) : null);
        if (this.hasContents()) {
            components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.contents));
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("Items");
    }

    public List<ItemStack> getDisplayStacks() {
        return this.displayStacks;
    }

    private List<ItemStack> collectDisplayStacks() {
        if (this.getBlockState().getValue(BronzeStrongboxBlock.NATURAL)) {
            return this.lootPreview();
        }
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : this.contents) {
            if (stack.isEmpty() || containsSame(result, stack)) {
                continue;
            }
            result.add(stack.copyWithCount(1));
        }
        return result;
    }

    private List<ItemStack> lootPreview() {
        if (this.lootPreview.isEmpty() && this.level instanceof ServerLevel serverLevel) {
            LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(DISPENSE_LOOT_TABLE);
            LootParams params = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
                    .create(LootContextParamSets.CHEST);
            List<ItemStack> sampled = new ArrayList<>();
            for (int i = 0; i < PREVIEW_SAMPLES && sampled.size() < MAX_PREVIEW_STACKS; i++) {
                for (ItemStack stack : table.getRandomItems(params)) {
                    if (!stack.isEmpty() && sampled.size() < MAX_PREVIEW_STACKS && !containsSame(sampled, stack)) {
                        sampled.add(stack.copyWithCount(1));
                    }
                }
            }
            this.lootPreview = sampled;
        }
        return this.lootPreview;
    }

    private static boolean containsSame(List<ItemStack> stacks, ItemStack candidate) {
        for (ItemStack existing : stacks) {
            if (ItemStack.isSameItemSameComponents(existing, candidate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide && this.getBlockState().getValue(BronzeStrongboxBlock.KEY) != 0) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (this.getBlockState().getValue(BronzeStrongboxBlock.KEY) != 0) {
            ListTag list = new ListTag();
            for (ItemStack stack : this.collectDisplayStacks()) {
                list.add(stack.save(registries));
            }
            tag.put(DISPLAY_TAG, list);
        }
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean hasPassword() {
        return this.password != null;
    }

    public boolean acceptsKey(ItemStack key) {
        if (this.password == null) {
            return true;
        }
        Component name = key.get(DataComponents.CUSTOM_NAME);
        return name != null && name.getString().equals(this.password);
    }

    public void insertKey(ItemStack key) {
        this.insertedKey = key.copyWithCount(1);
        this.setChanged();
    }

    public ItemStack takeKey() {
        ItemStack key = this.insertedKey.isEmpty() ? new ItemStack(RNItems.WINDING_KEY.get()) : this.insertedKey;
        this.insertedKey = ItemStack.EMPTY;
        this.setChanged();
        return key;
    }

    public void forgetKey() {
        this.insertedKey = ItemStack.EMPTY;
        this.setChanged();
    }

    public void dropKey(Level level, BlockPos pos) {
        if (!this.insertedKey.isEmpty()) {
            Block.popResource(level, pos, this.insertedKey);
            this.insertedKey = ItemStack.EMPTY;
        }
    }

    public int getCranksRemaining() {
        return this.cranksRemaining;
    }

    public void setCranksRemaining(int cranks) {
        this.cranksRemaining = cranks;
        this.setChanged();
    }

    public int consumeCrank() {
        this.cranksRemaining = Math.max(this.cranksRemaining, 1) - 1;
        this.setChanged();
        return this.cranksRemaining;
    }

    public void dispense(ServerLevel level, BlockPos pos, Player player) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(DISPENSE_LOOT_TABLE);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);

        for (ItemStack stack : table.getRandomItems(params)) {
            if (!stack.isEmpty()) {
                DefaultDispenseItemBehavior.spawnItem(level, stack, 2, Direction.UP, Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.5));
            }
        }
        level.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cranksRemaining = tag.getInt(CRANKS_TAG);
        this.contents.clear();
        ContainerHelper.loadAllItems(tag, this.contents, registries);
        this.password = tag.contains(PASSWORD_TAG) ? tag.getString(PASSWORD_TAG) : null;
        this.insertedKey = tag.contains(KEY_TAG)
                ? ItemStack.parse(registries, tag.getCompound(KEY_TAG)).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        List<ItemStack> display = new ArrayList<>();
        if (tag.contains(DISPLAY_TAG, Tag.TAG_LIST)) {
            ListTag list = tag.getList(DISPLAY_TAG, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                ItemStack.parse(registries, list.getCompound(i)).ifPresent(display::add);
            }
        }
        this.displayStacks = display;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.remove("CustomName");
        ContainerHelper.saveAllItems(tag, this.contents, registries);
        tag.putInt(CRANKS_TAG, this.cranksRemaining);
        if (this.password != null) {
            tag.putString(PASSWORD_TAG, this.password);
        }
        if (!this.insertedKey.isEmpty()) {
            tag.put(KEY_TAG, this.insertedKey.save(registries));
        }
    }
}
