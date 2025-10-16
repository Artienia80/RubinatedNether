package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(MaceItem.class)
public class MaceItemMixin {

    @Redirect(method = "hurtEnemy", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void redirectSetDeltaMovement(ServerPlayer player, Vec3 motion, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        try {
            Holder<Enchantment> sinkingCurse = player.level().registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getHolderOrThrow(RNEnchantments.SINKING_CURSE);

            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(sinkingCurse, stack);

            if (enchantLevel > 0 && MaceItem.canSmashAttack(player)) {
                Vec3 downwardMotion = new Vec3(motion.x, -1.5, motion.z);
                player.setDeltaMovement(downwardMotion);
            } else {
                player.setDeltaMovement(motion);
            }
        } catch (Exception e) {
            player.setDeltaMovement(motion);
        }
    }

    @Inject(method = "postHurtEnemy", at = @At("HEAD"))
    private void onPostHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        if (attacker instanceof ServerPlayer player) {
            try {
                Holder<Enchantment> sinkingCurse = player.level().registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getHolderOrThrow(RNEnchantments.SINKING_CURSE);

                int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(sinkingCurse, stack);

                if (enchantLevel > 0 && MaceItem.canSmashAttack(player)) {
                    ServerLevel serverLevel = (ServerLevel) player.level();
                    scheduleProgressiveSinkhole(serverLevel, player.blockPosition(), player.fallDistance);
                }
            } catch (Exception e) {
            }
        }
    }

    private void scheduleProgressiveSinkhole(ServerLevel level, BlockPos centerPos, float fallDistance) {
        BlockPos groundPos = findGroundLevel(level, centerPos);
        if (groundPos == null) return;

        float strength = Math.max(1.0f, fallDistance / 4.0f);
        float maxRadiusFloat = 1.5f + (float)Math.sqrt(fallDistance) * 0.15f;
        int maxRadius = (int) Math.ceil(maxRadiusFloat);

        Map<BlockPos, Double> blocksToBreak = new HashMap<>();

        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                for (int y = -maxRadius/2; y <= 1; y++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);

                    if (distance <= maxRadiusFloat) {
                        BlockPos targetPos = groundPos.offset(x, y, z);
                        BlockState currentState = level.getBlockState(targetPos);

                        if (shouldSkipBlock(level, targetPos, currentState, maxRadiusFloat, x, y, z)) {
                            continue;
                        }

                        float blockHardness = currentState.getDestroySpeed(level, targetPos);


                        double scaledHardness = Math.pow(blockHardness, 0.7);

                        double breakThreshold = distance * scaledHardness * 7.0;

                        if (strength > breakThreshold) {
                            blocksToBreak.put(targetPos, distance);
                        }
                    }
                }
            }
        }

        // Sort positions by distance to break from center outward
        List<BlockPos> sortedPositions = new ArrayList<>(blocksToBreak.keySet());
        sortedPositions.sort(Comparator.comparingDouble(blocksToBreak::get));

        // Break blocks progressively with delays
        breakBlocksProgressively(level, sortedPositions, 0);
    }

    private void breakBlocksProgressively(ServerLevel level, List<BlockPos> blocksToBreak, int index) {
        if (index >= blocksToBreak.size()) return;

        int batchSize = Math.min(8, blocksToBreak.size() - index); // Break 8 blocks at a time
        Random random = new Random();

        for (int i = 0; i < batchSize && (index + i) < blocksToBreak.size(); i++) {
            BlockPos pos = blocksToBreak.get(index + i);

            if (random.nextFloat() < 0.05f) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                boolean shouldDrop = random.nextFloat() < 0.5f;
                level.destroyBlock(pos, shouldDrop);
            }
        }

        if (index + batchSize < blocksToBreak.size()) {
            level.getServer().execute(() -> {
                try {
                    Thread.sleep(50); // 50ms delay between batches
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                breakBlocksProgressively(level, blocksToBreak, index + batchSize);
            });
        }
    }

    private boolean shouldSkipBlock(ServerLevel level, BlockPos targetPos, BlockState currentState,
                                    float maxRadius, int x, int y, int z) {
        // Skip bedrock, air, liquids, and unbreakable blocks
        if (currentState.isAir() || currentState.liquid() || currentState.getDestroySpeed(level, targetPos) < 0) {
            return true;
        }

        BlockPos abovePos = targetPos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        double aboveDistance = Math.sqrt(x*x + (y+1)*(y+1) + z*z);

        return !aboveState.isAir() && aboveState.isSolid() && aboveDistance > maxRadius;
    }

    private BlockPos findGroundLevel(ServerLevel level, BlockPos startPos) {
        BlockState currentState = level.getBlockState(startPos);
        if (!currentState.isAir() && currentState.isSolid()) {
            return startPos;
        }

        for (int y = startPos.getY() - 1; y >= level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = level.getBlockState(checkPos);

            if (!state.isAir() && state.isSolid()) {
                return checkPos;
            }
        }

        for (int y = startPos.getY() + 1; y <= level.getMaxBuildHeight(); y++) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = level.getBlockState(checkPos);

            if (!state.isAir() && state.isSolid()) {
                return checkPos;
            }
        }

        return null;
    }
}