package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.module.ToolModules;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public class DampeningDropsMixin {

    @ModifyVariable(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            argsOnly = true)
    private static ItemStack rn$dampeningActsAsSilkTouch(ItemStack tool) {
        if (!ToolModules.has(tool, ToolModules.DAMPENING)) return tool;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return tool;

        ItemStack silky = tool.copy();
        silky.enchant(server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH), 1);
        return silky;
    }
}
