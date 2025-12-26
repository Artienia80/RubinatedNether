package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class TarnishedShockwaveGoal extends Goal {
    private final BronzeEntity entity;
    private int defenseTicks = 0;

    public boolean isStunned = false;
    private int stunTicks = 0;

    private static final int MAX_DEFENSE_TICKS = 150;
    private static final int STUN_DURATION = 80;
    private static final int COOLDOWN_DURATION = 300;
    private static final double KNOCKBACK_RADIUS = 8.0;
    private static final double KNOCKBACK_STRENGTH = 3.5;
    private static final float SHOCKWAVE_DAMAGE = 8.0F;

    public TarnishedShockwaveGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return entity.getShockwaveCooldown() <= 0
                && entity.getTarnishLevel().equals(TarnishStage.TARNISHED)
                && entity.getTarget() instanceof Player;
    }

    @Override
    public void start() {
        defenseTicks = 0;
        this.entity.setDefending(true);
        isStunned = false;
        stunTicks = 0;
        entity.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return (this.entity.isDefending() || isStunned) && entity.getTarnishLevel().equals(TarnishStage.TARNISHED);
    }

    @Override
    public void tick() {
        if(!entity.getTarnishLevel().equals(TarnishStage.TARNISHED)) stop();

        if (this.entity.isDefending()) {
            // Keep entity still during defense
            entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
            entity.getNavigation().stop();

            if (!entity.level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, BronzeEntity.DEFENCE_START);
            }
            defenseTicks++;

            if (defenseTicks >= MAX_DEFENSE_TICKS) {
                this.entity.setDefending(false);
                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, BronzeEntity.DEFENCE_STOP);
                }

                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, BronzeEntity.SHOCKWAVE_START);
                }
                spawnShockwaveParticles();
                performShockwave();
                entity.setShockwaveCooldown(COOLDOWN_DURATION);
                this.stop();
            }
        }

        if (isStunned) {
            entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
            stunTicks--;
            if (stunTicks <= 0) {
                isStunned = false;
                entity.setShockwaveCooldown(COOLDOWN_DURATION);
                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, BronzeEntity.STUN_STOP);
                }
                this.stop();
            }
        }

        if (entity.getShockwaveCooldown() > 0) {
            entity.setShockwaveCooldown(COOLDOWN_DURATION);
        }
    }

    @Override
    public void stop() {
        this.entity.setDefending(false);
        isStunned = false;
        stunTicks = 0;

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, BronzeEntity.DEFENCE_STOP);
            entity.level().broadcastEntityEvent(entity, BronzeEntity.STUN_STOP);
        }
    }

    public void triggerStunFromKeyHit() {
        if (this.entity.isDefending()) {
            this.entity.setDefending(false);
            isStunned = true;
            stunTicks = STUN_DURATION;
            entity.setDeltaMovement(Vec3.ZERO);

            if (!entity.level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, BronzeEntity.DEFENCE_STOP);
                entity.level().broadcastEntityEvent(entity, BronzeEntity.STUN_START);
                // Play electrical malfunction sounds
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        net.minecraft.sounds.SoundEvents.BEACON_DEACTIVATE,
                        net.minecraft.sounds.SoundSource.HOSTILE, 1.2F, 0.8F);
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        net.minecraft.sounds.SoundEvents.REDSTONE_TORCH_BURNOUT,
                        net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 0.5F);
            }
        }
    }

    private void spawnShockwaveParticles() {
        ((ServerLevel) entity.level()).sendParticles(ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 20, 1, 1, 1, 0.2);
    }

    private void performShockwave() {
        buffNearbyBronzes();
        knockbackNearbyEntities();
    }

    private void buffNearbyBronzes() {
        List<BronzeEntity> allies = entity.level().getEntitiesOfClass(BronzeEntity.class, entity.getBoundingBox().inflate(12.0));
        for (BronzeEntity bronze : allies) {
            if (bronze != entity && bronze.isAlive()) {
                bronze.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
            }
        }
    }

    private void knockbackNearbyEntities() {
        Vec3 explosionCenter = entity.position();

        // Get all living entities in range
        List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(KNOCKBACK_RADIUS),
                e -> e != entity && !(e instanceof BronzeEntity)
        );

        for (LivingEntity target : nearbyEntities) {
            double distance = target.distanceTo(entity);

            // Only knockback if within radius
            if (distance <= KNOCKBACK_RADIUS) {
                // Calculate direction from explosion center to target
                Vec3 direction = target.position().subtract(explosionCenter).normalize();

                // Calculate knockback strength based on distance (closer = stronger)
                double distanceFactor = 1.0 - (distance / KNOCKBACK_RADIUS);
                double knockbackMultiplier = KNOCKBACK_STRENGTH * distanceFactor;

                // Apply knockback using explosion-like calculation
                double knockbackResistance = 0.0;
                if (target.getAttributes().hasAttribute(net.minecraft.world.entity.ai.attributes.Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)) {
                    knockbackResistance = target.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);
                }

                double finalKnockback = knockbackMultiplier * (1.0 - knockbackResistance);

                // Apply the knockback with more upward force
                Vec3 knockbackVec = new Vec3(
                        direction.x * finalKnockback,
                        Math.min(direction.y * finalKnockback + 0.8, 1.5), // Even more upward component
                        direction.z * finalKnockback
                );

                target.setDeltaMovement(target.getDeltaMovement().add(knockbackVec));
                target.hurtMarked = true; // Force velocity update on client

                // Deal damage
                target.hurt(entity.damageSources().mobAttack(entity), SHOCKWAVE_DAMAGE);
            }
        }

        // Play sound effect
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                net.minecraft.sounds.SoundSource.HOSTILE, 2.0F, 0.8F);
    }
}