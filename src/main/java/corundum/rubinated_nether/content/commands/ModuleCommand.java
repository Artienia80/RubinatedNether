package corundum.rubinated_nether.content.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.module.ToolModule;
import corundum.rubinated_nether.content.module.ToolModules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class ModuleCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
            entity -> Component.translatableEscape("commands.enchant.failed.entity", entity)
    );

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(
            entity -> Component.translatableEscape("commands.enchant.failed.itemless", entity)
    );

    private static final Dynamic2CommandExceptionType ERROR_INCOMPATIBLE = new Dynamic2CommandExceptionType(
            (item, module) -> Component.translatableEscape("commands.module.failed.incompatible", item, module)
    );

    private static final DynamicCommandExceptionType ERROR_INVALID_MODULE = new DynamicCommandExceptionType(
            module -> Component.translatableEscape("commands.module.failed.invalid", module)
    );

    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(
            Component.translatable("commands.module.failed")
    );

    private static final SuggestionProvider<CommandSourceStack> MODULE_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggestResource(
                    ToolModules.all().stream().map(ToolModule::id).toList(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("module")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("module", ResourceLocationArgument.id())
                                        .suggests(MODULE_SUGGESTIONS)
                                        .executes(ModuleCommand::execute)
                                )
                        )
        );
    }

    private static ToolModule resolve(ResourceLocation id) throws CommandSyntaxException {
        ToolModule module = ToolModules.byId(id);
        if (module == null && id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
            module = ToolModules.byId(RubinatedNether.id(id.getPath()));
        }
        if (module == null) throw ERROR_INVALID_MODULE.create(id.toString());
        return module;
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        ToolModule module = resolve(ResourceLocationArgument.getId(context, "module"));

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

            if (!ToolModules.canApply(heldItem, module)) {
                if (targets.size() == 1) {
                    throw ERROR_INCOMPATIBLE.create(
                            heldItem.getItem().getName(heldItem).getString(),
                            module.displayName().getString()
                    );
                }
                continue;
            }

            livingEntity.setItemInHand(InteractionHand.MAIN_HAND, ToolModules.withModule(heldItem, module));
            successCount++;
        }

        if (successCount == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
        }

        if (targets.size() == 1) {
            Entity target = targets.iterator().next();
            source.sendSuccess(() ->
                            Component.translatable("commands.module.success.single",
                                    module.displayName(),
                                    target.getDisplayName()),
                    true
            );
        } else {
            final int finalSuccessCount = successCount;
            source.sendSuccess(() ->
                            Component.translatable("commands.module.success.multiple",
                                    module.displayName(),
                                    finalSuccessCount),
                    true
            );
        }

        return successCount;
    }
}
