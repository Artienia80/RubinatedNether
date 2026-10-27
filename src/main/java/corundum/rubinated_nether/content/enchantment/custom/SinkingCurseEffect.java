package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
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

import java.util.Arrays;

public record SinkingCurseEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<SinkingCurseEffect> CODEC = MapCodec.unit(SinkingCurseEffect::new);

    private static final double SINKING_MOTION_Y = -1.5;
    private static final int MAX_RADIUS = 16;

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

        scheduleSinkhole(level, player.blockPosition(), player.fallDistance);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    private static void scheduleSinkhole(ServerLevel level, BlockPos centerPos, float fallDistance) {
        BlockPos groundPos = findGroundLevel(level, centerPos);
        if (groundPos == null) return;

        float strength = Math.max(1.0f, fallDistance * 0.25f);
        float maxRadiusFloat = 1.5f + (float) Math.sqrt(fallDistance) * 0.15f;
        int maxRadius = Math.min(MAX_RADIUS, (int) Math.ceil(maxRadiusFloat));
        float maxRadiusSquared = Math.min(maxRadiusFloat, MAX_RADIUS) * Math.min(maxRadiusFloat, MAX_RADIUS);

        int halfRadius = maxRadius / 2;
        int side = maxRadius * 2 + 1;

        long[] candidates = new long[side * side * (halfRadius + 2)];
        int count = 0;

        BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();

        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                int distXZ = x * x + z * z;
                if (distXZ > maxRadiusSquared) continue;

                for (int y = -halfRadius; y <= 1; y++) {
                    int distanceSquared = distXZ + y * y;
                    if (distanceSquared > maxRadiusSquared) continue;

                    target.set(groundPos.getX() + x, groundPos.getY() + y, groundPos.getZ() + z);
                    if (!level.isLoaded(target)) continue;

                    BlockState state = level.getBlockState(target);
                    if (state.isAir() || state.liquid()) continue;

                    float hardness = state.getDestroySpeed(level, target);
                    if (hardness < 0) continue;

                    BlockState aboveState = level.getBlockState(above.setWithOffset(target, Direction.UP));
                    if (!aboveState.isAir() && aboveState.isSolid()
                            && x * x + (y + 1) * (y + 1) + z * z > maxRadiusSquared) {
                        continue;
                    }

                    double breakThreshold = Math.sqrt(distanceSquared) * Math.pow(hardness, 0.7) * 7.0;
                    if (strength > breakThreshold) {
                        candidates[count++] = ((long) distanceSquared << 24) | ((long) (x + 128) << 16) | ((long) (y + 128) << 8) | (z + 128);
                    }
                }
            }
        }

        if (count == 0) return;

        Arrays.sort(candidates, 0, count);

        long[] positions = new long[count];
        for (int i = 0; i < count; i++) {
            long packed = candidates[i];
            int dx = (int) ((packed >> 16) & 0xFF) - 128;
            int dy = (int) ((packed >> 8) & 0xFF) - 128;
            int dz = (int) (packed & 0xFF) - 128;
            positions[i] = BlockPos.asLong(groundPos.getX() + dx, groundPos.getY() + dy, groundPos.getZ() + dz);
        }

        SinkholeScheduler.enqueue(level, positions);
    }

    private static BlockPos findGroundLevel(ServerLevel level, BlockPos startPos) {
        BlockState currentState = level.getBlockState(startPos);
        if (!currentState.isAir() && currentState.isSolid()) {
            return startPos;
        }

        BlockPos.MutableBlockPos check = startPos.mutable();

        // Search downward first (more likely)
        for (int y = startPos.getY() - 1; y >= level.getMinBuildHeight(); y--) {
            check.setY(y);
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && state.isSolid()) {
                return check.immutable();
            }
        }

        // Search upward if needed
        for (int y = startPos.getY() + 1; y <= level.getMaxBuildHeight(); y++) {
            check.setY(y);
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && state.isSolid()) {
                return check.immutable();
            }
        }

        return null;
    }
}
