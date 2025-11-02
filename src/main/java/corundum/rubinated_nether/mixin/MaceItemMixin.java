package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Mixin(MaceItem.class)
public class MaceItemMixin {
    private static final Vec3 SINKING_MOTION = new Vec3(0, -1.5, 0);
    private static final int BATCH_SIZE = 8;
    private static final int BATCH_DELAY_MS = 50;
    private static final float DROP_CHANCE = 0.5f;
    private static final float SKIP_CHANCE = 0.05f;

    @Redirect(method = "hurtEnemy", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void redirectSetDeltaMovement(ServerPlayer player, Vec3 motion, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        try {
            Holder<Enchantment> sinkingCurse = player.level().registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getHolderOrThrow(RNEnchantments.SINKING_CURSE);

            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(sinkingCurse, stack);

            if (enchantLevel > 0 && MaceItem.canSmashAttack(player)) {
                player.setDeltaMovement(new Vec3(motion.x, SINKING_MOTION.y, motion.z));
            } else {
                player.setDeltaMovement(motion);
            }
        } catch (Exception e) {
            player.setDeltaMovement(motion);
        }
    }

    @Inject(method = "postHurtEnemy", at = @At("HEAD"))
    private void onPostHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        if (!(attacker instanceof ServerPlayer player)) return;

        try {
            Holder<Enchantment> sinkingCurse = player.level().registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getHolderOrThrow(RNEnchantments.SINKING_CURSE);

            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(sinkingCurse, stack);

            if (enchantLevel > 0 && MaceItem.canSmashAttack(player)) {
                scheduleProgressiveSinkhole((ServerLevel) player.level(), player.blockPosition(), player.fallDistance);
            }
        } catch (Exception e) {
            // Silently fail
        }
    }

    private void scheduleProgressiveSinkhole(ServerLevel level, BlockPos centerPos, float fallDistance) {
        BlockPos groundPos = findGroundLevel(level, centerPos);
        if (groundPos == null) return;

        float strength = Math.max(1.0f, fallDistance * 0.25f);
        float maxRadiusFloat = 1.5f + (float)Math.sqrt(fallDistance) * 0.15f;
        int maxRadius = (int) Math.ceil(maxRadiusFloat);
        float maxRadiusSquared = maxRadiusFloat * maxRadiusFloat;

        // Use two parallel lists instead of a helper class
        List<BlockPos> positions = new ObjectArrayList<>();
        List<Float> distances = new ObjectArrayList<>();

        int halfRadius = maxRadius / 2;
        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                // Quick 2D distance check before doing 3D checks
                int distXZ = x*x + z*z;
                if (distXZ > maxRadiusSquared) continue;

                for (int y = -halfRadius; y <= 1; y++) {
                    float distanceSquared = distXZ + y*y;
                    if (distanceSquared > maxRadiusSquared) continue;

                    BlockPos targetPos = groundPos.offset(x, y, z);
                    BlockState currentState = level.getBlockState(targetPos);

                    if (shouldSkipBlock(level, targetPos, currentState, maxRadiusSquared, x, y, z)) {
                        continue;
                    }

                    float blockHardness = currentState.getDestroySpeed(level, targetPos);
                    double scaledHardness = Math.pow(blockHardness, 0.7);
                    float distance = (float)Math.sqrt(distanceSquared);
                    double breakThreshold = distance * scaledHardness * 7.0;

                    if (strength > breakThreshold) {
                        positions.add(targetPos);
                        distances.add(distance);
                    }
                }
            }
        }

        if (positions.isEmpty()) return;

        // Sort both lists by distance
        sortByDistance(positions, distances);

        // Break blocks progressively
        breakBlocksProgressively(level, positions, 0, new Random());
    }

    private void sortByDistance(List<BlockPos> positions, List<Float> distances) {
        // Create indices array and sort by distance
        Integer[] indices = new Integer[positions.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        java.util.Arrays.sort(indices, Comparator.comparingDouble(distances::get));

        // Reorder both lists
        List<BlockPos> sortedPos = new ObjectArrayList<>(positions.size());
        List<Float> sortedDist = new ObjectArrayList<>(distances.size());

        for (int idx : indices) {
            sortedPos.add(positions.get(idx));
            sortedDist.add(distances.get(idx));
        }

        positions.clear();
        positions.addAll(sortedPos);
        distances.clear();
        distances.addAll(sortedDist);
    }

    private void breakBlocksProgressively(ServerLevel level, List<BlockPos> blocksToBreak, int index, Random random) {
        if (index >= blocksToBreak.size()) return;

        int batchSize = Math.min(BATCH_SIZE, blocksToBreak.size() - index);

        for (int i = 0; i < batchSize; i++) {
            int currentIndex = index + i;
            if (currentIndex >= blocksToBreak.size()) break;

            if (random.nextFloat() < SKIP_CHANCE) continue;

            BlockPos pos = blocksToBreak.get(currentIndex);
            BlockState state = level.getBlockState(pos);

            if (!state.isAir()) {
                level.destroyBlock(pos, random.nextFloat() < DROP_CHANCE);
            }
        }

        // Schedule next batch
        int nextIndex = index + batchSize;
        if (nextIndex < blocksToBreak.size()) {
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + 1,
                    () -> breakBlocksProgressively(level, blocksToBreak, nextIndex, random)
            ));
        }
    }

    private boolean shouldSkipBlock(ServerLevel level, BlockPos targetPos, BlockState currentState,
                                    float maxRadiusSquared, int x, int y, int z) {
        // Skip unbreakable blocks
        if (currentState.isAir() || currentState.liquid() || currentState.getDestroySpeed(level, targetPos) < 0) {
            return true;
        }

        // Check if block above is solid and outside radius
        BlockPos abovePos = targetPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (!aboveState.isAir() && aboveState.isSolid()) {
            int aboveDistSquared = x*x + (y+1)*(y+1) + z*z;
            return aboveDistSquared > maxRadiusSquared;
        }

        return false;
    }

    private BlockPos findGroundLevel(ServerLevel level, BlockPos startPos) {
        BlockState currentState = level.getBlockState(startPos);
        if (!currentState.isAir() && currentState.isSolid()) {
            return startPos;
        }

        // Search downward first (more likely)
        for (int y = startPos.getY() - 1; y >= level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = level.getBlockState(checkPos);

            if (!state.isAir() && state.isSolid()) {
                return checkPos;
            }
        }

        // Search upward if needed
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