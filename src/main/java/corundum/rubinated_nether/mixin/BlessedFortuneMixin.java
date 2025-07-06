package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class BlessedFortuneMixin {

    private static boolean isDoublingLoot = false;

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", shift = At.Shift.AFTER))
    private static void doubleLootIfBlessed(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity,
                                            net.minecraft.world.entity.Entity entity, ItemStack tool, CallbackInfo ci) {

        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof Player player)) {
            return;
        }

        if (isDoublingLoot) {
            return;
        }

        if (!player.hasEffect(RNEffects.BLESSED)) {
            return;
        }

        int remainingDuration = player.getEffect(RNEffects.BLESSED).getDuration();
        float chance = Math.min(1.0f, remainingDuration / 384000.0f);

        if (player.getRandom().nextFloat() < chance) {
            isDoublingLoot = true;

            try {
                boolean shouldDuplicate = state.is(net.minecraft.tags.TagKey.create(
                        net.minecraft.core.registries.Registries.BLOCK,
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ores")
                ));

                if (shouldDuplicate) {
                    List<ItemStack> additionalLoot = Block.getDrops(state, serverLevel, pos, blockEntity, entity, tool);

                    for (ItemStack lootItem : additionalLoot) {
                        if (!lootItem.isEmpty()) {
                            Block.popResource(level, pos, lootItem);
                        }
                    }
                }

            } catch (Exception e) {
            }

            isDoublingLoot = false;
        }
    }
}