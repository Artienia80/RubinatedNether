package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import net.minecraft.core.Holder;
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
        if (contents.isEmpty()) {
            player.getPersistentData().remove(SOOTHING_KEY);
            stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state.stopped());
            return;
        }
        if (level.getGameTime() >= state.songEndTick()) {
            int next = state.looping() ? state.current() : (state.current() + 1) % contents.size();
            playFrom(stack, player, serverLevel, contents, next);
        }
    }

    private static void playFrom(ItemStack stack, ServerPlayer player, ServerLevel level, BundleContents contents, int startIndex) {
        int size = contents.size();
        for (int i = 0; i < size; i++) {
            int index = (startIndex + i) % size;
            Optional<Holder<JukeboxSong>> holder = JukeboxSong.fromStack(level.registryAccess(), contents.getItemUnsafe(index));
            if (holder.isEmpty()) {
                continue;
            }
            JukeboxSong song = holder.get().value();
            long end = level.getGameTime() + Mth.ceil(song.lengthInSeconds() * 20.0F) + GAP_TICKS;
            boolean sinner = holder.get().is(RNJukeboxSongs.SINNER);
            if (sinner) {
                player.getPersistentData().putLong(SOOTHING_KEY, end);
            } else {
                player.getPersistentData().remove(SOOTHING_KEY);
            }
            level.playSeededSound(null, player, song.soundEvent(), SoundSource.RECORDS, 4.0F, 1.0F, level.getRandom().nextLong());

            stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state(stack).withNowPlaying(index, end, sinner));
            return;
        }
        player.getPersistentData().remove(SOOTHING_KEY);
        stack.set(RNDataComponents.MUSIC_BOX_DATA.get(), state(stack).stopped());
    }

    private static void stopSound(ServerPlayer player, ServerLevel level, BundleContents contents, int current) {
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