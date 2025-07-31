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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
                createProportionalSinkhole((ServerLevel) player.level(), player, player.fallDistance);
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
                    serverLevel.getServer().execute(() -> {
                        createSinkholeAtPosition(serverLevel, player.blockPosition(), player.fallDistance);
                    });
                }
            } catch (Exception e) {
            }
        }
    }

    private void createProportionalSinkhole(ServerLevel level, LivingEntity attacker, float fallDistance) {
        BlockPos attackerPos = attacker.blockPosition();
        createSinkholeAtPosition(level, attackerPos, fallDistance);
    }

    private void createSinkholeAtPosition(ServerLevel level, BlockPos centerPos, float fallDistance) {
        BlockPos groundPos = findGroundLevel(level, centerPos);
        if (groundPos == null) return;

        float radiusFloat = 1.5f + (float)Math.sqrt(fallDistance) * 0.15f;
        int maxRadius = (int) Math.ceil(radiusFloat);

        Random random = new Random();

        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                for (int y = -maxRadius/2; y <= 1; y++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);

                    if (distance <= radiusFloat) {
                        BlockPos targetPos = groundPos.offset(x, y, z);
                        BlockState currentState = level.getBlockState(targetPos);

                        if (currentState.is(Blocks.BEDROCK) ||
                                currentState.isAir() ||
                                currentState.liquid() ||
                                currentState.getDestroySpeed(level, targetPos) < 0) {
                            continue;
                        }

                        BlockPos abovePos = targetPos.above();
                        BlockState aboveState = level.getBlockState(abovePos);
                        double aboveDistance = Math.sqrt(x*x + (y+1)*(y+1) + z*z);
                        if (!aboveState.isAir() && aboveState.isSolid() && aboveDistance > radiusFloat) {
                            continue;
                        }

                        if (random.nextFloat() < 0.05f && distance > radiusFloat * 0.85) {
                            continue;
                        }

                        level.destroyBlock(targetPos, true);
                    }
                }
            }
        }
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