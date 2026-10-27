package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public record ThornsShockwaveEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<ThornsShockwaveEffect> CODEC = MapCodec.unit(ThornsShockwaveEffect::new);

    private static final Map<UUID, Long> SHOCKWAVE_COOLDOWNS = new ConcurrentHashMap<>();
    private static final double SHOCKWAVE_RADIUS = 4.0;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse enchantedItem, Entity target, Vec3 origin) {
        if (!(target instanceof LivingEntity victim)) return;

        Holder<Enchantment> thorns = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS);

        UUID victimUUID = victim.getUUID();
        long now = level.getGameTime();

        Long lastShockwave = SHOCKWAVE_COOLDOWNS.get(victimUUID);
        if (lastShockwave != null && now >= lastShockwave && now - lastShockwave < getCooldownTicks(victim, thorns)) {
            return;
        }

        applyThornsShockwave(level, victim, thorns);

        SHOCKWAVE_COOLDOWNS.put(victimUUID, now);
        cleanupOldCooldowns(now);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    private static void applyThornsShockwave(ServerLevel level, LivingEntity victim, Holder<Enchantment> thorns) {
        List<ItemStack> thornsArmor = getThornsArmorPieces(victim, thorns);

        if (thornsArmor.isEmpty()) {
            return;
        }

        RandomSource random = level.getRandom();
        ItemStack selectedPiece = thornsArmor.get(random.nextInt(thornsArmor.size()));
        int selectedEnchantLevel = selectedPiece.getEnchantmentLevel(thorns);

        spawnShockwaveParticles(level, victim, selectedEnchantLevel, random);

        Vec3 victimPos = victim.position();
        AABB searchArea = new AABB(
                victimPos.x - SHOCKWAVE_RADIUS, victimPos.y - 2.0, victimPos.z - SHOCKWAVE_RADIUS,
                victimPos.x + SHOCKWAVE_RADIUS, victimPos.y + 4.0, victimPos.z + SHOCKWAVE_RADIUS
        );

        for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, searchArea)) {
            if (nearby.equals(victim)) {
                continue;
            }

            double distance = nearby.distanceTo(victim);
            if (distance <= SHOCKWAVE_RADIUS && distance > 0) {
                double knockbackStrength = ((selectedEnchantLevel / 1.5) - (distance / 2.0)) * 0.6666;

                if (knockbackStrength > 0) {
                    Vec3 targetPos = nearby.position();
                    double deltaX = targetPos.x - victimPos.x;
                    double deltaZ = targetPos.z - victimPos.z;

                    double magnitude = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                    if (magnitude > 0) {
                        deltaX /= magnitude;
                        deltaZ /= magnitude;

                        nearby.knockback(knockbackStrength, -deltaX, -deltaZ);
                    }
                }
            }
        }
    }

    private static void spawnShockwaveParticles(ServerLevel level, LivingEntity victim, int enchantmentLevel, RandomSource random) {
        Vec3 pos = victim.position();
        double feetY = pos.y + 0.1;

        int baseParticleCount = enchantmentLevel * 3;

        for (int ring = 0; ring < 3; ring++) {
            double radius = 1.0 + (ring * 1.5);
            int particleCount = (baseParticleCount / 3) + (ring * 2);

            for (int i = 0; i < particleCount; i++) {
                double angle = (i / (double) particleCount) * 2 * Math.PI;

                double startX = pos.x;
                double startZ = pos.z;

                double endX = pos.x + Math.cos(angle) * radius;
                double endZ = pos.z + Math.sin(angle) * radius;

                double velocityX = (endX - startX) * 0.3;
                double velocityZ = (endZ - startZ) * 0.3;

                level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        startX, feetY, startZ,
                        1, velocityX, 0.05, velocityZ, 0.5);
            }
        }

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

    private static List<ItemStack> getThornsArmorPieces(LivingEntity entity, Holder<Enchantment> thorns) {
        List<ItemStack> thornsArmor = new ArrayList<>();

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armorPiece = entity.getItemBySlot(slot);

            if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof ArmorItem && armorPiece.getEnchantmentLevel(thorns) > 0) {
                thornsArmor.add(armorPiece);
            }
        }

        return thornsArmor;
    }

    private static long getCooldownTicks(LivingEntity victim, Holder<Enchantment> thorns) {
        int thornsArmorCount = 0;
        int thornsVCount = 0;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armorPiece = victim.getItemBySlot(slot);

            if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof ArmorItem) {
                int enchantLevel = armorPiece.getEnchantmentLevel(thorns);

                if (enchantLevel > 0) {
                    thornsArmorCount++;
                    if (enchantLevel >= 5) {
                        thornsVCount++;
                    }
                }
            }
        }

        if (thornsArmorCount == 0) {
            return Long.MAX_VALUE;
        }

        double cooldownSeconds = (60.0 / thornsArmorCount) - (1.25 * thornsVCount);
        cooldownSeconds = Math.max(10.0, cooldownSeconds);

        return (long) (cooldownSeconds * 20);
    }

    private static void cleanupOldCooldowns(long now) {
        if (SHOCKWAVE_COOLDOWNS.size() < 32) return;
        SHOCKWAVE_COOLDOWNS.entrySet().removeIf(entry -> now < entry.getValue() || now - entry.getValue() > 6000);
    }
}
