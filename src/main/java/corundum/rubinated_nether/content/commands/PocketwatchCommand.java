package corundum.rubinated_nether.content.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.items.PocketwatchData;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import corundum.rubinated_nether.event.PocketwatchSavePayload;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Locale;

public class PocketwatchCommand {
    private static final int FULL_BUFFER_TICKS = 15 * 20;

    private static final SimpleCommandExceptionType ERROR_NOT_HOLDING = new SimpleCommandExceptionType(
            Component.literal("You must be holding a pocketwatch")
    );

    private static final SimpleCommandExceptionType ERROR_BROKEN = new SimpleCommandExceptionType(
            Component.literal("The pocketwatch is broken")
    );

    private static final SimpleCommandExceptionType ERROR_NOT_BROKEN = new SimpleCommandExceptionType(
            Component.literal("The pocketwatch is not broken")
    );

    private static final SimpleCommandExceptionType ERROR_ALREADY_TICKING = new SimpleCommandExceptionType(
            Component.literal("The pocketwatch is already running")
    );

    private static final SimpleCommandExceptionType ERROR_NOT_TICKING = new SimpleCommandExceptionType(
            Component.literal("The pocketwatch is not running")
    );

    private static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(
            Component.literal("The pocketwatch has no time left")
    );

    private static final SimpleCommandExceptionType ERROR_OTHER_TICKING = new SimpleCommandExceptionType(
            Component.literal("Another pocketwatch is already running")
    );

    private static final DynamicCommandExceptionType ERROR_INVALID_AMOUNT = new DynamicCommandExceptionType(
            value -> Component.literal("Invalid amount '" + value + "', use e.g. 6h, 120m or 50%")
    );

    private static final SuggestionProvider<CommandSourceStack> LOAD_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(List.of("full"), builder);

    private static final SuggestionProvider<CommandSourceStack> DRAIN_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(List.of("all"), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("pocketwatch")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("load")
                                .then(Commands.argument("amount", StringArgumentType.greedyString())
                                        .suggests(LOAD_SUGGESTIONS)
                                        .executes(PocketwatchCommand::load)))
                        .then(Commands.literal("drain")
                                .then(Commands.argument("amount", StringArgumentType.greedyString())
                                        .suggests(DRAIN_SUGGESTIONS)
                                        .executes(PocketwatchCommand::drain)))
                        .then(Commands.literal("start").executes(PocketwatchCommand::start))
                        .then(Commands.literal("stop").executes(PocketwatchCommand::stop))
                        .then(Commands.literal("break").executes(PocketwatchCommand::breakWatch))
                        .then(Commands.literal("repair").executes(PocketwatchCommand::repair))
        );
    }

    private static int load(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = findHeld(player);
        PocketwatchData data = dataOf(stack);

        if (data.broken()) {
            throw ERROR_BROKEN.create();
        }

        String raw = StringArgumentType.getString(context, "amount");
        long gameTime = player.level().getGameTime();
        int current = PocketwatchItem.getRemainingTicks(data, gameTime);

        long target;
        if (raw.trim().equalsIgnoreCase("full")) {
            target = thresholdTicks() + FULL_BUFFER_TICKS;
        } else {
            target = (long) current + parseTicks(raw);
        }

        int newAmount = (int) Math.min(Integer.MAX_VALUE, target);
        apply(player, stack, data, gameTime, newAmount);
        report(context.getSource(), newAmount);
        return 1;
    }

    private static int drain(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = findHeld(player);
        PocketwatchData data = dataOf(stack);

        if (data.broken()) {
            throw ERROR_BROKEN.create();
        }

        String raw = StringArgumentType.getString(context, "amount");
        long gameTime = player.level().getGameTime();
        int current = PocketwatchItem.getRemainingTicks(data, gameTime);

        long target;
        if (raw.trim().equalsIgnoreCase("all")) {
            target = 0L;
        } else {
            target = Math.max(0L, (long) current - parseTicks(raw));
        }

        apply(player, stack, data, gameTime, (int) target);
        report(context.getSource(), (int) target);
        return 1;
    }

    private static int start(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = findHeld(player);
        PocketwatchData data = dataOf(stack);

        if (data.broken()) {
            throw ERROR_BROKEN.create();
        }

        long gameTime = player.level().getGameTime();

        if (PocketwatchItem.isEffectivelyTicking(data, gameTime)) {
            throw ERROR_ALREADY_TICKING.create();
        }

        int available = PocketwatchItem.getRemainingTicks(data, gameTime);

        if (available <= 0) {
            throw ERROR_EMPTY.create();
        }

        for (ItemStack other : PocketwatchItem.findPocketwatches(player)) {
            if (other == stack) {
                continue;
            }
            if (PocketwatchItem.isEffectivelyTicking(dataOf(other), gameTime)) {
                throw ERROR_OTHER_TICKING.create();
            }
        }

        stack.set(RNDataComponents.POCKETWATCH_DATA, new PocketwatchData(available, true, gameTime));
        player.removeEffect(RNEffects.BLESSED);
        player.addEffect(new MobEffectInstance(RNEffects.BLESSED, available, 0, false, false, true));
        PocketwatchItem.syncModelFrame(stack, gameTime);

        context.getSource().sendSuccess(() -> Component.literal("Pocketwatch started"), true);
        return 1;
    }

    private static int stop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = findHeld(player);
        PocketwatchData data = dataOf(stack);

        if (data.broken()) {
            throw ERROR_BROKEN.create();
        }

        long gameTime = player.level().getGameTime();

        if (!PocketwatchItem.isEffectivelyTicking(data, gameTime)) {
            throw ERROR_NOT_TICKING.create();
        }

        int remaining = PocketwatchItem.getRemainingTicks(data, gameTime);
        stack.set(RNDataComponents.POCKETWATCH_DATA, new PocketwatchData(remaining, false, 0L));
        player.removeEffect(RNEffects.BLESSED);
        PocketwatchItem.syncModelFrame(stack, gameTime);

        context.getSource().sendSuccess(() -> Component.literal("Pocketwatch stopped"), true);
        return 1;
    }

    private static int breakWatch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = findHeld(player);
        PocketwatchData data = dataOf(stack);

        if (data.broken()) {
            throw ERROR_BROKEN.create();
        }

        long gameTime = player.level().getGameTime();

        stack.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.BROKEN);
        player.removeEffect(RNEffects.BLESSED);
        PocketwatchItem.syncModelFrame(stack, gameTime);
        PacketDistributor.sendToPlayer(player, PocketwatchSavePayload.INSTANCE);

        context.getSource().sendSuccess(() -> Component.literal("Pocketwatch broken"), true);
        return 1;
    }

    private static int repair(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = findHeld(player);
        PocketwatchData data = dataOf(stack);

        if (!data.broken()) {
            throw ERROR_NOT_BROKEN.create();
        }

        stack.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
        PocketwatchItem.syncModelFrame(stack, player.level().getGameTime());

        context.getSource().sendSuccess(() -> Component.literal("Pocketwatch repaired"), true);
        return 1;
    }

    private static void apply(ServerPlayer player, ItemStack stack, PocketwatchData data, long gameTime, int ticks) {
        boolean wasTicking = PocketwatchItem.isEffectivelyTicking(data, gameTime);

        if (ticks <= 0) {
            stack.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
            if (wasTicking) {
                player.removeEffect(RNEffects.BLESSED);
            }
        } else if (wasTicking) {
            stack.set(RNDataComponents.POCKETWATCH_DATA, new PocketwatchData(ticks, true, gameTime));
            player.removeEffect(RNEffects.BLESSED);
            player.addEffect(new MobEffectInstance(RNEffects.BLESSED, ticks, 0, false, false, true));
        } else {
            stack.set(RNDataComponents.POCKETWATCH_DATA, new PocketwatchData(ticks, false, 0L));
        }

        PocketwatchItem.syncModelFrame(stack, gameTime);
    }

    private static void report(CommandSourceStack source, int ticks) {
        long threshold = thresholdTicks();
        int percent = Math.round((ticks * 100.0F) / threshold);
        int totalSeconds = ticks / 20;
        String time = String.format("%d:%02d:%02d", totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60);
        source.sendSuccess(() -> Component.literal("Pocketwatch now at " + time + " (" + percent + "%)"), true);
    }

    private static long parseTicks(String raw) throws CommandSyntaxException {
        String text = raw.trim().toLowerCase(Locale.ROOT);

        if (text.length() < 2) {
            throw ERROR_INVALID_AMOUNT.create(raw);
        }

        char unit = text.charAt(text.length() - 1);
        double value;
        try {
            value = Double.parseDouble(text.substring(0, text.length() - 1));
        } catch (NumberFormatException e) {
            throw ERROR_INVALID_AMOUNT.create(raw);
        }

        if (!Double.isFinite(value) || value <= 0) {
            throw ERROR_INVALID_AMOUNT.create(raw);
        }

        double ticks;
        if (unit == 'h') {
            ticks = value * 60.0 * 60.0 * 20.0;
        } else if (unit == 'm') {
            ticks = value * 60.0 * 20.0;
        } else if (unit == '%') {
            ticks = (value / 100.0) * thresholdTicks();
        } else {
            throw ERROR_INVALID_AMOUNT.create(raw);
        }

        return (long) Math.min((double) Integer.MAX_VALUE, Math.round(ticks));
    }

    private static long thresholdTicks() {
        return RNConfig.altarFullBlessingThreshold * 60L * 20L;
    }

    private static PocketwatchData dataOf(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
    }

    private static ItemStack findHeld(ServerPlayer player) throws CommandSyntaxException {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof PocketwatchItem) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof PocketwatchItem) {
            return off;
        }
        throw ERROR_NOT_HOLDING.create();
    }
}