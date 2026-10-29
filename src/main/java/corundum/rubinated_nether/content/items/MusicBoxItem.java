package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import corundum.rubinated_nether.networking.MusicBoxPlayPayload;
import corundum.rubinated_nether.networking.MusicBoxRetargetPayload;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MusicBoxItem extends BundleItem {
    private static final int GAP_TICKS = 20;
    private static final int ACTION_COOLDOWN = 8;
    private static final double STOP_RANGE = 128.0;
    private static final String SOOTHING_KEY = "rubinated_nether_sinner_until";
    /** Set on whatever entity (player or dropped item) the song is currently attached to. */
    private static final String DISC_KEY = "rubinated_nether_music_disc";
    private static final String SOUND_KEY = "rubinated_nether_music_sound";
    /** How many checks in a row a player's song has had no playing box to belong to. */
    private static final String MISSING_KEY = "rubinated_nether_music_missing";
    private static final int MISSING_CHECKS_BEFORE_STOP = 4;
    /** On a freshly dropped box: the id of the player whose song it is taking over. */
    private static final String FROM_KEY = "rubinated_nether_music_from";
    private static final double SOUND_RANGE = 64.0;

    private static final int DISC_WEIGHT_STACK_SIZE = 64;

    public MusicBoxItem(Item.Properties properties) {
        super(properties);
    }

    private static MusicBoxData state(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.MUSIC_BOX_DATA.get(), MusicBoxData.EMPTY);
    }

    private static BundleContents contents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
    }

    public static boolean isPlayingSinner(Player player) {
        return player.getPersistentData().getLong(SOOTHING_KEY) > player.level().getGameTime();
    }

    public static boolean isSinnerPlaying(ItemStack stack) {
        MusicBoxData data = state(stack);
        return data.playing() && data.sinner();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (state(stack).looping()) {
            tooltip.add(Component.literal("Looping").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isSinnerPlaying(stack) || super.isFoil(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                MusicBoxData state = state(stack);
                boolean looping = !state.looping();
                stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state.withLooping(looping));
                player.displayClientMessage(Component.literal(looping ? "Loop: on" : "Loop: off"), true);
                player.getCooldowns().addCooldown(this, ACTION_COOLDOWN);
            }
            return InteractionResultHolder.consume(stack);
        }

        BundleContents contents = contents(stack);
        if (contents.isEmpty()) {
            return InteractionResultHolder.pass(stack);
        }
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            MusicBoxData state = state(stack);
            if (state.playing()) {
                stopSound(serverPlayer, serverLevel, contents, state.current());
                stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state.stopped());
            } else {
                playFrom(stack, serverPlayer, serverLevel, contents, state.current());
            }
            player.getCooldowns().addCooldown(this, ACTION_COOLDOWN);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide
                || !(entity instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel level)) {
            return false;
        }
        MusicBoxData state = state(stack);
        BundleContents contents = contents(stack);
        if (!state.playing() || contents.isEmpty() || player.getCooldowns().isOnCooldown(this)) {
            return false;
        }
        stopSound(player, level, contents, state.current());
        int size = contents.size();

        int target = player.isShiftKeyDown()
                ? Math.floorMod(state.current() - 1, size)
                : (state.current() + 1) % size;
        playFrom(stack, player, level, contents, target);
        player.getCooldowns().addCooldown(this, ACTION_COOLDOWN);
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        MusicBoxData state = state(stack);
        if (!state.playing()) {
            return;
        }
        BundleContents contents = contents(stack);
        // Also catches discs removed by any route (creative menu, hotkeys, other mods), not just the click handlers
        boolean hosted = player.getPersistentData().contains(SOUND_KEY);
        if (contents.isEmpty() || (hosted && discChanged(player, contents, state.current()))) {
            stopHost(serverLevel, player);
            stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state.stopped());
            return;
        }
        if (!hosted) {
            // The box is playing but no song is attached to this player (picked up from the ground, back from a
            // chest, ...): carry on with the current disc.
            playFrom(stack, player, serverLevel, contents, state.current());
            return;
        }
        if (state.sinner()) {
            // Keeps the Sinner effect alive after the box changed hands (the key is cleared when it is dropped)
            player.getPersistentData().putLong(SOOTHING_KEY, state.songEndTick());
        }
        if (level.getGameTime() >= state.songEndTick()) {
            int next = state.looping() ? state.current() : (state.current() + 1) % contents.size();
            playFrom(stack, player, serverLevel, contents, next);
        }
    }

    // ---------- Dropped box: the song is attached to the item entity instead of the player ----------

    /** Item entity tick (server). Hands a freshly dropped playing box its own song, then keeps it going. */
    public static void tickDropped(ItemEntity itemEntity, ServerLevel level) {
        ItemStack original = itemEntity.getItem();
        if (!(original.getItem() instanceof MusicBoxItem)) {
            return;
        }
        boolean hosted = itemEntity.getPersistentData().contains(SOUND_KEY);
        MusicBoxData state = state(original);
        if (!state.playing()) {
            if (hosted) stopHost(level, itemEntity);
            return;
        }
        BundleContents contents = contents(original);
        ItemStack work = original.copy();
        CompoundTag hostData = itemEntity.getPersistentData();
        if (hosted && hostData.contains(FROM_KEY)) {
            // Just dropped: the song the player was hearing now follows the item, from where it was
            retarget(level, itemEntity, hostData.getInt(FROM_KEY), itemEntity.getId());
            hostData.remove(FROM_KEY);
        }
        if (contents.isEmpty() || (hosted && discChanged(itemEntity, contents, state.current()))) {
            stopHost(level, itemEntity);
            work.set(RNDataComponents.MUSIC_BOX_DATA.get(), state.stopped());
        } else if (!hosted) {
            // Playing, but no song is attached to anything (e.g. dropped by a dispenser): start the current disc
            playFrom(work, itemEntity, level, contents, state.current());
        } else if (level.getGameTime() >= state.songEndTick()) {
            int next = state.looping() ? state.current() : (state.current() + 1) % contents.size();
            playFrom(work, itemEntity, level, contents, next);
        } else {
            return;
        }
        itemEntity.setItem(work);
    }

    /** The item entity is gone (despawned, burned, voided, killed, unloaded): its song ends with it. */
    public static void onDroppedRemoved(ItemEntity itemEntity, ServerLevel level) {
        if (itemEntity.getPersistentData().contains(SOUND_KEY)) {
            stopHost(level, itemEntity);
        }
    }

    /** The player threw a playing box: its song is handed to the item entity, which takes over on its first tick. */
    public static void onTossed(ServerPlayer player, ItemEntity dropped) {
        CompoundTag from = player.getPersistentData();
        if (!state(dropped.getItem()).playing() || !from.contains(SOUND_KEY)) {
            return;
        }
        CompoundTag to = dropped.getPersistentData();
        to.putString(DISC_KEY, from.getString(DISC_KEY));
        to.putString(SOUND_KEY, from.getString(SOUND_KEY));
        to.putInt(FROM_KEY, player.getId());
        from.remove(DISC_KEY);
        from.remove(SOUND_KEY);
        from.remove(SOOTHING_KEY);
        from.remove(MISSING_KEY);
    }

    /** The player picked up a box whose song is attached to the item: it keeps playing, now following the player. */
    public static void onPickedUp(ServerPlayer player, ItemEntity picked) {
        CompoundTag from = picked.getPersistentData();
        if (!from.contains(SOUND_KEY) || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        CompoundTag to = player.getPersistentData();
        if (to.contains(SOUND_KEY)) {
            // The player already has a song going: this one just ends
            stopHost(level, picked);
            return;
        }
        to.putString(DISC_KEY, from.getString(DISC_KEY));
        to.putString(SOUND_KEY, from.getString(SOUND_KEY));
        to.remove(MISSING_KEY);
        from.remove(DISC_KEY);
        from.remove(SOUND_KEY);
        from.remove(FROM_KEY);
        retarget(level, player, picked.getId(), player.getId());
    }

    private static void retarget(ServerLevel level, Entity near, int fromId, int toId) {
        PacketDistributor.sendToPlayersNear(level, null, near.getX(), near.getY(), near.getZ(), SOUND_RANGE,
                new MusicBoxRetargetPayload(fromId, toId));
    }

    /**
     * Player tick (server). A song that is attached to the player but whose box is no longer anywhere in their
     * inventory or on their cursor (deleted, cleared, put in a chest...) is stopped.
     */
    public static void checkPlayerSound(ServerPlayer player) {
        if (player.tickCount % 5 != 0) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (!data.contains(SOUND_KEY) || holdsPlayingBox(player)) {
            data.remove(MISSING_KEY);
            return;
        }
        // Not stopped on the first miss: the box can be briefly out of sight of the server (creative menu cursor)
        int missed = data.getInt(MISSING_KEY) + 1;
        if (missed >= MISSING_CHECKS_BEFORE_STOP) {
            data.remove(MISSING_KEY);
            if (player.level() instanceof ServerLevel level) {
                stopHost(level, player);
            }
        } else {
            data.putInt(MISSING_KEY, missed);
        }
    }

    private static boolean holdsPlayingBox(ServerPlayer player) {
        if (isPlayingBox(player.containerMenu.getCarried())) {
            return true;
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (isPlayingBox(inventory.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlayingBox(ItemStack stack) {
        return stack.getItem() instanceof MusicBoxItem && state(stack).playing();
    }

    private static boolean discChanged(Entity host, BundleContents contents, int current) {
        String recorded = host.getPersistentData().getString(DISC_KEY);
        if (recorded.isEmpty()) {
            return false;
        }
        if (current < 0 || current >= contents.size()) {
            return true;
        }
        return !BuiltInRegistries.ITEM.getKey(contents.getItemUnsafe(current).getItem()).toString().equals(recorded);
    }

    /** Stops the recorded song for everyone nearby and clears the host's bookkeeping. */
    private static void stopHost(ServerLevel level, Entity host) {
        CompoundTag data = host.getPersistentData();
        String sound = data.getString(SOUND_KEY);
        data.remove(SOUND_KEY);
        data.remove(DISC_KEY);
        if (host instanceof Player) {
            data.remove(SOOTHING_KEY);
        }
        ResourceLocation location = sound.isEmpty() ? null : ResourceLocation.tryParse(sound);
        if (location == null) {
            return;
        }
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(location, SoundSource.RECORDS);
        for (ServerPlayer other : level.players()) {
            if (other.distanceToSqr(host) <= STOP_RANGE * STOP_RANGE) {
                other.connection.send(packet);
            }
        }
    }

    private static void playFrom(ItemStack stack, Entity host, ServerLevel level, BundleContents contents, int startIndex) {
        int size = contents.size();
        ServerPlayer player = host instanceof ServerPlayer sp ? sp : null;
        for (int i = 0; i < size; i++) {
            int index = (startIndex + i) % size;
            ItemStack disc = contents.getItemUnsafe(index);
            Optional<Holder<JukeboxSong>> holder = JukeboxSong.fromStack(level.registryAccess(), disc);
            if (holder.isEmpty()) {
                continue;
            }
            JukeboxSong song = holder.get().value();
            long end = level.getGameTime() + Mth.ceil(song.lengthInSeconds() * 20.0F) + GAP_TICKS;
            boolean sinner = holder.get().is(RNJukeboxSongs.SINNER);
            if (player != null) {
                if (sinner) {
                    player.getPersistentData().putLong(SOOTHING_KEY, end);
                } else {
                    player.getPersistentData().remove(SOOTHING_KEY);
                }
            }
            // Played by our own client-side sound that follows the host, so it can change hands without restarting
            PacketDistributor.sendToPlayersNear(level, null, host.getX(), host.getY(), host.getZ(), SOUND_RANGE,
                    new MusicBoxPlayPayload(host.getId(), song.soundEvent().value().getLocation()));

            CompoundTag data = host.getPersistentData();
            data.putString(DISC_KEY, BuiltInRegistries.ITEM.getKey(disc.getItem()).toString());
            data.putString(SOUND_KEY, song.soundEvent().value().getLocation().toString());

            stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state(stack).withNowPlaying(index, end, sinner && player != null));
            return;
        }
        stopHost(level, host);
        stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state(stack).stopped());
    }

    private static void stopSound(ServerPlayer player, ServerLevel level, BundleContents contents, int current) {
        if (player.getPersistentData().contains(SOUND_KEY)) {
            stopHost(level, player);
            return;
        }
        // Older state without a recorded sound: work it out from the disc list
        player.getPersistentData().remove(SOOTHING_KEY);
        if (current < 0 || current >= contents.size()) {
            return;
        }
        JukeboxSong.fromStack(level.registryAccess(), contents.getItemUnsafe(current)).ifPresent(holder -> {
            ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(
                    holder.value().soundEvent().value().getLocation(), SoundSource.RECORDS);
            for (ServerPlayer other : level.players()) {
                if (other.distanceToSqr(player) <= STOP_RANGE * STOP_RANGE) {
                    other.connection.send(packet);
                }
            }
        });
    }

    public static void collectDiscs(ItemStack box, List<Slot> slots, Player player) {
        BundleContents before = box.get(DataComponents.BUNDLE_CONTENTS);
        BundleContents.Mutable mutable = new BundleContents.Mutable(contents(box));

        Set<Item> seen = new HashSet<>();
        contents(box).items().forEach(stored -> seen.add(stored.getItem()));

        for (Slot slot : slots) {
            if (!slot.hasItem() || !slot.mayPickup(player)) {
                continue;
            }
            ItemStack stack = slot.getItem();
            if (!isDisc(stack) || seen.contains(stack.getItem())) {
                continue;
            }
            applyWeightMarker(stack);
            int inserted = mutable.tryInsert(stack);
            stripWeightMarker(stack);
            if (inserted > 0) {
                seen.add(stack.getItem());
            }
            slot.setChanged();
        }
        box.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
        afterEdit(box, player, before);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        ItemStack target = slot.getItem();
        boolean disc = isDisc(target);
        if (!target.isEmpty() && !disc) {
            return false;
        }
        if (disc && containsDisc(stack, target)) {
            return true;
        }
        BundleContents before = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (disc) {
            applyWeightMarker(target);
        }
        boolean handled = super.overrideStackedOnOther(stack, slot, action, player);
        stripWeightMarker(slot.getItem());
        afterEdit(stack, player, before);
        return handled;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        boolean disc = isDisc(other);
        if (!other.isEmpty() && !disc) {
            return false;
        }
        if (disc && containsDisc(stack, other)) {
            return true;
        }
        BundleContents before = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (disc) {
            applyWeightMarker(other);
        }
        boolean handled = super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        stripWeightMarker(other);
        stripWeightMarker(access.get());
        afterEdit(stack, player, before);
        return handled;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        BundleContents contents = itemEntity.getItem().get(DataComponents.BUNDLE_CONTENTS);
        if (contents != null) {
            itemEntity.getItem().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            ItemUtils.onContainerDestroyed(itemEntity, contents.itemCopyStream().peek(MusicBoxItem::stripWeightMarker).toList());
        }
    }

    private static boolean isDisc(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.JUKEBOX_PLAYABLE);
    }

    private static boolean containsDisc(ItemStack box, ItemStack disc) {
        return contents(box).itemCopyStream().anyMatch(stored -> stored.getItem() == disc.getItem());
    }

    private static void applyWeightMarker(ItemStack disc) {
        disc.set(DataComponents.MAX_STACK_SIZE, DISC_WEIGHT_STACK_SIZE);
    }

    private static void stripWeightMarker(ItemStack disc) {
        if (isDisc(disc)) {
            disc.set(DataComponents.MAX_STACK_SIZE, disc.getItem().getDefaultMaxStackSize());

            disc.remove(DataComponents.BUNDLE_CONTENTS);
        }
    }

    private static void afterEdit(ItemStack stack, Player player, BundleContents before) {
        MusicBoxData state = state(stack);
        if (!state.playing() || before == null || before.equals(contents(stack))) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel level) {
            stopSound(serverPlayer, level, before, state.current());
        }
        stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state.stopped());
    }
}