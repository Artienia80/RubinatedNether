package corundum.rubinated_nether.content.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import corundum.rubinated_nether.content.world.FeatureLocations;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.List;

public class FeatureLocateCommand {
    private static final List<String> FEATURES = List.of("pumice_chasm", "pumice_sinkhole", "cinder_bridge", "cinder_overhang");

    private static final DynamicCommandExceptionType ERROR_UNKNOWN = new DynamicCommandExceptionType(
            value -> Component.literal("Unknown feature '" + value + "', use " + String.join(", ", FEATURES))
    );

    private static final DynamicCommandExceptionType ERROR_NONE = new DynamicCommandExceptionType(
            value -> Component.literal("No " + value + " has been generated in this dimension yet")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("rnlocate")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("feature", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(FEATURES, builder))
                                .executes(FeatureLocateCommand::locate))
        );
    }

    private static int locate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String feature = StringArgumentType.getString(context, "feature");
        if (!FEATURES.contains(feature)) {
            throw ERROR_UNKNOWN.create(feature);
        }

        CommandSourceStack source = context.getSource();
        List<BlockPos> found = FeatureLocations.positions(source.getLevel(), feature);
        if (found.isEmpty()) {
            throw ERROR_NONE.create(feature);
        }

        double fromX = source.getPosition().x;
        double fromZ = source.getPosition().z;
        BlockPos nearest = found.get(0);
        double best = Double.MAX_VALUE;
        for (BlockPos pos : found) {
            double dx = pos.getX() + 0.5 - fromX;
            double dz = pos.getZ() + 0.5 - fromZ;
            double distance = dx * dx + dz * dz;
            if (distance < best) {
                best = distance;
                nearest = pos;
            }
        }

        int blocks = Mth.floor(Math.sqrt(best));
        BlockPos target = nearest;
        MutableComponent coordinates = ComponentUtils.wrapInSquareBrackets(
                Component.translatable("chat.coordinates", target.getX(), target.getY(), target.getZ())
        ).withStyle(style -> style
                .withColor(ChatFormatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @s " + target.getX() + " " + (target.getY() + 1) + " " + target.getZ()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip"))));

        source.sendSuccess(() -> Component.literal("Nearest " + feature + " is at ")
                .append(coordinates)
                .append(" (" + blocks + " blocks away, " + found.size() + " known)"), false);
        return blocks;
    }
}
