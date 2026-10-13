package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PocketwatchItem extends Item {

    public static final int BROKEN_MODEL_VALUE = 16;

    public PocketwatchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        long gameTime = level.getGameTime();
        long lockTicks = RNConfig.pocketwatchStopLockMinutes * 60L * 20L;
        PocketwatchData data = stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);

        if (data.broken()) {
            player.displayClientMessage(Component.translatable("message.rubinated_nether.pocketwatch_broken"), true);
            return InteractionResultHolder.fail(stack);
        }

        if (isEffectivelyTicking(data, gameTime)) {
            long elapsed = gameTime - data.startedAtGameTime();

            if (elapsed < lockTicks) {
                player.displayClientMessage(Component.translatable("message.rubinated_nether.pocketwatch_locked"), true);
                return InteractionResultHolder.fail(stack);
            }

            int remaining = getRemainingTicks(data, gameTime);
            stack.set(RNDataComponents.POCKETWATCH_DATA, new PocketwatchData(remaining, false, 0L));
            player.removeEffect(RNEffects.BLESSED);
            syncModelFrame(stack, gameTime);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(stack);
        }

        int available = getRemainingTicks(data, gameTime);

        if (available <= 0) {
            if (data.ticking()) {
                stack.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
                syncModelFrame(stack, gameTime);
            }
            player.displayClientMessage(Component.translatable("message.rubinated_nether.pocketwatch_empty"), true);
            return InteractionResultHolder.fail(stack);
        }

        if (hasAnotherTickingPocketwatch(player, stack, gameTime)) {
            player.displayClientMessage(Component.translatable("message.rubinated_nether.pocketwatch_already_ticking"), true);
            return InteractionResultHolder.fail(stack);
        }

        stack.set(RNDataComponents.POCKETWATCH_DATA, new PocketwatchData(available, true, gameTime));
        player.addEffect(new MobEffectInstance(RNEffects.BLESSED, available, 0, false, false, true));
        player.getCooldowns().addCooldown(this, (int) lockTicks);
        syncModelFrame(stack, gameTime);
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            syncModelFrame(stack, level.getGameTime());
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        PocketwatchData data = stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
        if (data.broken()) {
            return false;
        }
        Rarity rarity = stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
        return rarity.equals(RNRarity.RUBINATED_NETHER_RUBY.get()) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        PocketwatchData data = stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);

        if (data.broken()) {
            tooltipComponents.add(Component.translatable("item.rubinated_nether.pocketwatch.tooltip.broken")
                    .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
            return;
        }

        Level level = context.level();
        long gameTime = level != null ? level.getGameTime() : data.startedAtGameTime();

        int currentTicks = getRemainingTicks(data, gameTime);
        boolean effectivelyTicking = isEffectivelyTicking(data, gameTime);
        long thresholdTicks = RNConfig.altarFullBlessingThreshold * 60L * 20L;
        int percent = Math.round((currentTicks * 100.0f) / thresholdTicks);

        tooltipComponents.add(Component.translatable("item.rubinated_nether.pocketwatch.tooltip.status", formatDuration(currentTicks), percent)
                .withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable(effectivelyTicking
                        ? "item.rubinated_nether.pocketwatch.tooltip.ticking"
                        : "item.rubinated_nether.pocketwatch.tooltip.stopped")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    public static void syncModelFrame(ItemStack stack, long gameTime) {
        PocketwatchData data = stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);

        int frame;
        boolean charged;
        if (data.broken()) {
            frame = BROKEN_MODEL_VALUE;
            charged = false;
        } else {
            int currentTicks = getRemainingTicks(data, gameTime);
            long thresholdTicks = RNConfig.altarFullBlessingThreshold * 60L * 20L;
            frame = (int) (((currentTicks * 16L) / thresholdTicks) % 16L);
            charged = currentTicks >= thresholdTicks;
        }

        CustomModelData existing = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        int existingValue = existing != null ? existing.value() : -1;

        if (existingValue != frame) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(frame));
        }

        syncRarity(stack, charged);
    }

    private static void syncRarity(ItemStack stack, boolean rubinated) {
        Rarity current = stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
        Rarity rubin = RNRarity.RUBINATED_NETHER_RUBY.get();

        if (rubinated) {
            if (!current.equals(rubin)) {
                stack.set(DataComponents.RARITY, rubin);
            }
        } else if (current.equals(rubin)) {
            Rarity base = stack.getItem().components().getOrDefault(DataComponents.RARITY, Rarity.COMMON);
            stack.set(DataComponents.RARITY, base);
        }
    }

    public static int getRemainingTicks(PocketwatchData data, long gameTime) {
        if (data.broken()) {
            return 0;
        }
        if (!data.ticking()) {
            return Math.max(0, data.storedTicks());
        }
        long elapsed = Math.max(0, gameTime - data.startedAtGameTime());
        return (int) Math.max(0, data.storedTicks() - elapsed);
    }

    public static boolean isEffectivelyTicking(PocketwatchData data, long gameTime) {
        return !data.broken() && data.ticking() && getRemainingTicks(data, gameTime) > 0;
    }

    /** Working pocketwatches only: broken ones are skipped, as if they weren't in the inventory. */
    public static List<ItemStack> findPocketwatches(Player player) {
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack stack : player.getInventory().items) {
            addIfUsable(stack, result);
        }

        for (ItemStack stack : player.getInventory().offhand) {
            addIfUsable(stack, result);
        }

        return result;
    }

    private static void addIfUsable(ItemStack stack, List<ItemStack> result) {
        if (stack.getItem() instanceof PocketwatchItem
                && !stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY).broken()) {
            result.add(stack);
        }
    }

    private static boolean hasAnotherTickingPocketwatch(Player player, ItemStack excluded, long gameTime) {
        for (ItemStack stack : findPocketwatches(player)) {
            if (stack == excluded) {
                continue;
            }
            PocketwatchData data = stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
            if (isEffectivelyTicking(data, gameTime)) {
                return true;
            }
        }
        return false;
    }

    private static String formatDuration(int ticks) {
        int totalSeconds = ticks / 20;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
}