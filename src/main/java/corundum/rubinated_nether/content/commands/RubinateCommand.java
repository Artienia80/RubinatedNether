package corundum.rubinated_nether.content.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.items.Rubination;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class RubinateCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
            entity -> Component.translatableEscape("commands.enchant.failed.entity", entity)
    );

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(
            entity -> Component.translatableEscape("commands.enchant.failed.itemless", entity)
    );

    private static final Dynamic2CommandExceptionType ERROR_INCOMPATIBLE = new Dynamic2CommandExceptionType(
            (item, rubination) -> Component.translatableEscape("commands.rubinate.failed.incompatible", item, rubination)
    );

    private static final DynamicCommandExceptionType ERROR_INVALID_RUBINATION = new DynamicCommandExceptionType(
            rubination -> Component.translatableEscape("commands.rubinate.failed.invalid", rubination)
    );

    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(
            Component.translatable("commands.rubinate.failed")
    );

    private static final SuggestionProvider<CommandSourceStack> RUBINATION_SUGGESTIONS = (context, builder) -> {
        List<String> rubinations = Arrays.stream(Rubination.values())
                .filter(r -> r != Rubination.EMPTY)
                .map(Rubination::getSerializedName)
                .toList();
        return SharedSuggestionProvider.suggest(rubinations, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("rubinate")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("rubination", StringArgumentType.word())
                                        .suggests(RUBINATION_SUGGESTIONS)
                                        .executes(RubinateCommand::execute)
                                )
                        )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        String rubinationName = StringArgumentType.getString(context, "rubination");

        // Parse the rubination type
        Rubination rubination;
        try {
            rubination = Rubination.valueOf(rubinationName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw ERROR_INVALID_RUBINATION.create(rubinationName);
        }

        if (rubination == Rubination.EMPTY) {
            throw ERROR_INVALID_RUBINATION.create(rubinationName);
        }

        int successCount = 0;

        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                if (targets.size() == 1) {
                    throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
                }
                continue;
            }

            ItemStack heldItem = livingEntity.getMainHandItem();

            if (heldItem.isEmpty()) {
                if (targets.size() == 1) {
                    throw ERROR_NO_ITEM.create(livingEntity.getName().getString());
                }
                continue;
            }

            // Check if the item is compatible with the rubination type
            if (!heldItem.is(rubination.getItemTag())) {
                if (targets.size() == 1) {
                    throw ERROR_INCOMPATIBLE.create(
                            heldItem.getItem().getName(heldItem).getString(),
                            rubination.getCapitalisedName()
                    );
                }
                continue;
            }

            // Get the enchantments for this rubination
            List<EnchantmentInstance> enchantments = rubination.getEnchantments(source.registryAccess());
            enchantments.removeIf(e -> e == null);

            if (enchantments.isEmpty()) {
                continue;
            }

            // Apply enchantments to the item
            ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(
                    heldItem.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
            );

            for (EnchantmentInstance enchantment : enchantments) {
                mutableEnchantments.set(enchantment.enchantment, enchantment.level);
            }

            heldItem.set(DataComponents.ENCHANTMENTS, mutableEnchantments.toImmutable());

            heldItem.set(DataComponents.RARITY, RNRarity.RUBINATED_NETHER_RUBY.get());

            successCount++;
        }

        if (successCount == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
        }

        // Success messages
        if (targets.size() == 1) {
            Entity target = targets.iterator().next();
            final Rubination finalRubination = rubination;
            source.sendSuccess(() ->
                            Component.translatable("commands.rubinate.success.single",
                                    Component.literal(finalRubination.getCapitalisedName()).withStyle(ChatFormatting.DARK_RED),
                                    target.getDisplayName()),
                    true
            );
        } else {
            final Rubination finalRubination = rubination;
            final int finalSuccessCount = successCount;
            source.sendSuccess(() ->
                            Component.translatable("commands.rubinate.success.multiple",
                                    Component.literal(finalRubination.getCapitalisedName()).withStyle(ChatFormatting.DARK_RED),
                                    finalSuccessCount),
                    true
            );
        }

        return successCount;
    }
}