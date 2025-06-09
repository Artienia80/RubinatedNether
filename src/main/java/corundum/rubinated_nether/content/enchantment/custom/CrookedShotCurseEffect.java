package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public class CrookedShotCurseEffect implements EnchantmentEntityEffect {
    public static final MapCodec<CrookedShotCurseEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.stable(new CrookedShotCurseEffect())
    );

    public CrookedShotCurseEffect() {
    }

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        System.out.println("CS: apply called, ent=" + entity.getClass().getSimpleName());

        if (entity instanceof AbstractArrow arrow) {
            // Schedule the velocity modification for the next tick to ensure the arrow has initial velocity
            level.getServer().execute(() -> {
                Vec3 currentVelocity = arrow.getDeltaMovement();
                System.out.println("CS: oldV=" + String.format("%.2f,%.2f,%.2f", currentVelocity.x, currentVelocity.y, currentVelocity.z));

                // Check if velocity is essentially zero (arrow hasn't been given initial velocity yet)
                if (currentVelocity.lengthSqr() < 0.001) {
                    System.out.println("CS: velocity too small, skipping");
                    return;
                }

                // Randomize velocity by ±25%
                double velocityMultiplier = 0.75 + (level.getRandom().nextDouble() * 0.5); // 0.75 to 1.25

                // Randomize aim by up to 25% (25% of 90 degrees = 22.5 degrees)
                double maxSpreadRadians = Math.toRadians(22.5);
                double horizontalSpread = (level.getRandom().nextDouble() - 0.5) * 2 * maxSpreadRadians;
                double verticalSpread = (level.getRandom().nextDouble() - 0.5) * 2 * maxSpreadRadians;

                System.out.println("CS: mult=" + String.format("%.2f", velocityMultiplier) + " hS=" + String.format("%.2f", Math.toDegrees(horizontalSpread)) + " vS=" + String.format("%.2f", Math.toDegrees(verticalSpread)));

                // Apply rotation to the velocity vector
                Vec3 rotatedVelocity = rotateVelocity(currentVelocity, horizontalSpread, verticalSpread);

                // Apply velocity randomization
                Vec3 finalVelocity = rotatedVelocity.scale(velocityMultiplier);

                System.out.println("CS: newV=" + String.format("%.2f,%.2f,%.2f", finalVelocity.x, finalVelocity.y, finalVelocity.z));

                // Set the new velocity
                arrow.setDeltaMovement(finalVelocity);
            });
        } else {
            System.out.println("CS: not arrow");
        }
    }

    private Vec3 rotateVelocity(Vec3 velocity, double yawOffset, double pitchOffset) {
        // Convert velocity to spherical coordinates
        double speed = velocity.length();

        // Safety check to prevent division by zero
        if (speed < 0.001) {
            return velocity;
        }

        double yaw = Math.atan2(-velocity.x, velocity.z);
        double pitch = Math.asin(Math.max(-1.0, Math.min(1.0, velocity.y / speed))); // Clamp to prevent NaN

        // Apply offsets
        yaw += yawOffset;
        pitch += pitchOffset;

        // Clamp pitch to prevent flipping
        pitch = Math.max(-Math.PI/2 + 0.01, Math.min(Math.PI/2 - 0.01, pitch));

        // Convert back to cartesian coordinates
        double x = -speed * Math.sin(yaw) * Math.cos(pitch);
        double y = speed * Math.sin(pitch);
        double z = speed * Math.cos(yaw) * Math.cos(pitch);

        return new Vec3(x, y, z);
    }

    @Override
    public MapCodec<CrookedShotCurseEffect> codec() {
        return CODEC;
    }
}