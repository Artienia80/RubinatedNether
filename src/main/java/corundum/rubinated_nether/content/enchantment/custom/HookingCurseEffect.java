package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record HookingCurseEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<HookingCurseEffect> CODEC = MapCodec.unit(HookingCurseEffect::new);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse enchantedItem, Entity target, Vec3 vec3) {
        System.out.println("HookingCurseEffect triggered with level: " + enchantmentLevel); // Debug

        if (!(enchantedItem.owner() instanceof LivingEntity attacker)) {
            System.out.println("No valid attacker");
            return;
        }
        if (!(target instanceof LivingEntity victim)) {
            System.out.println("No valid victim");
            return;
        }

        System.out.println("Applying knockback to: " + victim.getName().getString());

        // Calculate knockback direction (from attacker to victim)
        Vec3 attackerPos = attacker.position();
        Vec3 victimPos = victim.position();

        // Get horizontal direction vector
        double dx = victimPos.x - attackerPos.x;
        double dz = victimPos.z - attackerPos.z;

        // Apply knockback with vanilla-like strength calculation
        // Knockback I = 0.4 strength, Knockback II = 0.8 strength (0.4 * level)
        double knockbackStrength = 0.4 * enchantmentLevel;

        // Apply the knockback using vanilla logic
        applyKnockback(victim, knockbackStrength, dx, dz);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return null;
    }

    private void applyKnockback(LivingEntity entity, double strength, double x, double z) {
        // Apply knockback resistance reduction (exactly like vanilla)
        strength *= 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);

        if (strength <= 0) return;

        entity.hasImpulse = true;

        Vec3 currentMotion = entity.getDeltaMovement();

        // Handle case where direction vector is too small (vanilla logic)
        while (x * x + z * z < 9.999999747378752E-6) {
            x = (Math.random() - Math.random()) * 0.01;
            z = (Math.random() - Math.random()) * 0.01;
        }

        // Normalize and scale the knockback vector (vanilla logic)
        Vec3 knockbackVector = new Vec3(x, 0.0, z).normalize().scale(strength);

        // Apply knockback to entity motion (vanilla formula)
        entity.setDeltaMovement(
                currentMotion.x / 2.0 - knockbackVector.x,
                entity.onGround() ? Math.min(0.4, currentMotion.y / 2.0 + strength) : currentMotion.y,
                currentMotion.z / 2.0 - knockbackVector.z
        );
    }
}
