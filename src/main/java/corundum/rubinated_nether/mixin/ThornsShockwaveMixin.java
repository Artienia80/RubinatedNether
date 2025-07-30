package corundum.rubinated_nether.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Enchantment.class)
public class ThornsShockwaveMixin {

    @Unique
    private static final Map<UUID, Long> shockwaveCooldowns = new ConcurrentHashMap<>();

    @Unique
    private static final double SHOCKWAVE_RADIUS = 4.0;

    @Unique
    private static final Random random = new Random();

    @Inject(method = "doPostAttack(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;Lnet/minecraft/world/item/enchantment/EnchantmentTarget;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("TAIL"))
    private void addThornsShockwave(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item,
                                    EnchantmentTarget target, Entity entity, DamageSource damageSource,
                                    CallbackInfo ci) {

        // Only apply to victims (the entity being attacked)
        if (target != EnchantmentTarget.VICTIM || !(entity instanceof LivingEntity victim)) {
            return;
        }

        // Check if this is the Thorns enchantment
        Enchantment enchantment = (Enchantment) (Object) this;
        Holder<Enchantment> thornsHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.THORNS);

        if (!enchantment.equals(thornsHolder.value())) {
            return;
        }

        // Check cooldown
        UUID victimUUID = victim.getUUID();
        long currentTime = System.currentTimeMillis();

        if (shockwaveCooldowns.containsKey(victimUUID)) {
            long lastShockwave = shockwaveCooldowns.get(victimUUID);
            if (currentTime - lastShockwave < getCooldownTime(victim)) {
                return;
            }
        }

        // Apply shockwave
        applyThornsShockwave(level, victim, damageSource);

        // Update cooldown
        shockwaveCooldowns.put(victimUUID, currentTime);

        // Clean up old cooldowns (prevent memory leak)
        cleanupOldCooldowns(currentTime);
    }

    @Unique
    private void applyThornsShockwave(ServerLevel level, LivingEntity victim, DamageSource damageSource) {
        // Get all Thorns armor pieces
        List<ItemStack> thornsArmor = getThornsArmorPieces(victim, level);

        if (thornsArmor.isEmpty()) {
            return;
        }

        // Randomly select one piece to determine knockback strength
        ItemStack selectedPiece = thornsArmor.get(random.nextInt(thornsArmor.size()));
        Holder<Enchantment> thornsHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.THORNS);

        int selectedEnchantLevel = EnchantmentHelper.getItemEnchantmentLevel(thornsHolder, selectedPiece);

        // Spawn shockwave particles based on enchantment level
        spawnShockwaveParticles(level, victim, selectedEnchantLevel);

        // Find nearby entities to knockback
        Vec3 victimPos = victim.position();
        AABB searchArea = new AABB(
                victimPos.x - SHOCKWAVE_RADIUS, victimPos.y - 2.0, victimPos.z - SHOCKWAVE_RADIUS,
                victimPos.x + SHOCKWAVE_RADIUS, victimPos.y + 4.0, victimPos.z + SHOCKWAVE_RADIUS
        );

        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchArea);

        for (LivingEntity target : nearbyEntities) {
            // Don't knockback the victim themselves
            if (target.equals(victim)) {
                continue;
            }

            double distance = target.distanceTo(victim);
            if (distance <= SHOCKWAVE_RADIUS && distance > 0) {
                double knockbackStrength = ((selectedEnchantLevel / 1.5) - (distance / 2.0)) * 0.6666;

                if (knockbackStrength > 0) {
                    Vec3 targetPos = target.position();
                    double deltaX = targetPos.x - victimPos.x;
                    double deltaZ = targetPos.z - victimPos.z;

                    // Normalize the direction vector
                    double magnitude = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                    if (magnitude > 0) {
                        deltaX /= magnitude;
                        deltaZ /= magnitude;

                        // Apply knockback in the opposite direction (away from player)
                        target.knockback(knockbackStrength, -deltaX, -deltaZ);
                    }
                }
            }
        }
    }

    @Unique
    private void spawnShockwaveParticles(ServerLevel level, LivingEntity victim, int enchantmentLevel) {
        Vec3 pos = victim.position();
        double feetY = pos.y + 0.1; // Just above the ground at player's feet

        // Calculate particle count based on enchantment level (3 particles per level)
        int baseParticleCount = enchantmentLevel * 3;


        // Create radial shockwave particles
        for (int ring = 0; ring < 3; ring++) {
            double radius = 1.0 + (ring * 1.5); // Expanding rings
            int particleCount = (baseParticleCount / 3) + (ring * 2); // Distribute particles across rings

            for (int i = 0; i < particleCount; i++) {
                double angle = (i / (double) particleCount) * 2 * Math.PI;

                // Starting position at player's feet
                double startX = pos.x;
                double startZ = pos.z;

                // End position (radially outward)
                double endX = pos.x + Math.cos(angle) * radius;
                double endZ = pos.z + Math.sin(angle) * radius;

                // Direction vector for particle velocity
                double velocityX = (endX - startX) * 0.3;
                double velocityZ = (endZ - startZ) * 0.3;

                // Spawn particles that move outwards
                level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        startX, feetY, startZ,
                        1, velocityX, 0.05, velocityZ, 0.5);
            }
        }

        // Add some dust clouds for extra effect (also scaled with enchantment level)
        int cloudCount = Math.min(15, enchantmentLevel * 2);
        for (int i = 0; i < cloudCount; i++) {
            double angle = (i / (double) cloudCount) * 2 * Math.PI;
            double radius = 1.5 + random.nextDouble();
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.CLOUD,
                    x, feetY, z,
                    1, Math.cos(angle) * 0.2, 0.1, Math.sin(angle) * 0.2, 0.1);
        }
    }

    @Unique
    private List<ItemStack> getThornsArmorPieces(LivingEntity entity, ServerLevel level) {
        List<ItemStack> thornsArmor = new ArrayList<>();
        Holder<Enchantment> thornsHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.THORNS);

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack armorPiece = entity.getItemBySlot(slot);

            if (!armorPiece.isEmpty() &&
                    armorPiece.getItem() instanceof ArmorItem &&
                    EnchantmentHelper.getItemEnchantmentLevel(thornsHolder, armorPiece) > 0) {

                thornsArmor.add(armorPiece);
            }
        }

        return thornsArmor;
    }

    @Unique
    private long getCooldownTime(LivingEntity victim) {
        ServerLevel level = (ServerLevel) victim.level();
        Holder<Enchantment> thornsHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.THORNS);

        int thornsArmorCount = 0;
        int thornsVCount = 0;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack armorPiece = victim.getItemBySlot(slot);

            if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof ArmorItem) {
                int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(thornsHolder, armorPiece);

                if (enchantLevel > 0) {
                    thornsArmorCount++;
                    if (enchantLevel >= 5) { // V or above for mod compatibility
                        thornsVCount++;
                    }
                }
            }
        }

        if (thornsArmorCount == 0) {
            return Long.MAX_VALUE; // No cooldown if no Thorns armor
        }

        // Formula: (60 / thornsArmorCount) - (1.25 * thornsVCount)
        double cooldownSeconds = (60.0 / thornsArmorCount) - (1.25 * thornsVCount);

        // Ensure minimum cooldown of 10 seconds
        cooldownSeconds = Math.max(10.0, cooldownSeconds);

        return (long) (cooldownSeconds * 1000); // Convert to milliseconds
    }

    @Unique
    private void cleanupOldCooldowns(long currentTime) {
        // Remove cooldowns older than 5 minutes to prevent memory leaks
        long cutoffTime = currentTime - (5 * 60 * 1000);
        shockwaveCooldowns.entrySet().removeIf(entry -> entry.getValue() < cutoffTime);
    }
}