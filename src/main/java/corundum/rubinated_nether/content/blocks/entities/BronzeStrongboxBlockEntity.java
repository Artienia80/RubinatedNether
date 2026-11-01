package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BronzeStrongboxBlockEntity extends ChestBlockEntity {
    public static final ResourceKey<LootTable> DISPENSE_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "chests/bronze_strongbox")
    );

    private static final String CRANKS_TAG = "CranksRemaining";
    private static final String PASSWORD_TAG = "Password";
    private static final String KEY_TAG = "InsertedKey";

    private int cranksRemaining;

    /**
     * The password of a player-placed strongbox, taken from the name the strongbox item had when it was placed.
     * It is server-side only: it is never part of the block state or of any client sync (this block entity sends
     * no update packet or update tag). It is the title of the container screen, which is only reachable with the
     * matching key inserted.
     */
    @Nullable
    private String password;

    /** The winding key that is currently inserted, so it can be handed back with its name intact. */
    private ItemStack insertedKey = ItemStack.EMPTY;

    public BronzeStrongboxBlockEntity(BlockPos pos, BlockState state) {
        super(RNBlockEntities.BRONZE_STRONGBOX.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.bronze_strongbox");
    }

    // The password is also the name shown on the container screen.
    @Override
    public Component getName() {
        return this.password != null ? Component.literal(this.password) : this.getDefaultName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.password != null ? Component.literal(this.password) : null;
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        Component name = componentInput.get(DataComponents.CUSTOM_NAME);
        if (name != null && !name.getString().isEmpty()) {
            this.password = name.getString();
        }
    }

    // Breaking the strongbox hands the password back as the item's name.
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.password != null ? Component.literal(this.password) : null);
    }

    public boolean hasPassword() {
        return this.password != null;
    }

    /** A strongbox without a password takes any winding key, one with a password only a key renamed to it. */
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

    /** Removes and returns the inserted key. Strongboxes that got their key before keys were stored get a plain one. */
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
        this.password = tag.contains(PASSWORD_TAG) ? tag.getString(PASSWORD_TAG) : null;
        this.insertedKey = tag.contains(KEY_TAG)
                ? ItemStack.parse(registries, tag.getCompound(KEY_TAG)).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.remove("CustomName");
        tag.putInt(CRANKS_TAG, this.cranksRemaining);
        if (this.password != null) {
            tag.putString(PASSWORD_TAG, this.password);
        }
        if (!this.insertedKey.isEmpty()) {
            tag.put(KEY_TAG, this.insertedKey.save(registries));
        }
    }
}
