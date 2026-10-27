package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public record SinkingCurseEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<SinkingCurseEffect> CODEC = MapCodec.unit(SinkingCurseEffect::new);

    private static final double SINKING_MOTION_Y = -1.5;
    private static final int BATCH_SIZE = 8;
    private static final float DROP_CHANCE = 0.5f;
    private static final float SKIP_CHANCE = 0.05f;

    private record Candidate(BlockPos pos, float distance) {}

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse enchantedItem, Entity target, Vec3 origin) {
        if (!(enchantedItem.owner() instanceof ServerPlayer player)) return;
        if (!(enchantedItem.itemStack().getItem() instanceof MaceItem)) return;

        // Vanilla only runs MaceItem#hurtEnemy for living victims (dragon parts resolve to their parent)
        Entity victim = target instanceof PartEntity<?> part ? part.getParent() : target;
        if (!(victim instanceof LivingEntity)) return;

        if (!MaceItem.canSmashAttack(player)) return;

        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, SINKING_MOTION_Y, motion.z);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));

        scheduleProgressiveSinkhole(level, player.blockPosition(), player.fallDistance);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    private static void scheduleProgressiveSinkhole(ServerLevel level, BlockPos centerPos, float fallDistance) {
        BlockPos groundPos = findGroundLevel(level, centerPos);
        if (groundPos == null) return;

        float strength = Math.max(1.0f, fallDistance * 0.25f);
        float maxRadiusFloat = 1.5f + (float) Math.sqrt(fallDistance) * 0.15f;
        int maxRadius = (int) Math.ceil(maxRadiusFloat);
        float maxRadiusSquared = maxRadiusFloat * maxRadiusFloat;

        List<Candidate> candidates = new ArrayList<>();

        int halfRadius = maxRadius / 2;
        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                int distXZ = x * x + z * z;
                if (distXZ > maxRadiusSquared) continue;

                for (int y = -halfRadius; y <= 1; y++) {
                    float distanceSquared = distXZ + y * y;
                    if (distanceSquared > maxRadiusSquared) continue;

                    BlockPos targetPos = groundPos.offset(x, y, z);
                    BlockState currentState = level.getBlockState(targetPos);

                    if (shouldSkipBlock(level, targetPos, currentState, maxRadiusSquared, x, y, z)) {
                        continue;
                    }

                    float blockHardness = currentState.getDestroySpeed(level, targetPos);
                    double scaledHardness = Math.pow(blockHardness, 0.7);
                    float distance = (float) Math.sqrt(distanceSquared);
                    double breakThreshold = distance * scaledHardness * 7.0;

                    if (strength > breakThreshold) {
                        candidates.add(new Candidate(targetPos, distance));
                    }
                }
            }
        }

        if (candidates.isEmpty()) return;

        candidates.sort(Comparator.comparingDouble(Candidate::distance));

        breakBlocksProgressively(level, candidates, 0, new Random());
    }

    private static void breakBlocksProgressively(ServerLevel level, List<Candidate> blocksToBreak, int index, Random random) {
        if (index >= blocksToBreak.size()) return;

        int batchSize = Math.min(BATCH_SIZE, blocksToBreak.size() - index);

        for (int i = 0; i < batchSize; i++) {
            if (random.nextFloat() < SKIP_CHANCE) continue;

            BlockPos pos = blocksToBreak.get(index + i).pos();
            BlockState state = level.getBlockState(pos);

            if (!state.isAir()) {
                level.destroyBlock(pos, random.nextFloat() < DROP_CHANCE);
            }
        }

        int nextIndex = index + batchSize;
        if (nextIndex < blocksToBreak.size()) {
            level.getServer().tell(new TickTask(
                    level.getServer().getTickCount() + 1,
                    () -> breakBlocksProgressively(level, blocksToBreak, nextIndex, random)
            ));
        }
    }

    private static boolean shouldSkipBlock(ServerLevel level, BlockPos targetPos, BlockState currentState,
                                           float maxRadiusSquared, int x, int y, int z) {
        if (currentState.isAir() || currentState.liquid() || currentState.getDestroySpeed(level, targetPos) < 0) {
            return true;
        }

        BlockState aboveState = level.getBlockState(targetPos.above());

        if (!aboveState.isAir() && aboveState.isSolid()) {
            int aboveDistSquared = x * x + (y + 1) * (y + 1) + z * z;
            return aboveDistSquared > maxRadiusSquared;
        }

        return false;
    }

    private static BlockPos findGroundLevel(ServerLevel level, BlockPos startPos) {
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